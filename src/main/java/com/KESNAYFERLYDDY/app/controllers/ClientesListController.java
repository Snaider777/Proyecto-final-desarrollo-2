package com.KESNAYFERLYDDY.app.controllers;

import java.util.List;

import com.KESNAYFERLYDDY.app.animations.FadeDownAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeUpAnimation;
import com.KESNAYFERLYDDY.app.models.ClienteDto;
import com.KESNAYFERLYDDY.app.services.ClienteService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientesListController {
    @FXML private TableView<ClienteDto> table;
    @FXML private TableColumn<ClienteDto, Integer> colId;
    @FXML private TableColumn<ClienteDto, String> colNombres;
    @FXML private TableColumn<ClienteDto, String> colApellidos;
    @FXML private TableColumn<ClienteDto, String> colTelefono;
    @FXML private TableColumn<ClienteDto, String> colDui;
    @FXML private VBox overlay;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDui;
    @FXML private TextField txtDireccion;
    @FXML private Label lblMsg;

    private final ClienteService client = new ClienteService();
    private static String user = "";

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
    private void ocultarMensaje(){
        if(lblMsg.getOpacity() != 0){
            FadeDownAnimation.play(lblMsg);
            lblMsg.setOpacity(0);
        }
    }
    
    @FXML
    public void initialize() {
        colId.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getIdCliente()));
        colNombres.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getNombresCliente()));
        colApellidos.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getApellidosCliente()));
        colTelefono.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTelefonoCliente()));
        colDui.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDuiCliente()));
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
        if(txtNombres.getText().isEmpty() || txtApellidos.getText().isEmpty() || txtDireccion.getText().isEmpty() || txtDui.getText().isEmpty() || txtTelefono.getText().isEmpty()){
            Platform.runLater(() -> {
                lblMsg.getStyleClass().remove("exito");
                lblMsg.getStyleClass().add("error");
                lblMsg.setText("Complete todos los campos");
                FadeUpAnimation.play(lblMsg);
            });
        }else{
            ClienteDto cliente = new ClienteDto();
            cliente.setNombresCliente(txtNombres.getText());
            cliente.setApellidosCliente(txtApellidos.getText());
            cliente.setDireccionCliente(txtDireccion.getText());
            cliente.setDuiCliente(txtDui.getText());
            cliente.setTelefonoCliente(txtTelefono.getText());
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    client.insertarCliente(cliente);
                    return null;
                }
            };
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                lblMsg.getStyleClass().remove("error");
                lblMsg.getStyleClass().add("exito");
                lblMsg.setText("Cliente registrado exitosamente");
                txtNombres.setText("");
                txtApellidos.setText("");
                txtDireccion.setText("");
                txtDui.setText("");
                txtTelefono.setText("");
                FadeUpAnimation.play(lblMsg);
            }));
            task.setOnFailed(event -> {
                task.getException().printStackTrace();
                Platform.runLater(() -> {
                    lblMsg.getStyleClass().remove("exito");
                    lblMsg.getStyleClass().add("error");
                    lblMsg.setText("Error al insertar el cliente");
                    FadeUpAnimation.play(lblMsg);
                });
                System.out.println("Error en la tarea: " + task.getException().getMessage() );
            });
            new Thread(task).start();
        }
    }

    @FXML
    private void refrescarTabla() { cargarClientes(); }

    @FXML
    private void cancelarClickEnModal(MouseEvent event) { event.consume(); }

    @FXML
    private void cerrarVistaClientes() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
        DashboardController.showDashboard(user);
    }
    @FXML
    private void manejarFormulario(){
        if(overlay.isVisible()){
            overlay.setVisible(false);
        }else{
            overlay.setVisible(true);
        }
    }
}
