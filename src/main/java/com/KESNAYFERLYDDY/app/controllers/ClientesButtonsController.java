package com.KESNAYFERLYDDY.app.controllers;

import com.KESNAYFERLYDDY.app.models.ClienteDto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import java.io.IOException;

public class ClientesButtonsController extends TableCell<ClienteDto, Void> {

    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private HBox root;

    private ClientesListController parentController;

    public ClientesButtonsController(ClientesListController parentController) {
        this.parentController = parentController;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/botones_clientes.fxml"));
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
            ClienteDto cliente = getTableView().getItems().get(getIndex());

            btnEditar.setOnAction(event -> {
                parentController.setClienteParaEditar(cliente);
                parentController.manejarModalEditar();
            });

            btnEliminar.setOnAction(event -> {
                parentController.setClienteParaEliminar(cliente);
                parentController.manejarModalEliminar();
            });
            setGraphic(root);
        }
    }
}
