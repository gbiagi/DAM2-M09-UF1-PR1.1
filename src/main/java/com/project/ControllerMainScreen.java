package com.project;

import javafx.fxml.FXML;

public class ControllerMainScreen {

    @FXML
    private void initialize() {
        System.out.println("MainScreenController initialized");
    }

    @FXML
    private void encriptarArchivo() {
        UtilsViews.setView("EncriptarArchivo");
    }

    @FXML
    private void desencriptarArchivo() {
        UtilsViews.setView("DesencriptarArchivo");
    }
}
