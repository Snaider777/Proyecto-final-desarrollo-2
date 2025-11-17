package com.KESNAYFERLYDDY.app.controllers;

import java.util.List;

import com.KESNAYFERLYDDY.app.animations.FadeDownAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeUpAnimation;
import com.KESNAYFERLYDDY.app.models.EmpleadosDto;
import com.KESNAYFERLYDDY.app.services.EmpleadoService;

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

public class EmpleadosListController {

    @FXML private TableView<EmpleadosDto> tablaEmpleados;
    @FXML private TableColumn<EmpleadosDto, Integer> columnaId;
    @FXML private TableColumn<EmpleadosDto, String> columnaNombres;
    @FXML private TableColumn<EmpleadosDto, String> columnaApellidos;
    @FXML private TableColumn<EmpleadosDto, String> columnaDireccion;
    @FXML private TableColumn<EmpleadosDto, String> columnaDUI;
    @FXML private TableColumn<EmpleadosDto, String> columnaTel;
    @FXML private TableColumn<EmpleadosDto, Void> columnaAcciones;

    @FXML private VBox overlayRegistrar;
    @FXML private VBox overlayEditar;
    @FXML private VBox overlayEliminar;

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtDui;
    @FXML private TextField txtTelefono;

    @FXML private TextField txtNombresEditar;
    @FXML private TextField txtApellidosEditar;
    @FXML private TextField txtDireccionEditar;
    @FXML private TextField txtDuiEditar;
    @FXML private TextField txtTelefonoEditar;

    @FXML private Label lblMsgRegistro;
    @FXML private Label lblMsgEdicion;
    @FXML private Label lblEliminar;

    private final EmpleadoService empleadoService = new EmpleadoService();

    private static String user = "";
    private EmpleadosDto empleadoParaEliminar;
    private EmpleadosDto empleadoParaEditar;

    public static void show(String username) {
        try {
            user = username;
            FXMLLoader loader = new FXMLLoader(EmpleadosListController.class.getResource("/fxml/empleados_list.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.setTitle("Empleados - " + username);
            stage.setMaximized(true);

            EmpleadosListController controller = loader.getController();
            controller.cargarEmpleados();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        columnaId.setCellValueFactory(cd ->new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getIdEmpleado()));
        columnaNombres.setCellValueFactory(cd ->new javafx.beans.property.SimpleStringProperty(cd.getValue().getNombresEmpleado()));
        columnaApellidos.setCellValueFactory(cd ->new javafx.beans.property.SimpleStringProperty(cd.getValue().getApellidosEmpleado()));
        columnaDireccion.setCellValueFactory(cd ->new javafx.beans.property.SimpleStringProperty(cd.getValue().getDireccionEmpleado()));
        columnaDUI.setCellValueFactory(cd ->new javafx.beans.property.SimpleStringProperty(cd.getValue().getDuiEmpleado()));
        columnaTel.setCellValueFactory(cd ->new javafx.beans.property.SimpleStringProperty(cd.getValue().getTelefonoEmpleado()));
        columnaAcciones.setCellFactory(col ->new EmpleadosButtonsController(this));
    }

    public void cargarEmpleados() {

        Task<List<EmpleadosDto>> task = new Task<>() {
            @Override
            protected List<EmpleadosDto> call() throws Exception {
                return empleadoService.listarEmpleados();
            }
        };

        task.setOnSucceeded(e -> {
            tablaEmpleados.setItems(FXCollections.observableArrayList(task.getValue()));
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,
                "Error cargando empleados: " + task.getException().getMessage()).show());
        });

        new Thread(task).start();
    }

    @FXML
    public void registrarEmpleado() {

        if (txtNombres.getText().isEmpty() ||
            txtApellidos.getText().isEmpty() ||
            txtDireccion.getText().isEmpty() ||
            txtDui.getText().isEmpty() ||
            txtTelefono.getText().isEmpty()) {

            lblMsgRegistro.getStyleClass().setAll("error");
            lblMsgRegistro.setText("Complete todos los campos");
            FadeUpAnimation.play(lblMsgRegistro);
            return;
        }

        EmpleadosDto empleado = new EmpleadosDto();
        empleado.setNombresEmpleado(txtNombres.getText());
        empleado.setApellidosEmpleado(txtApellidos.getText());
        empleado.setDireccionEmpleado(txtDireccion.getText());
        empleado.setDuiEmpleado(txtDui.getText());
        empleado.setTelefonoEmpleado(txtTelefono.getText());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                empleadoService.insertarEmpleado(empleado);
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            lblMsgRegistro.getStyleClass().setAll("exito");
            lblMsgRegistro.setText("Empleado registrado exitosamente");
            FadeUpAnimation.play(lblMsgRegistro);

            txtNombres.setText("");
            txtApellidos.setText("");
            txtDireccion.setText("");
            txtDui.setText("");
            txtTelefono.setText("");

            cargarEmpleados();
        }));

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
            lblMsgRegistro.getStyleClass().setAll("error");
            lblMsgRegistro.setText("Error al registrar empleado: " + (ex != null ? ex.getMessage() : "Unknown"));
            FadeUpAnimation.play(lblMsgRegistro);
        });

        new Thread(task).start();
    }

    @FXML
    public void editarEmpleado() {

        if (txtNombresEditar.getText().isEmpty() ||
            txtApellidosEditar.getText().isEmpty() ||
            txtDireccionEditar.getText().isEmpty() ||
            txtDuiEditar.getText().isEmpty() ||
            txtTelefonoEditar.getText().isEmpty()) {

            lblMsgEdicion.getStyleClass().setAll("error");
            lblMsgEdicion.setText("Complete todos los campos");
            FadeUpAnimation.play(lblMsgEdicion);
            return;
        }

        EmpleadosDto empleado = new EmpleadosDto();
        empleado.setIdEmpleado(empleadoParaEditar.getIdEmpleado());
        empleado.setNombresEmpleado(txtNombresEditar.getText());
        empleado.setApellidosEmpleado(txtApellidosEditar.getText());
        empleado.setDireccionEmpleado(txtDireccionEditar.getText());
        empleado.setDuiEmpleado(txtDuiEditar.getText());
        empleado.setTelefonoEmpleado(txtTelefonoEditar.getText());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                empleadoService.editarEmpleado(empleado);
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            lblMsgEdicion.getStyleClass().setAll("exito");
            lblMsgEdicion.setText("Empleado actualizado exitosamente");
            FadeUpAnimation.play(lblMsgEdicion);
            cargarEmpleados();
        }));

        task.setOnFailed(e -> {
            lblMsgEdicion.getStyleClass().setAll("error");
            lblMsgEdicion.setText("Error al actualizar empleado");
            FadeUpAnimation.play(lblMsgEdicion);
        });

        new Thread(task).start();
    }

    @FXML
    public void eliminarEmpleado() {

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                empleadoService.eliminarEmpleado(empleadoParaEliminar.getIdEmpleado());
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            manejarModalEliminar();
            cargarEmpleados();
        });

        task.setOnFailed(e -> {
            System.out.println("Error al eliminar empleado");
        });

        new Thread(task).start();
    }

    public void setEmpleadoParaEliminar(EmpleadosDto empleado) {
        this.empleadoParaEliminar = empleado;
    }

    public void setEmpleadoParaEditar(EmpleadosDto empleado) {
        this.empleadoParaEditar = empleado;
    }

    @FXML
    private void cancelarClickEnModal(MouseEvent event) {
        event.consume();
    }

    @FXML
    public void manejarModalRegistrar() {
        overlayRegistrar.setVisible(!overlayRegistrar.isVisible());
    }

    @FXML
    public void manejarModalEditar() {

        if (!overlayEditar.isVisible()) {
            txtNombresEditar.setText(empleadoParaEditar.getNombresEmpleado());
            txtApellidosEditar.setText(empleadoParaEditar.getApellidosEmpleado());
            txtDireccionEditar.setText(empleadoParaEditar.getDireccionEmpleado());
            txtDuiEditar.setText(empleadoParaEditar.getDuiEmpleado());
            txtTelefonoEditar.setText(empleadoParaEditar.getTelefonoEmpleado());
        }

        overlayEditar.setVisible(!overlayEditar.isVisible());
    }

    @FXML
    public void manejarModalEliminar() {
        if (!overlayEliminar.isVisible()) {
            lblEliminar.setText("¿Seguro que desea eliminar a " +
                                empleadoParaEliminar.getNombresEmpleado() + "?");
        }
        overlayEliminar.setVisible(!overlayEliminar.isVisible());
    }

    @FXML
    private void ocultarMensajeRegistro() {
        if (lblMsgRegistro.getOpacity() != 0) {
            FadeDownAnimation.play(lblMsgRegistro);
            lblMsgRegistro.setOpacity(0);
        }
    }

    @FXML
    private void ocultarMensajeEdicion() {
        if (lblMsgEdicion.getOpacity() != 0) {
            FadeDownAnimation.play(lblMsgEdicion);
            lblMsgEdicion.setOpacity(0);
        }
    }

    @FXML
    private void accionCerrar() {
        Stage stage = (Stage) tablaEmpleados.getScene().getWindow();
        stage.close();
        DashboardController.showDashboard(user);
    }

    @FXML
    public void cerrarVistaEmpleados() {
        accionCerrar();
    }

    @FXML
    private void accionNuevo() {
        manejarModalRegistrar();
    }

}
