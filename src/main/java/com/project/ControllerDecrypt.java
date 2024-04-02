package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
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
    TextField textPassword = new TextField();
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
    if (fileDesencriptar != null && fileDestino != null && fileClavePrivada != null && !textPassword.getText().isEmpty()) {
        // Read the private key
        try (InputStream in = new BufferedInputStream(new FileInputStream(fileClavePrivada))) {
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(in), new JcaKeyFingerprintCalculator());
            Iterator<PGPSecretKeyRing> rIt = pgpSec.getKeyRings();
            while (rIt.hasNext()) {
                PGPSecretKeyRing kRing = rIt.next();
                Iterator<PGPSecretKey> kIt = kRing.getSecretKeys();
                while (kIt.hasNext()) {
                    PGPSecretKey k = kIt.next();
                    if (k.isSigningKey()) {
                        privateKey = k.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(textPassword.getText().toCharArray()));
                        // Use privateKey here
                    }
                }
            }
            
            // Decrypt the file
            try (InputStream in2 = new BufferedInputStream(new FileInputStream(fileDesencriptar));
                 OutputStream out = new BufferedOutputStream(new FileOutputStream(fileDestino))) {
                PGPObjectFactory pgpF = new PGPObjectFactory(PGPUtil.getDecoderStream(in2), new JcaKeyFingerprintCalculator());
                PGPEncryptedDataList enc;
                Object o = pgpF.nextObject();
                if (o instanceof PGPEncryptedDataList) {
                    enc = (PGPEncryptedDataList) o;
                } else {
                    enc = (PGPEncryptedDataList) pgpF.nextObject();
                }
                PGPPublicKeyEncryptedData pbe = getPublicKeyEncryptedData(enc);
                try (InputStream clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey))) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = clear.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }
}

    private PGPPublicKeyEncryptedData getPublicKeyEncryptedData(PGPEncryptedDataList enc) {
        Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects();
        PGPPublicKeyEncryptedData pbe = null;
        while (it.hasNext()) {
            pbe = (PGPPublicKeyEncryptedData) it.next();
            if (pbe.getKeyID() == privateKey.getKeyID()) {
                break;
            }
        }
        if (pbe == null) {
            throw new IllegalArgumentException("Secret key for message not found.");
        }
        return pbe;
    }

    public void returnMainScreen() {
        UtilsViews.setView("MainScreen");
    }

}
