package javafx.scenes.login;

import admin_utills.http.HttpAdminUtil;;
import javafx.ABS_Admin;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scenes.loadFile.adminLoadFileScene;
import javafx.stage.Stage;
import admin_utills.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminLoginScene  implements Initializable{
    @FXML
    public Button enter_button_admin_login;
    @FXML
    public Label error_label_admin_login;
    @FXML
    public TextField username_admin_login;

    private adminLoadFileScene adminLoadFileScene;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    protected void onClickEnterButton(ActionEvent event)
    {
        String adminName = username_admin_login.getText();
        if(adminName.isEmpty())
        {
            error_label_admin_login.setText("Please Enter Username");
            error_label_admin_login.setTextFill(Color.RED);
            error_label_admin_login.setVisible(true);
        }
        else{
            String finalUrl = HttpUrl
                    .parse(admin_utills.Constants.LOGIN_PAGE)
                    .newBuilder()
                    .addQueryParameter("username", adminName)
                    .build()
                    .toString();

           // updateHttpStatusLine("New request is launched for: " + finalUrl);
            error_label_admin_login.setVisible(false);
            HttpAdminUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        error_label_admin_login.setText("Something went wrong: " + e.getMessage());
                        error_label_admin_login.setVisible(true);
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        String responseBody = response.body().string();
                        Platform.runLater(() -> {
                            error_label_admin_login.setText("Something went wrong: " + responseBody);
                            error_label_admin_login.setVisible(true);
                        });
                        System.out.println(responseBody);
                    } else {
                        Platform.runLater(() -> {
                            ABS_Admin.admin_name = adminName;
                            //
                            try {
                                FXMLLoader loader = new FXMLLoader();
                                URL url = getClass().getResource(Constants.ADMIN_DASHBOARD_PAGE_FXML_RESOURCE_LOCATION);
                                loader.setLocation(url);
                                Parent root = loader.load();
                                Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                                stage.setScene(new Scene(root));
                                stage.show();
                                //adminLoadFileScene.updateWelcomeAdminLabel(username_admin_login.getText());
                            } catch (IOException ex) {
                                System.err.println(ex);
                            }
                        });

                    }
                }
            });
        }
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        error_label_admin_login.setText("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

}
