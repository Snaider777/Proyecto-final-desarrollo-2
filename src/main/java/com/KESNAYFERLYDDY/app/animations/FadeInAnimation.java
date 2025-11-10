package com.KESNAYFERLYDDY.app.animations;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInAnimation {
    public static void play(Node nodo) {
        FadeTransition opacidadNodo = new FadeTransition(Duration.seconds(0.3), nodo);
        opacidadNodo.setFromValue(0);
        opacidadNodo.setToValue(1);

        opacidadNodo.play();
    }
}
