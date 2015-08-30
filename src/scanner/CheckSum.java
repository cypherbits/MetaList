package scanner;

/**
 *
 * @author juanjo
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSum {

    /**
     * *
     * Convierte un arreglo de bytes a String usando valores hexadecimales
     *
     * @param digest arreglo de bytes a convertir
     * @return String creado a partir de <code>digest</code>
     */
    private static String toHexadecimal(byte[] digest) {
        String hash = "";
        for (byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) {
                hash += "0";
            }
            hash += Integer.toHexString(b);
        }
        return hash;
    }

    /**
     * *
     * Realiza la suma de verificación de un archivo mediante MD5
     *
     * @param archivo archivo a que se le aplicara la suma de verificación
     * @return valor de la suma de verificación.
     */
    public static String getMD5Checksum(File archivo) {
        byte[] textBytes = new byte[1024];
        MessageDigest md = null;
        int read = 0;
        String md5 = null;
        try {
            InputStream is = new FileInputStream(archivo);
            md = MessageDigest.getInstance("MD5");
            while ((read = is.read(textBytes)) > 0) {
                md.update(textBytes, 0, read);
            }
            is.close();
            byte[] md5sum = md.digest();
            md5 = toHexadecimal(md5sum);
        } catch (FileNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (IOException e) {
        }

        return md5;
    }
}
