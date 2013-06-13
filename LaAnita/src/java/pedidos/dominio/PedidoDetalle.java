package pedidos.dominio;

/**
 *
 * @author Julio
 */
public class PedidoDetalle {
    private String cod_emp;
    private String cod_pro;
    private String descripcion;
    private double peso;
    private int pzasepq;
    private double estadistica;
    private double existencia;
    private double fincado;
    private double pedido;
    private int diasInventario;
    private String banDiasInv;
    private double directa;
    private double sugerida;
    private double precio;
    private double cajas;
    private boolean grabado;
    private int diasInvBod;
    private double cajasOriginal;

    public double getCajasOriginal() {
        return cajasOriginal;
    }

    public void setCajasOriginal(double cajasOriginal) {
        this.cajasOriginal = cajasOriginal;
    }

    public double getSugerida() {
        return sugerida;
    }

    public void setSugerida(double sugerida) {
        this.sugerida = sugerida;
    }

    public String getBanDiasInv() {
        return banDiasInv;
    }

    public void setBanDiasInv(String banDiasInv) {
        this.banDiasInv = banDiasInv;
    }

    public double getCajas() {
        return cajas;
    }

    public void setCajas(double cajas) {
        this.cajas = cajas;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getCod_pro() {
        return cod_pro;
    }

    public void setCod_pro(String cod_pro) {
        this.cod_pro = cod_pro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDiasInventario() {
        return diasInventario;
    }

    public void setDiasInventario(int diasInventario) {
        this.diasInventario = diasInventario;
    }

    public double getDirecta() {
        return directa;
    }

    public void setDirecta(double directa) {
        this.directa = directa;
    }

    public double getEstadistica() {
        return estadistica;
    }

    public void setEstadistica(double estadistica) {
        this.estadistica = estadistica;
    }

    public double getExistencia() {
        return existencia;
    }

    public void setExistencia(double existencia) {
        this.existencia = existencia;
    }

    public double getFincado() {
        return fincado;
    }

    public void setFincado(double fincado) {
        this.fincado = fincado;
    }

    public double getPedido() {
        //pedido=Math.ceil((diasInventario*estadistica));
        return pedido;
    }

    public void setPedido(double pedido) {
        this.pedido = pedido;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getPzasepq() {
        return pzasepq;
    }

    public void setPzasepq(int pzasepq) {
        this.pzasepq = pzasepq;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isGrabado() {
        return grabado;
    }

    public void setGrabado(boolean grabado) {
        this.grabado = grabado;
    }
    /*
    private double Round(double Rval, int Rpl) {
        double p = (double) Math.pow(10, Rpl);
        Rval = Rval * p;
        double tmp = Math.round(Rval);
        return (double) tmp / p;
    }
     * 
     */

    public int getDiasInvBod() {
        return diasInvBod;
    }

    public void setDiasInvBod(int diasInvBod) {
        this.diasInvBod = diasInvBod;
    }
    
    
}
