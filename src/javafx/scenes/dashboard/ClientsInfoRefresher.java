package javafx.scenes.dashboard;

import admin_utills.Constants;
import admin_utills.http.HttpAdminUtil;
import com.google.gson.reflect.TypeToken;
import javafx.ABS_Admin;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static admin_utills.Constants.GSON_INSTANCE;

public class ClientsInfoRefresher extends TimerTask {

    private final Consumer<List<AdminDashboard.ClientInfo>> clientsInfoConsumer;
    public ClientsInfoRefresher( Consumer<List<AdminDashboard.ClientInfo>> clientsInfoConsumer) {
        this.clientsInfoConsumer = clientsInfoConsumer;
    }

    @Override
    public void run() {
        if(ABS_Admin.admin_name != null && !ABS_Admin.admin_name.equalsIgnoreCase("null")) {
            HttpAdminUtil.runAsync(Constants.UPDATE_CUSTOMERS_INFO, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failure!!!");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    System.out.println("onResponse ClientsInfoRefresher");
                    String jsonSt = response.body().string();
//                    System.out.println("Response: " + jsonSt);
                    if (response.code() == 200) {
                        Type listType = new TypeToken<Map<String, List<AdminDashboard.ClientInfo>>>() {
                        }.getType();

                        Map<String, List<AdminDashboard.ClientInfo>> hashJson = GSON_INSTANCE.fromJson(jsonSt, listType);
                        List<AdminDashboard.ClientInfo> clients = hashJson.get("clients");
//                        System.out.println(clients);
//                        for (AdminDashboard.ClientInfo client : clients) {
//                            System.out.println(client);
//                        }
                        Platform.runLater(() -> {
                            clientsInfoConsumer.accept(clients);
                        });

                    }
                }
            });
        }
    }
}
