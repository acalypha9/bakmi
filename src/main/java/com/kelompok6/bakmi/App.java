package com.kelompok6.bakmi;

import com.kelompok6.bakmi.Database.DBUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application entry point for the Bakmi app.
 *
 * <p>Initializes the embedded database if needed, loads the initial (welcome)
 * scene and the customer menu scene, then shows the primary stage.</p>
 *
 * <p>Note: this class delegates to JavaFX's Application.launch(...) in {@code main}.</p>
 */
public class App extends Application {
    public static Stage stage;
    public static Scene scene1;
    public static Scene scene2;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try { DBUtil.initDatabaseIfNeeded(); }
        catch (Exception ex) { System.err.println("Warning: DB initialization failed: " + ex.getMessage()); }

        FXMLLoader root = new FXMLLoader(getClass().getResource("/Fxml/Welcome.fxml"));
        scene1 = new Scene(root.load());
        FXMLLoader second = new FXMLLoader(getClass().getResource("/Fxml/Customers/CustomerMenu.fxml"));
        scene2 = new Scene(second.load());

        stage = primaryStage;
        stage.setScene(scene1);
        stage.setTitle("Bakmi App");
        stage.show();
    }

    /**
     * Standard JVM entry point.
     * Delegates to JavaFX application launch.
     */
    public static void main(String[] args) { launch(); }
}