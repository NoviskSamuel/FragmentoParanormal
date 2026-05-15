module com.mycompany.fragmentoparanormal {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.fragmentoparanormal to javafx.fxml;
    exports com.mycompany.fragmentoparanormal;
}
