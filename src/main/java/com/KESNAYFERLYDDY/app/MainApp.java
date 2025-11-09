package com.KESNAYFERLYDDY.app;

import com.KESNAYFERLYDDY.app.controllers.DashboardController;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /* FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
        Parent nodoRaiz = loader.load();
        Scene scene = new Scene(nodoRaiz);
        stage.setTitle("MiApp - Login");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/stageIcon.png")));
        stage.setMaximized(true);
        stage.show(); */
       /*  ClientesListController.show("pepito");*/
       DashboardController.showDashboard("Nayeli");
    }

    public static void main(String[] args) {
        launch();
    }
}
