package com.KESNAYFERLYDDY.app.controllers;

import java.util.List;

import com.KESNAYFERLYDDY.app.models.EmpleadosDto;
import com.KESNAYFERLYDDY.app.models.RolDto;
import com.KESNAYFERLYDDY.app.models.UsuarioDto;
import com.KESNAYFERLYDDY.app.services.RolService;
import com.KESNAYFERLYDDY.app.services.UsuarioRolService;
import com.KESNAYFERLYDDY.app.services.UsuarioService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EmpleadoUsuarioController {

    @FXML private Label lblTitulo;
    @FXML private Label lblEstadoUsuario;
    @FXML private TextField txtUsername;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnCrearEditar;
    @FXML private Button btnEliminar;

    @FXML private TableView<RolDto> tablaRolesAsignados;
    @FXML private TableColumn<RolDto, String> colRolNombre;
    @FXML private TableColumn<RolDto, Void> colRolAccion;
    @FXML private ComboBox<RolDto> comboRolesDisponibles;

    private final UsuarioService usuarioService = new UsuarioService();
    private final RolService rolService = new RolService();
    private final UsuarioRolService usuarioRolService = new UsuarioRolService();

    private EmpleadosDto empleadoActual;
    private UsuarioDto usuarioActual;

    @FXML
    public void initialize() {
        colRolNombre.setCellValueFactory(new PropertyValueFactory<>("rol"));

        // Botón de eliminar rol de la tabla
        colRolAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Quitar");
            {
                btn.setOnAction(event -> {
                    RolDto rol = getTableView().getItems().get(getIndex());
                    quitarRol(rol);
                });
                btn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        cargarRolesDisponibles();
    }

    public void setEmpleado(EmpleadosDto empleado) {
        this.empleadoActual = empleado;
        lblTitulo.setText("Gestión de Usuario para: " + empleado.getNombresEmpleado() + " " + empleado.getApellidosEmpleado());
        buscarUsuarioDelEmpleado();
    }

    private void cargarRolesDisponibles() {
        // Carga todos los roles que existen en el sistema para el ComboBox
        new Thread(() -> {
            try {
                List<RolDto> roles = rolService.listarRoles();
                Platform.runLater(() -> comboRolesDisponibles.setItems(FXCollections.observableArrayList(roles)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void buscarUsuarioDelEmpleado() {
        if (empleadoActual == null) return;

        // Ejecutamos la búsqueda en un hilo secundario para no congelar la pantalla
        new Thread(() -> {
            // 1. Llamamos al nuevo servicio que creamos en el Paso 3
            UsuarioDto u = usuarioService.obtenerUsuarioPorEmpleado(empleadoActual.getIdEmpleado());
            
            // 2. Volvemos al hilo principal (UI) para mostrar los datos
            Platform.runLater(() -> {
                if (u != null) {
                    // ¡SÍ TIENE USUARIO! -> Mostramos sus datos
                    cargarDatosUsuario(u);
                } else {
                    // NO TIENE -> Mostramos formulario vacío
                    modoCrear();
                }
            });
        }).start();
    }

    private void cargarDatosUsuario(UsuarioDto u) {
        this.usuarioActual = u;
        lblEstadoUsuario.setText("ACTIVO (ID: " + u.getIdUsuario() + ")");
        lblEstadoUsuario.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        
        txtUsername.setText(u.getNombreUsuario());
        txtCorreo.setText(u.getCorreo());
        txtPassword.clear(); // No mostramos password
        txtPassword.setPromptText("Dejar vacío para no cambiar");
        
        btnCrearEditar.setText("Actualizar Usuario");
        btnEliminar.setDisable(false);
        
        // Habilitar panel de roles
        tablaRolesAsignados.setDisable(false);
        comboRolesDisponibles.setDisable(false);
        
        cargarRolesDelUsuario();
    }

    private void modoCrear() {
        this.usuarioActual = null;
        lblEstadoUsuario.setText("NO ASIGNADO");
        lblEstadoUsuario.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        
        txtUsername.clear();
        txtCorreo.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Requerido");
        
        btnCrearEditar.setText("Crear Usuario");
        btnEliminar.setDisable(true);
        
        // Deshabilitar roles hasta que se cree el usuario
        tablaRolesAsignados.setDisable(true);
        comboRolesDisponibles.setDisable(true);
    }

    private void cargarRolesDelUsuario() {
        if (usuarioActual == null) return;
        // TODO: Implementar usuarioRolService.listarRolesPorUsuario(id)
        /*
        new Thread(() -> {
            try {
                List<RolDto> roles = usuarioRolService.obtenerRolesUsuario(usuarioActual.getIdUsuario());
                Platform.runLater(() -> tablaRolesAsignados.setItems(FXCollections.observableArrayList(roles)));
            } catch(Exception e) { e.printStackTrace(); }
        }).start();
        */
    }

    @FXML
    public void guardarUsuario() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        String mail = txtCorreo.getText();

        if (usuarioActual == null) {
            // CREAR
            if (user.isEmpty() || pass.isEmpty()) {
                mostrarAlerta("Error", "Usuario y contraseña son obligatorios");
                return;
            }
            UsuarioDto nuevo = new UsuarioDto();
            nuevo.setNombreUsuario(user);
            nuevo.setContrasena(pass);
            nuevo.setCorreo(mail);

            new Thread(() -> {
                try {
                    // 1. Crear Usuario
                    UsuarioDto creado = usuarioService.crearUsuario(nuevo);
                    // 2. Vincular a Empleado (POST /usuario_empleados/)
                    usuarioService.asignarUsuarioAEmpleado(creado.getIdUsuario(), empleadoActual.getIdEmpleado());
                    
                    Platform.runLater(() -> {
                        mostrarAlerta("Éxito", "Usuario creado y vinculado.");
                        cargarDatosUsuario(creado);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarAlerta("Error", "Fallo al crear usuario: " + e.getMessage()));
                }
            }).start();

        } else {
            // EDITAR
            usuarioActual.setNombreUsuario(user);
            usuarioActual.setCorreo(mail);
            if (!pass.isEmpty()) usuarioActual.setContrasena(pass);

            new Thread(() -> {
                try {
                    usuarioService.editarUsuario(usuarioActual.getIdUsuario(), usuarioActual);
                    Platform.runLater(() -> mostrarAlerta("Éxito", "Usuario actualizado."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @FXML
    public void eliminarUsuarioActual() {
        if (usuarioActual == null) return;
        // Confirmación y llamada a usuarioService.eliminarUsuario(...)
        // Luego llamar a modoCrear();
    }

    @FXML
    public void asignarRol() {
        RolDto seleccionado = comboRolesDisponibles.getValue();
        if (seleccionado != null && usuarioActual != null) {
            new Thread(() -> {
                try {
                    usuarioRolService.asignarUsuarioRol(usuarioActual.getIdUsuario(), seleccionado.getIdRol());
                    Platform.runLater(() -> {
                        cargarRolesDelUsuario(); // Recargar tabla
                        mostrarAlerta("Éxito", "Rol asignado.");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void quitarRol(RolDto rol) {
        // Lógica para desasignar rol
    }

    @FXML
    public void accionCerrarModal() {
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        new Alert(Alert.AlertType.INFORMATION, contenido).show();
    }
    
}