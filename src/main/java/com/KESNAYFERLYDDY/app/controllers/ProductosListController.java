package com.KESNAYFERLYDDY.app.controllers;

import java.util.List;

import com.KESNAYFERLYDDY.app.models.CategoriaDto;
import com.KESNAYFERLYDDY.app.models.MuebleDto;
import com.KESNAYFERLYDDY.app.services.MuebleService;
import com.KESNAYFERLYDDY.app.services.CategoriaService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ProductosListController {

    @FXML private VBox overlayRegistrarMuebles;
    @FXML private VBox overlayEditarMuebles;
    @FXML private VBox overlayRegistrarCategorias;
    @FXML private VBox overlayEliminar;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtExistencias;
    @FXML private TextField txtMaterial;
    @FXML private ComboBox<CategoriaDto> comboCategoria;

    @FXML private TextField txtNombreCategoria;

    @FXML private TextField txtNombreEditar;
    @FXML private TextField txtDescripcionEditar;
    @FXML private TextField txtExistenciasEditar;
    @FXML private TextField txtMaterialEditar;
    @FXML private ComboBox<CategoriaDto> comboCategoriaEditar;

    @FXML private Label lblEliminar;
    @FXML private VBox contenedorMuebles;

    private final MuebleService mueble = new MuebleService();
    private final CategoriaService categoria = new CategoriaService();
    private static String user = "";
    private MuebleDto muebleParaEliminar;
    private MuebleDto muebleParaEditar;

    public static void show(String username) {
        try {
            user = username;
            FXMLLoader loader = new FXMLLoader(ProductosListController.class.getResource("/fxml/productos_list.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Productos - " + username);
            stage.setMaximized(true);
            ProductosListController controladorProductos = loader.getController();
            controladorProductos.cargarMuebles();
            controladorProductos.cargarCategorias();
            stage.show();
        } catch (Exception error) { error.printStackTrace(); }
    }

    public void cargarMuebles(){
        Task<List<MuebleDto>> task = new Task<>() {
            @Override protected List<MuebleDto> call() throws Exception {
                return mueble.listarMuebles();
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

    public void cargarCategorias(){
        Task<List<CategoriaDto>> task = new Task<>() {
            @Override protected List<CategoriaDto> call() throws Exception {
                return categoria.listarCategorias();
            }
        };
        task.setOnSucceeded(event -> {
            List<CategoriaDto> categorias = task.getValue();
            comboCategoria.getItems().clear();
            comboCategoria.getItems().addAll(categorias);
            comboCategoriaEditar.getItems().clear();
            comboCategoriaEditar.getItems().addAll(categorias);
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void mostrarMuebles(List<MuebleDto> muebles) {
        // Limpiar el contenedor y crear un FlowPane para el grid
        contenedorMuebles.getChildren().clear();
        
        FlowPane gridMuebles = new FlowPane();
        gridMuebles.setHgap(16);  // Espaciado horizontal
        gridMuebles.setVgap(16);  // Espaciado vertical
        gridMuebles.setPrefWrapLength(1200); // Ancho aproximado para 4 columnas
        gridMuebles.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        gridMuebles.setStyle("-fx-padding: 16;");
        
        for (MuebleDto mueble : muebles) {
            // Contenedor principal de la tarjeta
            VBox tarjeta = new VBox();
            tarjeta.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-spacing: 12;");
            tarjeta.setPrefWidth(250);
            tarjeta.setMaxWidth(250);
            
            // Nombre del mueble
            Label nombre = new Label(mueble.getNombreMueble());
            nombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");
            
            // Descripción
            Label descripcion = new Label(mueble.getDescripcionMueble());
            descripcion.setStyle("-fx-font-size: 12px; -fx-text-fill: #495057; -fx-wrap-text: true;");
            descripcion.setWrapText(true);
            
            // Contenedor de información (2 columnas)
            HBox infoRow1 = new HBox(12);
            infoRow1.setStyle("-fx-spacing: 12;");
            
            VBox infoCategoria = new VBox(4);
            Label labelCategoria = new Label("Categoría");
            labelCategoria.setStyle("-fx-font-size: 11px; -fx-text-fill: #868e96;");
            Label valorCategoria = new Label(mueble.getCategoria() != null ? mueble.getCategoria().getCategoria() : "N/A");
            valorCategoria.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d3436;");
            infoCategoria.getChildren().addAll(labelCategoria, valorCategoria);
            
            VBox infoExistencia = new VBox(4);
            Label labelExistencia = new Label("Existencias");
            labelExistencia.setStyle("-fx-font-size: 11px; -fx-text-fill: #868e96;");
            Label valorExistencia = new Label(String.valueOf(mueble.getExistencia()));
            valorExistencia.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d3436;");
            infoExistencia.getChildren().addAll(labelExistencia, valorExistencia);
            
            infoRow1.getChildren().addAll(infoCategoria, infoExistencia);
            
            // Segunda fila de información
            HBox infoRow2 = new HBox(12);
            infoRow2.setStyle("-fx-spacing: 12;");
            
            VBox infoMaterial = new VBox(4);
            Label labelMaterial = new Label("Material");
            labelMaterial.setStyle("-fx-font-size: 11px; -fx-text-fill: #868e96;");
            Label valorMaterial = new Label(mueble.getNombreMaterial());
            valorMaterial.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d3436;");
            infoMaterial.getChildren().addAll(labelMaterial, valorMaterial);
            
            VBox infoEstado = new VBox(4);
            Label labelEstado = new Label("Estado");
            labelEstado.setStyle("-fx-font-size: 11px; -fx-text-fill: #868e96;");
            Label valorEstado = new Label(mueble.getEstado());
            valorEstado.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (mueble.getEstado().equals("En stock") ? "#51cf66" : "#ff6b6b") + ";");
            infoEstado.getChildren().addAll(labelEstado, valorEstado);
            
            infoRow2.getChildren().addAll(infoMaterial, infoEstado);
            
            // Botones de acción
            HBox botonesAccion = new HBox(8);
            botonesAccion.setStyle("-fx-spacing: 8; -fx-padding: 8 0 0 0;");
            
            Button btnEditar = new Button("Editar");
            btnEditar.setStyle("-fx-background-color: #4c6ef5; -fx-text-fill: white; -fx-padding: 6 16; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 12px; -fx-cursor: hand;");
            btnEditar.setOnAction(event -> {setMuebleParaEditar(mueble); manejarModalEditarMuebles();});

            Button btnEliminar = new Button("Eliminar");
            btnEliminar.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 6 16; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 12px; -fx-cursor: hand;");
            btnEliminar.setOnAction(event -> {setMuebleParaEliminar(mueble); manejarModalEliminar();});
            botonesAccion.getChildren().addAll(btnEditar, btnEliminar);
            
            // Agregar todos los elementos a la tarjeta
            tarjeta.getChildren().addAll(nombre, descripcion, infoRow1, infoRow2, botonesAccion);
            
            // Agregar la tarjeta al grid
            gridMuebles.getChildren().add(tarjeta);
        }
        
        // Agregar el grid al contenedor principal
        contenedorMuebles.getChildren().add(gridMuebles);
    }

    public void registrarMueble() {
        if(
            txtNombre.getText().isEmpty() ||
            txtDescripcion.getText().isEmpty() ||
            txtExistencias.getText().isEmpty() ||
            txtMaterial.getText().isEmpty() ||
            comboCategoria.getValue() == null
        ){
           
        }else{
            MuebleDto muebleObj = new MuebleDto();
            muebleObj.setNombreMueble(txtNombre.getText());
            muebleObj.setDescripcionMueble(txtDescripcion.getText());
            muebleObj.setEstado(obtenerEstadoProducto());
            muebleObj.setExistencia(Integer.parseInt(txtExistencias.getText()));
            muebleObj.setCategoria(comboCategoria.getValue());
            muebleObj.setNombreMaterial(txtMaterial.getText());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    mueble.insertarMueble(muebleObj);
                    return null;
                }
            };
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                cargarMuebles();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                Platform.runLater(() -> {
                    
                });
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    public void editarMueble() {
        if(
            txtNombreEditar.getText().isEmpty() ||
            txtDescripcionEditar.getText().isEmpty() ||
            txtExistenciasEditar.getText().isEmpty() ||
            txtMaterialEditar.getText().isEmpty()
        ){
            
        }else if(
            txtNombreEditar.getText().equals(muebleParaEditar.getNombreMueble()) &&
            txtDescripcionEditar.getText().equals(muebleParaEditar.getDescripcionMueble()) &&
            txtExistenciasEditar.getText().equals(String.valueOf(muebleParaEditar.getExistencia())) &&
            txtMaterialEditar.getText().equals(muebleParaEditar.getNombreMaterial()) &&
            comboCategoriaEditar.getValue().equals(muebleParaEditar.getCategoria())
        ){

        }else{
            MuebleDto muebleObj = new MuebleDto();
            muebleObj.setIdMueble(muebleParaEditar.getIdMueble());
            muebleObj.setNombreMueble(txtNombreEditar.getText());
            muebleObj.setDescripcionMueble(txtDescripcionEditar.getText());
            muebleObj.setEstado(muebleParaEditar.getEstado());
            muebleObj.setExistencia(Integer.parseInt(txtExistenciasEditar.getText()));
            muebleObj.setNombreMaterial(txtMaterialEditar.getText());
            muebleObj.setCategoria(comboCategoriaEditar.getValue());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    mueble.editarMueble(muebleObj);
                    return null;
                }
            };
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                this.muebleParaEditar = muebleObj;
                cargarMuebles();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();

                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    public void eliminarMueble(){
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                mueble.eliminarMueble(muebleParaEliminar.getIdMueble());
                return null;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            manejarModalEliminar();
            cargarMuebles();
        }));
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    public void registrarCategoria() {
        if(
            txtNombreCategoria.getText().isEmpty()
        ){

        }else{
            CategoriaDto categoriaObj = new CategoriaDto();
            categoriaObj.setCategoria(txtNombreCategoria.getText());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    categoria.insertarCategoria(categoriaObj);
                    return null;
                }
            };
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                cargarCategorias();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                Platform.runLater(() -> {
                    
                });
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }
    
    private String obtenerEstadoProducto(){
        if(Integer.parseInt(txtExistencias.getText()) > 0){
            return "En stock";
        }else{
            return "Sin stock";
        }
    }

    public void setMuebleParaEliminar(MuebleDto muebleParaEliminar){
        this.muebleParaEliminar = muebleParaEliminar;
    }
    public void setMuebleParaEditar(MuebleDto muebleParaEditar){
        this.muebleParaEditar = muebleParaEditar;
    }

    @FXML
    private void cerrarVistaProductos() {
        Stage stage = (Stage) contenedorMuebles.getScene().getWindow();
        stage.close();
        DashboardController.showDashboard(user);
    }
    
    @FXML
    private void cancelarClickEnModal(MouseEvent event) {
         event.consume();
    }

    @FXML
    private void manejarModalRegistrarMuebles(){
        if(overlayRegistrarMuebles.isVisible()){
            overlayRegistrarMuebles.setVisible(false);
        }else{
            overlayRegistrarMuebles.setVisible(true);
        }
    }

    @FXML
    private void manejarModalRegistrarCategorias(){
        if(overlayRegistrarCategorias.isVisible()){
            overlayRegistrarCategorias.setVisible(false);
        }else{
            overlayRegistrarCategorias.setVisible(true);
        }
    }
    @FXML
    public void manejarModalEliminar(){
        if(overlayEliminar.isVisible()){
            overlayEliminar.setVisible(false);
        }else{
            lblEliminar.setText("Eliminar: " + muebleParaEliminar.getNombreMueble());
            overlayEliminar.setVisible(true);
        }
    }
    @FXML
    public void manejarModalEditarMuebles(){
        if(overlayEditarMuebles.isVisible()){
            overlayEditarMuebles.setVisible(false);
        }else{
            txtNombreEditar.setText(muebleParaEditar.getNombreMueble());
            txtDescripcionEditar.setText(muebleParaEditar.getDescripcionMueble());
            txtExistenciasEditar.setText(String.valueOf(muebleParaEditar.getExistencia()));
            txtMaterialEditar.setText(muebleParaEditar.getNombreMaterial());
            comboCategoriaEditar.setValue(muebleParaEditar.getCategoria());
            overlayEditarMuebles.setVisible(true);
        }
    }
}
