package com.KESNAYFERLYDDY.app.controllers;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.KESNAYFERLYDDY.app.models.VentaDto;
import com.KESNAYFERLYDDY.app.services.DashboardService;
import com.KESNAYFERLYDDY.app.services.PermisoService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblUser;
    @FXML private Label lblClientesCount;
    @FXML private Label lblVentasCount;
    @FXML private Label lblUsuariosCount;
    @FXML private Label lblProductosCount;
    @FXML private LineChart<String, Number> lineChartVentas;
    @FXML private BarChart<String, Number> barChartMuebles;
    @FXML private Button btnEmpleados;
    @FXML private Button btnClientes;
    @FXML private Button btnVentas;
    @FXML private Button btnProductos;
    @FXML private GridPane gridAdmin;
    @FXML private GridPane gridVendedor;
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
            List<VentaDto> ventasMes;
            List<com.KESNAYFERLYDDY.app.models.MuebleVendidoDto> mueblesMasVendidos;
            @Override protected Void call() {
                clientes = service.cantidadDeClientes();
                ventas = service.cantidadDeVentas();
                usuarios = service.cantidadDeEmpleados();
                productos = service.cantidadDeProductos();
                ventasMes = service.ventasDelMes();
                mueblesMasVendidos = service.mueblesMasVendidosDelMes();
                return null;
            }
            @Override protected void succeeded() {
                Platform.runLater(() -> {
                    lblClientesCount.setText(String.valueOf(clientes));
                    lblVentasCount.setText(String.valueOf(ventas));
                    lblUsuariosCount.setText(String.valueOf(usuarios));
                    lblProductosCount.setText(String.valueOf(productos));
                    
                    XYChart.Series<String, Number> seriesVentas = new XYChart.Series<>();
                    seriesVentas.setName("Ventas del Mes");
                    Map<String, Double> ventasPorDia = ventasMes.stream()
                        .collect(Collectors.groupingBy(
                            v -> v.getFechaVenta().toLocalDate().format(DateTimeFormatter.ofPattern("dd")),
                            Collectors.summingDouble(venta -> venta.getTotal().doubleValue())
                        ));
                    
                    ventasPorDia.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> seriesVentas.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
                    
                    lineChartVentas.getData().clear();
                    lineChartVentas.getData().add(seriesVentas);

                    XYChart.Series<String, Number> seriesMuebles = new XYChart.Series<>();
                    seriesMuebles.setName("Muebles Más Vendidos del Mes");
                    mueblesMasVendidos.stream().forEach(m -> {
                        seriesMuebles.getData().add(new XYChart.Data<>(m.getMueble().getNombreMueble(), m.getCantidadVendida()));
                    });
                    
                    barChartMuebles.getData().clear();
                    barChartMuebles.getData().add(seriesMuebles);
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
            boolean puedenVerClientes = false;
            @Override
            protected Void call() throws Exception {
                // Preguntamos al backend
                puedeVerEmpleados = permisoService.tienePermiso(username, "VER_EMPLEADOS");
                puedenVerClientes = permisoService.tienePermiso(username, "VER_CLIENTES");
                return null;
            }

            @Override
            protected void succeeded() {
                // Actualizamos la interfaz gráfica
                // Si el usuario no puede ver empleados ni clientes, mostramos la vista de vendedor
                if (!puedeVerEmpleados && !puedenVerClientes) {
                    if (gridAdmin != null) {
                        gridAdmin.setVisible(false);
                        gridAdmin.setManaged(false);
                    }
                    if (gridVendedor != null) {
                        gridVendedor.setVisible(true);
                        gridVendedor.setManaged(true);
                    }
                    return;
                }

                // Si estamos aquí, mostramos la vista de admin y reordenamos las tarjetas
                if (gridAdmin != null) {
                    // Preparar la lista de tarjetas que deben mostrarse
                    List<Button> tarjetas = new ArrayList<>();
                    if (puedenVerClientes) tarjetas.add(btnClientes);
                    if (puedeVerEmpleados) tarjetas.add(btnEmpleados);
                    // Ventas y Productos se muestran por defecto
                    tarjetas.add(btnVentas);
                    tarjetas.add(btnProductos);

                    // Limpiar y volver a agregar en orden compacto (2 columnas)
                    gridAdmin.getChildren().clear();
                    for (int i = 0; i < tarjetas.size(); i++) {
                        Button b = tarjetas.get(i);
                        if (b == null) continue;
                        b.setVisible(true);
                        b.setManaged(true);
                        int col = i % 2;
                        int row = i / 2;
                        gridAdmin.add(b, col, row);
                    }
                    gridAdmin.setVisible(true);
                    gridAdmin.setManaged(true);
                }

                // Asegurarse de ocultar la vista de vendedor si existe
                if (gridVendedor != null) {
                    gridVendedor.setVisible(false);
                    gridVendedor.setManaged(false);
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
