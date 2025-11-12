package com.KESNAYFERLYDDY.app.animations;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInLeftAnimation {
    public static void play(Node nodo) {
        FadeTransition opacidadNodo = new FadeTransition(Duration.seconds(0.3), nodo);
        TranslateTransition trasladarNodo = new TranslateTransition(Duration.seconds(0.3), nodo);
        
        trasladarNodo.setFromX(0);
        trasladarNodo.setToX(-20);

        opacidadNodo.setFromValue(0);
        opacidadNodo.setToValue(1);

        opacidadNodo.play();
        trasladarNodo.play();
    }
}
