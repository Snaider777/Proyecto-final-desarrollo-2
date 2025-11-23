package com.KESNAYFERLYDDY.app.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.KESNAYFERLYDDY.app.animations.FadeDownAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeUpAnimation;
import com.KESNAYFERLYDDY.app.models.ClienteDto;
import com.KESNAYFERLYDDY.app.models.DetalleVentasDto;
import com.KESNAYFERLYDDY.app.models.EmpleadosDto;
import com.KESNAYFERLYDDY.app.models.MuebleDto;
import com.KESNAYFERLYDDY.app.models.VentaDto;
import com.KESNAYFERLYDDY.app.services.ClienteService;
import com.KESNAYFERLYDDY.app.services.EmpleadoService;
import com.KESNAYFERLYDDY.app.services.MuebleService;
import com.KESNAYFERLYDDY.app.services.VentaService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentasListController {
    private static String user = "";
    private final VentaService ventaService = new VentaService();
    private final MuebleService muebleService = new MuebleService();
    private final ClienteService clienteService = new ClienteService();
    private final EmpleadoService empleadoService = new EmpleadoService();

    private final Map<Integer, DetalleVentasDto> detallesPorMueble = new HashMap<>();

    @FXML private VBox overlayRegistrarVentas;
    @FXML private VBox overlayEliminarVentas;
    @FXML private VBox overlayEditarVentas;
    @FXML private VBox overlayDetalleVentas;

    @FXML private Label lblMsgRegistroVenta;
    @FXML private Label lblMsgEditarVenta;
    @FXML private Label lblEliminarVenta;

    @FXML private VBox contenedorVentas;
    @FXML private VBox contenedorMuebles;
    @FXML private VBox contenedorMueblesEditar;
    @FXML private VBox contenedorProductosVendidos;

    @FXML private ComboBox<ClienteDto> comboClientes;
    @FXML private ComboBox<ClienteDto> comboClientesEditar;
    @FXML private ComboBox<EmpleadosDto> comboEmpleados;
    @FXML private ComboBox<EmpleadosDto> comboEmpleadosEditar;

    @FXML private ScrollPane contenedorTarjetasProducto;
    @FXML private ScrollPane contenedorTarjetasProductoEditar;

    

    private List<DetalleVentasDto> listaDetalles = new ArrayList<>();
    private VentaDto ventaParaEliminar;
    private VentaDto ventaParaEditar;

    public static void show(String username) {
        try {
            user = username;
            FXMLLoader loader = new FXMLLoader(VentasListController.class.getResource("/fxml/ventas_list.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ventas - " + username);
            stage.setMaximized(true);
            VentasListController controladorVentas = loader.getController();
            controladorVentas.cargarVentas();
            stage.show();
        } catch (Exception error) { error.printStackTrace(); }
    }

    public void cargarVentas(){
        Task<List<VentaDto>> task = new Task<>() {
            @Override protected List<VentaDto> call() throws Exception {
                return ventaService.listarVentas();
            }
        };
        task.setOnSucceeded(event -> {
            List<VentaDto> ventas = task.getValue();
            mostrarVentas(ventas);
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    public void cargarMuebles(){
        Task<List<MuebleDto>> task = new Task<>() {
            @Override protected List<MuebleDto> call() throws Exception {
                return muebleService.listarMuebles();
            }
        };
        task.setOnSucceeded(event -> {
            List<MuebleDto> muebles = task.getValue();
            mostrarMuebles(muebles);
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    public void cargarClientes() {
        Task<List<ClienteDto>> task = new Task<>() {
            @Override protected List<ClienteDto> call() throws Exception {
                return clienteService.listarClientes();
            }
        };
        task.setOnSucceeded(event -> {
            List<ClienteDto> clientes = task.getValue();
            comboClientes.getItems().clear();
            comboClientes.getItems().addAll(clientes);
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    public void cargarEmpleados() {
        Task<List<EmpleadosDto>> task = new Task<>() {
            @Override protected List<EmpleadosDto> call() throws Exception {
                return empleadoService.listarEmpleados();
            }
        };
        task.setOnSucceeded(event -> {
            List<EmpleadosDto> empleados = task.getValue();
            comboEmpleados.getItems().clear();
            comboEmpleados.getItems().addAll(empleados);
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    public void cargarDetallesDeVenta(VentaDto venta) {
        Task<List<DetalleVentasDto>> task = new Task<>()  {
            @Override protected List<DetalleVentasDto> call() throws Exception{
                return ventaService.obtenerDetallesPorVentaId(venta.getIdVenta());
            }
        };
        task.setOnSucceeded(event -> {
            List<DetalleVentasDto> detalles = task.getValue();
            venta.setDetalles(detalles);
            mostrarDetalles(venta);
            overlayDetalleVentas.setVisible(true);
        });
        task.setOnFailed(event -> {
            System.err.println("Error al cargar detalles: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void mostrarMuebles(List<MuebleDto> muebles){
        contenedorMuebles.getChildren().clear();
        
        VBox columnaMuebles = new VBox(10);
        columnaMuebles.setPadding(new Insets(8));
        columnaMuebles.setStyle("-fx-background-color: transparent;");
        
        for (MuebleDto muebleId : muebles){
            DetalleVentasDto detalleExistente = detallesPorMueble.get(muebleId.getIdMueble());
            int cantidadInicial = (detalleExistente != null && detalleExistente.getCantidad() != null) 
                ? detalleExistente.getCantidad() : 0;
            BigDecimal precioInicial = (detalleExistente != null && detalleExistente.getPrecio() != null)
                ? detalleExistente.getPrecio() : BigDecimal.ZERO;
            
            HBox tarjeta = new HBox(10);
            tarjeta.getStyleClass().add("tarjeta-producto");
            tarjeta.setPrefHeight(54);
            tarjeta.setAlignment(Pos.CENTER);
            tarjeta.setMaxWidth(Double.MAX_VALUE);
            
            HBox leftBox = new HBox(6);
            leftBox.setAlignment(Pos.CENTER_LEFT);
            
            TextField txtPrecio = new TextField();
            txtPrecio.setPromptText("Precio");
            txtPrecio.setText(precioInicial.compareTo(BigDecimal.ZERO) > 0 ? precioInicial.toString() : "");
            txtPrecio.setPrefWidth(80);
            txtPrecio.setMaxWidth(80);
            txtPrecio.getStyleClass().add("precio-input");
            txtPrecio.setOnMouseClicked(event -> ocultarMensajeRegistroVenta());

            Button btnMenos = new Button("-");
            btnMenos.setPrefWidth(40);
            btnMenos.setMaxWidth(40);
            btnMenos.getStyleClass().add("btn-menos");
            
            leftBox.getChildren().addAll(txtPrecio, btnMenos);
            
            Label lblNombre = new Label(muebleId.getNombreMueble() != null ? muebleId.getNombreMueble() : "Sin nombre");
            lblNombre.getStyleClass().add("producto-nombre");
            lblNombre.setWrapText(true);

            Label lblCantidad = new Label("(" + cantidadInicial + ")");
            lblCantidad.getStyleClass().add("producto-cantidad");

            HBox centerBox = new HBox(4, lblNombre, lblCantidad);
            centerBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(centerBox, javafx.scene.layout.Priority.ALWAYS);
            
            HBox rightBox = new HBox(8);
            rightBox.setAlignment(Pos.CENTER_RIGHT);
            
            Button btnMas = new Button("+");
            btnMas.setPrefWidth(40);
            btnMas.setMaxWidth(40);
            btnMas.getStyleClass().add("btn-mas");
            
            Label lblSubtotal = new Label("$0");
            lblSubtotal.getStyleClass().add("producto-subtotal");
            
            rightBox.getChildren().addAll(btnMas, lblSubtotal);
            
            Runnable recalcular = () -> {
                DetalleVentasDto detalle = detallesPorMueble.get(muebleId.getIdMueble());
                if (detalle == null || detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                    lblSubtotal.setText("$0");
                    return;
                }
                BigDecimal precioValor = detalle.getPrecio() != null ? detalle.getPrecio() : BigDecimal.ZERO;
                BigDecimal subtotal = precioValor.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                detalle.setSubTotal(subtotal);
                lblSubtotal.setText("$" + subtotal);
            };
            
            if (cantidadInicial > 0) {
                recalcular.run();
            }
            
            btnMas.setOnAction(event -> {
                ocultarMensajeRegistroVenta();
                int cantidadActual = 0;
                DetalleVentasDto detalle = detallesPorMueble.get(muebleId.getIdMueble());
                if (detalle != null && detalle.getCantidad() != null) {
                    cantidadActual = detalle.getCantidad();
                }
                cantidadActual++;
                lblCantidad.setText("(" + cantidadActual + ")");
                
                BigDecimal precioValor = BigDecimal.ZERO;
                String textoPrecio = txtPrecio.getText();
                if (textoPrecio != null && !textoPrecio.isBlank()) {
                    try { precioValor = new BigDecimal(textoPrecio.trim()); } catch (NumberFormatException ex) { precioValor = BigDecimal.ZERO; }
                }
                
                if (detalle == null) {
                    detalle = new DetalleVentasDto();
                    detalle.setMuebleId(muebleId.getIdMueble());
                }
                detalle.setCantidad(cantidadActual);
                detalle.setPrecio(precioValor);
                detallesPorMueble.put(muebleId.getIdMueble(), detalle);
                recalcular.run();
            });
            
            btnMenos.setOnAction(e -> {
                ocultarMensajeRegistroVenta();
                DetalleVentasDto detalle = detallesPorMueble.get(muebleId.getIdMueble());
                if (detalle == null || detalle.getCantidad() == null || detalle.getCantidad() <= 0) { return; }
                int nuevaCantidad = detalle.getCantidad() - 1;
                detalle.setCantidad(nuevaCantidad);
                lblCantidad.setText("(" + nuevaCantidad + ")");
                detallesPorMueble.put(muebleId.getIdMueble(), detalle);
                recalcular.run();
            });
            

            txtPrecio.textProperty().addListener((obs, oldV, newV) -> {
                DetalleVentasDto detalle = detallesPorMueble.get(muebleId.getIdMueble());
                if (detalle == null) { return; }
                BigDecimal precioValor;
                try { 
                    precioValor = (newV == null || newV.isBlank()) ? BigDecimal.ZERO : new BigDecimal(newV.trim()); 
                } catch (Exception ex) { 
                    precioValor = BigDecimal.ZERO; 
                }
                detalle.setPrecio(precioValor);
                detallesPorMueble.put(muebleId.getIdMueble(), detalle);
                recalcular.run();
            });
            
            tarjeta.getChildren().addAll(leftBox, centerBox, rightBox);
            columnaMuebles.getChildren().add(tarjeta);
        }
        contenedorMuebles.getChildren().add(columnaMuebles);
    }
    
    private void mostrarVentas(List<VentaDto> ventas){
        contenedorVentas.getChildren().clear();
        contenedorVentas.setAlignment(Pos.TOP_CENTER);
        contenedorVentas.setSpacing(16);
        contenedorVentas.setPadding(new Insets(20));
        
        for (VentaDto venta : ventas) {
            VBox contenedorCentrado = new VBox();
            contenedorCentrado.setMaxWidth(Double.MAX_VALUE);
            contenedorCentrado.setAlignment(Pos.CENTER);
            
            HBox tarjeta = new HBox(20);
            tarjeta.getStyleClass().add("tarjeta-venta");
            tarjeta.setAlignment(Pos.CENTER_LEFT);
            tarjeta.setMaxWidth(Double.MAX_VALUE * 0.75);
            tarjeta.setPrefHeight(80);
            
            VBox infoVenta = new VBox(8);
            infoVenta.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(infoVenta, javafx.scene.layout.Priority.ALWAYS);
            
            HBox clienteBox = new HBox(8);
            clienteBox.setAlignment(Pos.CENTER_LEFT);
            Label lblClienteTitulo = new Label("Cliente:");
            lblClienteTitulo.getStyleClass().add("venta-label-titulo");
            String nombreCliente = "N/A";
            if (venta.getCliente() != null) {
                nombreCliente = venta.getCliente().getNombresCliente() + " " + venta.getCliente().getApellidosCliente();
            }
            Label lblCliente = new Label(nombreCliente);
            lblCliente.getStyleClass().add("venta-label-valor");
            clienteBox.getChildren().addAll(lblClienteTitulo, lblCliente);
            
            HBox empleadoBox = new HBox(8);
            empleadoBox.setAlignment(Pos.CENTER_LEFT);
            Label lblEmpleadoTitulo = new Label("Empleado:");
            lblEmpleadoTitulo.getStyleClass().add("venta-label-titulo");
            String nombreEmpleado = "N/A";
            if (venta.getEmpleado() != null) {
                nombreEmpleado = venta.getEmpleado().getNombresEmpleado() + " " + venta.getEmpleado().getApellidosEmpleado();
            }
            Label lblEmpleado = new Label(nombreEmpleado);
            lblEmpleado.getStyleClass().add("venta-label-valor");
            empleadoBox.getChildren().addAll(lblEmpleadoTitulo, lblEmpleado);
            
            infoVenta.getChildren().addAll(clienteBox, empleadoBox);
            
            VBox infoDetalles = new VBox(8);
            infoDetalles.setAlignment(Pos.CENTER_LEFT);
            infoDetalles.setPrefWidth(200);
            
            HBox fechaBox = new HBox(8);
            fechaBox.setAlignment(Pos.CENTER_LEFT);
            Label lblFechaTitulo = new Label("Fecha:");
            lblFechaTitulo.getStyleClass().add("venta-label-titulo");
            String fechaTexto = venta.getFechaVenta() != null 
                ? venta.getFechaVenta().toString().substring(0, 10) 
                : "N/A";
            Label lblFecha = new Label(fechaTexto);
            lblFecha.getStyleClass().add("venta-label-valor");
            fechaBox.getChildren().addAll(lblFechaTitulo, lblFecha);
            
            HBox totalBox = new HBox(8);
            totalBox.setAlignment(Pos.CENTER_LEFT);
            Label lblTotalTitulo = new Label("Total:");
            lblTotalTitulo.getStyleClass().add("venta-label-titulo");
            String totalTexto = venta.getTotal() != null 
                ? "$" + venta.getTotal().toString() 
                : "$0.00";
            Label lblTotal = new Label(totalTexto);
            lblTotal.getStyleClass().add("venta-label-total");
            totalBox.getChildren().addAll(lblTotalTitulo, lblTotal);
            
            infoDetalles.getChildren().addAll(fechaBox, totalBox);
            
            HBox contenedorBoton = new HBox();
            contenedorBoton.setAlignment(Pos.CENTER_RIGHT);
            
            Button btnVerDetalles = new Button("Ver detalles");
            btnVerDetalles.getStyleClass().add("btn-ver-detalles");
            btnVerDetalles.setOnAction(e -> cargarDetallesDeVenta(venta));

            Button btnEliminarVenta = new Button("Eliminar");
            btnEliminarVenta.setOnAction( e -> {setVentaParaEliminar(venta); manejarModalEliminarVentas();});
            
            Button btnEditarVenta = new Button("Editar");
            btnEditarVenta.setOnAction( e -> {setVentaParaEditar(venta); manejarModalEditarVentas();});

            contenedorBoton.getChildren().add(btnEliminarVenta);
            contenedorBoton.getChildren().add(btnVerDetalles);
            

            tarjeta.getChildren().addAll(infoVenta, infoDetalles, contenedorBoton);
            contenedorCentrado.getChildren().add(tarjeta);
            contenedorVentas.getChildren().add(contenedorCentrado);
        }
    }

    private void mostrarDetalles(VentaDto venta){
        contenedorProductosVendidos.getChildren().clear();
        
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            Label lblVacio = new Label("No hay productos en esta venta");
            lblVacio.setStyle("-fx-font-size: 13px; -fx-text-fill: #657786; -fx-padding: 20; -fx-alignment: center;");
            contenedorProductosVendidos.getChildren().add(lblVacio);
            return;
        }
        
        HBox header = new HBox(10);
        header.getStyleClass().add("detalle-header");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label lblHeaderProducto = new Label("Producto");
        lblHeaderProducto.getStyleClass().add("detalle-header-text");
        lblHeaderProducto.setPrefWidth(200);
        HBox.setHgrow(lblHeaderProducto, javafx.scene.layout.Priority.ALWAYS);
        
        Label lblHeaderCategoria = new Label("Categoría");
        lblHeaderCategoria.getStyleClass().add("detalle-header-text");
        lblHeaderCategoria.setPrefWidth(150);
        
        Label lblHeaderPrecio = new Label("Precio/Unidad");
        lblHeaderPrecio.getStyleClass().add("detalle-header-text");
        lblHeaderPrecio.setPrefWidth(120);
        lblHeaderPrecio.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblHeaderSubtotal = new Label("Subtotal");
        lblHeaderSubtotal.getStyleClass().add("detalle-header-text");
        lblHeaderSubtotal.setPrefWidth(100);
        lblHeaderSubtotal.setAlignment(Pos.CENTER_RIGHT);
        
        header.getChildren().addAll(lblHeaderProducto, lblHeaderCategoria, lblHeaderPrecio, lblHeaderSubtotal);
        contenedorProductosVendidos.getChildren().add(header);
        
        for (DetalleVentasDto detalle : venta.getDetalles()) {
            HBox fila = new HBox(10);
            fila.getStyleClass().add("detalle-fila");
            fila.setAlignment(Pos.CENTER_LEFT);
            
            MuebleDto muebleDto = detalle.getMueble();
            String nombreProducto = (muebleDto != null && muebleDto.getNombreMueble() != null) 
                ? muebleDto.getNombreMueble() 
                : "Producto desconocido";
            String nombreCategoria = (muebleDto != null && muebleDto.getCategoria() != null) 
                ? muebleDto.getCategoria().getCategoria() 
                : "Sin categoría";
            
            Label lblProducto = new Label(nombreProducto);
            lblProducto.getStyleClass().add("detalle-producto-nombre");
            lblProducto.setPrefWidth(200);
            HBox.setHgrow(lblProducto, javafx.scene.layout.Priority.ALWAYS);
            
            Label lblCategoria = new Label(nombreCategoria);
            lblCategoria.getStyleClass().add("detalle-categoria");
            lblCategoria.setPrefWidth(150);
            
            String precioTexto = detalle.getPrecio() != null ? "$" + detalle.getPrecio().toString() : "$0.00";
            Label lblPrecio = new Label(precioTexto);
            lblPrecio.getStyleClass().add("detalle-precio");
            lblPrecio.setPrefWidth(120);
            lblPrecio.setAlignment(Pos.CENTER_RIGHT);
            
            String subtotalTexto = detalle.getSubTotal() != null ? "$" + detalle.getSubTotal().toString() : "$0.00";
            Label lblSubtotal = new Label(subtotalTexto);
            lblSubtotal.getStyleClass().add("detalle-subtotal");
            lblSubtotal.setPrefWidth(100);
            lblSubtotal.setAlignment(Pos.CENTER_RIGHT);
            
            fila.getChildren().addAll(lblProducto, lblCategoria, lblPrecio, lblSubtotal);
            contenedorProductosVendidos.getChildren().add(fila);
        }
    }

    private void sincronizarDetalles() {
        listaDetalles.clear();
        detallesPorMueble.values().stream()
            .filter(d -> d.getCantidad() != null && d.getCantidad() > 0)
            .forEach(listaDetalles::add);
    }

    private void limpiarFormularioVenta() {
        detallesPorMueble.clear();
        listaDetalles.clear();
        comboClientes.setValue(null);
        comboEmpleados.setValue(null);
        contenedorMuebles.getChildren().clear();
        cargarMuebles();
    }

    public void registrarVenta(){
        sincronizarDetalles();
        if(listaDetalles.isEmpty()){
            lblMsgRegistroVenta.getStyleClass().removeAll("error", "exito");
            lblMsgRegistroVenta.getStyleClass().add("error");
            lblMsgRegistroVenta.setText("Debe seleccionar almenos un producto");
            FadeUpAnimation.play(lblMsgRegistroVenta);
        }else if(comboClientes.getValue() == null){
            lblMsgRegistroVenta.getStyleClass().removeAll("error", "exito");
            lblMsgRegistroVenta.getStyleClass().add("error");
            lblMsgRegistroVenta.setText("Debe seleccionar un cliente");
            FadeUpAnimation.play(lblMsgRegistroVenta);
        }else if(comboEmpleados.getValue() == null){
            lblMsgRegistroVenta.getStyleClass().removeAll("error", "exito");
            lblMsgRegistroVenta.getStyleClass().add("error");
            lblMsgRegistroVenta.setText("Debe seleccionar un empleado");
            FadeUpAnimation.play(lblMsgRegistroVenta);
        }else{
            VentaDto ventaObj = new VentaDto();
            ventaObj.setDetalles(listaDetalles);
            ventaObj.setClienteId(comboClientes.getValue().getIdCliente());
            ventaObj.setEmpleadoId(comboEmpleados.getValue().getIdEmpleado());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    ventaService.insertarVenta(ventaObj);
                    return null;
                }
            };
            task.setOnSucceeded(event -> {
                lblMsgRegistroVenta.getStyleClass().removeAll("error", "exito");
                lblMsgRegistroVenta.getStyleClass().add("exito");
                lblMsgRegistroVenta.setText("Venta registrada con éxito");
                FadeUpAnimation.play(lblMsgRegistroVenta);
                cargarVentas();
                limpiarFormularioVenta();
            });
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
            });
            new Thread(task).start();
        }
    }

    public void eliminarVenta(){
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ventaService.eliminarVenta(ventaParaEliminar.getIdVenta());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            cargarVentas();
            manejarModalEliminarVentas();
        });
        task.setOnFailed( e -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    public void setVentaParaEliminar(VentaDto ventaParaEliminar){
        this.ventaParaEliminar = ventaParaEliminar;
    }
    public void setVentaParaEditar(VentaDto ventaParaEditar){
        this.ventaParaEditar = ventaParaEditar;
    }

    @FXML
    private void manejarModalRegistrarVentas(){
        if(overlayRegistrarVentas.isVisible()){
            overlayRegistrarVentas.setVisible(false);
            FadeDownAnimation.play(lblMsgRegistroVenta);
        }else{
            overlayRegistrarVentas.setVisible(true);
            cargarMuebles();
            cargarClientes();
            cargarEmpleados();
        }
    }

    @FXML
    public void manejarModalEliminarVentas(){
        if(overlayEliminarVentas.isVisible()){
            overlayEliminarVentas.setVisible(false);
        }else{
            lblEliminarVenta.setText("Eliminar venta");
            overlayEliminarVentas.setVisible(true);
        }
    }

    @FXML
    public void manejarModalEditarVentas(){
        if(overlayEditarVentas.isVisible()){
            overlayEditarVentas.setVisible(false);
        }else{
            overlayEditarVentas.setVisible(true);
        }
    }

    @FXML
    private void manejarModalVerDetalles(){
        overlayDetalleVentas.setVisible(!overlayDetalleVentas.isVisible());
    }

    @FXML
    private void cancelarClickEnModal(MouseEvent event) {
        event.consume();
    }

    @FXML
    private void ocultarMensajeRegistroVenta(){
        if(lblMsgRegistroVenta.getOpacity() != 0){
            FadeDownAnimation.play(lblMsgRegistroVenta);
            lblMsgRegistroVenta.setOpacity(0);
        }
    }

    @FXML
    private void cerrarVistaVentas() {
        Stage stage = (Stage) contenedorVentas.getScene().getWindow();
        stage.close();
        DashboardController.showDashboard(user);
    }
}
