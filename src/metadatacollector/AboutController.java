
package metadatacollector;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author juanjo
 */
public class AboutController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button btnClose;
    @FXML
    private Label labelVersion;

    @FXML
    private void close() {
        Scene s = (Scene) btnClose.getScene();
        s.getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        labelVersion.setText(MetadataCollector.appversion);
    }

}
