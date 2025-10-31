package com.KESNAYFERLYDDY.app.controllers;

import com.KESNAYFERLYDDY.app.services.DashboardService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
            Scene scene = new Scene(loader.load());
            Stage st = new Stage();
            st.setScene(scene);
            st.setTitle("Dashboard - " + username);
            DashboardController ctrl = loader.getController();
            ctrl.init(username);
            st.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(String username) {
        this.username = username;
        lblUser.setText("Usuario: " + username);
        loadCounts();
    }

    private void loadCounts() {
        Task<Void> t = new Task<>() {
            int clientes = 0, ventas = 0, usuarios = 0, productos = 0;
            @Override protected Void call() throws Exception {
                clientes = service.countClientes();
                ventas = service.countVentas();
                usuarios = service.countUsuarios();
                productos = service.countProductos();
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
        new Thread(t).start();
    }

    @FXML
    private void onOpenClientes() { ClientesListController.show(username); }

    @FXML
    private void onOpenVentas() { /* Implementa VentasListController.show(username) para mostrar lo del listado de ventas */ }

    @FXML
    private void onOpenUsuarios() { /* Implementa UsuariosListController.show(username) para implementar la vista de usuario y ahi editar permisos y roles */ }

    @FXML
    private void onOpenProductos() { /* Implementa ProductosListController.show(username)  para CRUD de productos*/ }

    @FXML
    private void onLogout(){
        // Cerrar ventana del dashboard
        Stage s = (Stage) lblUser.getScene().getWindow();
        s.close();
        // Opcional: volver a mostrar login siento que esta bien pero ahi deciden
    }
}
