package com.project;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;

import java.io.File;

public class ControllerEncrypt {
    @FXML
    Label labelClauPublica = new Label();
    @FXML
    Label labelArchivo = new Label();
    @FXML
    Label labelDestino = new Label();
    File fileClavePublica;
    File fileEncriptar;
    File fileDestino;

    // select a file to encrypt
    public File selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo a encriptar");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        return fileChooser.showOpenDialog(null);
    }

    // select a public key to encrypt
    public void selectPublicKey(){
        fileClavePublica = selectFile();
        if (fileClavePublica != null) {
            labelClauPublica.setText(fileClavePublica.getName());
        }
    }
    // select a file to encrypt
    public void selectFileToEncrypt(){
        fileEncriptar = selectFile();
        if (fileEncriptar != null) {
            labelArchivo.setText(fileEncriptar.getName());
        }
    }
    // select a destination to save the encrypted file
    public void selectDestination(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar destino");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileDestino = fileChooser.showSaveDialog(null);
        if (fileDestino != null) {
            labelDestino.setText(fileDestino.getName());
        }
    }

    // encrypt the file with gpg
    public void encriptarArchivo() throws IOException, PGPException {
        if (fileClavePublica != null && fileEncriptar != null && fileDestino != null) {
            encryptFile(fileEncriptar.getAbsolutePath(), fileDestino.getAbsolutePath(), fileClavePublica.getAbsolutePath(), true, true);
        }
    }

    public void returnMainScreen() {
        UtilsViews.setView("MainScreen");
    }
}
