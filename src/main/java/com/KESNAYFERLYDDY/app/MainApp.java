package com.KESNAYFERLYDDY.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static Stage stagePrincipal = new Stage();

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
        Parent nodoRaiz = loader.load();
        Scene scene = new Scene(nodoRaiz);
        stage.setTitle("MiApp - Login");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/stageIcon.png")));
        stage.setMaximized(true);
        stage.show();
    }

    public static Stage getPrincipalStage(){
        return stagePrincipal;
    }

    public static void main(String[] args) {
        launch();
    }
}
