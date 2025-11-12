package com.KESNAYFERLYDDY.app.animations;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeOutRightAnimation {
    public static void play(Node nodo) {
        FadeTransition opacidadNodo = new FadeTransition(Duration.seconds(0.3), nodo);
        TranslateTransition trasladarNodo = new TranslateTransition(Duration.seconds(0.3), nodo);
        
        trasladarNodo.setFromX(-20);
        trasladarNodo.setToX(0);

        opacidadNodo.setFromValue(1);
        opacidadNodo.setToValue(0);

        opacidadNodo.play();
        trasladarNodo.play();
    }
}
