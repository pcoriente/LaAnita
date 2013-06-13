
package leyenda.dominio;


public class ClientesBancos {
    public int idClienteBanco;
    public int codigoCliente;
    public int idBanco;
    public String numCtaPago;
    public String medioPago;
    public BancoLeyenda idbanco;

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public int getIdClienteBanco() {
        return idClienteBanco;
    }

    public void setIdClienteBanco(int idClienteBanco) {
        this.idClienteBanco = idClienteBanco;
    }

    public BancoLeyenda getIdbanco() {
        return idbanco;
    }

    public void setIdbanco(BancoLeyenda idbanco) {
        this.idbanco = idbanco;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getNumCtaPago() {
        return numCtaPago;
    }

    public void setNumCtaPago(String numCtaPago) {
        this.numCtaPago = numCtaPago;
    }
}
