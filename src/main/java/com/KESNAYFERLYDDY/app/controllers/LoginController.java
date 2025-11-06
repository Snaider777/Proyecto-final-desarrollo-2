package com.KESNAYFERLYDDY.app.controllers;

import com.KESNAYFERLYDDY.app.models.UsuarioDto;
import com.KESNAYFERLYDDY.app.services.AuthService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.KESNAYFERLYDDY.app.animations.FadeDownAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeUpAnimation;

public class LoginController {
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnLogin;
    @FXML private Label lblMsg;

    private final AuthService authService = new AuthService();

    @FXML
    private void ocultarMensaje(){
        if(lblMsg.getOpacity() != 0){
            FadeDownAnimation.play(lblMsg);
            lblMsg.setOpacity(0);
        }
    }

    @FXML
    private void manejarLogin() {
        String user = txtUser.getText().trim();
        String pass = txtPass.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            lblMsg.setText("Usuario y contraseña obligatorios");
            lblMsg.getStyleClass().add("error");
            FadeUpAnimation.play(lblMsg);
            return;
        }
        btnLogin.setDisable(true);
        lblMsg.setText("");

        UsuarioDto usuario = new UsuarioDto();
        usuario.setNombreUsuario(user);
        usuario.setContrasena(pass);

        Task<Boolean> task = new Task<>() {
            @Override protected Boolean call() throws Exception {
                return authService.login(usuario);
            }
        };

        task.setOnSucceeded(event -> {
            boolean ok = task.getValue();
            btnLogin.setDisable(false);
            if (ok) {
                lblMsg.getStyleClass().add("exito");
                lblMsg.setText("Login correcto. Abriendo dashboard...");
                Stage stage = (Stage) lblMsg.getScene().getWindow();
                stage.close();
                DashboardController.showDashboard(user);
            } else {
                lblMsg.getStyleClass().add("error");
                lblMsg.setText("Credenciales incorrectas");
                FadeUpAnimation.play(lblMsg);
            }
        });

        task.setOnFailed(event -> {
            btnLogin.setDisable(false);
            lblMsg.setText("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }
}
