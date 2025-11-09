package com.KESNAYFERLYDDY.app.controllers;

import com.KESNAYFERLYDDY.app.services.DashboardService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblUser;
    @FXML private Label lblClientesCount;
    @FXML private Label lblVentasCount;
    @FXML private Label lblUsuariosCount;
    @FXML private Label lblProductosCount;

    private final DashboardService service = new DashboardService();
    private String username;

    public static void showDashboard(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(DashboardController.class.getResource("/fxml/dashboard.fxml"));
            Parent nodoDashboard = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(nodoDashboard);
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + username);
            stage.setMaximized(true);
            DashboardController controladorDashboard = loader.getController();
            controladorDashboard.iniciar(username);
            stage.show();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void iniciar(String username) {
        this.username = username;
        lblUser.setText("Bienvenido " + username);
        cargarDatos();
    }

    private void cargarDatos() {
        Task<Void> task = new Task<>() {
            int clientes = 0, ventas = 0, usuarios = 0, productos = 0;
            @Override protected Void call() throws Exception {
                clientes = service.cantidadDeClientes();
                ventas = service.cantidadDeVentas();
                usuarios = service.cantidadDeUsuarios();
                productos = service.cantidadDeProductos();
                return null;
            }
            @Override protected void succeeded() {
                Platform.runLater(() -> {
                    lblClientesCount.setText(String.valueOf(clientes));
                    lblVentasCount.setText(String.valueOf(ventas));
                    lblUsuariosCount.setText(String.valueOf(usuarios));
                    lblProductosCount.setText(String.valueOf(productos));
                });
            }
            @Override protected void failed() {
                getException().printStackTrace();
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onOpenClientes(){
        ClientesListController.show(username);
        onLogout();
    }

    @FXML
    private void onOpenVentas() { /* Implementa VentasListController.show(username) para mostrar lo del listado de ventas */ }

    @FXML
    private void onOpenUsuarios() { /* Implementa UsuariosListController.show(username) para implementar la vista de usuario y ahi editar permisos y roles */ }

    @FXML
    private void onOpenProductos() { /* Implementa ProductosListController.show(username)  para CRUD de productos*/ }

    @FXML
    private void onLogout(){
        Stage stage = (Stage) lblUser.getScene().getWindow();
        stage.close();
    }
}
