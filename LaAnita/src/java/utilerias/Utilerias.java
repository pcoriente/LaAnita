package utilerias;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Julio
 */
public class Utilerias {
    
    public static String date2String(Date fecha) {
        String strFecha="";
        if(fecha != null) {
            DateFormat simpleFormatter = new SimpleDateFormat("dd/MM/yyyy");
            strFecha=simpleFormatter.format(fecha);
        }
        return strFecha;
    }
    
  
    public static Date string2Date(String strFecha) {
        Date fecha=null;
        if(!strFecha.isEmpty()) {
            String fecha_RegExp="(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
            Pattern pattern = Pattern.compile(fecha_RegExp);
            Matcher matcher = pattern.matcher(strFecha);
            if(matcher.matches()) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                formatter.setLenient(false);
                try {
                    fecha = (Date)formatter.parse(strFecha);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Fecha no válida: "+strFecha, "Error !!!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Formato de fecha incorrecto (dd/mm/yyyy)", "Error !!!", JOptionPane.ERROR_MESSAGE);
            }
        }
        return fecha;
    }
    
    public static Date hoy() {
        return string2Date(date2String(new Date()));
    }
    
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    
    public static String Acentos(String str) {
        String sp=str;
        sp=sp.replace("Ã¡", "á");
        sp=sp.replace("Ã©", "é");
        sp=sp.replace("Ã­", "í");
        sp=sp.replace("Ã³", "ó");
        sp=sp.replace("Ãº", "ú");
        sp=sp.replace("Ã±", "ñ");
        sp=sp.replace("Ã", "Ñ");
        return sp;
    }
    
    public static double Round(double d, int dec) {
        int temp=(int)((d*Math.pow(10, dec)+0.5));
        return (((double)temp)/Math.pow(10, dec));
    }
}
