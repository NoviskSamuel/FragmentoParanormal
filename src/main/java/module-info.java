module fragmentoparanormal {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens Default to javafx.fxml;
    opens Controller to javafx.fxml;
    opens Util to javafx.fxml;
    opens Graphics to javafx.fxml;
    opens Model to javafx.fxml;
    opens Dao to javafx.fxml;

    exports Default;
    exports Controller;
    exports Model;
    exports Dao;
    exports Util;
    exports Graphics;
}