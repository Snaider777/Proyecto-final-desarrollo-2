package com.KESNAYFERLYDDY.app.controllers;

import java.io.IOException;

import com.KESNAYFERLYDDY.app.models.EmpleadosDto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class EmpleadosButtonsController extends TableCell<EmpleadosDto, Void> {

    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnUsuario; 

    @FXML private HBox root;

    private EmpleadosListController parent;

    public EmpleadosButtonsController(EmpleadosListController parent) {
        this.parent = parent;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/empleados_botones.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            EmpleadosDto empleado = getTableView().getItems().get(getIndex());

            // EDITAR
            btnEditar.setOnAction(event -> {
                parent.setEmpleadoParaEditar(empleado);
                parent.manejarModalEditar();
            });

            // ELIMINAR
            btnEliminar.setOnAction(event -> {
                parent.setEmpleadoParaEliminar(empleado);
                parent.manejarModalEliminar();
            });

            // USUARIO -> Abrir modal de usuario para este empleado
            btnUsuario.setOnAction(event -> {
                // Llama al método del parent que abre el modal específico para el empleado
                parent.showEmpleadoUsuarioModal(empleado);
            });

            setGraphic(root);
        }
    }
}
