package javafx.scenes.dashboard;


import admin_utills.Constants;
import admin_utills.Loan;
import admin_utills.http.HttpAdminUtil;
import com.google.gson.reflect.TypeToken;
import javafx.ABS_Admin;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class LoansInfoRefresher  extends TimerTask {
    private final Consumer<List<Loan>> loansInfoConsumer;
    public LoansInfoRefresher( Consumer<List<Loan>> loansInfoConsumer) {
        this.loansInfoConsumer = loansInfoConsumer;
    }

    @Override
    public void run() {
        if(ABS_Admin.admin_name != null && !ABS_Admin.admin_name.equalsIgnoreCase("null")) {
            String finalUrl = HttpUrl
                    .parse(Constants.LOANS_INFO)
                    .newBuilder()
                    .build()
                    .toString();
            HttpAdminUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failure!!!");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    System.out.println("onResponse");
                    String jsonSt = response.body().string();
//                    System.out.println("Response: " + jsonSt);
                    if (response.code() == 200) {
                        Type listType = new TypeToken<List<Loan>>() {
                        }.getType();

                        List<Loan> loans = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonSt, listType);
//                        System.out.println(loans);
//                        for (Loan loan : loans) {
//                            System.out.println(loan);
//                        }
                        Platform.runLater(() -> {
                            loansInfoConsumer.accept(loans);
                        });
                    }
                }
            });
        }
    }
}
