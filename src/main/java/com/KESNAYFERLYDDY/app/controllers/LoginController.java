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

public class LoginController {
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnLogin;
    @FXML private Label lblMsg;

    private final AuthService authService = new AuthService();

    @FXML
    private void onLogin() {
        String user = txtUser.getText().trim();
        String pass = txtPass.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            lblMsg.setText("Usuario y contraseña obligatorios");
            return;
        }
        btnLogin.setDisable(true);
        lblMsg.setText("");

        UsuarioDto u = new UsuarioDto();
        u.setNombreUsuario(user);
        u.setContrasena(pass);

        Task<Boolean> task = new Task<>() {
            @Override protected Boolean call() throws Exception {
                return authService.login(u);
            }
        };

        task.setOnSucceeded(ev -> {
            boolean ok = task.getValue();
            btnLogin.setDisable(false);
            if (ok) {
                lblMsg.setStyle("-fx-text-fill: green;");
                lblMsg.setText("Login correcto. Abriendo dashboard...");
                // aquí abrirás dashboard (crearás un DashboardController + FXML)
                // por ahora cerramos la ventana
                Stage s = (Stage) btnLogin.getScene().getWindow();
                s.close();
            } else {
                lblMsg.setText("Credenciales incorrectas");
            }
        });

        task.setOnFailed(ev -> {
            btnLogin.setDisable(false);
            lblMsg.setText("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }
}
