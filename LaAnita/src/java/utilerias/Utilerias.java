package utilerias;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Julio
 */
public class Utilerias {

    public static String date2String(Date fecha) {
        String strFecha = "";
        if (fecha != null) {
            DateFormat simpleFormatter = new SimpleDateFormat("dd/MM/yyyy");
            strFecha = simpleFormatter.format(fecha);
        }
        return strFecha;
    }

    public static Date string2Date(String strFecha) throws Exception {
        Date fecha = null;
        if (!strFecha.isEmpty()) {
            String fecha_RegExp = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
            Pattern pattern = Pattern.compile(fecha_RegExp);
            Matcher matcher = pattern.matcher(strFecha);
            if (matcher.matches()) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                formatter.setLenient(false);
                try {
                    fecha = (Date) formatter.parse(strFecha);
                } catch (ParseException ex) {
                    throw new Exception("Fecha no válida: " + strFecha + " Error !!!");
                    //JOptionPane.showMessageDialog(null, "Fecha no válida: " + strFecha, "Error !!!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                throw new Exception("Fecha no válida: " + strFecha + " Error !!!");
                //JOptionPane.showMessageDialog(null, "Formato de fecha incorrecto (dd/mm/yyyy)", "Error !!!", JOptionPane.ERROR_MESSAGE);
            }
        }
        return fecha;
    }

    public static Date hoy() throws Exception {
        return string2Date(date2String(new Date()));
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static String Acentos(String str) {
        String sp = str;
        sp = sp.replace("Ã¡", "á");
        sp = sp.replace("Ã©", "é");
        sp = sp.replace("Ã­", "í");
        sp = sp.replace("Ã³", "ó");
        sp = sp.replace("Ãº", "ú");
        sp = sp.replace("Ã±", "ñ");
        sp = sp.replace("Ã", "Ñ");
        return sp;
    }

    public static double Round(double d, int dec) {
        int temp = (int) ((d * Math.pow(10, dec) + 0.5));
        return (((double) temp) / Math.pow(10, dec));
    }

    public static String md5(String clear) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(clear.getBytes());
        int size = b.length;
        StringBuilder h = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int u = b[i] & 255;
            if (u < 16) {
                h.append("0");
                h.append(Integer.toHexString(u));
            } else {
                h.append(Integer.toHexString(u));
            }
        }
        return h.toString();
    }

    public static String formatoMonedas(double importe) {
        NumberFormat formatoImporte;
        formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String cantidad = formatoImporte.format(importe);
        return cantidad;
    }

    public String quitarComasNumero(String numero) {
        String num = "";
        num = numero.replace(",", "");
        num = num.replace("$", "");
        return num;
    }
}
