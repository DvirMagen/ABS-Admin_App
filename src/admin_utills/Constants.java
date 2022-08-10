package admin_utills;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Constants {
    public static final String ADMIN_LOAD_FILE_PAGE_FXML_RESOURCE_LOCATION = "/javafx/scenes/loadFile/adminLoadFileScene.fxml";
    public static final String ADMIN_LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/javafx/scenes/login/AdminLoginScene.fxml";
    public static final String ADMIN_DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/javafx/scenes/dashboard/AdminDashboard.fxml";
    public static final String BASE_DOMAIN = "localhost";
    private static final String BASE_URL = "http://localhost:8080";
    private static final String CONTEXT_PATH = "/chatApp";
    private static final String FULL_SERVER_PATH = "http://localhost:8080/web_app";
    public static final String LOGIN_PAGE = "http://localhost:8080/web_app/adminLoginShortResponse";
    public final static String YAZ_TIME = FULL_SERVER_PATH + "/";
    public final static String UPDATE_CUSTOMERS_INFO = FULL_SERVER_PATH + "/admin/clients_info";
    public final static String LOANS_INFO = FULL_SERVER_PATH + "/loans";
    public final static String UPDATE_YAZ_TIME = FULL_SERVER_PATH + "/yaz_time";
    public final static String REWIND_MODE = FULL_SERVER_PATH + "/rewind_mode";
    public final static String UPDATE_REWIND_MODE = FULL_SERVER_PATH + "/update_rewind_mode";
    public final static String UPDATE_TIME_UNIT_REWIND_MODE = FULL_SERVER_PATH + "/update_time_unit_rewind_mode";
    public final static String CUSTOMERS_DATA_IN_REWIND_MODE = FULL_SERVER_PATH + "/customers_data_in_rewind_mode";
    public final static String LOANS_DATA_IN_REWIND_MODE = FULL_SERVER_PATH + "/loans_data_in_rewind_mode";
    public static final String USERS_LIST = "http://localhost:8080/chatApp/userslist";
    public static final String LOGOUT = FULL_SERVER_PATH + "/logout";
    public static final String SEND_CHAT_LINE = "http://localhost:8080/chatApp/pages/chatroom/sendChat";
    public static final String CHAT_LINES_LIST = "http://localhost:8080/chatApp/chat";
    public final static Gson GSON_INSTANCE = new GsonBuilder().registerTypeAdapter(Loan.Lender.class, new JsonDeserializer<Loan.Lender>() {
        @Override
        public Loan.Lender deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Gson().fromJson(jsonElement.getAsString(), new TypeToken<Loan.Lender>(){}.getType());
        }
    }).create();

    public final static int REFRESH_RATE = 500;

    public Constants() {
    }
}

