package metadatacollector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import scanner.Metadata;
import scanner.Scanner;

/**
 *
 * @author juanjo
 */
public class MainController implements Initializable {

    @FXML
    Parent root;
    @FXML
    private MenuItem btnAbout;
    @FXML
    private MenuItem btnClose;
    @FXML
    private MenuItem btnScanDir;
    @FXML
    private MenuItem btnScanFiles;
    @FXML
    private MenuItem menu_metadata_itemRemoveSelected;
    @FXML
    private MenuItem menu_metadata_itemRemoveAll;
    @FXML
    private ListView<String> listView_files;
    @FXML
    private TableView tableView_Metadata;
    @FXML
    private ProgressIndicator progress_files;
    @FXML
    private TextField txtSearchFiles;
    @FXML
    private TextField txtSearchMetadata;
    /*Context menus items*/
    @FXML
    private MenuItem contextMenu_files_itemRemoveSelected;
    @FXML
    private MenuItem contextMenu_files_itemRemoveAll;
    @FXML
    private MenuItem metadata_contextMenu_btnCopy;

    private ObservableList<String> files;

    private ObservableList<Metadata> data;
    
    private ResourceBundle rb;

    /*Functions*/
    @FXML
    private void handleButtonAction(ActionEvent event) {
        Object source = event.getSource();

        if (source == this.btnAbout) {
            try {
                Parent root;
                root = FXMLLoader.load(getClass().getResource("About.fxml"), ResourceBundle.getBundle("resources.locale", new Locale(MetadataCollector.LOCALE)));
                Stage stage = new Stage();
                stage.setTitle(rb.getString("about.title"));
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (source == this.btnClose) {
            Platform.exit();
            System.exit(0);
        } else if (source == this.btnScanDir) {
            DirectoryChooser directoryChooser = new DirectoryChooser();

            directoryChooser.setTitle(rb.getString("main.selectDirectory"));

            File selectedDirectory
                    = directoryChooser.showDialog((Stage) root.getScene().getWindow());

            if (selectedDirectory != null) {

                //System.out.println(selectedDirectory.getAbsolutePath().toString());
                final String strDir = selectedDirectory.getAbsolutePath().toString();

                final Scanner scan = new Scanner();

                progress_files.setVisible(true);

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        files = scan.listDirectory(strDir);

                        if (txtSearchFiles.getText().toString().isEmpty()) {
                            listView_files.setItems(files);
                        } else {
                            searchInFiles(null, txtSearchFiles.getText().toString());
                        }

                        progress_files.setVisible(false);

                        if (files.size() > 0) {
                            menu_metadata_itemRemoveAll.setDisable(false);
                            contextMenu_files_itemRemoveAll.setDisable(false);
                            txtSearchFiles.setDisable(false);
                        } else {
                            menu_metadata_itemRemoveAll.setDisable(true);
                            contextMenu_files_itemRemoveAll.setDisable(true);
                        }

                    }

                });

                thread.start();
            }

        } else if (source == this.btnScanFiles) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(rb.getString("main.selectFiles"));
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("All Files", "*.*"),
                    new ExtensionFilter("Document Files", "*.txt", "*.doc", "*.pdf", "*.docx", ".odt"),
                    new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg", "*.raw", "*.tif", "*.bmp"));
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog((Stage) root.getScene().getWindow());
//            if (selectedFile != null) {
//                mainStage.display(selectedFile);
//            }

            files = FXCollections.observableArrayList();
            files.removeAll(files);

            for (Iterator<File> it = selectedFiles.iterator(); it.hasNext();) {
                File fi = it.next();
                files.add(fi.getAbsolutePath());
            }

            listView_files.setItems(files);

            if (files.size() > 0) {
                menu_metadata_itemRemoveAll.setDisable(false);
                contextMenu_files_itemRemoveAll.setDisable(false);
                txtSearchFiles.setDisable(false);
            } else {
                menu_metadata_itemRemoveAll.setDisable(true);
                contextMenu_files_itemRemoveAll.setDisable(true);
            }
        }
    }

    @FXML
    private void handleMetadataContextMenu(ActionEvent event) {
        Object source = event.getSource();

        if (source == this.metadata_contextMenu_btnCopy) {

            Metadata selected = (Metadata) tableView_Metadata.getSelectionModel().getSelectedItem();

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(selected.getValue());
            clipboard.setContent(content);
        }
    }

    @FXML
    private void handleFilesContextMenuAction(ActionEvent event) {
        Object source = event.getSource();

        if (source == this.contextMenu_files_itemRemoveSelected || source == this.menu_metadata_itemRemoveSelected) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    progress_files.setVisible(true);
                    String[] array = array = new String[1];
                    array[0] = (String) listView_files.getSelectionModel().getSelectedItem().toString();
                    Scanner.removeMetadata(array);
                    progress_files.setVisible(false);
                }

            });
            thread.start();

        } else if (source == this.contextMenu_files_itemRemoveAll || source == this.menu_metadata_itemRemoveAll) {
            Alert alert = new Alert(AlertType.CONFIRMATION, rb.getString("main.removeAllMetadata"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        progress_files.setVisible(true);
                        String[] array = (String[]) files.toArray(new String[0]);
                        Scanner.removeMetadata(array);
                        progress_files.setVisible(false);
                    }

                });
                thread.start();

            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.rb = rb;

        tableView_Metadata.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn colName = new TableColumn("Metadata");
        TableColumn colValue = new TableColumn(rb.getString("main.value"));

        colName.setCellValueFactory(
                new PropertyValueFactory<Metadata, String>("name")
        );
        colValue.setCellValueFactory(
                new PropertyValueFactory<Metadata, String>("value")
        );

        tableView_Metadata.getColumns().addAll(colName, colValue);

        listView_files.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                //System.out.println("Getting metadata from " + newValue);
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        if (newValue != null && new File(newValue).exists()) {

                            //contextMenu, enable remove selected metadata
                            contextMenu_files_itemRemoveSelected.setDisable(false);
                            menu_metadata_itemRemoveSelected.setDisable(false);

                            //get metadata
                            JSONObject jo = Scanner.getJSONMetadata(newValue);

                            if (jo != null) {

                                txtSearchMetadata.setDisable(false);

                                Iterator<?> keys = jo.keys();

                                data = FXCollections.observableArrayList();

                                while (keys.hasNext()) {
                                    try {
                                        String key = (String) keys.next();

                                        //System.out.println(key);
//                                    if (jo.get(key) instanceof JSONObject) {
//                                        data.add(new Metadata(key, jo.getString(key)));
//                                    }
                                        data.add(new Metadata(key, jo.get(key).toString()));

                                    } catch (JSONException ex) {
                                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                                if (txtSearchMetadata.getText().toString().isEmpty()) {
                                    tableView_Metadata.setItems(data);
                                } else {
                                    searchInMetadata(null, txtSearchMetadata.getText().toString());
                                }

                                //System.out.println(jo.toString());
                            }

                        } else {
                            System.err.println("Error: file " + newValue + " does not exist, so we can not show its metadata.");
                            //contextMenu, disable remove selected metadata
                            contextMenu_files_itemRemoveSelected.setDisable(true);
                            menu_metadata_itemRemoveSelected.setDisable(true);
                        }

                    }

                });

                thread.start();
            }
        });

        //TableViewMetadata on row selected enable copy context menu
        tableView_Metadata.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Metadata>() {
            @Override
            public void changed(ObservableValue<? extends Metadata> observable, Metadata oldValue, Metadata newValue) {
                if (newValue != null) {
                    metadata_contextMenu_btnCopy.setDisable(false);
                } else {
                    metadata_contextMenu_btnCopy.setDisable(true);
                }
            }
        });

        //search files
        txtSearchFiles.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                    Object newVal) {
                //listView_files.selectionModelProperty().set(null);
                searchInFiles((String) oldVal, (String) newVal);
            }
        });

        //search metadata
        txtSearchMetadata.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                    Object newVal) {
                //listView_files.selectionModelProperty().set(null);
                searchInMetadata((String) oldVal, (String) newVal);
            }
        });

        checkFiles();

    } //end init func

    public void checkFiles() {
        File exiftoolExec = new File("exiftool/exiftool");
        File exiftoolExecWin = new File("exiftool/exiftool.exe");
        if (exiftoolExec.exists() && exiftoolExecWin.exists()) {
            if (!exiftoolExec.canExecute()) {
                exiftoolExec.setExecutable(true);
            }
        } else {

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(rb.getString("error.coreFiles"));

            alert.showAndWait();

            System.err.println("ERROR: some core files do not exist or do not have execution privileges.");
            System.exit(-1);
        }
    }

    private void searchInFiles(String oldVal, String newVal) {
        if (oldVal != null && (newVal.length() < oldVal.length())) {
            listView_files.setItems(files);
        }
        String value = newVal.toUpperCase();
        ObservableList<String> subentries = FXCollections.observableArrayList();
        for (Object entry : files) {

            String entryText = (String) entry;

            if (entryText.toUpperCase().contains(value)) {
                subentries.add(entryText);
            }
        }
        listView_files.setItems(subentries);
    }

    private void searchInMetadata(String oldVal, String newVal) {
        if (oldVal != null && (newVal.length() < oldVal.length())) {
            tableView_Metadata.setItems(data);
        }
        String value = newVal.toUpperCase();
        ObservableList<Metadata> subentries = FXCollections.observableArrayList();
        for (Object entry : data) {

            Metadata mentry = (Metadata) entry;

            if (mentry.getName().toUpperCase().contains(value) || mentry.getValue().toUpperCase().contains(value)) {
                subentries.add(mentry);
            }
        }
        tableView_Metadata.setItems(subentries);
    }

}
