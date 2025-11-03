package com.KESNAYFERLYDDY.app.animations;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeDownAnimation {
    //Animacion que hace que el nodo desaparezca poquito a poquito con una animacion que baja
    public static void play(Node nodo) {
        FadeTransition opacidadNodo = new FadeTransition(Duration.seconds(0.3), nodo);
        TranslateTransition trasladarNodo = new TranslateTransition(Duration.seconds(0.3), nodo);
        
        trasladarNodo.setFromY(-10);
        trasladarNodo.setToY(20);

        opacidadNodo.setFromValue(1);
        opacidadNodo.setToValue(0);

        opacidadNodo.play();
        trasladarNodo.play();
    }
}
