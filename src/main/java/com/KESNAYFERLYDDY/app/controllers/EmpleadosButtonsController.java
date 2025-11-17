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
    @FXML private HBox root;

    private EmpleadosListController parentController;

    public EmpleadosButtonsController(EmpleadosListController parentController) {
        this.parentController = parentController;
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

            btnEditar.setOnAction(event -> {
                parentController.setEmpleadoParaEditar(empleado);
                parentController.manejarModalEditar();
            });

            btnEliminar.setOnAction(event -> {
                parentController.setEmpleadoParaEliminar(empleado);
                parentController.manejarModalEliminar();
            });
            setGraphic(root);
        }
    }
}
