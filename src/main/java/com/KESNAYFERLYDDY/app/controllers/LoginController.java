package com.KESNAYFERLYDDY.app.controllers;

import com.KESNAYFERLYDDY.app.models.UsuarioDto;
import com.KESNAYFERLYDDY.app.services.AuthService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.KESNAYFERLYDDY.app.animations.FadeDownAnimation;
import com.KESNAYFERLYDDY.app.animations.FadeUpAnimation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnLogin;
    @FXML private Label lblMsg;

    // Ahora usamos dos ImageView para slide suave
    @FXML private ImageView bgImageView;
    @FXML private ImageView bgImageView2;
    @FXML private Region bgOverlay;

    private final AuthService authService = new AuthService();

    private final List<Image> images = new ArrayList<>();
    private int currentIndex = 0;      // índice de la imagen actualmente visible (en bgImageView)
    private Timeline slideshow;

    // Campo que guarda el ancho actual del área de slide (evita capturar variables locales en lambdas)
    private double slideWidth = 800; // valor por defecto; se actualizará cuando la Scene esté lista

    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("/fxml/login.fxml"));
            Parent nodoRaiz = loader.load();
            Scene scene = new Scene(nodoRaiz);
            Stage stage = new Stage();
            stage.setTitle("MiApp - Login");
            stage.setScene(scene);
            stage.getIcons().add(new Image(LoginController.class.getResourceAsStream("/images/stageIcon.png")));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    /*-----------------------------------------------------------------------------------------------------*/
    /* CARRUSEL (SLIDE HORIZONTAL SUAVE) */
    @FXML
    private void initialize() {
        String basePath = "/images/";
        String[] files = {"slider1.jpg", "slider2.jpg", "slider3.jpg", "slider4.jpg"};

        for (String f : files) {
            try (InputStream is = getClass().getResourceAsStream(basePath + f)) {
                if (is != null) {
                    images.add(new Image(is));
                } else {
                    System.err.println("No se encontró imagen: " + basePath + f);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (images.isEmpty()) return;

        // Inicializar ambas ImageView
        bgImageView.setImage(images.get(0));
        currentIndex = 0;
        int nextIndex = (currentIndex + 1) % images.size();
        bgImageView2.setImage(images.get(nextIndex));

        bgImageView.setPreserveRatio(false);
        bgImageView2.setPreserveRatio(false);

        bgImageView.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) {
                bgImageView.fitWidthProperty().bind(newS.widthProperty());
                bgImageView.fitHeightProperty().bind(newS.heightProperty());
                bgImageView2.fitWidthProperty().bind(newS.widthProperty());
                bgImageView2.fitHeightProperty().bind(newS.heightProperty());

                newS.widthProperty().addListener((wObs, oldW, newWVal) -> {
                    slideWidth = newWVal.doubleValue();
                    bgImageView.setTranslateX(0);
                    bgImageView2.setTranslateX(slideWidth);
                });

                slideWidth = newS.getWidth() > 0 ? newS.getWidth() : slideWidth;
                bgImageView.setTranslateX(0);
                bgImageView2.setTranslateX(slideWidth);
            }
        });

        bgImageView.setEffect(new GaussianBlur(3));
        bgImageView2.setEffect(new GaussianBlur(3));

        startSlideshow();
    }

    private void startSlideshow() {
        slideshow = new Timeline(new KeyFrame(Duration.seconds(5), e -> slideToNext()));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();
    }

    private void slideToNext() {
    if (images.isEmpty()) return;

    Scene scene = bgImageView.getScene();
    if (scene == null) return;

    double w = slideWidth > 0 ? slideWidth : (bgImageView.getFitWidth() > 0 ? bgImageView.getFitWidth() : 800);

    bgImageView.setTranslateX(0);
    bgImageView2.setTranslateX(w);

    int nextIndex = (currentIndex + 1) % images.size();
    bgImageView2.setImage(images.get(nextIndex));

    TranslateTransition tVisible = new TranslateTransition(Duration.seconds(0.8), bgImageView);
    TranslateTransition tIncoming = new TranslateTransition(Duration.seconds(0.8), bgImageView2);

    tVisible.setFromX(0);
    tVisible.setToX(-w);

    tIncoming.setFromX(w);
    tIncoming.setToX(0);

    tVisible.setOnFinished(ev -> {
        currentIndex = nextIndex;

        ImageView tmp = bgImageView;
        bgImageView = bgImageView2;
        bgImageView2 = tmp;

        int following = (currentIndex + 1) % images.size();
        bgImageView2.setImage(images.get(following));
        bgImageView2.setTranslateX(w);

        bgImageView.setTranslateX(0);
    });

    tVisible.play();
    tIncoming.play();
}
    /*-----------------------------------------------------------------------------------------------------*/

    @FXML
    private void ocultarMensaje(){
        if(lblMsg.getOpacity() != 0){
            FadeDownAnimation.play(lblMsg);
            lblMsg.setOpacity(0);
        }
    }

    @FXML
    private void manejarLogin() {
        String user = txtUser.getText().trim();
        String pass = txtPass.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            lblMsg.setText("Usuario y contraseña obligatorios");
            if (!lblMsg.getStyleClass().contains("error")) lblMsg.getStyleClass().add("error");
            FadeUpAnimation.play(lblMsg);
            return;
        }
        btnLogin.setDisable(true);
        lblMsg.setText("");

        UsuarioDto usuario = new UsuarioDto();
        usuario.setNombreUsuario(user);
        usuario.setContrasena(pass);

        Task<Boolean> task = new Task<>() {
            @Override protected Boolean call() throws Exception {
                return authService.login(usuario);
            }
        };

        task.setOnSucceeded(event -> {
            boolean ok = task.getValue();
            btnLogin.setDisable(false);
            if (ok) {
                if (!lblMsg.getStyleClass().contains("exito")) lblMsg.getStyleClass().add("exito");
                lblMsg.setText("Login correcto. Abriendo dashboard...");
                Stage stage = (Stage) lblMsg.getScene().getWindow();
                stage.close();
                DashboardController.showDashboard(user);
            } else {
                if (!lblMsg.getStyleClass().contains("error")) lblMsg.getStyleClass().add("error");
                lblMsg.setText("Credenciales incorrectas");
                FadeUpAnimation.play(lblMsg);
            }
        });

        task.setOnFailed(event -> {
            btnLogin.setDisable(false);
            lblMsg.setText("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }
}
