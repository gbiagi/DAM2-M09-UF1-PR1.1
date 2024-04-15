package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;

import java.io.*;
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
            try {
                // Open the encrypted file
                try (InputStream in = new BufferedInputStream(new FileInputStream(fileDesencriptar))) {
                    // Create a PGP object factory
                    PGPObjectFactory pgpF = new PGPObjectFactory(PGPUtil.getDecoderStream(in), new JcaKeyFingerprintCalculator());
                    System.out.println("PGP OK");

                    // Read the next object from the factory
                    Object o = pgpF.nextObject();
                    System.out.println("Object OK");
                    if (o instanceof PGPEncryptedDataList) {
                        System.out.println("aaaaaaaaaaaaaaaaaaaaa");
                        // Found an encrypted data list
                        PGPEncryptedDataList enc = (PGPEncryptedDataList) o;
                        System.out.println("Encrypted data OK");

                        // Get the encrypted data object
                        PGPPublicKeyEncryptedData pbe = (PGPPublicKeyEncryptedData) enc.get(0);
                        System.out.println("Encrypted data object OK");

                        // Find the secret key
                        PGPSecretKey pgpSecKey = findSecretKey(fileClavePrivada.getAbsolutePath(), pbe.getKeyID(), textPassword.getText().toCharArray());
                        if (pgpSecKey == null) {
                            throw new IllegalArgumentException("Secret key for message not found.");
                        }
                        System.out.println("Secret key OK");

                        // Decrypt the data
                        InputStream clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(textPassword.getText().toCharArray()))));
                        System.out.println("Data decrypted OK");

                        // Write the decrypted data to the destination file
                        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(fileDestino))) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = clear.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                            }
                        }
                        System.out.println("Archivo desencriptado correctamente");
                    } else {
                        throw new IllegalArgumentException("No encrypted data found in the file.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method to find the secret key from the key ring file
    private PGPSecretKey findSecretKey(String keyRingFile, long keyID, char[] pass) throws IOException {
        try (InputStream keyIn = new FileInputStream(keyRingFile)) {
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection((Collection<PGPSecretKeyRing>) keyIn);
            PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
            if (pgpSecKey == null) {
                return null;
            }
            return pgpSecKey;
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
    }

    public void returnMainScreen() {
        UtilsViews.setView("MainScreen");
    }

}
