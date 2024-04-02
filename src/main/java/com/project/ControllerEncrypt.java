package com.project;

import javafx.event.ActionEvent;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRing;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
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
    PGPPublicKey publicKey;

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
            // Read the public key
            try (InputStream in = new FileInputStream(fileClavePublica)) {
                JcaPGPPublicKeyRingCollection pgpPub = new JcaPGPPublicKeyRingCollection(PGPUtil.getDecoderStream(in));
                Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();
                while (rIt.hasNext()) {
                    PGPPublicKeyRing kRing = rIt.next();
                    Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
                    while (kIt.hasNext()) {
                        PGPPublicKey k = kIt.next();
                        if (k.isEncryptionKey()) {
                            publicKey = k;
                        }
                    }
                }
            } catch (IOException | PGPException e) {
                e.printStackTrace();
            }
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

    // encrypt the file with OpenPGP
    public void encriptarArchivo() {
    if (fileEncriptar != null && fileDestino != null && publicKey != null) {
        try {
            // Create a key ring generator using the public key
            JcePublicKeyKeyEncryptionMethodGenerator keyGen = new JcePublicKeyKeyEncryptionMethodGenerator(publicKey);

            // Create an encrypted data generator
            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(true).setSecureRandom(new SecureRandom()).setProvider("BC"));
            encGen.addMethod(keyGen);

            // Create an armored output stream with the destination file
            try (OutputStream out = new BufferedOutputStream(new ArmoredOutputStream(new FileOutputStream(fileDestino)))) {
                // Create a compressed data generator
                PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

                // Create a literal data generator
                PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

                // Open a literal data object on the data generator
                try (
                     OutputStream cOut = comData.open(out);
                     OutputStream pOut = lData.open(cOut, PGPLiteralData.BINARY, fileEncriptar)) {
                    // Write the file data to the literal data object
                    Files.copy(fileEncriptar.toPath(), pOut);
                }
            }
            System.out.println("Archivo encriptado correctamente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
        public void returnMainScreen() {
        UtilsViews.setView("MainScreen");
    }
}
