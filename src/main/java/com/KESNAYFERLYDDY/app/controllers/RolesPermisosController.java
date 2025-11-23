package com.KESNAYFERLYDDY.app.controllers;

import java.util.List;
import java.util.Optional;

import com.KESNAYFERLYDDY.app.models.PermisoDto;
import com.KESNAYFERLYDDY.app.models.RolDto;
import com.KESNAYFERLYDDY.app.services.PermisoService;
import com.KESNAYFERLYDDY.app.services.RolPermisoService;
import com.KESNAYFERLYDDY.app.services.RolService;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controlador para crear/editar/eliminar roles y permisos
 * y asignar permisos a roles (mediante diálogo).
 */
public class RolesPermisosController {

    @FXML private TableView<RolDto> tablaRoles;
    @FXML private TableColumn<RolDto, Integer> colRolId;
    @FXML private TableColumn<RolDto, String> colRolNombre;
    @FXML private TextField txtNuevoRol;

    @FXML private TableView<PermisoDto> tablaPermisos;
    @FXML private TableColumn<PermisoDto, Integer> colPermisoId;
    @FXML private TableColumn<PermisoDto, String> colPermisoNombre;
    @FXML private TextField txtNuevoPermiso;

    private final RolService rolService = new RolService();
    private final PermisoService permisoService = new PermisoService();
    private final RolPermisoService rolPermisoService = new RolPermisoService();

    @FXML
    public void initialize() {
        colRolId.setCellValueFactory(new PropertyValueFactory<>("idRol"));
        colRolNombre.setCellValueFactory(new PropertyValueFactory<>("rol"));

        colPermisoId.setCellValueFactory(new PropertyValueFactory<>("idPermiso"));
        colPermisoNombre.setCellValueFactory(new PropertyValueFactory<>("permiso"));

        // al hacer clic derecho en fila de rol -> menú (Editar / Eliminar / Asignar permiso)
        tablaRoles.setRowFactory(tv -> {
            TableRow<RolDto> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem editar = new MenuItem("Editar");
            editar.setOnAction(e -> editarRol(row.getItem()));

            MenuItem eliminar = new MenuItem("Eliminar");
            eliminar.setOnAction(e -> eliminarRol(row.getItem()));

            MenuItem asignarPermiso = new MenuItem("Asignar permiso");
            asignarPermiso.setOnAction(e -> mostrarAsignarPermisoARol(row.getItem()));

            menu.getItems().addAll(editar, asignarPermiso, eliminar);
            row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));
            return row;
        });

        // clic derecho en permiso -> editar/eliminar/asignar a rol o usuario (aquí solo rol)
        tablaPermisos.setRowFactory(tv -> {
            TableRow<PermisoDto> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem editar = new MenuItem("Editar");
            editar.setOnAction(e -> editarPermiso(row.getItem()));

            MenuItem eliminar = new MenuItem("Eliminar");
            eliminar.setOnAction(e -> eliminarPermiso(row.getItem()));

            MenuItem asignarARol = new MenuItem("Asignar a rol");
            asignarARol.setOnAction(e -> mostrarAsignarPermisoARolDesdePermiso(row.getItem()));

            menu.getItems().addAll(editar, asignarARol, eliminar);
            row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));
            return row;
        });

        cargarRoles();
        cargarPermisos();
    }

    private void cargarRoles() {
        Task<List<RolDto>> task = new Task<>() {
            @Override protected List<RolDto> call() throws Exception {
                return rolService.listarRoles();
            }
        };
        task.setOnSucceeded(e -> tablaRoles.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    public void crearRol() {
        String nombre = txtNuevoRol.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Ingrese un nombre de rol").show();
            return;
        }
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                RolDto r = new RolDto();
                r.setRol(nombre.trim());
                rolService.crearRol(r);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            txtNuevoRol.setText("");
            cargarRoles();
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo crear rol: " + task.getException().getMessage()).show();
        });
        new Thread(task).start();
    }

    private void editarRol(RolDto rol) {
        if (rol == null) return;
        TextInputDialog d = new TextInputDialog(rol.getRol());
        d.setTitle("Editar rol");
        d.setHeaderText("Editar rol ID " + rol.getIdRol());
        d.setContentText("Nuevo nombre:");
        Optional<String> res = d.showAndWait();
        res.ifPresent(nuevo -> {
            if (nuevo.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Nombre vacío").show();
                return;
            }
            Task<Void> t = new Task<>() {
                @Override protected Void call() throws Exception {
                    RolDto r = new RolDto();
                    r.setIdRol(rol.getIdRol());
                    r.setRol(nuevo.trim());
                    rolService.crearRol(r); 
                    return null;
                }
            };
            t.setOnSucceeded(ev -> cargarRoles());
            t.setOnFailed(ev -> {
                t.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error editando rol: " + t.getException().getMessage()).show();
            });
            new Thread(t).start();
        });
    }

    private void eliminarRol(RolDto rol) {
        if (rol == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar rol " + rol.getRol() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            Task<Void> t = new Task<>() {
                @Override protected Void call() throws Exception {
                    rolService.eliminarRol(rol.getIdRol());
                    return null;
                }
            };
            t.setOnSucceeded(ev -> cargarRoles());
            t.setOnFailed(ev -> {
                t.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error eliminando rol: " + t.getException().getMessage()).show();
            });
            new Thread(t).start();
        }
    }

    private void cargarPermisos() {
        Task<List<PermisoDto>> task = new Task<>() {
            @Override protected List<PermisoDto> call() throws Exception {
                return permisoService.listarPermisos();
            }
        };
        task.setOnSucceeded(e -> tablaPermisos.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    public void crearPermiso() {
        String nombre = txtNuevoPermiso.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Ingrese un nombre de permiso").show();
            return;
        }
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                PermisoDto p = new PermisoDto();
                p.setPermiso(nombre.trim());
                permisoService.crearPermiso(p);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            txtNuevoPermiso.setText("");
            cargarPermisos();
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo crear permiso: " + task.getException().getMessage()).show();
        });
        new Thread(task).start();
    }

    private void editarPermiso(PermisoDto permiso) {
        if (permiso == null) return;
        TextInputDialog d = new TextInputDialog(permiso.getPermiso());
        d.setTitle("Editar permiso");
        d.setHeaderText("Editar permiso ID " + permiso.getIdPermiso());
        d.setContentText("Nuevo nombre:");
        Optional<String> res = d.showAndWait();
        res.ifPresent(nuevo -> {
            if (nuevo.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Nombre vacío").show();
                return;
            }
            Task<Void> t = new Task<>() {
                @Override protected Void call() throws Exception {
                    PermisoDto p = new PermisoDto();
                    p.setIdPermiso(permiso.getIdPermiso());
                    p.setPermiso(nuevo.trim());
                    permisoService.crearPermiso(p);
                    return null;
                }
            };
            t.setOnSucceeded(ev -> cargarPermisos());
            t.setOnFailed(ev -> {
                t.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error editando permiso: " + t.getException().getMessage()).show();
            });
            new Thread(t).start();
        });
    }

    private void eliminarPermiso(PermisoDto permiso) {
        if (permiso == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar permiso " + permiso.getPermiso() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            Task<Void> t = new Task<>() {
                @Override protected Void call() throws Exception {
                    permisoService.eliminarPermiso(permiso.getIdPermiso());
                    return null;
                }
            };
            t.setOnSucceeded(ev -> cargarPermisos());
            t.setOnFailed(ev -> {
                t.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error eliminando permiso: " + t.getException().getMessage()).show();
            });
            new Thread(t).start();
        }
    }

    private void mostrarAsignarPermisoARol(RolDto rol) {
        if (rol == null) return;
        List<PermisoDto> permisos = tablaPermisos.getItems();
        if (permisos == null || permisos.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No hay permisos disponibles. Créelos primero.").show();
            return;
        }
        ChoiceDialog<PermisoDto> dialog = new ChoiceDialog<>(permisos.get(0), permisos);
        dialog.setTitle("Asignar permiso");
        dialog.setHeaderText("Asignar permiso al rol: " + rol.getRol());
        dialog.setContentText("Seleccione permiso:");
        Optional<PermisoDto> sel = dialog.showAndWait();
        sel.ifPresent(p -> {
            Task<Void> t = new Task<>() {
                @Override protected Void call() throws Exception {
                    rolPermisoService.asignarRolPermiso(rol.getIdRol(), p.getIdPermiso());
                    return null;
                }
            };
            t.setOnSucceeded(ev -> {
                new Alert(Alert.AlertType.INFORMATION, "Permiso asignado al rol").show();
            });
            t.setOnFailed(ev -> {
                t.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error asignando permiso: " + t.getException().getMessage()).show();
            });
            new Thread(t).start();
        });
    }

    private void mostrarAsignarPermisoARolDesdePermiso(PermisoDto permiso) {
        if (permiso == null) return;
        List<RolDto> roles = tablaRoles.getItems();
        if (roles == null || roles.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No hay roles disponibles. Créelos primero.").show();
            return;
        }
        ChoiceDialog<RolDto> dialog = new ChoiceDialog<>(roles.get(0), roles);
        dialog.setTitle("Asignar permiso a rol");
        dialog.setHeaderText("Asignar permiso: " + permiso.getPermiso());
        dialog.setContentText("Seleccione rol:");
        Optional<RolDto> sel = dialog.showAndWait();
        sel.ifPresent(r -> {
            Task<Void> t = new Task<>() {
                @Override protected Void call() throws Exception {
                    rolPermisoService.asignarRolPermiso(r.getIdRol(), permiso.getIdPermiso());
                    return null;
                }
            };
            t.setOnSucceeded(ev -> new Alert(Alert.AlertType.INFORMATION, "Permiso asignado al rol").show());
            t.setOnFailed(ev -> {
                t.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error asignando permiso: " + t.getException().getMessage()).show();
            });
            new Thread(t).start();
        });
    }

    @FXML
    public void accionCerrar() {
        Stage stage = (Stage) tablaRoles.getScene().getWindow();
        stage.close();
    }
}
