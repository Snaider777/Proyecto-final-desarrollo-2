package com.KESNAYFERLYDDY.app.controllers;

import java.util.ArrayList;
import java.util.List;

import com.KESNAYFERLYDDY.app.animations.FadeUpAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeInLeftAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeOutRightAnimation;
import com.KESNAYFERLYDDY.app.models.CategoriaDto;
import com.KESNAYFERLYDDY.app.models.MuebleDto;
import com.KESNAYFERLYDDY.app.services.MuebleService;
import com.KESNAYFERLYDDY.app.services.CategoriaService;

import com.KESNAYFERLYDDY.app.animations.FadeDownAnimation;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;

public class ProductosListController {

    @FXML private VBox overlayRegistrarMuebles;
    @FXML private VBox overlayEditarMuebles;
    @FXML private VBox overlayEliminar;
    @FXML private VBox overlayRegistrarCategorias;
    @FXML private VBox overlayEditarCategorias;
    @FXML private VBox overlayEliminarCategorias;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtExistencias;
    @FXML private TextField txtMaterial;
    @FXML private ComboBox<CategoriaDto> comboCategoria;

    @FXML private TextField txtNombreCategoria;
    @FXML private TextField txtNombreCategoriaEditar;

    @FXML private Label lblMsgRegistroMueble;
    @FXML private Label lblMsgEdicionMueble;
    @FXML private Label lblEliminar;
    @FXML private Label lblMsgRegistroCategoria;
    @FXML private Label lblMsgEdicionCategoria;
    @FXML private Label lblEliminarCategoria;
    @FXML private Label lblSinCategorias;

    @FXML private TextField txtNombreEditar;
    @FXML private TextField txtDescripcionEditar;
    @FXML private TextField txtExistenciasEditar;
    @FXML private TextField txtMaterialEditar;
    @FXML private ComboBox<CategoriaDto> comboCategoriaEditar;

    @FXML private VBox contenedorMuebles;
    @FXML private VBox contenedorCategorias;

    private final MuebleService mueble = new MuebleService();
    private final CategoriaService categoria = new CategoriaService();
    private static String user = "";
    private static List<String> listaCategoriasUsadas = new ArrayList<>();
    private MuebleDto muebleParaEliminar;
    private MuebleDto muebleParaEditar;
    private CategoriaDto categoriaParaEditar;
    private CategoriaDto categoriaParaEliminar;
    private Boolean listaDeCategoriasVacia;

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
            setListaDeCategoriasUsadas(muebles);
            mostrarMuebles(muebles);
            cargarCategorias();
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
            listaDeCategoriasVacia = categorias.isEmpty();
            mostrarCategorias(categorias);
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }
    
    private void mostrarCategorias(List<CategoriaDto> categorias) {
        contenedorCategorias.getChildren().clear();
        
        Label titulo = new Label("Categorías");
        titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2d3436; -fx-padding: 0 0 12 0;");
        
        VBox contenidoCategorias = new VBox(8);
        contenidoCategorias.setStyle("-fx-spacing: 8; -fx-padding: 0;");
        
        for (CategoriaDto cat : categorias) {
            HBox tarjetaCategoria = new HBox();
            tarjetaCategoria.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 12; -fx-spacing: 8;");
            tarjetaCategoria.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label nombreCategoria = new Label(cat.getCategoria());
            nombreCategoria.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2d3436; -fx-wrap-text: true;");
            nombreCategoria.setWrapText(true);
            
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            
            HBox botonesAccion = new HBox(8);
            botonesAccion.setStyle("-fx-spacing: 8;");
            botonesAccion.setAlignment(javafx.geometry.Pos.CENTER);
            
            try {
                Image imgEditar = new Image(ProductosListController.class.getResourceAsStream("/images/editar.png"));
                ImageView iconEditar = new ImageView(imgEditar);
                iconEditar.setFitWidth(20);
                iconEditar.setFitHeight(20);
                iconEditar.setPickOnBounds(true);
                iconEditar.setCursor(Cursor.HAND);
                iconEditar.setOnMouseClicked(event -> { setCategoriaParaEditar(cat); manejarModalEditarCategorias(); });
                botonesAccion.getChildren().add(iconEditar);
            } catch (Exception e) {
                System.err.println("Error cargando imagen editar: " + e.getMessage());
            }

            if(listaCategoriasUsadas.contains(cat.getCategoria())){
                try {
                    Image imgEliminar = new Image(ProductosListController.class.getResourceAsStream("/images/borrar_negado.png"));
                    ImageView iconEliminar = new ImageView(imgEliminar);
                    iconEliminar.setFitWidth(20);
                    iconEliminar.setFitHeight(20);
                    botonesAccion.getChildren().add(iconEliminar);
                } catch (Exception e) {
                    System.err.println("Error cargando imagen borrar: " + e.getMessage());
                }
            }else{
                try {
                    Image imgEliminar = new Image(ProductosListController.class.getResourceAsStream("/images/borrar.png"));
                    ImageView iconEliminar = new ImageView(imgEliminar);
                    iconEliminar.setFitWidth(20);
                    iconEliminar.setFitHeight(20);
                    iconEliminar.setPickOnBounds(true);
                    iconEliminar.setCursor(Cursor.HAND);
                    iconEliminar.setOnMouseClicked(event -> { setCategoriaParaEliminar(cat); manejarModalEliminarCategorias(); });
                    botonesAccion.getChildren().add(iconEliminar);
                } catch (Exception e) {
                    System.err.println("Error cargando imagen borrar: " + e.getMessage());
                }
            }
            
            tarjetaCategoria.getChildren().addAll(nombreCategoria, spacer, botonesAccion);
            contenidoCategorias.getChildren().add(tarjetaCategoria);
        }
        
        contenedorCategorias.getChildren().addAll(titulo, contenidoCategorias);
    }

    private void mostrarMuebles(List<MuebleDto> muebles) {
        contenedorMuebles.getChildren().clear();
        
        FlowPane gridMuebles = new FlowPane();
        gridMuebles.setHgap(16);
        gridMuebles.setVgap(16);
        gridMuebles.setPrefWrapLength(1200);
        gridMuebles.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        gridMuebles.setStyle("-fx-padding: 16;");
        
        for (MuebleDto mueble : muebles) {
            VBox tarjeta = new VBox();
            tarjeta.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-spacing: 12;");
            tarjeta.setPrefWidth(250);
            tarjeta.setMaxWidth(250);
            
            Label nombre = new Label(mueble.getNombreMueble());
            nombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");
            
            Label descripcion = new Label(mueble.getDescripcionMueble());
            descripcion.setStyle("-fx-font-size: 12px; -fx-text-fill: #495057; -fx-wrap-text: true;");
            descripcion.setWrapText(true);
            
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
            
            HBox botonesAccion = new HBox(8);
            botonesAccion.setStyle("-fx-spacing: 8; -fx-padding: 8 0 0 0;");
            
            Button btnEditar = new Button("Editar");
            btnEditar.setStyle("-fx-background-color: #4c6ef5; -fx-text-fill: white; -fx-padding: 6 16; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 12px; -fx-cursor: hand;");
            btnEditar.setOnAction(event -> {setMuebleParaEditar(mueble); manejarModalEditarMuebles();});

            Button btnEliminar = new Button("Eliminar");
            btnEliminar.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 6 16; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 12px; -fx-cursor: hand;");
            btnEliminar.setOnAction(event -> {setMuebleParaEliminar(mueble); manejarModalEliminar();});
            botonesAccion.getChildren().addAll(btnEditar, btnEliminar);
            
            tarjeta.getChildren().addAll(nombre, descripcion, infoRow1, infoRow2, botonesAccion);
            
            gridMuebles.getChildren().add(tarjeta);
        }
        
        contenedorMuebles.getChildren().add(gridMuebles);
    }

    public void registrarMueble() {
        Boolean existenciasValidas = verificarExistenciasIngresadas(txtExistencias);
        if(
            txtNombre.getText().isEmpty() ||
            txtDescripcion.getText().isEmpty() ||
            txtExistencias.getText().isEmpty() ||
            txtMaterial.getText().isEmpty() ||
            comboCategoria.getValue() == null
        ){
            lblMsgRegistroMueble.getStyleClass().removeAll("exito");
            lblMsgRegistroMueble.getStyleClass().add("error");
            lblMsgRegistroMueble.setText("Complete todos los campos");
            FadeUpAnimation.play(lblMsgRegistroMueble);
        }else if(!existenciasValidas){
            lblMsgRegistroMueble.getStyleClass().remove("exito");
            lblMsgRegistroMueble.getStyleClass().add("error");
            lblMsgRegistroMueble.setText("El valor de las existencias no es válido");
            txtExistencias.getStyleClass().add("textoIngresadoInvalido");
            FadeUpAnimation.play(lblMsgRegistroMueble);
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
                lblMsgRegistroMueble.getStyleClass().removeAll("error");
                lblMsgRegistroMueble.getStyleClass().add("exito");
                lblMsgRegistroMueble.setText("Mueble registrado con exito");
                FadeUpAnimation.play(lblMsgRegistroMueble);
                txtDescripcion.setText("");
                txtNombre.setText("");
                txtExistencias.setText("");
                txtMaterial.setText("");
                comboCategoria.setValue(null);
                cargarMuebles();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                lblMsgRegistroMueble.getStyleClass().remove("exito");
                lblMsgRegistroMueble.getStyleClass().add("error");
                lblMsgRegistroMueble.setText("Error al insertar el mueble");
                FadeUpAnimation.play(lblMsgRegistroMueble);
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    public void editarMueble() {
        Boolean existenciasValidas = verificarExistenciasIngresadas(txtExistenciasEditar);
        if(
            txtNombreEditar.getText().isEmpty() ||
            txtDescripcionEditar.getText().isEmpty() ||
            txtExistenciasEditar.getText().isEmpty() ||
            txtMaterialEditar.getText().isEmpty()
        ){
            lblMsgEdicionMueble.getStyleClass().removeAll("exito", "advertencia", "error");
            lblMsgEdicionMueble.getStyleClass().add("error");
            lblMsgEdicionMueble.setText("Complete todos los campos");
            FadeUpAnimation.play(lblMsgEdicionMueble);
        }else if(
            txtNombreEditar.getText().equals(muebleParaEditar.getNombreMueble()) &&
            txtDescripcionEditar.getText().equals(muebleParaEditar.getDescripcionMueble()) &&
            txtExistenciasEditar.getText().equals(String.valueOf(muebleParaEditar.getExistencia())) &&
            txtMaterialEditar.getText().equals(muebleParaEditar.getNombreMaterial()) &&
            comboCategoriaEditar.getValue().equals(muebleParaEditar.getCategoria())
        ){
            lblMsgEdicionMueble.getStyleClass().removeAll("exito", "error", "advertencia");
            lblMsgEdicionMueble.getStyleClass().add("advertencia");
            lblMsgEdicionMueble.setText("Ningún campo ha sido modificado");
            FadeUpAnimation.play(lblMsgEdicionMueble);
        }else if(!existenciasValidas){
            lblMsgEdicionMueble.getStyleClass().removeAll("exito","error", "advertencia" );
            lblMsgEdicionMueble.getStyleClass().add("error");
            lblMsgEdicionMueble.setText("El valor de las existencias no es válido");
            txtExistenciasEditar.getStyleClass().add("textoIngresadoInvalido");
            FadeUpAnimation.play(lblMsgEdicionMueble);
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
                lblMsgEdicionMueble.getStyleClass().removeAll("exito", "error", "advertencia");
                lblMsgEdicionMueble.getStyleClass().add("exito");
                lblMsgEdicionMueble.setText("Producto actualizado exitosamente");
                FadeUpAnimation.play(lblMsgEdicionMueble);
                this.muebleParaEditar = muebleObj;
                cargarMuebles();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                lblMsgEdicionMueble.getStyleClass().removeAll("exito", "error", "advertencia");
                lblMsgEdicionMueble.getStyleClass().add("error");
                lblMsgEdicionMueble.setText("Error al editar el producto");
                FadeUpAnimation.play(lblMsgEdicionMueble);
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
            lblMsgRegistroCategoria.getStyleClass().removeAll("exito");
            lblMsgRegistroCategoria.getStyleClass().add("error");
            lblMsgRegistroCategoria.setText("Complete el campo solicitado");
            FadeUpAnimation.play(lblMsgRegistroCategoria);
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
                lblMsgRegistroCategoria.getStyleClass().removeAll("error");
                lblMsgRegistroCategoria.getStyleClass().add("exito");
                lblMsgRegistroCategoria.setText("Categoria registrada con exito");
                txtNombreCategoria.setText("");
                FadeUpAnimation.play(lblMsgRegistroCategoria);
            }));
            task.setOnFailed(event -> {
                Platform.runLater(() -> {
                task.getException().printStackTrace();
                lblMsgRegistroCategoria.getStyleClass().removeAll("exito", "error", "advertencia");
                lblMsgRegistroCategoria.getStyleClass().add("error");
                lblMsgRegistroCategoria.setText("Error al registrar la categoria");
                FadeUpAnimation.play(lblMsgRegistroCategoria);
                });
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    public void editarCategoria() {
        if(txtNombreCategoriaEditar.getText().isEmpty()){
            lblMsgEdicionCategoria.getStyleClass().removeAll("exito", "advertencia", "error");
            lblMsgEdicionCategoria.getStyleClass().add("error");
            lblMsgEdicionCategoria.setText("Complete el campo solicitado");
            FadeUpAnimation.play(lblMsgEdicionCategoria);
        }else if(txtNombreCategoriaEditar.getText().equals(categoriaParaEditar.getCategoria())){
            lblMsgEdicionCategoria.getStyleClass().removeAll("exito", "error", "advertencia");
            lblMsgEdicionCategoria.getStyleClass().add("advertencia");
            lblMsgEdicionCategoria.setText("El campo no ha sido modificado");
            FadeUpAnimation.play(lblMsgEdicionCategoria);
        }else{
            CategoriaDto categoriaObj = new CategoriaDto();
            categoriaObj.setIdCategoria(categoriaParaEditar.getIdCategoria());
            categoriaObj.setCategoria(txtNombreCategoriaEditar.getText());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    categoria.editarCategoria(categoriaObj);
                    return null;
                }
            };
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                lblMsgEdicionCategoria.getStyleClass().removeAll("exito", "error", "advertencia");
                lblMsgEdicionCategoria.getStyleClass().add("exito");
                lblMsgEdicionCategoria.setText("Categoria actualizada exitosamente");
                FadeUpAnimation.play(lblMsgEdicionCategoria);
                this.categoriaParaEditar = categoriaObj;
                cargarCategorias();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                lblMsgEdicionCategoria.getStyleClass().removeAll("exito", "error", "advertencia");
                lblMsgEdicionCategoria.getStyleClass().add("error");
                lblMsgEdicionCategoria.setText("Error al editar la categoria");
                FadeUpAnimation.play(lblMsgEdicionCategoria);
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    public void eliminarCategoria(){
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                categoria.eliminarCategoria(categoriaParaEliminar.getIdCategoria());
                return null;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            manejarModalEliminarCategorias();
            cargarCategorias();
        }));
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }
    
    private String obtenerEstadoProducto(){
        if(Integer.parseInt(txtExistencias.getText()) > 0){
            return "En stock";
        }else{
            return "Sin stock";
        }
    }
    private boolean verificarExistenciasIngresadas(TextField nodo){
            String existenciasIngresadas = nodo.getText();
            try {
                int numero = Integer.parseInt(existenciasIngresadas);
                System.out.println("Numero valido para existencias: " + numero);
                return true;
            } catch (NumberFormatException error) {
                return false;
            }
        }

    public void setMuebleParaEliminar(MuebleDto muebleParaEliminar){
        this.muebleParaEliminar = muebleParaEliminar;
    }
    public void setMuebleParaEditar(MuebleDto muebleParaEditar){
        this.muebleParaEditar = muebleParaEditar;
    }
    public void setCategoriaParaEliminar(CategoriaDto categoriaParaEliminar){
        this.categoriaParaEliminar = categoriaParaEliminar;
    }
    public void setCategoriaParaEditar(CategoriaDto categoriaParaEditar){
        this.categoriaParaEditar = categoriaParaEditar;
    }
    public void setListaDeCategoriasUsadas(List<MuebleDto> listaDeMuebles){
        listaCategoriasUsadas.clear();
        for(MuebleDto mueble : listaDeMuebles){
            listaCategoriasUsadas.add(mueble.getCategoria().getCategoria());
        }
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
            FadeDownAnimation.play(lblMsgRegistroMueble);
            txtExistencias.getStyleClass().removeAll("textoIngresadoInvalido");
        }else{
            if(listaDeCategoriasVacia){
                Platform.runLater(() -> {
                    lblSinCategorias.setText("Aun no existen categorias");
                    FadeInLeftAnimation.play(lblSinCategorias);
                    PauseTransition delay = new PauseTransition(Duration.seconds(2));
                    lblSinCategorias.getStyleClass().add("errorMsjSinCategorias");
                    delay.setOnFinished(event -> {
                        FadeOutRightAnimation.play(lblSinCategorias);
                    });
                    delay.play();
                });
            }else{
                overlayRegistrarMuebles.setVisible(true);
            }
        }
    }

    @FXML
    public void manejarModalEditarMuebles(){
        if(overlayEditarMuebles.isVisible()){
            overlayEditarMuebles.setVisible(false);
            FadeDownAnimation.play(lblMsgEdicionMueble);
        }else{
            txtNombreEditar.setText(muebleParaEditar.getNombreMueble());
            txtDescripcionEditar.setText(muebleParaEditar.getDescripcionMueble());
            txtExistenciasEditar.setText(String.valueOf(muebleParaEditar.getExistencia()));
            txtMaterialEditar.setText(muebleParaEditar.getNombreMaterial());
            comboCategoriaEditar.setValue(muebleParaEditar.getCategoria());
            overlayEditarMuebles.setVisible(true);
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
    private void manejarModalRegistrarCategorias(){
        if(overlayRegistrarCategorias.isVisible()){
            FadeDownAnimation.play(lblMsgRegistroCategoria);
            overlayRegistrarCategorias.setVisible(false);
        }else{
            overlayRegistrarCategorias.setVisible(true);
        }
    }

    @FXML
    public void manejarModalEditarCategorias(){
        if(overlayEditarCategorias.isVisible()){
            overlayEditarCategorias.setVisible(false);
            FadeDownAnimation.play(lblMsgEdicionCategoria);
        }else{
            txtNombreCategoriaEditar.setText(categoriaParaEditar.getCategoria());
            overlayEditarCategorias.setVisible(true);
        }
    }

    @FXML
    public void manejarModalEliminarCategorias(){
        if(overlayEliminarCategorias.isVisible()){
            overlayEliminarCategorias.setVisible(false);
        }else{
            lblEliminarCategoria.setText("Eliminar: " + categoriaParaEliminar.getCategoria());
            overlayEliminarCategorias.setVisible(true);
        }
    }

    @FXML
    private void ocultarMensajeRegistroMueble(){
        if(lblMsgRegistroMueble.getOpacity() != 0){
            FadeDownAnimation.play(lblMsgRegistroMueble);
            txtExistencias.getStyleClass().removeAll("textoIngresadoInvalido");
            lblMsgRegistroMueble.setOpacity(0);
        }
        if(lblMsgEdicionMueble.getOpacity() != 0){
            FadeDownAnimation.play(lblMsgEdicionMueble);
            txtExistenciasEditar.getStyleClass().removeAll("textoIngresadoInvalido");
            lblMsgEdicionMueble.setOpacity(0);
        }
        if(lblMsgRegistroCategoria.getOpacity() != 0){
            FadeDownAnimation.play(lblMsgRegistroCategoria);
            lblMsgRegistroCategoria.setOpacity(0);
        }
        if(lblMsgEdicionCategoria.getOpacity() != 0){
            FadeDownAnimation.play(lblMsgEdicionCategoria);
            lblMsgEdicionCategoria.setOpacity(0);
        }   
    }
}
