package com.KESNAYFERLYDDY.app;

import com.KESNAYFERLYDDY.app.controllers.LoginController;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        LoginController.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
