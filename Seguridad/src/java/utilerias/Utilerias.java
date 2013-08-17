/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilerias;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Comodoro
 */
public class Utilerias {

    public String dameFecha() {
        String fecha = null;
        Date d = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        fecha = formateador.format(d);
        return fecha;
    }

    public String md5(String clear) throws Exception {
        //String dato = null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(clear.getBytes());
        int size = b.length;
        StringBuilder h = new StringBuilder(size);
        //algoritmo y arreglo md5
        for (int i = 0; i < size; i++) {
            int u = b[i] & 255;
            if (u < 16) {
                h.append("0");
                h.append(Integer.toHexString(u));
                //h.append("0" + Integer.toHexString(u));
            } else {
                h.append(Integer.toHexString(u));
            }
        }
        //clave encriptada
        return h.toString();
    }

    public boolean validarEmail(String email) {
        boolean validar = false;
        Pattern pattern = Pattern.compile("\\w+@\\w+\\.\\w+");
        Matcher matcher = pattern.matcher(email);
        validar = matcher.matches();
        return validar;
    }

    public void x() {
    }
}
