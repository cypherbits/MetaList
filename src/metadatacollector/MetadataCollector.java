package metadatacollector;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author juanjo
 */
public class MetadataCollector extends Application {

    public static final String appversion = "v1.1.2 (22/10/2016)";

    public static String LOCALE;

    @Override
    public void start(Stage stage) throws Exception {

        if (getOSLocale().equals("es")){
            LOCALE = "es";
        }else{
             LOCALE = "en";
        }

        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"), ResourceBundle.getBundle("resources.locale", new Locale(MetadataCollector.LOCALE)));

        Scene scene = new Scene(root);
        stage.setTitle("Metacol " + appversion);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("meta.png")));

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static String getOSLocale() {
        Locale currentLocale = Locale.getDefault();

//        System.out.println(currentLocale.getDisplayLanguage());
//        System.out.println(currentLocale.getDisplayCountry());
//
//        System.out.println(currentLocale.getLanguage());
//        System.out.println(currentLocale.getCountry());
//
//        System.out.println(System.getProperty("user.country"));
//        System.out.println(System.getProperty("user.language"));
        return currentLocale.getLanguage();
    }

}
