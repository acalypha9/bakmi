package com.kelompok6.bakmi.Controllers;

import com.kelompok6.bakmi.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.io.IOException;

public class WelcomeController {

    @FXML
    private Button dineInButton;

    @FXML
    private Button takeAwayButton;

    @FXML
    void btnDineInClick(ActionEvent event) throws IOException {
        // Switch to the Customer Menu scene
        App.stage.setScene(App.scene2);
    }

    @FXML
    void btnTakeAwayClick(ActionEvent event) {
        // You can implement a separate scene or functionality here
        App.stage.setScene(App.scene2);
    }
}
