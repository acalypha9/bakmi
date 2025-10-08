package com.kelompok6.bakmi;

import com.kelompok6.bakmi.Models.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    public static Stage stage;
    public static Scene scene1;
    public static Scene scene2;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the first scene
        FXMLLoader root = new FXMLLoader(getClass().getResource("/Fxml/Welcome.fxml"));
        scene1 = new Scene(root.load());

        // Load the second scene
        FXMLLoader second = new FXMLLoader(getClass().getResource("/Fxml/Customers/CustomerMenu.fxml"));
        scene2 = new Scene(second.load());

        // Assign the static stage reference
        stage = primaryStage;
        stage.setScene(scene1);
        stage.setTitle("Bakmi App");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
