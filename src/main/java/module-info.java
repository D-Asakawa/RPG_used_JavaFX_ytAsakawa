module com.ytasakawa.rpg_used_javafx_ytasakawa {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ytasakawa.rpg_used_javafx_ytasakawa to javafx.fxml;
    exports com.ytasakawa.rpg_used_javafx_ytasakawa;
}