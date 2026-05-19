module com.mycompany.fragmentoparanormal {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.fragmentoparanormal to javafx.fxml;
    opens com.mycompany.fragmentoparanormal.model to javafx.fxml;
    opens com.mycompany.fragmentoparanormal.dao to javafx.fxml;
    
    exports com.mycompany.fragmentoparanormal;
    exports com.mycompany.fragmentoparanormal.model;
    exports com.mycompany.fragmentoparanormal.dao;
}
