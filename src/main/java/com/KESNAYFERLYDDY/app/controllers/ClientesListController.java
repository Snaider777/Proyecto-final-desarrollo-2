package com.KESNAYFERLYDDY.app.controllers;

import java.util.List;

import com.KESNAYFERLYDDY.app.animations.FadeDownAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeInAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeOutAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeUpAnimation;
import com.KESNAYFERLYDDY.app.models.ClienteDto;
import com.KESNAYFERLYDDY.app.services.ClienteService;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ClientesListController {
    @FXML private TableView<ClienteDto> table;
    @FXML private TableColumn<ClienteDto, Integer> colId;
    @FXML private TableColumn<ClienteDto, String> colNombres;
    @FXML private TableColumn<ClienteDto, String> colApellidos;
    @FXML private TableColumn<ClienteDto, String> colTelefono;
    @FXML private TableColumn<ClienteDto, String> colDui;
    @FXML private TableColumn<ClienteDto, String> colDireccion;
    @FXML private TableColumn<ClienteDto, Void> colAcciones;

    @FXML private VBox overlayRegistrar;
    @FXML private VBox overlayEliminar;
    @FXML private VBox overlayEditar;

    @FXML private TextField txtNombresRegistro;
    @FXML private TextField txtApellidosRegistro;
    @FXML private TextField txtTelefonoRegistro;
    @FXML private TextField txtDuiRegistro;
    @FXML private TextField txtDireccionRegistro;

    @FXML private TextField txtNombresEditar;
    @FXML private TextField txtApellidosEditar;
    @FXML private TextField txtTelefonoEditar;
    @FXML private TextField txtDuiEditar;
    @FXML private TextField txtDireccionEditar;

    @FXML private Label lblEliminar;
    @FXML private Label lblMsgRegistro;
    @FXML private Label lblMsgEdicion;
    @FXML private Label lblEliminadoCorrectamente;

    private final ClienteService client = new ClienteService();
    private static String user = "";
    private ClienteDto clienteParaEliminar;
    private ClienteDto clienteParaEditar;

    public static void show(String username) {
        try {
            user = username;
            FXMLLoader loader = new FXMLLoader(ClientesListController.class.getResource("/fxml/clientes_list.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Clientes - " + username);
            stage.setMaximized(true);
            ClientesListController controladorClientes = loader.getController();
            controladorClientes.cargarClientes();
            stage.show();
        } catch (Exception error) { error.printStackTrace(); }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getIdCliente()));
        colNombres.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getNombresCliente()));
        colApellidos.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getApellidosCliente()));
        colTelefono.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTelefonoCliente()));
        colDui.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDuiCliente()));
        colDireccion.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDireccionCliente()));
        colAcciones.setCellFactory(col -> new ClientesButtonsController(this));
    }

    public void cargarClientes() {
        Task<List<ClienteDto>> task = new Task<>() {
            @Override protected List<ClienteDto> call() throws Exception {
                return client.listarClientes();
            }
        };
        task.setOnSucceeded(event -> table.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error cargando clientes: " + task.getException().getMessage());
                alert.showAndWait();
            });
        });
        new Thread(task).start();
    }

    public void registrarCliente() {
        if(txtNombresRegistro.getText().isEmpty() || txtApellidosRegistro.getText().isEmpty() || txtDuiRegistro.getText().isEmpty() || txtTelefonoRegistro.getText().isEmpty() || txtDireccionRegistro.getText().isEmpty()){
            Platform.runLater(() -> {
                lblMsgRegistro.getStyleClass().remove("exito");
                lblMsgRegistro.getStyleClass().add("error");
                lblMsgRegistro.setText("Complete todos los campos");
                FadeUpAnimation.play(lblMsgRegistro);
            });
        }else{
            ClienteDto cliente = new ClienteDto();
            cliente.setNombresCliente(txtNombresRegistro.getText());
            cliente.setApellidosCliente(txtApellidosRegistro.getText());
            cliente.setDuiCliente(txtDuiRegistro.getText());
            cliente.setTelefonoCliente(txtTelefonoRegistro.getText());
            cliente.setDireccionCliente(txtDireccionRegistro.getText());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    client.insertarCliente(cliente);
                    return null;
                }
            };
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                lblMsgRegistro.getStyleClass().remove("error");
                lblMsgRegistro.getStyleClass().add("exito");
                lblMsgRegistro.setText("Cliente registrado exitosamente");
                txtNombresRegistro.setText("");
                txtApellidosRegistro.setText("");
                txtDuiRegistro.setText("");
                txtTelefonoRegistro.setText("");
                txtDireccionRegistro.setText("");
                FadeUpAnimation.play(lblMsgRegistro);
                cargarClientes();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                Platform.runLater(() -> {
                    lblMsgRegistro.getStyleClass().remove("exito");
                    lblMsgRegistro.getStyleClass().add("error");
                    lblMsgRegistro.setText("Error al insertar el cliente");
                    FadeUpAnimation.play(lblMsgRegistro);
                });
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    public void editarCliente() {
        if(
            txtNombresEditar.getText().isEmpty() ||
            txtApellidosEditar.getText().isEmpty() ||
            txtDuiEditar.getText().isEmpty() ||
            txtTelefonoEditar.getText().isEmpty() ||
            txtDireccionEditar.getText().isEmpty()
        ){
            lblMsgEdicion.getStyleClass().removeAll("exito", "error", "advertencia");
            lblMsgEdicion.getStyleClass().add("error");
            lblMsgEdicion.setText("Complete todos los campos");
            FadeUpAnimation.play(lblMsgEdicion);
        }else if(
            txtNombresEditar.getText().equals(clienteParaEditar.getNombresCliente()) &&
            txtApellidosEditar.getText().equals(clienteParaEditar.getApellidosCliente()) &&
            txtDuiEditar.getText().equals(clienteParaEditar.getDuiCliente()) &&
            txtTelefonoEditar.getText().equals(clienteParaEditar.getTelefonoCliente()) &&
            txtDireccionEditar.getText().equals(clienteParaEditar.getDireccionCliente())
        ){
            lblMsgEdicion.getStyleClass().removeAll("exito", "error", "advertencia");
            lblMsgEdicion.getStyleClass().add("advertencia");
            lblMsgEdicion.setText("Ningún campo ha sido modificado");
            FadeUpAnimation.play(lblMsgEdicion);
        }else{
            ClienteDto cliente = new ClienteDto();
            cliente.setIdCliente(clienteParaEditar.getIdCliente());
            cliente.setNombresCliente(txtNombresEditar.getText());
            cliente.setApellidosCliente(txtApellidosEditar.getText());
            cliente.setDuiCliente(txtDuiEditar.getText());
            cliente.setTelefonoCliente(txtTelefonoEditar.getText());
            cliente.setDireccionCliente(txtDireccionEditar.getText());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    client.editarCliente(cliente);
                    return null;
                }
            };
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                lblMsgEdicion.getStyleClass().removeAll("exito", "error", "advertencia");
                lblMsgEdicion.getStyleClass().add("exito");
                lblMsgEdicion.setText("Cliente actualizado exitosamente");
                this.clienteParaEditar = cliente;
                FadeUpAnimation.play(lblMsgEdicion);
                cargarClientes();
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                lblMsgEdicion.getStyleClass().removeAll("exito", "error", "advertencia");
                lblMsgEdicion.getStyleClass().add("error");
                lblMsgEdicion.setText("Error al editar el cliente");
                FadeUpAnimation.play(lblMsgEdicion);
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    public void eliminarCliente(){
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                client.eliminarCliente(clienteParaEliminar.getIdCliente());
                return null;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            manejarModalEliminar();
            manejarMensajeEliminacion();
            cargarClientes();
        }));
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    public void setClienteParaEliminar(ClienteDto clienteParaEliminar){
        this.clienteParaEliminar = clienteParaEliminar;
    }

    public void setClienteParaEditar(ClienteDto clienteParaEditar){
        this.clienteParaEditar = clienteParaEditar;
    }

    @FXML
    private void ocultarMensajeRegistro(){
        if(lblMsgRegistro.getOpacity() != 0){
            FadeDownAnimation.play(lblMsgRegistro);
            lblMsgRegistro.setOpacity(0);
        }
    }

    @FXML
    private void ocultarMensajeEdicion(){
        if(lblMsgEdicion.getOpacity() != 0){
            FadeDownAnimation.play(lblMsgEdicion);
            lblMsgEdicion.setOpacity(0);
        }
    }

    @FXML
    private void cancelarClickEnModal(MouseEvent event) {
         event.consume();
    }

    @FXML
    private void manejarModalRegistrar(){
        if(overlayRegistrar.isVisible()){
            overlayRegistrar.setVisible(false);
            FadeDownAnimation.play(lblMsgRegistro);
        }else{
            overlayRegistrar.setVisible(true);
        }
    }

    @FXML
    public void manejarModalEditar(){
        if(overlayEditar.isVisible()){
            overlayEditar.setVisible(false);
            FadeDownAnimation.play(lblMsgEdicion);
        }else{
            txtNombresEditar.setText(clienteParaEditar.getNombresCliente());
            txtApellidosEditar.setText(clienteParaEditar.getApellidosCliente());
            txtDuiEditar.setText(clienteParaEditar.getDuiCliente());
            txtTelefonoEditar.setText(clienteParaEditar.getTelefonoCliente());
            txtDireccionEditar.setText(clienteParaEditar.getDireccionCliente());
            overlayEditar.setVisible(true);
        }
    }

    @FXML
    public void manejarModalEliminar(){
        if(overlayEliminar.isVisible()){
            overlayEliminar.setVisible(false);
        }else{
            lblEliminar.setText("¿Seguro que quiere eliminar a " + clienteParaEliminar.getNombresCliente() + "?");
            overlayEliminar.setVisible(true);
        }
    }

    @FXML
    public void manejarMensajeEliminacion(){
        lblEliminadoCorrectamente.setText("Cliente eliminado con exito");
        FadeInAnimation.play(lblEliminadoCorrectamente);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        lblEliminadoCorrectamente.getStyleClass().add("eliminadoConExito");
        delay.setOnFinished(event -> {
            FadeOutAnimation.play(lblEliminadoCorrectamente);
        });
        delay.play();
    }

    @FXML
    private void cerrarVistaClientes() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
        DashboardController.showDashboard(user);
    }
}
