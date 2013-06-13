package pedidos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import pedidos.dominio.Envio;
import pedidos.dominio.Pedido;
import pedidos.dominio.PedidoDetalle;
import pedidos.dominio.Fincado;
import pedidos.dominio.FincadoDetalle;

/**
 *
 * @author Julio
 */
public class DAOPedidos {
    Format formato=new SimpleDateFormat("dd/MM/yyyy");
    private DataSource ds;
    
    public DAOPedidos() throws NamingException {
        try {
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/jdbc/__anita");
        } catch (NamingException ex) {
            throw(ex);
        }
    }
    
    public void grabarPrioridades(ArrayList<Envio> listaEnvios) throws SQLException {
        String strSQL="UPDATE enviomer SET prioridad=? WHERE cod_bod=? and cod_env=?";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        PreparedStatement ps=cn.prepareStatement(strSQL);
        try {
            st.executeUpdate("begin transaction");
            
            for(Envio e: listaEnvios) {
                ps.setInt(1, e.getPrioridad());
                ps.setString(2, e.getCod_bod());
                ps.setString(3, e.getCod_env());
                ps.executeUpdate();
            }
            
            st.executeUpdate("commit transaction");
        } catch(SQLException e) {
            st.executeUpdate("rollback transaction");
            throw(e);
        } finally {
            cn.close();
        }
    }
    
    public ArrayList<Envio> obtenerListaEnvios() throws SQLException {
        ArrayList<Envio> listaEnvios=new ArrayList<Envio>();
        String strSQL=""
                + "SELECT cod_bod, cod_env, fechaGen, prioridad "
                + "FROM enviomer "
                + "WHERE estado='A' "
                + "ORDER BY fechaGen";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                listaEnvios.add(construirEnvio(rs));
            }
        } finally {
            cn.close();
        }
        return listaEnvios;
    }
        
    private Envio construirEnvio(ResultSet rs) throws SQLException {
        Envio e=new Envio();
        e.setCod_bod(rs.getString("cod_bod"));
        e.setCod_env(rs.getString("cod_env"));
        e.setFechaGen(formato.format(rs.getDate("fechaGen")));
        e.setPrioridad(rs.getInt("prioridad"));
        return e;
    }
    
    public void grabar(Pedido pedido, ArrayList<Fincado> listaFincados, ArrayList<PedidoDetalle> listaPedidoDetalle, double pesoTotal) throws SQLException {
        double costoTotal=0.00;
        String hoy;
        String cod_env;
        Format formatoSQL=new SimpleDateFormat("yyyy-MM-dd");
        String strSQL;
        
        Connection cn=this.ds.getConnection();
        strSQL=""
            + "UPDATE pedbodeg SET entDirec=?, orden=? "
            + "WHERE cod_bod='"+pedido.getCod_bod()+"' and cod_cli=? and cod_ped=?";
        PreparedStatement ps1=cn.prepareStatement(strSQL);
        strSQL=""
            + "UPDATE detenvio "
            + "SET cantsuge=?, existen=?, diasinv=?, bdia=?, cantped=?, cantfinc=?, cantdire=?, cajas=?, sugerBod=? "
            + "WHERE cod_bod=? and cod_env=? and cod_emp=? and cod_pro=?";
        PreparedStatement ps2=cn.prepareStatement(strSQL);
        strSQL=""
            + "INSERT INTO detenvio (cod_bod, cod_env, cod_emp, cod_pro, cantsuge, cantdire, cantfinc, cantped, existen, estadis, diasinv, bdia, cajas, refe, estado, cod_ser, cod_fac, cod_empf, t_canenv, sugerBod) "
            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '', 'A', '', '', '', 0.00, ?)";
        PreparedStatement ps3=cn.prepareStatement(strSQL);
        strSQL=""
            + "UPDATE depedcont "
            + "SET cod_env=? "
            + "WHERE cod_bod='"+pedido.getCod_bod()+"' and cod_ped=? and cod_cli=? and cod_emp=? and cod_env='000000'";
        PreparedStatement ps4=cn.prepareStatement(strSQL);
        
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            
            if(pedido.getCod_env().equals("000000")) {
                strSQL = ""
                        + "SELECT COALESCE(CAST(MAX(cod_env) AS INT), 0)+1 AS idPedido, GETDATE() AS hoy "
                        + "FROM enviomer "
                        + "WHERE cod_bod='" + pedido.getCod_bod() + "'";
                ResultSet rs=st.executeQuery(strSQL);
                rs.next();
                cod_env=String.format("%06d", rs.getInt("idPedido"));
                hoy=formatoSQL.format(rs.getDate("hoy").getTime());
                
                strSQL="INSERT INTO enviomer (cod_bod, cod_env, fechagen, fechaenv, peso, estado, costo, prioridad, chofer) "
                        + "VALUES('"+pedido.getCod_bod()+"', '"+cod_env+"', '"+hoy+"', '1900-01-01', 0.00, 'A', 0.00, 0, '')";
                st.executeUpdate(strSQL);
                
                for(Fincado f:listaFincados) {
                    ps4.setString(1, cod_env);
                    ps4.setString(2, f.getCod_ped());
                    ps4.setString(3, f.getCod_cli());
                    ps4.setString(4, f.getCod_emp());
                    ps4.executeUpdate();
                }
            } else {
                hoy=pedido.getFechagen();
                cod_env=pedido.getCod_env();
            }
            
            for(Fincado f:listaFincados) {
                ps1.setString(1, f.isDirecto() ? "D" : "F");
                ps1.setInt(2, f.getOrden());
                ps1.setString(3, f.getCod_cli());
                ps1.setString(4, f.getCod_ped());
                ps1.executeUpdate();
            }
            
            for(PedidoDetalle d: listaPedidoDetalle) {
                if(d.isGrabado()) {
                    ps2.setDouble(1, d.getPrecio());
                    ps2.setDouble(2, d.getExistencia());
                    if(d.getBanDiasInv().equals("1")) {
                        ps2.setInt(3, d.getDiasInventario());
                    } else {
                        ps2.setInt(3, pedido.getDiasInventario());
                    }
                    ps2.setString(4, d.getBanDiasInv());
                    ps2.setDouble(5, d.getCajasOriginal());
                    ps2.setDouble(6, d.getFincado());
                    ps2.setDouble(7, d.getDirecta());
                    ps2.setDouble(8, d.getCajas());
                    ps2.setDouble(9, d.getSugerida());
                    ps2.setString(10, pedido.getCod_bod());
                    ps2.setString(11, cod_env);
                    ps2.setString(12, d.getCod_emp());
                    ps2.setString(13, d.getCod_pro());
                    ps2.executeUpdate();
                } else {
                    ps3.setString(1, pedido.getCod_bod());
                    ps3.setString(2, cod_env);
                    ps3.setString(3, d.getCod_emp());
                    ps3.setString(4, d.getCod_pro());
                    ps3.setDouble(5, d.getPrecio());
                    ps3.setDouble(6, d.getDirecta());
                    ps3.setDouble(7, d.getFincado());
                    ps3.setDouble(8, d.getCajasOriginal());
                    ps3.setDouble(9, d.getExistencia());
                    ps3.setDouble(10, d.getEstadistica());
                    ps3.setInt(11, pedido.getDiasInventario());
                    ps3.setString(12, d.getBanDiasInv());
                    ps3.setDouble(13, d.getCajas());
                    ps3.setDouble(14, d.getSugerida());
                    ps3.executeUpdate();
                    
                    d.setGrabado(true);
                }
                costoTotal+=(d.getCajas()*d.getPzasepq())*d.getPrecio();
            }
            strSQL="UPDATE enviomer SET peso="+pesoTotal+", costo="+costoTotal+" "
                    + "WHERE cod_bod='"+pedido.getCod_bod()+"' and cod_env='"+cod_env+"'";
            st.executeUpdate(strSQL);
            
            st.executeUpdate("commit Transaction");
            
            pedido.setCod_env(cod_env);
            pedido.setFechagen(hoy);
            pedido.setCosto(costoTotal);
            pedido.setPeso(pesoTotal);
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw(ex);
        } finally {
            cn.close();
        }
    }
    
    public PedidoDetalle crearPedidoDetalleProducto(String cod_emp, String cod_pro, String cod_bod) throws SQLException {
        PedidoDetalle detalle=new PedidoDetalle();
        String strSQL=""
                + "select p.cod_emp, p.cod_pro, p.descrip, coalesce(l.precio, 0.00) as precio, 0.00 as cantdire, 0.00 as fincado, p.pzasepq, p.peso, 0 as cantped "
                + "         , 0.00 as estadis, coalesce(i.existen,0.00) as existen, 0 as diasinv, '2' as bdia, 0.00 as pedido, 0 as cajas, 0 as grabado, 0 as sugerbod "
                + "from producto p "
                + "left join (select cod_emp, cod_pro, precio from listas "
                + "			where cod_gru='200' and status='A') l on l.cod_emp=p.cod_emp and l.cod_pro=p.cod_pro "
                + "left join (select cod_emp, cod_pro, existen from invprod "
                + "			where cod_bod='"+cod_bod+"') i on i.cod_emp=p.cod_emp and i.cod_pro=p.cod_pro "
                + "where p.cod_emp='"+cod_emp+"' and p.cod_pro='"+cod_pro+"'";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                detalle=construirDetalle(rs);
            }
        } finally {
            cn.close();
        }
        return detalle;
    }
    
    public int cambiaFincadoDirecto(String cod_bod, String cod_ped, String cod_cli, String cod_emp, boolean directo) throws SQLException {
        int filas=0;
        String entDirec="F";
        if(directo) {
            entDirec="D";
        }
        String strSQL1="select entDirec from pedbodeg "
                + "where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"'";
        String strSQL2="update pedbodeg set entDirec='"+entDirec+"' "
                + "where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"'";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin transaction");
            
            ResultSet rs=st.executeQuery(strSQL1);
            if(rs.next()) {
                String antDirec=rs.getString("entDirec");
                if(!antDirec.equals(entDirec)) {
                    filas=st.executeUpdate(strSQL2);
                }
            }
            
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw(ex);
        } finally {
            cn.close();
        }
        return filas;
    }
    
    public void actualizaFincadoProductos(String cod_bod, String cod_ped, String cod_cli, String cod_emp, boolean agregado, String cod_env) throws SQLException {
        String strSQL;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            if(agregado) {
                strSQL="update depedcont set cod_env='"+cod_env+"' "
                    + "where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"' and cod_env=''";
            } else {
                strSQL="update depedcont set cod_env='' "
                    + "where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"' and cod_env='"+cod_env+"'";
            }
            st.executeUpdate(strSQL);
        } finally {
            cn.close();
        }
    }
    
    public ArrayList<FincadoDetalle> obtenerFincadoProductos(String cod_bod, String cod_ped, String cod_cli, String cod_emp, String cod_env) throws SQLException {
        ArrayList<FincadoDetalle> listaDetalle=new ArrayList<FincadoDetalle>();
        String strSQL=""
                + "select d.cod_emp, d.cod_pro, p.descrip as descripcion, d.cantped, p.peso, p.pzasepq, case when '"+cod_env+"'='' then 0 else 1 end as agregado, 1 as orden "
                + "from (select cod_emp, cod_pro, sum(cantped) as cantped "
                + "      from depedcont "
                + "      where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"' and cod_env='"+cod_env+"' "
                + "      group by cod_emp, cod_pro) d "
                + "inner join producto p on p.cod_emp=d.cod_emp and p.cod_pro=d.cod_pro "
                + "order by d.cod_emp, d.cod_pro";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                listaDetalle.add(construirFincadoDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return listaDetalle;
    }
    
    public ArrayList<FincadoDetalle> obtenerFincadoProductos(String cod_bod, String cod_ped, String cod_cli, String cod_emp, String cod_pro, int orden, String cod_env) throws SQLException {
        ArrayList<FincadoDetalle> listaDetalle=new ArrayList<FincadoDetalle>();
        String strSQL=""
                + "select d.cod_emp, d.cod_pro, p.descrip as descripcion, d.cantped, p.peso, p.pzasepq "
                + "     , case when d.cod_env='' then 0 else 1 end as agregado, d.orden "
                + "from depedcont d "
                + "inner join producto p on p.cod_emp=d.cod_emp and p.cod_pro=d.cod_pro "
                + "where d.cod_bod='"+cod_bod+"' and d.cod_ped='"+cod_ped+"' and d.cod_cli='"+cod_cli+"' and d.cod_emp='"+cod_emp+"'";
        if(!cod_pro.equals("")) {
            strSQL+=" and d.cod_pro='"+cod_pro+"' and d.orden="+orden;
        }
        strSQL+=" and (d.cod_env='' or d.cod_env='"+cod_env+"') "
                + "order by d.cod_emp, d.cod_pro, d.orden";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                listaDetalle.add(construirFincadoDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return listaDetalle;
    }
    
    public FincadoDetalle obtenerFincadoProducto(String cod_bod, String cod_ped, String cod_cli, String cod_emp, String cod_pro, String cod_env) throws SQLException {
        FincadoDetalle d=null;
        String strSQL=""
                + "select d.cod_emp, d.cod_pro, p.descrip as descripcion, d.cantped, p.peso, p.pzasepq, case when '"+cod_env+"'='' then 0 else 1 end as agregado, 1 as orden "
                + "from (select cod_emp, cod_pro, sum(cantped) as cantped "
                + "      from depedcont "
                + "      where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"' and cod_pro='"+cod_pro+"' and cod_env='"+cod_env+"' "
                + "      group by cod_emp, cod_pro) d "
                + "inner join producto p on p.cod_emp=d.cod_emp and p.cod_pro=d.cod_pro "
                + "order by d.cod_emp, d.cod_pro";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                d=construirFincadoDetalle(rs);
            }
        } finally {
            cn.close();
        }
        return d;
    }
    
    public void actualizaFincadoProducto(String cod_bod, String cod_ped, String cod_cli, String cod_emp, String cod_pro, int orden, boolean agregado, String cod_env) throws SQLException {
        String strSQL;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            if(agregado) {
                strSQL="update depedcont set cod_env='"+cod_env+"' "
                    + "where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"' and cod_pro='"+cod_pro+"' and orden="+orden+" and cod_env='' ";
                
            } else {
                strSQL="update depedcont set cod_env='' "
                    + "where cod_bod='"+cod_bod+"' and cod_ped='"+cod_ped+"' and cod_cli='"+cod_cli+"' and cod_emp='"+cod_emp+"' and cod_pro='"+cod_pro+"' and orden="+orden+" and cod_env='"+cod_env+"' ";
            }
            st.executeUpdate(strSQL);
        } finally {
            cn.close();
        }
    }
    
    private FincadoDetalle construirFincadoDetalle(ResultSet rs) throws SQLException {
        FincadoDetalle d=new FincadoDetalle();
        d.setCod_emp(rs.getString("cod_emp"));
        d.setCod_pro(rs.getString("cod_pro"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setPzasepq(rs.getInt("pzasepq"));
        d.setCantPed(rs.getDouble("cantPed"));
        d.setPeso(rs.getDouble("peso")/d.getPzasepq());
        d.setAgregado(rs.getBoolean("agregado"));
        d.setOrden(rs.getInt("orden"));
        return d;
    }
    
    public ArrayList<Fincado> obtenerFincados(String cod_bod, String cod_env, String estado) throws SQLException {
        String strSQL;
        ArrayList<Fincado> listaPedidos=new ArrayList<Fincado>();
        /*
        strSQL1="select d.*, c.nombre as cliente, p.entDirec, p.orden, 1 as asignados "
                + "from (select d.cod_bod, d.cod_env, d.cod_ped, d.cod_cli, d.cod_emp, sum(d.cantped*p.peso/p.pzasepq) as peso, 1 as disponibles "
                + "	from depedcont d "
                + "	inner join envioped e on d.cod_bod=e.cod_bod and d.cod_ped=e.cod_ped and d.cod_cli=e.cod_cli and d.cod_emp=e.cod_emp and d.cod_env=e.cod_env "
                + "	inner join producto p on p.cod_emp=d.cod_emp and p.cod_pro=d.cod_pro "
                + "	where e.cod_bod='"+cod_bod+"' and e.cod_env='"+cod_env+"' "
                + "	group by d.cod_bod, d.cod_env, d.cod_ped, d.cod_cli, d.cod_emp) d "
                + "inner join pedbodeg p on p.cod_bod=d.cod_bod and p.cod_ped=d.cod_ped and p.cod_cli=d.cod_cli and p.cod_emp=d.cod_emp "
                + "inner join clientes c on c.cod_cli=d.cod_cli ";
         * 
         */
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            if(estado.equals("A")) {
                /*
                strSQL1+="union "
                        + "select p.cod_bod, '      ' as cod_env, p.cod_ped, p.cod_cli, p.cod_emp, 0 as peso "
                        + "     , coalesce((select sum(cantped) from depedcont where cod_bod=p.cod_bod and cod_env='' and cod_ped=p.cod_ped and cod_cli=p.cod_cli and cod_emp=p.cod_emp), 0) as disponibles  "
                        + " 	, c.nombre as cliente, p.entDirec, p.orden, 0 as asignados "
                        + "from pedbodeg p "
                        + "left join envioped e on e.cod_bod=p.cod_bod and e.cod_ped=p.cod_ped and e.cod_cli=p.cod_cli and e.cod_emp=p.cod_emp "
                        + "inner join clientes c on c.cod_cli=p.cod_cli "
                        + "where p.cod_bod='"+cod_bod+"' and p.bfactur='0' and p.bped='0' and e.cod_env is null ";
                */
                /*
                strSQL="select p.* "
                    + "from(select pb.cod_emp, pb.cod_cli, c.nombre as cliente, pb.cod_ped, pb.entDirec, pb.orden "
                    + "         , coalesce((select sum(d.cantped*p.peso/p.pzasepq) "
                    + "             		from depedcont d "
                    + "                 	inner join producto p on p.cod_emp=d.cod_emp and p.cod_pro=d.cod_pro "
                    + "                     where d.cod_bod=pb.cod_bod and d.cod_ped=pb.cod_ped and cod_cli=pb.cod_cli and d.cod_emp=pb.cod_emp and d.cod_env='"+cod_env+"'), 0.00) as peso "
                    + "         , coalesce((select sum(cantped) from depedcont where cod_bod=pb.cod_bod and cod_ped=pb.cod_ped and cod_cli=pb.cod_cli and cod_emp=pb.cod_emp and cod_env=''), 0) as disponibles "
                    + "         , coalesce((select sum(cantped) from depedcont where cod_bod=pb.cod_bod and cod_ped=pb.cod_ped and cod_cli=pb.cod_cli and cod_emp=pb.cod_emp and cod_env='"+cod_env+"'), 0) as asignados "
                    + "     from pedbodeg pb "
                    + "     inner join Clientes c on c.cod_cli=pb.cod_cli "
                    + "     where pb.cod_bod='"+cod_bod+"' and pb.bfactur='0' and pb.bped='0') p "
                    + "where p.disponibles+p.asignados > 0 "
                    + "order by p.cliente, p.cod_ped";
                 * 
                 */
                if(cod_env.equals("000000")) {
                    strSQL="update depedcont set cod_env='"+cod_env+"' "
                            + "from pedbodeg p "
                            + "inner join depedcont d on d.cod_bod=p.cod_bod and d.cod_ped=p.cod_ped and d.cod_cli=p.cod_cli and d.cod_emp=p.cod_emp "
                            + "where p.cod_bod='"+cod_bod+"' and p.bfactur='0' and p.bped='0' and d.cod_env=''";
                    st.executeUpdate(strSQL);
                }
                
                strSQL="select p.* "
                    + "from(select pb.cod_emp, pb.cod_cli, c.nombre as cliente, pb.cod_ped, pb.entDirec, pb.orden "
                    + "         , coalesce((select sum(d.cantped*p.peso/p.pzasepq) from depedcont d "
                    + "                 	inner join producto p on p.cod_emp=d.cod_emp and p.cod_pro=d.cod_pro "
                    + "                     where d.cod_bod=pb.cod_bod and d.cod_ped=pb.cod_ped and d.cod_cli=pb.cod_cli and d.cod_emp=pb.cod_emp and d.cod_env='"+cod_env+"'), 0.00) as peso "
                    + "         , coalesce((select sum(cantped) from depedcont d"
                    + "                     where d.cod_bod=pb.cod_bod and d.cod_ped=pb.cod_ped and d.cod_cli=pb.cod_cli and d.cod_emp=pb.cod_emp and d.cod_env='"+cod_env+"'), 0.00) as asignados "
                    + "     from pedbodeg pb "
                    + "     inner join Clientes c on c.cod_cli=pb.cod_cli "
                    + "     where pb.cod_bod='"+cod_bod+"' and pb.bfactur='0' and pb.bped='0') p "
                    + "where p.asignados > 0 "
                    + "order by p.cliente, p.cod_ped";
            } else {
                strSQL="select p.* "
                    + "from(select pb.cod_emp, pb.cod_cli, c.nombre as cliente, pb.cod_ped, pb.entDirec, pb.orden "
                    + "         , coalesce((select sum(d.cantped*p.peso/p.pzasepq) from depedcont d "
                    + "                 	inner join producto p on p.cod_emp=d.cod_emp and p.cod_pro=d.cod_pro "
                    + "                     where d.cod_bod=pb.cod_bod and d.cod_ped=pb.cod_ped and d.cod_cli=pb.cod_cli and d.cod_emp=pb.cod_emp and d.cod_env='"+cod_env+"'), 0.00) as peso "
                    + "         , coalesce((select sum(cantped) from depedcont d "
                    + "                     where d.cod_bod=pb.cod_bod and d.cod_ped=pb.cod_ped and d.cod_cli=pb.cod_cli and d.cod_emp=pb.cod_emp and d.cod_env='"+cod_env+"'), 0) as asignados "
                    + "     from pedbodeg pb "
                    + "     inner join Clientes c on c.cod_cli=pb.cod_cli "
                    + "     where pb.cod_bod='"+cod_bod+"') p "
                    + "where p.asignados > 0 "
                    + "order by p.cliente, p.cod_ped";
            }
            //strSQL1+="order by cliente, cod_ped";
        
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
          //      if(rs.getDouble("disponibles") > 0) 
                    listaPedidos.add(construirFincado(rs));
            }
        } finally {
            cn.close();
        }
        return listaPedidos;
    }
    
    private Fincado construirFincado(ResultSet rs) throws SQLException {
        String tmp;
        Fincado p=new Fincado();
        p.setCod_emp(rs.getString("cod_emp"));
        p.setCod_cli(rs.getString("cod_cli"));
        p.setTienda(rs.getString("cliente"));
        p.setCod_ped(rs.getString("cod_ped"));
        tmp=rs.getString("entDirec");
        if(tmp==null) {
            p.setDirecto(false);
        } else {
            p.setDirecto(tmp.equals("D"));
        }
        p.setOrden(rs.getInt("orden"));
        p.setPeso(rs.getDouble("peso"));
        p.setAgregado(rs.getDouble("asignados")>0);
        return p;
    }
    
    public ArrayList<PedidoDetalle> obtenerPedidoDetalle(String cod_bod, String cod_env, String estado) throws SQLException {
        String strSQL;
        ArrayList<PedidoDetalle> listaDetalle=new ArrayList<PedidoDetalle>();
        if(estado.equals("A")) {
            strSQL=""
                + "select p.cod_emp, p.cod_pro, p.descrip, l.precio, d.estadis, 1 as grabado "
                + "         , coalesce(i.existen, 0.00) as existen, coalesce(d.diasinv, 0) as diasinv, coalesce(d.bdia, '0') as bdia "
                + "         , d.cantfinc as fincado, d.cantdire, d.cajas, p.peso, p.pzasepq,p.cod_inv "
                + "         , d.cajas*p.pzasepq-d.cantdire as pedido, d.sugerBod, d.cantped "
                + "from producto p "
                + "inner join (select * from detenvio "
                + "         where cod_bod='"+cod_bod+"' and cod_env='"+cod_env+"') d on d.cod_emp=p.cod_emp and d.cod_pro=p.cod_pro "
                + "left join (select cod_emp, cod_pro, precio from listas "
                + "			where cod_gru='200' and status='A') l on l.cod_emp=p.cod_emp and l.cod_pro=p.cod_pro "
                + "left join (select cod_emp, cod_pro, existen from invprod "
                + "			where cod_bod='"+cod_bod+"') i on i.cod_emp=p.cod_emp and i.cod_pro=p.cod_pro "
                + "order by p.cod_inv,p.cod_emp, p.cod_pro";
        } else {
            strSQL=""
                + "select p.cod_emp, p.cod_pro, p.descrip, d.cantsuge as precio, d.estadis, 1 as grabado "
                + "         , d.existen, d.diasinv, d.bdia "
                + "         , d.cantfinc as fincado, d.cantdire, d.cajas, p.peso, p.pzasepq,p.cod_inv"
                + "         , d.cajas*p.pzasepq-cantdire as pedido, d.sugerBod, d.cantped "
                + "from producto p "
                + "inner join (select * from detenvio "
                + "         where cod_bod='"+cod_bod+"' and cod_env='"+cod_env+"') d on d.cod_emp=p.cod_emp and d.cod_pro=p.cod_pro "
                + "order by p.cod_inv,p.cod_emp, p.cod_pro";
        }
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                listaDetalle.add(construirDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return listaDetalle;
    }
    
    private PedidoDetalle construirDetalle(ResultSet rs) throws SQLException {
        PedidoDetalle detalle=new PedidoDetalle();
        detalle.setCod_emp(rs.getString("cod_emp"));
        detalle.setCod_pro(rs.getString("cod_pro"));
        detalle.setDescripcion(rs.getString("descrip"));
        detalle.setPrecio(rs.getDouble("precio"));
        detalle.setPzasepq(rs.getInt("pzasepq")); //
        detalle.setDirecta(rs.getDouble("cantdire"));
        detalle.setFincado(rs.getDouble("fincado"));
        detalle.setPedido(rs.getDouble("pedido")); //pedido mercancia en caja
        detalle.setEstadistica(rs.getDouble("estadis")); //Venta promedio?
        //System.out.println("Estadistica: "+detalle.getEstadistica());
        detalle.setExistencia(rs.getDouble("existen"));
        detalle.setDiasInventario(rs.getInt("diasInv")); //
        detalle.setBanDiasInv(rs.getString("bdia"));
        detalle.setCajas(rs.getDouble("cajas"));
        detalle.setGrabado(rs.getBoolean("grabado"));
        detalle.setPeso(rs.getDouble("peso")/detalle.getPzasepq()); //?
        detalle.setSugerida(rs.getDouble("sugerBod"));
        detalle.setCajasOriginal(rs.getDouble("cantped"));
        return detalle;
    }
    
    public ArrayList<PedidoDetalle> nuevoPedido(String cod_bod) throws SQLException {
        String strSQL;
        ArrayList<PedidoDetalle> lstDetalle=new ArrayList<PedidoDetalle>();
        Format formatoSQL=new SimpleDateFormat("yyyy-MM-dd");
        
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            
            strSQL="SELECT GETDATE() AS hoy, DATEADD(YEAR, -1, GETDATE()) AS anioAnterior "
                    + "FROM enviomer "
                    + "WHERE cod_bod='"+cod_bod+"'";
            ResultSet rs=st.executeQuery(strSQL);
            
            rs.next();
            String cod_env="000000";
            String hoy=formatoSQL.format(rs.getDate("hoy").getTime());
            String anioAnterior=formatoSQL.format(rs.getDate("anioAnterior"));
            
            //strSQL="INSERT INTO enviomer (cod_bod, cod_env, fechagen, fechaenv, peso, estado, costo, prioridad, chofer) "
            //    + "VALUES('"+cod_bod+"', '"+cod_env+"', '"+hoy+"', '1900-01-01', 0.00, 'A', 0.00, 0, '')";
            //st.executeUpdate(strSQL);
            
            //hoy="2012-07-27";
            //anioAnterior="2011-07-27";
            //generaDetalle(cn, cod_bod, hoy, anioAnterior);
            
            //strSQL="insert into detenvio (cod_bod, cod_env, cod_emp, cod_pro, cantsuge, cantdire, cantfinc, cantped, existen, estadis, diasinv, bdia, cajas, refe, estado, cod_ser, cod_fac, cod_empf, t_canenv) "
            //strSQL="select '"+cod_bod+"' as cod_bod, '"+cod_env+"' as cod_env, e.cod_emp, e.cod_pro, 0.00 as cantsuge, 0.00 as cantdire, 0.00 as cantfinc, e.bp as cantped, 0.00 as existen, e.bp as estadis, 1.00 as diasinv, 0 as bdia, ceiling(e.bp/e.pzasepq) as cajas, '', 'A' as estado, '', '', '', 0.00 "
            strSQL="select '"+cod_bod+"' as cod_bod, '"+cod_env+"' as cod_env, e.cod_emp, e.cod_pro, e.descrip, e.precio, e.pzasepq, e.cantdire, e.fincado, 0.00 as pedido, e.bp as estadis, e.existen, 0.00 as diasinv, '0' as bdia, 0 as grabado, e.peso, 0 as sugerBod"
                    + "     , ceiling((e.fincado+e.cantdire+e.bp-e.existen)/e.pzasepq) as cajas, ceiling((e.fincado+e.cantdire+e.bp-e.existen)/e.pzasepq) as cantped "
                    + "from (select p.cod_emp, p.cod_pro, p.descrip, p.pzasepq, p.precio, p.cantdire, p.fincado, p.existen, p.peso "
                    + "		, case when p.ten <= 0 then 0 "
                    + "            when p.est_p <= 0 then p.ten "
                    + "            when p.p < 0 then p.ten*0.10 + p.ma*0.55 + p.est_f2*0.35 "
                    + "			   when p.p > 0 then p.ten*0.20 + p.ma*0.60 + p.est_f2*0.20 "
                    + "            else 0 end as bp "
                    + "from(select p.cod_emp, p.cod_pro, p.descrip, p.pzasepq, coalesce(l.precio, 0.00) as precio, coalesce(r.cantdire, 0.00) as cantdire, coalesce(f.fincado, 0.00) as fincado, coalesce(i.existen, 0.00) as existen, p.peso "
                    + "					, coalesce(ten.ten,0) as ten, coalesce(ma.ma, 0) as ma, coalesce(est_p.est_p, 0) as est_p, coalesce(est_f2.est_f2, 0) as est_f2 "
                    + "					, case when coalesce(p2.p2, 0) = 0 then 0 "
                    + "                                         when coalesce(p1.p1, 0) = coalesce(p2.p2, 0) then -1 "
                    + "                                         else (coalesce(p1.p1, 0)-coalesce(p2.p2, 0))/coalesce(p2.p2, 0) end as p "
                    + "    from producto p "
                    + "    inner join (select cod_inv from gruprodu where bvtas=1) g on g.cod_inv=p.cod_inv "
                    + "    left join (select cod_emp, idProducto, sum(cantidad)/90 as ten "
                    + "                from estadisticaVentas "
                    + "                where fecha between DATEADD(day, -91, '"+hoy+"') and DATEADD(day, -1, '"+hoy+"') and cod_bod='"+cod_bod+"'"
                    + "                group by cod_emp, idProducto) ten on ten.cod_emp=p.cod_emp and ten.idProducto=p.cod_pro"
                    + "    left join (select cod_emp, idProducto, sum(cantidad)/30 as p1 "
                    + "                from estadisticaVentas "
                    + "                where fecha between DATEADD(day, -31, '"+hoy+"') and DATEADD(day, -1, '"+hoy+"') and cod_bod='"+cod_bod+"'"
                    + "                group by cod_emp, idProducto) p1 on p1.cod_emp=p.cod_emp and p1.idProducto=p.cod_pro"
                    + "    left join (select cod_emp, idProducto, sum(cantidad)/30 as p2 "
                    + "                from estadisticaVentas "
                    + "                where fecha between DATEADD(day, -62, '"+hoy+"') and DATEADD(day, -32, '"+hoy+"') and cod_bod='"+cod_bod+"'"
                    + "                group by cod_emp, idProducto) p2 on p2.cod_emp=p.cod_emp and p2.idProducto=p.cod_pro"
                    + "    left join (select cod_emp, idProducto, sum(cantidad)/30 as ma "
                    + "                from estadisticaVentas"
                    + "                where fecha between DATEADD(day, 1, '"+anioAnterior+"') and DATEADD(day, 30, '"+anioAnterior+"') and cod_bod='"+cod_bod+"'"
                    + "                group by cod_emp, idProducto) ma on ma.cod_emp=p.cod_emp and ma.idProducto=p.cod_pro"
                    + "    left join (select cod_emp, idProducto, sum(cantidad)/90 as est_p "
                    + "                from estadisticaVentas"
                    + "                where fecha between DATEADD(day, -90, '"+anioAnterior+"') and DATEADD(day, -1, '"+anioAnterior+"') and cod_bod='"+cod_bod+"'"
                    + "                group by cod_emp, idProducto) est_p on est_p.cod_emp=p.cod_emp and est_p.idProducto=p.cod_pro "
                    + "    left join (select cod_emp, idProducto, sum(cantidad)/60 as est_f2 "
                    + "                from estadisticaVentas"
                    + "                where fecha between DATEADD(day, 30, '"+anioAnterior+"') and DATEADD(day, 90, '"+anioAnterior+"') and cod_bod='"+cod_bod+"'"
                    + "                group by cod_emp, idProducto) est_f2 on est_f2.cod_emp=p.cod_emp and est_f2.idProducto=p.cod_pro "
                    + "    left join (select d.cod_emp, d.cod_pro, sum(d.cantped) as fincado "
                    + "                from pedbodeg p "
                    + "                inner join depedcont d on d.cod_bod=p.cod_bod and d.cod_ped=p.cod_ped and d.cod_cli=p.cod_cli and d.cod_emp=p.cod_emp "
                    + "                where p.cod_bod='"+cod_bod+"' and p.bfactur='0' and p.bped=0 and p.entDirec = 'F' and d.cod_env='"+cod_env+"' "
                    + "                group by d.cod_emp, d.cod_pro) f on f.cod_emp=p.cod_emp and f.cod_pro=p.cod_pro "
                    + "    left join (select d.cod_emp, d.cod_pro, sum(d.cantped) as cantdire "
                    + "                from pedbodeg p "
                    + "                inner join depedcont d on d.cod_bod=p.cod_bod and d.cod_ped=p.cod_ped and d.cod_cli=p.cod_cli and d.cod_emp=p.cod_emp "
                    + "                where p.cod_bod='"+cod_bod+"' and p.bfactur='0' and p.bped=0 and p.entDirec = 'D' and d.cod_env='"+cod_env+"' "
                    + "                group by d.cod_emp, d.cod_pro) r on r.cod_emp=p.cod_emp and r.cod_pro=p.cod_pro "
                    + "    left join (select cod_emp, cod_pro, precio from listas "
                    + "                where cod_gru='200' and status='A') l on l.cod_emp=p.cod_emp and l.cod_pro=p.cod_pro "
                    + "    left join (select cod_emp, cod_pro, existen from invprod "
                    + "                where cod_bod='"+cod_bod+"') i on i.cod_emp=p.cod_emp and i.cod_pro=p.cod_pro "
                    + "	   ) p "
                    + ") e "
                    + "where (e.bp+e.fincado+e.cantdire) > 0 and e.precio > 0 and e.peso > 0 "
                    + "order by e.cod_emp, e.cod_pro";
            //st.executeUpdate(strSQL);
            rs=st.executeQuery(strSQL);
            
            while(rs.next()) {
                lstDetalle.add(construirDetalle(rs));
            }
            
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw(ex);
        } finally {
            cn.close();
        }
        //return idPedido;
        return lstDetalle;
    }
    
    public Pedido obtenerPedido(String cod_bod, String cod_env) throws SQLException {
        Pedido to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(""
                    + "SELECT e.*, coalesce((select top 1 diasinv from detenvio "
                    + "                         where cod_bod=e.cod_bod and cod_env=e.cod_env and bdia=0), 0) as diasInventario "
                    + "FROM enviomer e WHERE e.cod_bod='"+cod_bod+"' and e.cod_env='"+cod_env+"'");
            if(rs.next()) {
                to=construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
    public ArrayList<Pedido> obtenerPedidos(String cod_bod) throws SQLException {
        ArrayList<Pedido> pedidos=new ArrayList<Pedido>();
        
        Connection cn=ds.getConnection();
        String strSQL=""
                + "SELECT top 100 e.* "
                + "     , coalesce((select top 1 diasinv from detenvio "
                + "                         where cod_bod=e.cod_bod and cod_env=e.cod_env and bdia=0), 0) as diasInventario "
                + "FROM enviomer e "
                + "WHERE e.cod_bod='"+cod_bod+"' "
                + "ORDER BY e.fechagen desc, e.cod_env desc";
        try {
            Statement sentencia = cn.createStatement();
            ResultSet rs = sentencia.executeQuery(strSQL);
            while(rs.next()) {
                pedidos.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return pedidos;
    }
    
    private Pedido construir(ResultSet rs) throws SQLException {
        Pedido to=new Pedido();
        to.setCod_bod(rs.getString("cod_bod"));
        to.setCod_env(rs.getString("cod_env"));
        //to.setFechagen(new java.util.Date(rs.getDate("fechagen").getTime()));
        to.setFechagen(formato.format(rs.getDate("fechagen")));
        to.setFechaenv(new java.util.Date(rs.getDate("fechaenv").getTime()));
        to.setPeso(rs.getDouble("peso"));
        to.setEstado(rs.getString("estado"));
        if(to.getEstado().equals("A")) {
            to.setEditable("true");
        } else {
            to.setEditable("false");
        }
        to.setSoloLectura(!to.getEstado().equals("A"));
        to.setCosto(rs.getDouble("costo"));
        to.setPrioridad(rs.getInt("prioridad"));
        to.setChofer(rs.getString("chofer"));
        to.setPesoE(rs.getDouble("pesoE"));
        to.setFechadist(new java.util.Date(rs.getDate("fechadist").getTime()));
        to.setFechapani(new java.util.Date(rs.getDate("fechapani").getTime()));
        to.setFechapqui(new java.util.Date(rs.getDate("fechapqui").getTime()));
        to.setFechaalm(new java.util.Date(rs.getDate("fechaalm").getTime()));
        to.setTipoent(rs.getString("tipoent"));
        to.setDiasInventario(rs.getInt("diasInventario"));
        return to;
    }
}
