package com.kelompok6.bakmi.Controllers;

import com.kelompok6.bakmi.App;
import com.kelompok6.bakmi.Models.DineInCustomer;
import com.kelompok6.bakmi.Models.TakeAwayCustomer;
import com.kelompok6.bakmi.Session;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * Controller for the Welcome screen.
 *
 * <p>This controller handles the user's selection between Dine-In and Take-Away,
 * prepares and stores the corresponding {@link com.kelompok6.bakmi.Models.Customer}
 * in {@link Session}, and navigates to the customer menu scene (scene2) defined in {@link App}.</p>
 *
 * <p>It also manages a small floating "Admin" button whose visibility is governed
 * by mouse hover events on the root pane.</p>
 */
public class WelcomeController {

    /**
     * Root container of the welcome FXML. Used to detect mouse enter/exit events
     * to show/hide the floating admin button.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Floating admin button anchored in the welcome layout. Initially hidden;
     * shown when the mouse enters the rootPane and hidden again after a short delay
     * when the mouse leaves.
     */
    @FXML
    private Button floatingButton;

    /**
     * Called by the FXML loader after fields are injected.
     * Initializes UI behavior such as the floating button hover logic.
     */
    @FXML
    private void initialize() {
        setupFloatingButtonHoverBehavior();
    }

    /**
     * Configures hover behavior for the {@link #floatingButton}.
     *
     * <p>The method attaches mouse listeners to {@link #rootPane} and the button itself
     * so the button appears when the user moves the mouse into the welcome area and
     * disappears shortly after the mouse leaves. A short {@link PauseTransition} is used
     * to avoid flicker when the pointer moves quickly.</p>
     */
    private void setupFloatingButtonHoverBehavior() {
        if (rootPane == null || floatingButton == null) return;

        PauseTransition hideDelay = new PauseTransition(Duration.millis(250));
        hideDelay.setOnFinished(ev -> {
            floatingButton.setVisible(false);
            floatingButton.setManaged(false);
        });

        rootPane.setOnMouseEntered(e -> {
            hideDelay.stop();
            floatingButton.setManaged(true);
            floatingButton.setVisible(true);
        });

        rootPane.setOnMouseExited(e -> hideDelay.playFromStart());

        floatingButton.setOnMouseEntered(e -> {
            hideDelay.stop();
            floatingButton.setManaged(true);
            floatingButton.setVisible(true);
        });

        floatingButton.setOnMouseExited(e -> hideDelay.playFromStart());
    }

    /**
     * Handler for the "Makan Disini" button.
     *
     * <p>Creates a {@link DineInCustomer} with {@code nomorMeja = 0} to indicate the table
     * has not yet been selected, stores it into {@link Session}, and switches the primary
     * stage to {@link App#scene2} (customer menu).</p>
     *
     * @param event the action event provided by JavaFX
     */
    @FXML
    void btnDineInClick(ActionEvent event) {
        try {
            DineInCustomer dine = new DineInCustomer();
            dine.setNomorMeja(0);
            dine.setNamaCustomer("");
            Session.setCurrentCustomer(dine);

            if (App.scene2 != null) {
                App.stage.setScene(App.scene2);
            } else {
                System.err.println("[WelcomeController] App.scene2 == null - pastikan App men-setup scene2");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handler for the "Bawa Pulang" button.
     *
     * <p>Creates a {@link TakeAwayCustomer}, sets a default pickup time of now + 20 minutes,
     * stores it into {@link Session}, then navigates to {@link App#scene2} (customer menu).</p>
     *
     * @param event the action event provided by JavaFX
     */
    @FXML
    void btnTakeAwayClick(ActionEvent event) {
        try {
            TakeAwayCustomer tak = new TakeAwayCustomer();
            tak.setNamaCustomer("");
            tak.aturWaktuAmbil(LocalDateTime.now().plusMinutes(20));
            Session.setCurrentCustomer(tak);

            if (App.scene2 != null) {
                App.stage.setScene(App.scene2);
            } else {
                System.err.println("[WelcomeController] App.scene2 == null - pastikan App men-setup scene2");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handler for the floating "Admin" button.
     *
     * <p>Attempts to load the admin FXML from {@code /Fxml/Admin/Admin.fxml} and switch
     * the application's primary stage to the admin scene.</p>
     *
     * @param event the action event provided by JavaFX
     */
    @FXML
    void onFloatingButtonClick(ActionEvent event) {
        try {
            URL adminFxml = getClass().getResource("/Fxml/Admin/Admin.fxml");
            if (adminFxml == null) {
                System.err.println("Admin.fxml not found at /Fxml/Admin/Admin.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(adminFxml);
            Scene adminScene = new Scene(loader.load());
            App.stage.setScene(adminScene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}