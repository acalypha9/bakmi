module com.kelompok6.bakmi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires de.jensd.fx.glyphs.fontawesome;

    // ✅ Allow FXML loader to access controller classes reflectively
    opens com.kelompok6.bakmi to javafx.fxml;
    opens com.kelompok6.bakmi.Controllers to javafx.fxml;
    opens com.kelompok6.bakmi.Controllers.Admin to javafx.fxml;
    opens com.kelompok6.bakmi.Controllers.Customers to javafx.fxml;

    // ✅ Export packages (for public API access, not for FXML)
    exports com.kelompok6.bakmi;
    exports com.kelompok6.bakmi.Controllers;
    exports com.kelompok6.bakmi.Controllers.Admin;
    exports com.kelompok6.bakmi.Controllers.Customers;
    exports com.kelompok6.bakmi.Models;
    exports com.kelompok6.bakmi.Views;
}