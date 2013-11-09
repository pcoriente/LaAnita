package pedidos;

import cedis.MbCedis;
import cedis.MbMiniCedis;
import cedis.dominio.Cedis;
import cedis.dominio.MiniCedis;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import pedidos.dao.DAOPedidos;
import pedidos.dominio.Envio;
import pedidos.dominio.Fincado;
import pedidos.dominio.FincadoDetalle;
import pedidos.dominio.Pedido;
import pedidos.dominio.PedidoDetalle;
import productosOld.MbBuscarOld;
import productosOld.dominio.ProductoOld;

/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbPedido")
@SessionScoped
public class MbPedido implements Serializable {
    private Cedis cedis;
    private MiniCedis miniCedis;
    private Fincado fincado;
    private Pedido pedido;
    @ManagedProperty(value = "#{mbBuscar}")
    private MbBuscarOld mbBuscar;
    @ManagedProperty(value = "#{mbMiniCedis}")
    private MbMiniCedis mbMiniCedis;
    @ManagedProperty(value = "#{mbCedis}")
    private MbCedis mbCedis;
    //private int xDiasInventario;
    private double capacidadCamion;
    private double pesoDirectos;
    private double pesoBodega;
    //private ArrayList<SelectItem> listaMiniCedis;
    private ArrayList<SelectItem> listaPedidos;
    private ArrayList<PedidoDetalle> listaPedidoDetalle;
    private ArrayList<FincadoDetalle> listaFincadoDetalle;
    private ArrayList<Fincado> listaFincados;
    private ArrayList<Envio> listaEnvios;
    private DAOPedidos dao;
    //ArrayList<MiniCedis> lstMiniCedis;

    public MbPedido() {
        try {
            this.dao = new DAOPedidos();
            pedido=new Pedido();
            pedido.setDiasInventario(0);
        } catch (NamingException ex) {
            Logger.getLogger(MbPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
public String Salir(){
    //String str="pedidos.salir";
    String str="index.xhtml";
    this.pedido = null;
    this.miniCedis = null;
    //this.listaMiniCedis = null;
    this.mbMiniCedis.setListaMiniCedis(null);
    this.listaPedidos = new ArrayList<SelectItem>();
    this.listaFincados = new ArrayList<Fincado>();
    this.listaPedidoDetalle = new ArrayList<PedidoDetalle>();
    this.capacidadCamion = 0;
    this.pesoBodega = 0;
    this.pesoDirectos = 0;
    return str;
}
    //public void editarFincado() {}
    public void calcularDiasInv() {
        pesoBodega = 0;
        pedido.setDiasInventario(0);
        actualizaDiasInventarioGeneral();
        while (pesoDirectos + pesoBodega < this.capacidadCamion) {
            pedido.setDiasInventario(pedido.getDiasInventario() + 1);
            actualizaDiasInventarioGeneral();
        }
        if (pedido.getDiasInventario() > 0) {
            pedido.setDiasInventario(pedido.getDiasInventario() - 1);
            actualizaDiasInventarioGeneral();
        } // Mensaje de capacidad de camion excedida
    }

    public void grabarPrioridades() throws SQLException {
        this.dao.grabarPrioridades(listaEnvios);
    }

    public void grabar() throws SQLException, NamingException {
        this.dao.grabar(pedido, listaFincados, listaPedidoDetalle, pesoDirectos + pesoBodega);
        actualizaListaPedidos();
        //if(!FacesContext.getCurrentInstance().getMessageList().isEmpty()) 
        //    FacesContext.getCurrentInstance().getMessageList().remove(0);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Proceso finalizado", "Grabar de modificaciones"));
    }

    public void cancelar() throws SQLException {
        if (pedido.getCod_env().equals("000000")) {
            this.listaPedidos.remove(0);
            this.pedido = (Pedido) this.listaPedidos.get(0).getValue();
        } else {
            String cod_bod = pedido.getCod_bod();
            String cod_env = pedido.getCod_env();
            this.pedido = this.dao.obtenerPedido(cod_bod, cod_env);
        }
        obtenerPedido();
    }

    public void cancelarProducto() {
        mbBuscar.setCadena("");
        mbBuscar.setTipoBusqueda("1");
        mbBuscar.setListaEncontrados(new ArrayList<ProductoOld>());
        mbBuscar.setProducto(new ProductoOld());
    }

    public void agregarProducto() throws SQLException {
        String cod_emp = mbBuscar.getProducto().getCod_emp();
        String cod_pro = mbBuscar.getProducto().getCod_pro();
        //String cod_bod = miniCedis.getCodigo();
        String cod_bod = Integer.toString(miniCedis.getIdCedis());
        //String cod_bod = String.format("%02d", miniCedis.getCodigo());

        if (!productoAgregado(cod_emp, cod_pro, cod_bod)) {
        }// Aqui hay que crear mensaje de aviso que el producto ya existe en el pedido y no es necesario agregar
        cancelarProducto();
    }

    //private boolean productoAgregado(String cod_emp, String cod_pro, String cod_bod) throws SQLException {
    private boolean productoAgregado(String cod_emp, String cod_pro, String cod_bod) throws SQLException {
        int i = 0;
        boolean agregado = false;
        for (PedidoDetalle d : listaPedidoDetalle) {
            if (d.getCod_emp().equals(cod_emp) && d.getCod_pro().equals(cod_pro)) {
                break;
            }
            i++;
        }
        if (i == listaPedidoDetalle.size()) {
            PedidoDetalle d = this.dao.crearPedidoDetalleProducto(cod_emp, cod_pro, cod_bod);
            listaPedidoDetalle.add(d);
            agregado = true;
        }
        return agregado;
    }

    private void calcularDiaInvX(PedidoDetalle d) {
        double fincadoNeto;
        if (d.getBanDiasInv().equals("2")) {
            // Modificacion directa de las cajas
            // FincadoNeto es la cantidad de piezas requeridad para surtir la bodega sus pedidos
            // fincados y que no tiene en existencia, y que por tanto hay que mandarle
            fincadoNeto = d.getFincado() - d.getExistencia();
            if (fincadoNeto < 0) {
                fincadoNeto = 0;    // Si la existencia es mayor que los pedidos fincados no se manda nada
            }
            // Si se modifica la cantidad y es menor que fincado + directo, lo cual no puede ser
            // Se modifica cajas para que sea igual a fincado + directo
            if (d.getCajas() * d.getPzasepq() < fincadoNeto + d.getDirecta()) {
                d.setCajas((fincadoNeto + d.getDirecta()) / d.getPzasepq());
            }
            // Pedido: es lo que va a llegar a la bodega por eso se le quita la parte de directa
            d.setPedido(d.getCajas() * d.getPzasepq() - d.getDirecta());
            if (d.getEstadistica() == 0) {
                d.setDiasInventario(0);
            } else {
                d.setDiasInventario((int) Math.floor(d.getPedido() / d.getEstadistica()));
            }
        } else if (d.getBanDiasInv().equals("1")) {
            // Modificacion directa de dias inventario del producto
            // Se ajustan los valores de cajas y pedido y lo que sea necesario,
            // pero el pedido por estadistica no se mueve, pedidoOriginal mantiene su valor.
            d.setPedido(d.getEstadistica()*d.getDiasInventario() - d.getExistencia() + d.getFincado());
            if(d.getPedido()<0) {
                d.setPedido(0);
            }
            d.setCajas(Math.ceil((d.getPedido() + d.getDirecta()) / d.getPzasepq()));
            /*
            if (d.getExistencia() + d.getPedido() >= d.getFincado()) {
                d.setCajas(Math.ceil((d.getPedido() + d.getDirecta()) / d.getPzasepq()));
            } else {
                // Hay que mandar el minimo para cubrir fincado neto (fincado - existencia) y directos
                d.setCajas(Math.ceil((d.getFincado() - d.getExistencia() + d.getDirecta()) / d.getPzasepq()));
                d.setPedido(d.getCajas() * d.getPzasepq() - d.getDirecta());
                d.setDiasInventario((int) Math.floor(d.getPedido() / d.getEstadistica()));
                // Mensaje: Los dias de inventario no son suficientes para directos+fincados agregados
            }
            * */
        } else {
            d.setPedido(d.getEstadistica() * d.getDiasInventario());
            if (d.getPedido() + d.getFincado() <= d.getExistencia()) {
                // Si hay suficiente en existencia de bodega para surtir pedido + fincado, solo se manda lo directo
                d.setCajas(d.getDirecta() / d.getPzasepq());
                d.setPedido(0.00);
                d.setCajasOriginal(d.getCajas());
            } else {
                d.setCajas(Math.ceil((d.getPedido() + d.getFincado() + d.getDirecta() - d.getExistencia()) / d.getPzasepq()));
                d.setPedido(d.getCajas() * d.getPzasepq() - d.getDirecta());
                d.setCajasOriginal(d.getCajas());
            }
        }
    }

    // Esta funcion solo esta habilitada en IDE, si la estadistica es mayor que cero
    public void actualizaDiasInventarioProducto(String cod_emp, String cod_pro) {
        for (PedidoDetalle d : listaPedidoDetalle) {
            if (d.getCod_emp().equals(cod_emp) && d.getCod_pro().equals(cod_pro)) {
                restaPeso(d);
                d.setBanDiasInv("1");
                calcularDiaInvX(d);
                if (d.getDiasInventario() == pedido.getDiasInventario()) {
                    d.setBanDiasInv("0");
                }
                sumaPeso(d);
                break;
            }
        }
    }

    public void actualizaDiasInventarioGeneral() {
        pesoDirectos = 0;
        pesoBodega = 0;
        for (PedidoDetalle p : listaPedidoDetalle) {
            if (p.getBanDiasInv().equals("0")) {
                p.setDiasInventario(pedido.getDiasInventario());
                calcularDiaInvX(p);
            }
            sumaPeso(p);
        }
    }

    private void restaPeso(PedidoDetalle p) {
        pesoBodega -= p.getPedido() * p.getPeso();
        pesoDirectos -= p.getDirecta() * p.getPeso();
    }

    private void sumaPeso(PedidoDetalle p) {
        pesoBodega += p.getPedido() * p.getPeso();
        pesoDirectos += p.getDirecta() * p.getPeso();
    }

    private void restaPesoFincado(PedidoDetalle p, double cantped, boolean directo) {
        if (directo) {
            pesoDirectos -= cantped * p.getPeso();
            p.setDirecta(p.getDirecta() - cantped);
            this.calcularDiaInvX(p);
        } else {
            pesoBodega -= p.getPedido() * p.getPeso();
            p.setFincado(p.getFincado() - cantped);
            this.calcularDiaInvX(p);
            pesoBodega += p.getPedido() * p.getPeso();
        }
    }

    private void sumaPesoFincado(PedidoDetalle p, double cantped, boolean directo) {
        if (directo) {
            pesoDirectos += cantped * p.getPeso();
            p.setDirecta(p.getDirecta() + cantped);
            this.calcularDiaInvX(p);
        } else {
            pesoBodega -= p.getPedido() * p.getPeso();
            p.setFincado(p.getFincado() + cantped);
            this.calcularDiaInvX(p);
            pesoBodega += p.getPedido() * p.getPeso();
        }
    }

    // Esta funcion recalcula los dias de inventario, cuando se modifican las cajas a enviar directamente
    public void actualizaDetalleCajas(String cod_emp, String cod_pro) {
        for (PedidoDetalle p : listaPedidoDetalle) {
            if (p.getCod_emp().equals(cod_emp) && p.getCod_pro().equals(cod_pro)) {
                double cajas = p.getCajas();
                restaPeso(p);
                p.setBanDiasInv("2");
                this.calcularDiaInvX(p);
                if (p.getDiasInventario() == pedido.getDiasInventario()) {
                    p.setBanDiasInv("0");
                }
                sumaPeso(p);
                if (cajas != p.getCajas()) {
                    if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                        FacesContext.getCurrentInstance().getMessageList().remove(0);
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso de Modificación", "Las cajas no pueden ser manor a directo + fincado"));
                }
            }
        }
    }

    // Cambia UN PEDIDO FINCADO a DIRECTO y viceversa
    public void cambiaFincadoDirecto() throws SQLException {
        String cod_ped = fincado.getCod_ped();
        String cod_cli = fincado.getCod_cli();
        String cod_emp = fincado.getCod_emp();
        boolean agregado = fincado.isAgregado();
        boolean directo = fincado.isDirecto();
        int filas = this.dao.cambiaFincadoDirecto(pedido.getCod_bod(), cod_ped, cod_cli, cod_emp, directo);
        if (filas == 1) {
            if (agregado) {
                int i;
                PedidoDetalle d;
                int n = this.listaPedidoDetalle.size();
                listaFincadoDetalle = this.dao.obtenerFincadoProductos(pedido.getCod_bod(), cod_ped, cod_cli, cod_emp, pedido.getCod_env());
                for (FincadoDetalle f : listaFincadoDetalle) {
                    i = this.buscaFincadoEnDetalle(f);
                    d = this.listaPedidoDetalle.get(i);
                    if (i < n) { // El producto existe en listaPedidoDetalle, se resta su peso del estado anterior
                        restaPesoFincado(d, f.getCantPed(), !directo);
                    } else {    // El producto no existia y se agrego, no hay nada que restar
                        n++;    // Se agrega uno a la longitud de listaPedidoDetalle porque se agrego
                    }
                    sumaPesoFincado(d, f.getCantPed(), directo);
                }
                listaFincadoDetalle = null;
            }
        } else {
            fincado.setDirecto(!fincado.isDirecto());
        }
    }

    private int buscaFincadoEnDetalle(FincadoDetalle f) throws SQLException {
        int i = 0;
        for (PedidoDetalle p : listaPedidoDetalle) {
            if (p.getCod_emp().equals(f.getCod_emp()) && p.getCod_pro().equals(f.getCod_pro())) {
                break;
            }
            i++;
        }
        if (i == listaPedidoDetalle.size()) {
            PedidoDetalle d = this.dao.crearPedidoDetalleProducto(f.getCod_emp(), f.getCod_pro(), pedido.getCod_bod());
            listaPedidoDetalle.add(d);
        }
        return i;
    }

    public void obtenerFincadoProductos() throws SQLException {
        if (this.fincado == null || !this.fincado.isAgregado()) {
            this.listaFincadoDetalle = new ArrayList<FincadoDetalle>();
        } else {
            this.listaFincadoDetalle = this.dao.obtenerFincadoProductos(pedido.getCod_bod(), fincado.getCod_ped(), fincado.getCod_cli(), fincado.getCod_emp(), "", 0, pedido.getCod_env());
        }
    }

    // Cambia TODO un pedido fincado entre agregado y no agregado
    public void actualizaFincadoProductos(String cod_ped, String cod_cli, String cod_emp, boolean agregado, boolean directo) throws SQLException {
        int i;
        PedidoDetalle d;

        try {
            ArrayList<FincadoDetalle> lst = this.dao.obtenerFincadoProductos(pedido.getCod_bod(), cod_ped, cod_cli, cod_emp, pedido.getCod_env());
            this.dao.actualizaFincadoProductos(pedido.getCod_bod(), cod_ped, cod_cli, cod_emp, agregado, pedido.getCod_env());
            listaFincadoDetalle = this.dao.obtenerFincadoProductos(pedido.getCod_bod(), cod_ped, cod_cli, cod_emp, pedido.getCod_env());

            for (FincadoDetalle f : lst) {
                i = buscaFincadoEnDetalle(f);
                d = listaPedidoDetalle.get(i);
                restaPesoFincado(d, f.getCantPed(), directo);
            }
            for (FincadoDetalle f : listaFincadoDetalle) {
                i = buscaFincadoEnDetalle(f);
                d = listaPedidoDetalle.get(i);
                sumaPesoFincado(d, f.getCantPed(), directo);
            }
            listaFincadoDetalle = null;
        } catch (SQLException ex) {
            throw (ex);
        }
    }

    // Cambia un PRODUCTO fincado entre agregado y no agregado
    public void actualizaFincadoProducto(String cod_emp, String cod_pro, int orden, boolean agregado) throws SQLException {
        int i;
        PedidoDetalle d;

        try {
            FincadoDetalle f_pre = this.dao.obtenerFincadoProducto(pedido.getCod_bod(), fincado.getCod_ped(), fincado.getCod_cli(), cod_emp, cod_pro, pedido.getCod_env());
            this.dao.actualizaFincadoProducto(pedido.getCod_bod(), fincado.getCod_ped(), fincado.getCod_cli(), cod_emp, cod_pro, orden, agregado, pedido.getCod_env());
            FincadoDetalle f_pos = this.dao.obtenerFincadoProducto(pedido.getCod_bod(), fincado.getCod_ped(), fincado.getCod_cli(), cod_emp, cod_pro, pedido.getCod_env());

            if (f_pre != null) {
                i = buscaFincadoEnDetalle(f_pre);
                d = listaPedidoDetalle.get(i);
                restaPesoFincado(d, f_pre.getCantPed(), fincado.isDirecto());
            }
            if (f_pos != null) {
                i = buscaFincadoEnDetalle(f_pos);
                d = listaPedidoDetalle.get(i);
                sumaPesoFincado(d, f_pos.getCantPed(), fincado.isDirecto());
            }
        } catch (SQLException ex) {
            throw (ex);
        }
    }
    /*
     private double actualizaFincadoProductoPeso1(FincadoDetalle f, boolean agregar, boolean directo) throws SQLException {
     boolean encontrado = false;
     double peso=f.getCantPed()*f.getPeso();
     for (PedidoDetalle p : listaPedidoDetalle) {
     if (p.getCod_emp().equals(f.getCod_emp()) && p.getCod_pro().equals(f.getCod_pro())) {
     encontrado = true;
     f.setAgregado(agregar);
     if (agregar) {
     if (directo) {
     p.setDirecta(p.getDirecta() + f.getCantPed());
     pesoDirectos += peso;
     } else {
     p.setFincado(p.getFincado() + f.getCantPed());
     pesoBodega += peso;
     }
     p.setCajas(p.getCajas()+f.getCantPed()/f.getPzasepq());
     } else {
     if (directo) {
     p.setDirecta(p.getDirecta() - f.getCantPed());
     pesoDirectos -= peso;
     } else {
     p.setFincado(p.getFincado() - f.getCantPed());
     pesoBodega -= peso;
     }
     p.setCajas(p.getCajas()-f.getCantPed()/f.getPzasepq());
     }
     break;
     }
     }
     if (!encontrado) {
     f.setAgregado(agregar);
     PedidoDetalle d = this.dao.crearPedidoDetalleProducto(f.getCod_emp(), f.getCod_pro(), cedis.getCod_bod());
     if (agregar) {
     if (directo) {
     d.setDirecta(d.getDirecta() + f.getCantPed());
     pesoDirectos += peso;
     } else {
     d.setFincado(d.getFincado() + f.getCantPed());
     pesoBodega += peso;
     }
     } else if (directo) {
     d.setDirecta(d.getDirecta() - f.getCantPed());
     pesoDirectos -= peso;
     } else {
     d.setFincado(d.getFincado() - f.getCantPed());
     pesoBodega -= peso;
     }
     listaPedidoDetalle.add(d);
     }
     return peso;
     }
     * 
     */

    public void nuevoPedido() throws SQLException {
        pedido = new Pedido();
        //pedido.setCod_bod(miniCedis.getCodigo());
        pedido.setCod_bod(Integer.toString(miniCedis.getIdCedis()));
        pedido.setCod_env("000000");
        pedido.setEstado("A");
        pedido.setDiasInventario(0);

        SelectItem nuevo = new SelectItem(pedido, pedido.toString());
        listaPedidos.add(0, nuevo);

        listaFincados = this.dao.obtenerFincados(pedido.getCod_bod(), pedido.getCod_env(), pedido.getEstado());
        listaPedidoDetalle = this.dao.nuevoPedido(pedido.getCod_bod());
        obtenerDetalle("N");
    }

    public void obtenerPedido() throws SQLException {
        listaFincados = this.dao.obtenerFincados(pedido.getCod_bod(), pedido.getCod_env(), pedido.getEstado());
        listaPedidoDetalle = this.dao.obtenerPedidoDetalle(pedido.getCod_bod(), pedido.getCod_env(), pedido.getEstado());
        obtenerDetalle(pedido.getEstado());
    }

    private void obtenerDetalle(String estado) throws SQLException {
        String msg = "Carga de Pedido !!!";
        if (estado.equals("N")) {
            msg = "Nuevo Pedido !!!";
            actualizaDiasInventarioGeneral();
        } else {
            pesoDirectos = 0;
            pesoBodega = 0;
            for (PedidoDetalle p : listaPedidoDetalle) {
                sumaPeso(p);
            }
        }

        fincado = null;
        listaFincadoDetalle = null;
        if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
            FacesContext.getCurrentInstance().getMessageList().remove(0);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Proceso Finalizado", msg));
    }

    public void actualizaListaPedidos() throws SQLException, NamingException {
        cedis = this.mbCedis.obtenerCedis(miniCedis.getIdCedis());
        listaPedidos = new ArrayList<SelectItem>();
        ArrayList<Pedido> aPedidos = this.dao.obtenerPedidos(Integer.toString(miniCedis.getIdCedis()));
        for (Pedido p : aPedidos) {
            listaPedidos.add(new SelectItem(p, p.toString()));
        }
        if (aPedidos.size() > 0) {
            this.pedido = aPedidos.get(0);
            obtenerPedido();
        }
    }

    public MiniCedis getMiniCedis() {
        return miniCedis;
    }

    public void setMiniCedis(MiniCedis miniCedis) {
        this.miniCedis = miniCedis;
    }
    /*
    public ArrayList<SelectItem> getListaMiniCedis() throws SQLException {
        if (this.listaMiniCedis == null) {
            this.listaMiniCedis=this.mbMiniCedis.obtenerListaMiniCedis();
            this.pedido = new Pedido();
            this.pedido.setCod_bod("00");
            this.pedido.setCod_env("000000");
            this.pedido.setDiasInventario(0);
        }
        return listaMiniCedis;
    }

    public void setListaCedis(ArrayList<SelectItem> listaMiniCedis) {
        this.listaMiniCedis = listaMiniCedis;
    }
    * */
    public ArrayList<SelectItem> getListaPedidos() {
        return listaPedidos;
    }

    public void setListaPedidos(ArrayList<SelectItem> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public double getPesoDirectos() {
        return pesoDirectos;
    }

    public void setPesoDirectos(double pesoDirectos) {
        this.pesoDirectos = pesoDirectos;
    }

    public double getPesoBodega() {
        return pesoBodega;
    }

    public void setPesoBodega(double pesoBodega) {
        this.pesoBodega = pesoBodega;
    }

    public List<PedidoDetalle> getListaPedidoDetalle() {
        return listaPedidoDetalle;
    }

    public void setListaPedidoDetalle(ArrayList<PedidoDetalle> listaPedidoDetalle) {
        this.listaPedidoDetalle = listaPedidoDetalle;
    }

    public ArrayList<Envio> getListaEnvios() throws SQLException {
        if (listaEnvios == null && pedido != null) {
            listaEnvios = this.dao.obtenerListaEnvios();
        }
        return listaEnvios;
    }

    public void setListaEnvios(ArrayList<Envio> listaEnvios) {
        this.listaEnvios = listaEnvios;
    }

    public List<Fincado> getListaFincados() {
        return listaFincados;
    }

    public void setListaFincados(ArrayList<Fincado> listaFincados) {
        this.listaFincados = listaFincados;
    }

    public ArrayList<FincadoDetalle> getListaFincadoDetalle() {
        return listaFincadoDetalle;
    }

    public void setListaFincadoDetalle(ArrayList<FincadoDetalle> listaFincadoDetalle) {
        this.listaFincadoDetalle = listaFincadoDetalle;
    }

    public Fincado getFincado() {
        return fincado;
    }

    public void setFincado(Fincado fincado) {
        this.fincado = fincado;
    }

    public MbBuscarOld getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbBuscarOld mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public MbMiniCedis getMbMiniCedis() {
        return mbMiniCedis;
    }

    public void setMbMiniCedis(MbMiniCedis mbMiniCedis) {
        this.mbMiniCedis = mbMiniCedis;
    }

    public MbCedis getMbCedis() {
        return mbCedis;
    }

    public void setMbCedis(MbCedis mbCedis) {
        this.mbCedis = mbCedis;
    }
    
    public double getCapacidadCamion() {
        return capacidadCamion;
    }

    public void setCapacidadCamion(double capacidadCamion) {
        this.capacidadCamion = capacidadCamion;
    }

    public Cedis getCedis() {
        return cedis;
    }

    public void setCedis(Cedis cedis) {
        this.cedis = cedis;
    }

    public static float Round(float Rval, int Rpl) {
        float p = (float) Math.pow(10, Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float) tmp / p;
    }

    // REPORTES
    public void rDetallePedido() {
        String ubicacion = "C:\\Carlos Pat\\Reportes\\rDetallePedido.jasper";
        JasperReport report;
        //JRExporter exporter = null;
        System.out.println(pedido.getDiasInventario());
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("bodega", String.format("%02d", cedis.getCodigo()));
        parametros.put("ciudad", cedis.getCedis());
        parametros.put("estado", cedis.getDireccion().getEstado());
        parametros.put("numPedido", pedido.getCod_env());
        parametros.put("fechagen", pedido.getFechagen());
        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(ubicacion); //DEPRECADO
            JasperPrint jasperprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(listaPedidoDetalle));
            // DAVID
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=rDetallePedido.pdf");
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperprint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void rEnvioEmpresa() throws NamingException, SQLException {
        String ubicacion = "C:\\Carlos Pat\\Reportes\\rEnvioEmpresaA.jasper";
        JasperReport report;
        //JRExporter exporter = null;
        System.out.println(pedido.getDiasInventario());
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("bodega", String.format("%02d", cedis.getCodigo()));
        parametros.put("ciudad", cedis.getCedis());
        parametros.put("estado", cedis.getDireccion().getEstado());
        parametros.put("nombre", cedis.getRepresentante());
        parametros.put("dir", cedis.getDireccion());
        parametros.put("numPedido", pedido.getCod_env());
        parametros.put("fechagen", pedido.getFechagen());
        parametros.put("pzasepq", pedido.getFechagen());
        parametros.put("precio", pedido.getFechagen());
        parametros.put("nombreEmpresa", "La Anita Condimentos y Salsas, S.A. de C.V.");
        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(ubicacion); //DEPRECADO
            JasperPrint jasperprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(listaPedidoDetalle));
            // DAVID
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=rEnvioEmpresaAnita.pdf");
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperprint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public void rEnvioEmpresaQuimicos() throws NamingException, SQLException {
        String ubicacion = "C:\\Carlos Pat\\Reportes\\rEnvioEmpresaQ.jasper";
        JasperReport report;
        //JRExporter exporter = null;
        System.out.println(pedido.getDiasInventario());
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("bodega", String.format("%02d", cedis.getCodigo()));
        parametros.put("ciudad", cedis.getDireccion().getLocalidad());
        parametros.put("estado", cedis.getDireccion().getEstado());
        parametros.put("nombre", cedis.getRepresentante());
        parametros.put("dir", cedis.getDireccion());
        parametros.put("numPedido", pedido.getCod_env());
        parametros.put("fechagen", pedido.getFechagen());
        parametros.put("pzasepq", pedido.getFechagen());
        parametros.put("precio", pedido.getFechagen());
        parametros.put("nombreEmpresa", "Productos Quimicos, La Anita, S.A. de C.V.");
        try {
            report = (JasperReport) JRLoader.loadObjectFromFile(ubicacion); //DEPRECADO
            JasperPrint jasperprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(listaPedidoDetalle));
            // DAVID
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=rEnvioEmpresaQuimicos.pdf");
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperprint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
