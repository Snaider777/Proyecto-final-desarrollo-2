package com.KESNAYFERLYDDY.app.controllers;

import com.KESNAYFERLYDDY.app.services.DashboardService;
import com.KESNAYFERLYDDY.app.services.PermisoService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblUser;
    @FXML private Label lblClientesCount;
    @FXML private Label lblVentasCount;
    @FXML private Label lblUsuariosCount;
    @FXML private Label lblProductosCount;
    @FXML private Button btnEmpleados;
    @FXML private Button btnClientes;
    @FXML private Button btnVentas;
    @FXML private Button btnProductos;
    private final DashboardService service = new DashboardService();
    private String username;
    private final PermisoService permisoService = new PermisoService();
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
        lblUser.setText("Hola, " + username);
        
        cargarDatos();      
        validarPermisos(); 
    }

    private void cargarDatos() {
        Task<Void> task = new Task<>() {
            int clientes = 0, ventas = 0, usuarios = 0, productos = 0;
            @Override protected Void call() throws Exception {
                clientes = service.cantidadDeClientes();
                ventas = service.cantidadDeVentas();
                usuarios = service.cantidadDeEmpleados();
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
    private void validarPermisos() {
        // Ejecutamos en un hilo secundario para no congelar la pantalla
        Task<Void> task = new Task<>() {
            boolean puedeVerEmpleados = false;

            @Override
            protected Void call() throws Exception {
                // Preguntamos al backend
                puedeVerEmpleados = permisoService.tienePermiso(username, "ver_empleado");
                return null;
            }

            @Override
            protected void succeeded() {
                // Actualizamos la interfaz gráfica
                if (!puedeVerEmpleados) {
                    btnEmpleados.setVisible(false);  // Lo hace invisible
                    btnEmpleados.setManaged(false); 
                }
            }
            
            @Override
            protected void failed() {
                getException().printStackTrace();
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onOpenClientes(){
        ClientesListController.show(username);
        onChangeView();
    }

    @FXML
    private void onOpenVentas() { 
        VentasListController.show(username);
        onChangeView();
    }

    @FXML
    private void onOpenEmpleados() {
        EmpleadosListController.show(username);
        onChangeView();
    }

    @FXML
    private void onOpenProductos() {
        ProductosListController.show(username);
        onChangeView();
    }

    @FXML
    private void onLogout(){
        Stage stage = (Stage) lblUser.getScene().getWindow();
        stage.close();
        LoginController.show();
    }

    @FXML
    private void onChangeView(){
        Stage stage = (Stage) lblUser.getScene().getWindow();
        stage.close();
    }
}
