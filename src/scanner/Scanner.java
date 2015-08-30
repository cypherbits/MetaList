
package scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author juanjo
 */
public class Scanner {

    public Scanner() {

    }

    public static synchronized ObservableList<String> listDirectory(String dirPath) {
        ObservableList<String> arrFiles = FXCollections.observableArrayList();

        File dir = new File(dirPath);
        File[] firstLevelFiles = dir.listFiles();
        if (firstLevelFiles != null && firstLevelFiles.length > 0) {
            for (File aFile : firstLevelFiles) {
                if (aFile.isDirectory()) {
                    //System.out.println("[" + aFile.getName() + "]");
                    arrFiles.addAll(listDirectory(aFile.getAbsolutePath()));
                } else {
                    arrFiles.add(aFile.getAbsolutePath());
                    // System.out.println(aFile.getAbsolutePath().toString());
                }
            }
        }

        return arrFiles;
    }

    //http://www.sno.phy.queensu.ca/~phil/exiftool/exiftool_pod.html#item_n
    public static synchronized JSONObject getJSONMetadata(String file) {

        try {
            JSONArray ja = new JSONArray(executeCommand(file));
            return ja.getJSONObject(0);
        } catch (JSONException ex) {
            Logger.getLogger(Scanner.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private static synchronized String executeCommand(String file) {

        StringBuilder output = new StringBuilder();

        String etool;
        if (System.getProperty("os.name").startsWith("Windows")) {
            // includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP
            etool = "exiftool/exiftool.exe";
        } else {
            // everything else
            etool = "exiftool/exiftool";
        }
        if (new File(etool).exists()) {
            ProcessBuilder pb = new ProcessBuilder(etool, "-j", file);

            try {
                Process p = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                int exitValue = p.waitFor();
                //System.out.println("\n\nExit Value is " + exitValue);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println("Command output:" + output.toString());

            return output.toString();
        } else {

            System.err.println("Exiftool not found");

            return null;
        }

    }

    public static synchronized void removeMetadata(String[] files) {

        for (int i = 0; i < files.length; i++) {
            System.out.println(executeRemoveCommand(files[i]));
        }
    }

    //exiftool -all= -overwrite_original photo.jpg
    private static synchronized String executeRemoveCommand(String file) {

        StringBuilder output = new StringBuilder();

        String etool;
        if (System.getProperty("os.name").startsWith("Windows")) {
            // includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP
            etool = "exiftool/exiftool.exe";
        } else {
            // everything else
            etool = "exiftool/exiftool";
        }
        if (new File(etool).exists()) {
            ProcessBuilder pb = new ProcessBuilder(etool, "-all=", "-overwrite_original", "-j", file);

            try {
                Process p = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                int exitValue = p.waitFor();
                //System.out.println("\n\nExit Value is " + exitValue);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println("Command output:" + output.toString());

            return output.toString();
        } else {

            System.err.println("Exiftool not found");

            return null;
        }

    }

}
