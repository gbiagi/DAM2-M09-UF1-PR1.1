package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

public class ControllerDecrypt {
    @FXML
    Label labelClauPrivada = new Label();
    @FXML
    Label labelArchivo = new Label();
    @FXML
    Label labelDestino = new Label();
    @FXML
    PasswordField textPassword = new PasswordField();
    File fileClavePrivada;
    File fileDesencriptar;
    File fileDestino;
    PGPPrivateKey privateKey;

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

    public void selectPrivateKey() {
        fileClavePrivada = selectFile();
        if (fileClavePrivada != null) {
            labelClauPrivada.setText(fileClavePrivada.getName());
        }
    }
    public void selectFileToDecrypt(){
        fileDesencriptar = selectFile();
        if (fileDesencriptar != null) {
            labelArchivo.setText(fileDesencriptar.getName());
        }
    }
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

    public void desencriptarArchivo() {
        if (fileDesencriptar != null && fileDestino != null && fileClavePrivada != null) {
            try (InputStream in = PGPUtil.getDecoderStream(new FileInputStream(fileDesencriptar))) {
                // Load the secret key
                PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                        PGPUtil.getDecoderStream(new FileInputStream(fileClavePrivada)),
                        new JcaKeyFingerprintCalculator());

                // Get the first secret key
                PGPSecretKey secretKey = pgpSec.getKeyRings().next().getSecretKey();

                // Decrypt the file
                try (InputStream keyIn = new FileInputStream(fileClavePrivada)) {
                    PGPPrivateKey privateKey = secretKey.extractPrivateKey(
                            new JcePBESecretKeyDecryptorBuilder()
                                    .setProvider("BC")
                                    .build(textPassword.getText().toCharArray()));

                    PGPObjectFactory pgpFactory = new PGPObjectFactory(in, new JcaKeyFingerprintCalculator());
                    Object message = pgpFactory.nextObject();
                    PGPLiteralData literalData = null;

                    if (message instanceof PGPEncryptedDataList) {
                        PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) message;
                        PGPPublicKeyEncryptedData encryptedData = (PGPPublicKeyEncryptedData) encryptedDataList.get(0);

                        // Decrypt the data
                        InputStream clear = encryptedData.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder()
                                .setProvider("BC").build(privateKey));

                        // Read the decrypted data
                        PGPObjectFactory plainFactory = new PGPObjectFactory(clear, new JcaKeyFingerprintCalculator());
                        message = plainFactory.nextObject();

                        if (message instanceof PGPLiteralData) {
                            literalData = (PGPLiteralData) message;
                        }
                    }

                    // Write the decrypted data to the output file
                    try (InputStream dataStream = literalData.getInputStream();
                         OutputStream out = new BufferedOutputStream(new FileOutputStream(fileDestino))) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = dataStream.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                }
                System.out.println("Archivo desencriptado correctamente");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void returnMainScreen() {
        UtilsViews.setView("MainScreen");
    }

}
