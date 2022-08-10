package javafx.scenes.dashboard;


import admin_utills.Constants;
import admin_utills.Loan;
import admin_utills.http.HttpAdminUtil;
import com.google.gson.reflect.TypeToken;
import javafx.ABS_Admin;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.table.TableRowExpanderColumn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

import static admin_utills.Constants.GSON_INSTANCE;
import static admin_utills.Constants.REFRESH_RATE;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class AdminDashboard implements Initializable {

    @FXML
    public TableView<Loan> loans_info_tableview;
    @FXML
    public TableColumn<Loan,String> loans_info_loan_id_column;
    @FXML
    public TableColumn<Loan, String> loans_info_owner_column;
    @FXML
    public TableColumn<Loan, String> loans_info_category_column;
    @FXML
    public TableColumn<Loan, String> loans_info_loan_payed_column;
    @FXML
    public TableColumn<Loan, String> loans_info_capital_column;
    @FXML
    public TableColumn<Loan, String> loans_info_total_time_column;
    @FXML
    public TableColumn<Loan, String> loans_info_status_column;
    @FXML
    public ToggleSwitch rewind_mode_toggle_switch;
    @FXML
    public Button logout_btn;

    public static class ClientInfo implements Serializable {
        public String name;
        public double balance;

        public ClientInfo() {
        }

        public ClientInfo(String name, double balance) {
            this.name = name;
            this.balance = balance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "ClientInfo{" +
                    "name='" + name + '\'' +
                    ", balance=" + balance +
                    '}';
        }
    }

    public static class LoanCustomerDto {
        int risksLoans;
        int newLoans;
        int activeLoans;
        int pendingLoans;


        public LoanCustomerDto(int risksLoans, int newLoans, int activeLoans, int pendingLoans) {
            this.risksLoans = risksLoans;
            this.newLoans = newLoans;
            this.activeLoans = activeLoans;
            this.pendingLoans = pendingLoans;
        }

        @Override
        public String toString() {
            return "LoanCustomerDto{" +
                    "risksLoans=" + risksLoans +
                    ", newLoans=" + newLoans +
                    ", activeLoans=" + activeLoans +
                    ", pendingLoans=" + pendingLoans +
                    '}';
        }

        public int getRisksLoans() {
            return risksLoans;
        }

        public int getNewLoans() {
            return newLoans;
        }

        public int getActiveLoans() {
            return activeLoans;
        }

        public int getPendingLoans() {
            return pendingLoans;
        }
    }

    @FXML
    public Label welcome_admin_labe_dashboard_pagel;
    @FXML
    public TableView<ClientInfo> customer_info_tableview;
    @FXML
    public Button admin_decrease_yaz_btn_dashboard_page;
    @FXML
    public Label admin_current_yaz_label_dashboard_page;
    @FXML
    public Button admin_increase_yaz_time_btn_dashboard;
    @FXML
    public TableColumn<ClientInfo,String> customer_info_table_name_column;
    @FXML
    public TableColumn<ClientInfo,String> customer_info_table_balance_column;

    private ObservableList<ClientInfo> clientInfoObservableList = FXCollections.observableArrayList();

    private ObservableList<Loan> loanObservableList = FXCollections.observableArrayList();

    private TimerTask clientsInfoRefresher;
    private TimerTask loansInfoRefresher;
    private Timer clientsInfoRefresh_Timer;
    private Timer loansInfoRefresh_Timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.customer_info_tableview.setItems(this.clientInfoObservableList);
        this.customer_info_table_name_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.customer_info_table_balance_column.setCellValueFactory(new PropertyValueFactory<>("balance"));

        this.loans_info_tableview.setItems(this.loanObservableList);
        this.loans_info_loan_id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.loans_info_owner_column.setCellValueFactory(new PropertyValueFactory<>("owner"));
        this.loans_info_category_column.setCellValueFactory(new PropertyValueFactory<>("category"));
        this.loans_info_loan_payed_column.setCellValueFactory(new PropertyValueFactory<>("loanPayed"));
        this.loans_info_capital_column.setCellValueFactory(new PropertyValueFactory<>("capital"));
        this.loans_info_total_time_column.setCellValueFactory(new PropertyValueFactory<>("totalYazTime"));
        this.loans_info_status_column.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.loans_info_tableview.getColumns().add(0,new TableRowExpanderColumn<>(this::createLoansInfoEditor));
        {
            ObservableList<TableColumn<Loan, ?>> columns = loans_info_tableview.getColumns();
            for (TableColumn<Loan, ?> column : columns) {
                column.setMaxWidth(1f * Integer.MAX_VALUE * (96 / (columns.size() - 1))); // 30% width
            }
            columns.get(0).setMaxWidth(1f * Integer.MAX_VALUE * 4); // 30% width
        }

        welcome_admin_labe_dashboard_pagel.setText("Welcome " + ABS_Admin.admin_name + "!");
       updateTimeUnit();

        this.rewind_mode_toggle_switch.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                admin_decrease_yaz_btn_dashboard_page.setDisable(!newValue);
                updateRewindMode(newValue);
                updateRewindModeInEngine(newValue);
                if (!newValue)
                    updateTimeUnit();

            }
        });

        startClientsInfoRefresher();
        startLoansInfoRefresher();

    }

    private void updateTimeUnit(){
        String finalUrl = HttpUrl
                .parse(admin_utills.Constants.YAZ_TIME)
                .newBuilder()
                .build()
                .toString();
        HttpAdminUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("onFailure updateTimeUnit");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                System.out.println("onResponse");
                String jsonSt = response.body().string();
//                System.out.println("Response: " + jsonSt);
                if(response.code() ==  200) {
                    HashMap<String,Object> hashJson = GSON_INSTANCE.fromJson(jsonSt, HashMap.class);
                    int yaz_time = (int) (Math.floor((Double) hashJson.get("yaz_time")));
                    Platform.runLater(() -> {
//                        System.out.println("Time Unit: "+yaz_time);
                        admin_current_yaz_label_dashboard_page.setText("Time Unit: " + yaz_time);
                    });
//                        }

                } /*else {

                }*/
            }
        });
    }


    private GridPane createLoansInfoEditor(TableRowExpanderColumn.TableRowDataFeatures<Loan> param) {
        GridPane editor = new GridPane();
        editor.setPadding(new Insets(10));
        editor.setHgap(10);
        editor.setVgap(5);

        Loan loan = param.getValue();

//        editor.addRow(0,new Label("ID"),new Label(loan.getId()));
        editor.addRow(1, new Label("\t"), new Label("Payment Every " + loan.getPaysEveryYaz() + " Yaz"));
        editor.addRow(2, new Label("\t"), new Label("Intrist Per Payment: " + loan.getIntristPerPayment() + "%"));
        editor.addRow(3, new Label("\t"), new Label("Loan is Active " + loan.getYazTimeActive() + " Yaz"));
        editor.addRow(4, new Label("\t"), new Label("Loan Left " + loan.getYazTimeLeft() + " Yaz"));
        editor.addRow(5, new Label("\t"), new Label("Loan Last Time Payment  " + loan.getLastYazTimePayment() + " Yaz"));
      //  editor.addRow(6, new Label("\t"), new Label("Loan Upcoming Payment  " + loan.getTheUpcomingPayment()));
        editor.addRow(6, new Label("\t"), new Label("Loan Total Fund  " + (loan.getCapital() - loan.leftToFund())));

        if (!loan.getLenders().isEmpty()) {
            TableView<Loan.Lender> lendersTableView = new TableView<>();

            TableColumn<Loan.Lender, String> lenderNameTableColumn = new TableColumn<>("Name");
            TableColumn<Loan.Lender, Integer> lenderInvestTableColumn = new TableColumn<>("Invest");
            //Label payEveryYazLabel = new Label();
            //payEveryYazLabel.setText("Payment Every Yaz: " + loan.getPaysEveryYaz());


            lenderNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            lenderInvestTableColumn.setCellValueFactory(new PropertyValueFactory<>("invest"));

            lendersTableView.getColumns().addAll(lenderNameTableColumn, lenderInvestTableColumn);
            GridPane.setHgrow(lendersTableView, Priority.ALWAYS);
            lendersTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
            ObservableList<Loan.Lender> lenders = FXCollections.observableArrayList();
            lenders.addAll(loan.getLenders().keySet());
            lendersTableView.setItems(lenders);
            editor.addRow(8, new Label("\t"), new Label("Lenders: "));
            editor.addRow(9, new Label("\t"), lendersTableView);
        } else {
            editor.addRow(8, new Label("\t"), new Label("Lenders:  No Lenders"));
        }

        return editor;
    }

    @FXML
    protected void onCLickIncreaseYaz(final ActionEvent event) {
        if(!this.rewind_mode_toggle_switch.selectedProperty().getValue()) {
            String finalUrl = HttpUrl
                    .parse(admin_utills.Constants.UPDATE_YAZ_TIME)
                    .newBuilder()
                    .addQueryParameter("m", "increase")
                    .build()
                    .toString();
            HttpAdminUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("onFailure onCLickIncreaseYaz");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("onResponse");
                    String jsonTimeUnit = response.body().string();
                    System.out.println("Response: " + jsonTimeUnit);
                    if (response.code() == 200) {
                        HashMap<String, Object> hashJson = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonTimeUnit, HashMap.class);
                        int yaz_time = (int) (Math.floor((Double) hashJson.get("yaz_time")));
                        Platform.runLater(() -> {
                            admin_current_yaz_label_dashboard_page.setText("Time Unit: " + yaz_time);
                            // error_label_load_file.setText("File Loaded Successfully!");
                            // error_label_load_file.setVisible(true);
                            // error_label_load_file.setTextFill(Color.GREEN);
                            // error_label_load_file.setMinWidth(100);
                            //error_label_load_file.setMinHeight(100);
                        });

//                        }

                    } else {
                        HashMap<String, String> mapfromJson = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonTimeUnit, HashMap.class);

                        for (Map.Entry<String, String> stringStringEntry : mapfromJson.entrySet()) {
                            System.out.println(stringStringEntry.getKey() + " , " + stringStringEntry.getValue());
                            Platform.runLater(() -> {
                                // error_label_load_file.setText(stringStringEntry.getKey() + " , " + stringStringEntry.getValue());
                                //error_label_load_file.setVisible(true);
                                //error_label_load_file.setTextFill(Color.RED);
                                //error_label_load_file.setMinWidth(50);
                                //error_label_load_file.setMinHeight(50);
                            });
                        }
                    }
                }
            });
        }
        else{
            String finalUrl = HttpUrl
                    .parse(Constants.UPDATE_TIME_UNIT_REWIND_MODE)
                    .newBuilder()
                    .addQueryParameter("rewind_action", "increase")
                    .build()
                    .toString();
            HttpAdminUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("onFailure onCLickIncreaseYaz");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("onResponse");
                    String jsonTimeUnit = response.body().string();
                    System.out.println("Response: " + jsonTimeUnit);
                    if (response.code() == 200) {
                        HashMap<String, Object> hashJson = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonTimeUnit, HashMap.class);
                        int yaz_time = (int) (Math.floor((Double) hashJson.get("time_unit_rewind_mode")));
                        Platform.runLater(() -> {
                            admin_current_yaz_label_dashboard_page.setText("Time Unit: " + yaz_time);
                            // error_label_load_file.setText("File Loaded Successfully!");
                            // error_label_load_file.setVisible(true);
                            // error_label_load_file.setTextFill(Color.GREEN);
                            // error_label_load_file.setMinWidth(100);
                            //error_label_load_file.setMinHeight(100);
                            updateClientsInfoInRewindMode(true);
                            updateLoansInfoInRewindMode(true);
                        });

//                        }

                    } else {
                        HashMap<String, String> mapfromJson = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonTimeUnit, HashMap.class);

                        for (Map.Entry<String, String> stringStringEntry : mapfromJson.entrySet()) {
                            System.out.println(stringStringEntry.getKey() + " , " + stringStringEntry.getValue());
                            Platform.runLater(() -> {
                                // error_label_load_file.setText(stringStringEntry.getKey() + " , " + stringStringEntry.getValue());
                                //error_label_load_file.setVisible(true);
                                //error_label_load_file.setTextFill(Color.RED);
                                //error_label_load_file.setMinWidth(50);
                                //error_label_load_file.setMinHeight(50);
                            });
                        }
                    }
                }
            });

        }
    }

    @FXML
    protected void onClickDecreaseYazTime(final ActionEvent event)
    {
        if(this.rewind_mode_toggle_switch.selectedProperty().getValue())
        {
            String finalUrl = HttpUrl
                    .parse(Constants.UPDATE_TIME_UNIT_REWIND_MODE)
                    .newBuilder()
                    .addQueryParameter("rewind_action", "decrease")
                    .build()
                    .toString();
            HttpAdminUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("onFailure onClickDecreaseYazTime");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("onResponse");
                    String jsonTimeUnit = response.body().string();
                    System.out.println("Response: " + jsonTimeUnit);
                    if (response.code() == 200) {
                        HashMap<String, Object> hashJson = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonTimeUnit, HashMap.class);
                        int yaz_time = (int) (Math.floor((Double) hashJson.get("time_unit_rewind_mode")));
                        Platform.runLater(() -> {
                            admin_current_yaz_label_dashboard_page.setText("Time Unit: " + yaz_time);
                            // error_label_load_file.setText("File Loaded Successfully!");
                            // error_label_load_file.setVisible(true);
                            // error_label_load_file.setTextFill(Color.GREEN);
                            // error_label_load_file.setMinWidth(100);
                            //error_label_load_file.setMinHeight(100);
                            updateClientsInfoInRewindMode(true);
                            updateLoansInfoInRewindMode(true);
                        });
                    } else {
                        HashMap<String, String> mapfromJson = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonTimeUnit, HashMap.class);

                        for (Map.Entry<String, String> stringStringEntry : mapfromJson.entrySet()) {
                            System.out.println(stringStringEntry.getKey() + " , " + stringStringEntry.getValue());
                            Platform.runLater(() -> {
                                // error_label_load_file.setText(stringStringEntry.getKey() + " , " + stringStringEntry.getValue());
                                //error_label_load_file.setVisible(true);
                                //error_label_load_file.setTextFill(Color.RED);
                                //error_label_load_file.setMinWidth(50);
                                //error_label_load_file.setMinHeight(50);
                            });
                        }
                    }
                }
            });

        }
    }

    @FXML
    void onClickLogout(ActionEvent event) {
        // chatCommands.updateHttpLine(Constants.LOGOUT);
        // chatCommands.updateHttpLine(Constants.LOGOUT);


        HttpAdminUtil.runAsync(admin_utills.Constants.LOGOUT, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // chatCommands.updateHttpLine("Logout request ended with failure...:(");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() || response.isRedirect()) {
                    HttpAdminUtil.removeCookiesOf(admin_utills.Constants.BASE_DOMAIN);
                    Platform.runLater(() -> {
                        try {
                            ABS_Admin.admin_name = null;
                            FXMLLoader loader = new FXMLLoader();
                            URL url = getClass().getResource(Constants.ADMIN_LOGIN_PAGE_FXML_RESOURCE_LOCATION);
                            loader.setLocation(url);
                            Parent root = loader.load();
                            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                            rewind_mode_toggle_switch.setSelected(false);
                            admin_decrease_yaz_btn_dashboard_page.setDisable(true);
                            //adminLoadFileScene.updateWelcomeAdminLabel(username_admin_login.getText());
                        } catch (IOException ex) {
                            System.err.println(ex);
                        }
                    });
                }
            }
        });

    }

    /*Update Data Every 500 MiliSec*/
    /*TODO Data not in tables*/
    public void startClientsInfoRefresher(){
        clientsInfoRefresher = new ClientsInfoRefresher(
                this::updateClientsInfo);

        clientsInfoRefresh_Timer = new Timer();
        clientsInfoRefresh_Timer.schedule(clientsInfoRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateClientsInfo(List<AdminDashboard.ClientInfo> clients) {
        if(!this.rewind_mode_toggle_switch.selectedProperty().getValue()) {
            clients.sort(Comparator.comparing(ClientInfo::getName));
            if (!clientInfoObservableList.equals(clients)) {
                System.out.println("updateClientsInfo");
                System.out.println(clients);
                for (ClientInfo client : clients) {
                    System.out.println(client);
                }
                clientInfoObservableList.clear();
                clientInfoObservableList.setAll(clients);
                customer_info_tableview.refresh();
            }
        }
    }

    public void startLoansInfoRefresher(){
        loansInfoRefresher = new javafx.scenes.dashboard.LoansInfoRefresher(
                this::updateLoansInfo);

        loansInfoRefresh_Timer = new Timer();
        loansInfoRefresh_Timer.schedule(loansInfoRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateRewindMode(Boolean rewindMode)
    {
        String action = "disable";
        if(rewindMode)
            action = "active";

        String finalUrl = HttpUrl
                .parse(Constants.REWIND_MODE)
                .newBuilder()
                .addQueryParameter("action", action)
                .build()
                .toString();
        HttpAdminUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("onFailure updateRewindMode");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            }
        });
    }

    private void updateRewindModeInEngine(Boolean rewindMode)
    {
        String action = "false";
        if(rewindMode)
            action = "true";

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
                String jsonSt = response.body().string();
               if(response.code() !=  200) {
                    HashMap<String,String> mapfromJson = admin_utills.Constants.GSON_INSTANCE.fromJson(jsonSt, HashMap.class);

                    for (Map.Entry<String, String> stringStringEntry : mapfromJson.entrySet()) {
                        System.out.println(stringStringEntry.getKey() + " , " + stringStringEntry.getValue());
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error!");
                            alert.setHeaderText(stringStringEntry.getKey());
                            alert.setContentText(stringStringEntry.getValue());
                            alert.showAndWait();

                        });
                    }
                }
            }
        });
    }
    private void updateClientsInfoInRewindMode(Boolean rewindMode){
        if(rewindMode){
            HttpAdminUtil.runAsync(Constants.CUSTOMERS_DATA_IN_REWIND_MODE, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failure!!!");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String jsonSt = response.body().string();
                    if (response.code() == 200) {
                        Type listType = new TypeToken<Map<String, List<AdminDashboard.ClientInfo>>>() {
                        }.getType();

                        Map<String, List<AdminDashboard.ClientInfo>> hashJson = GSON_INSTANCE.fromJson(jsonSt, listType);
                        List<AdminDashboard.ClientInfo> clients = hashJson.get("clients");
                        System.out.println("clients");
                        System.out.println(clients);
                        clientInfoObservableList.clear();
                        clientInfoObservableList.setAll(clients);
                        customer_info_tableview.refresh();
                    }else{
                        System.out.println("error response.code is :"+ response.code() );
                    }
                }
            });
        }
    }

    private void updateLoansInfoInRewindMode(Boolean rewindMode){
        if(rewindMode){
            HttpAdminUtil.runAsync(Constants.LOANS_DATA_IN_REWIND_MODE, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failure!!!");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String jsonSt = response.body().string();
                    if (response.code() == 200) {
                        Type listType = new TypeToken<Map<String, List<Loan>>>() {
                        }.getType();

                        Map<String, List<Loan>> hashJson = GSON_INSTANCE.fromJson(jsonSt, listType);
                        List<Loan> loans = hashJson.get("loans");
                        System.out.println("loans");
                        System.out.println(loans);
                        loanObservableList.clear();
                        loanObservableList.setAll(loans);
                        loans_info_tableview.refresh();
                    }else{
                        System.out.println("error response.code is :"+ response.code() );
                    }
                }
            });
        }

    }



    private void updateLoansInfo(List<Loan> loans) {
        if (!this.rewind_mode_toggle_switch.selectedProperty().getValue()) {
            loans.sort(Comparator.comparing(o -> o.id));
            if (!loanObservableList.equals(loans)) {
                System.out.println("updateLoansInfo");
                System.out.println(loans);
                for (Loan loan : loans) {
                    System.out.println(loan);
                }
                loanObservableList.clear();
                loanObservableList.setAll(loans);
                loans_info_tableview.refresh();
            }
        }
    }

}
