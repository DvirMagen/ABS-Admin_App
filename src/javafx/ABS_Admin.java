package javafx;

import admin_utills.Constants;
import admin_utills.http.HttpAdminUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ABS_Admin extends Application {

    public static String admin_name = null;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(admin_utills.Constants.ADMIN_LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(url);
        Parent playersRoot = fxmlLoader.load(fxmlLoader.getLocation().openStream());
//        AdminLoginScene adminLoginScene = fxmlLoader.getController();
        Scene scene = new Scene(playersRoot);
        primaryStage.setTitle("A.B.S - Alternative Banking System");
        primaryStage.setScene(scene);
        Image mini_icon = new Image("javafx/resources/images/main_mini_logo.png");
        primaryStage.getIcons().add(mini_icon);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop");
        updateRewindModeInEngine(unused -> {
            System.out.println("LOGOUT");
            HttpAdminUtil.runAsync(admin_utills.Constants.LOGOUT, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // chatCommands.updateHttpLine("Logout request ended with failure...:(");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() || response.isRedirect()) {
                        HttpAdminUtil.removeCookiesOf(admin_utills.Constants.BASE_DOMAIN);
                        ABS_Admin.admin_name = null;
                        HttpAdminUtil.shutdown();
                    }
                }
            });
        });
    }

    private void updateRewindModeInEngine(Consumer<Void> consumer)
    {
        String action = "false";
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_REWIND_MODE)
                .newBuilder()
                .addQueryParameter("rewind_mode", action)
                .build()
                .toString();
        HttpAdminUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("onFailure updateRewindModeInEngine");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                consumer.accept(null);
            }
        });
    }
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
