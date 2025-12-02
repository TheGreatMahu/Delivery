package com.delivery;

import com.delivery.database.Database;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // Force Database initialization BEFORE loading any UI
        System.out.println("=== Initializing Database ===");
        try {
            Database.getConnection().close();
            System.out.println("=== Database Ready ===");
        } catch (Exception e) {
            System.err.println("=== Database Initialization Failed ===");
            e.printStackTrace();
            throw e;
        }
        
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Online Delivery System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxmlTitle, String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle(fxmlTitle);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}