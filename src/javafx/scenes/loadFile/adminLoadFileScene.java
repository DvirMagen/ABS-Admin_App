package javafx.scenes.loadFile;

import admin_utills.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class adminLoadFileScene implements Initializable {
    @FXML
    public TextField file_path_textfield;
    @FXML
    public Button analyze_file_btn;
    @FXML
    public Button choose_file_btn;
    @FXML
    public Label error_label_load_file_page;
    @FXML
    public Button enter_btn_to_dashboard;
    @FXML
    public Label welcome_admin_label_load_file_page;

    protected String update_admin_text;
    protected boolean is_valid = true;

    private File selectedFile =null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    protected void onClickAdminSelectFile(ActionEvent event){
        Stage mainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        selectedFile = fileChooser.showOpenDialog(mainStage);

        analyze_file_btn.setDisable(selectedFile == null);
        file_path_textfield.setText(selectedFile != null? selectedFile.getAbsolutePath() : "");
    }

    public void updateWelcomeAdminLabel(String adminUsername)
    {
        this.welcome_admin_label_load_file_page.setText("Welcome, " + adminUsername+ "!");
    }
    @FXML
    protected void onClickAdminAnalyzeFile(ActionEvent event){

        if (selectedFile != null) {

            error_label_load_file_page.setVisible(false);
          //  enter_btn_to_dashboard.setDisable(true);
          //  this.engine = new Engine();
            System.out.println(selectedFile.getAbsolutePath());
            String xmlPath = selectedFile.getAbsolutePath();
           // try{
             //   this.engine.loadXmlPath(xmlPath);
          //  }catch (XmlPathIsDoesNotEndAsNeededException e){
                is_valid = false;
            //    update_admin_text = e.getMessage();
                return;
            }
         //   setViewByEngine(); //TODO remove false
       // }
    }

    private void setViewByEngine(){
        this.setViewByEngine(true);
    }
    private void setViewByEngine(boolean showProgress) {
      //  boolean flag = this.engine.isXmlPathValid();
       // if (flag) {
           // try {
           //     this.engine.loadDataFromXmlFile();
           // } catch (LoanDoesNotPayEveryYazTimeCorrectlyException | CategoryDoesNotExistException |
             //        CustomerDoesNotExistException e) {
            //    update_admin_text = e.getMessage();
           //     is_valid = false;
         //  } catch (JAXBException t) {
          //      is_valid = false;
           //     update_admin_text = "XML file does not exist!";
          //  }
       // }
    }

    @FXML
    protected void onClickAdminEnterToDashBoard(ActionEvent event){

        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource(Constants.ADMIN_DASHBOARD_PAGE_FXML_RESOURCE_LOCATION);
            loader.setLocation(url);
            Parent root = loader.load();

             //  CustomerScene scene2Controller = loader.getController();
            //  scene2Controller.setEngineAndSelectedCustomer(this.engine,this.selectedCustomer);
               Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
               stage.setScene(new Scene(root));
              stage.show();
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

}
