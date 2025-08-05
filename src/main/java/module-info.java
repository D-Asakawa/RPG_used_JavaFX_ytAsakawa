module com.ytasakawa.rpg_used_javafx_ytasakawa {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.ytasakawa.rpg_used_javafx_ytasakawa to javafx.fxml;
    exports com.ytasakawa.rpg_used_javafx_ytasakawa;
}