package com.KESNAYFERLYDDY.app.controllers;

import java.util.List;

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
import javafx.stage.Stage;

public class ClientesListController {
    @FXML private TableView<ClienteDto> table;
    @FXML private TableColumn<ClienteDto, Integer> colId;
    @FXML private TableColumn<ClienteDto, String> colNombres;
    @FXML private TableColumn<ClienteDto, String> colApellidos;
    @FXML private TableColumn<ClienteDto, String> colTelefono;
    @FXML private TableColumn<ClienteDto, String> colDui;

    private final ClienteService client = new ClienteService();

    public static void show(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(ClientesListController.class.getResource("/fxml/clientes_list.fxml"));
            Scene s = new Scene(loader.load());
            Stage st = new Stage();
            st.setScene(s);
            st.setTitle("Clientes - " + username);
            ClientesListController ctrl = loader.getController();
            ctrl.loadClientes();
            st.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getIdCliente()));
        colNombres.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getNombresCliente()));
        colApellidos.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getApellidosCliente()));
        colTelefono.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTelefonoCliente()));
        colDui.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDuiCliente()));
    }

    public void loadClientes() {
        Task<List<ClienteDto>> t = new Task<>() {
            @Override protected List<ClienteDto> call() throws Exception {
                return client.listarClientes();
            }
        };

        t.setOnSucceeded(e -> table.setItems(FXCollections.observableArrayList(t.getValue())));
        t.setOnFailed(e -> {
            t.getException().printStackTrace();
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR, "Error cargando clientes: " + t.getException().getMessage());
                a.showAndWait();
            });
        });

        new Thread(t).start();
    }

    @FXML
    private void onRefresh() { loadClientes(); }

    @FXML
    private void onClose() {
        Stage s = (Stage) table.getScene().getWindow();
        s.close();
    }
}
