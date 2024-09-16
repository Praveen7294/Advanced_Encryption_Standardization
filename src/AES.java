import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES extends Application {
    private static final int KEY_SIZE = 128;
    private  static final int T_LEN = 128;
    private SecretKey key;
    private byte[] IV;
    private SecretKey wrappingKey;
    TextField textField1, textField2, textField3, textField4, textField5, textField6;

    Wrapping wrap = new Wrapping();

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("AES-GCM File Encryptor");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10,10,10,10));
        Scene scene = new Scene(root, 800,400);

        Button selectFileButton = new Button("Select File");
        selectFileButton.setId("selectFileButton");

        Region buttonSpace = new Region();
        buttonSpace.setMinWidth(85);

        HBox button = new HBox();
        button.getChildren().addAll(buttonSpace, selectFileButton);

        Text statusText = new Text("Choose a file to encrypt or decrypt.");
        statusText.setFont(new Font("Arial",16));

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(button, statusText);

        Region mainSpace = new Region();
        mainSpace.setMinWidth(250);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(mainSpace, vBox);


        HBox buttons = new HBox(120);
        Button encryptButton = new Button("Encrypt");
        Button decryptButton = new Button("Decrypt");

        Region space2 = new Region();
        space2.setMinWidth(20);

        HBox secret_Key = new HBox(10);
        Text sk1 = new Text("Secret Key:");
        sk1.setFont(new Font("Arial",14));
        textField1 = new TextField();
        textField1.setPrefWidth(220);
        Button copy1 = new Button("copy");

        Text sk2 = new Text("Secret Key:");
        sk2.setFont(new Font("Arial",14));
        textField3 = new TextField();
        textField3.setPrefWidth(250);
        secret_Key.getChildren().addAll(sk1,textField1, copy1, space2, sk2, textField3);

        Region space1 = new Region();
        space1.setMinWidth(45);

        Region space3 = new Region();
        space3.setMinWidth(75);

        HBox Iv = new HBox(10);
        Text iv1 = new Text("IV:");
        iv1.setFont(new Font("Arial",14));
        textField2 = new TextField();
        textField2.setPrefWidth(220);
        Button copy2 = new Button("copy");

        Text iv2 = new Text("IV:");
        iv2.setFont(new Font("Arial",14));
        textField4 = new TextField();
        textField4.setPrefWidth(250);
        Iv.getChildren().addAll( space1, iv1, textField2, copy2, space3, iv2, textField4);

        HBox Wrapping_key = new HBox(10);
        Text Wrapping_key1 = new Text("Wrap Key:");
        Wrapping_key1.setFont(new Font("Arial",14));
        textField5 = new TextField();
        textField5.setPrefWidth(220);
        Button copy3 = new Button("copy");

        Region space4 = new Region();
        space4.setMinWidth(25);

        Region space5 = new Region();
        space5.setMinWidth(0);

        Text Wrapping_key2 = new Text("Wrap Key:");
        Wrapping_key2.setFont(new Font("Arial",14));
        textField6 = new TextField();
        textField6.setPrefWidth(250);
        Wrapping_key.getChildren().addAll(space5,  Wrapping_key1, textField5, copy3,space4, Wrapping_key2, textField6);

        encryptButton.setDisable(true);
        decryptButton.setDisable(true);

        selectFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File to Encrypt/Decrypt");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                selectFileButton.setUserData(selectedFile); // Store the selected file
                statusText.setText("Selected File: "+selectedFile.getName());
                encryptButton.setDisable(false); // Enable encryption button
                decryptButton.setDisable(false); // Enable decryption button
            } else {
                statusText.setText("No file selected.");
            }
        });

        encryptButton.setOnAction(event -> encryptFile(selectFileButton, statusText));
        decryptButton.setOnAction(event -> {
            wrappingKey = new SecretKeySpec(decode(textField6.getText()),"AES");
            byte[] unWrap_key = decode(textField3.getText());
            try {
                key = wrap.unwrapKey(unWrap_key,wrappingKey);
            } catch (Exception e){
                e.printStackTrace();
            }
            IV = decode(textField4.getText());
            decryptFile(selectFileButton, statusText);
        });

        copy1.setOnAction(e -> {
            // Get the system clipboard
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            // Set the text to copy
            content.putString(textField1.getText());

            // Place the text in the clipboard
            clipboard.setContent(content);

            // Optional: Indicate that the text has been copied
            System.out.println("Text copied to clipboard.");
        });

        copy2.setOnAction(e -> {
            // Get the system clipboard
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            // Set the text to copy
            content.putString(textField2.getText());

            // Place the text in the clipboard
            clipboard.setContent(content);

            // Optional: Indicate that the text has been copied
            System.out.println("Text copied to clipboard.");
        });

        copy3.setOnAction(e -> {
            // Get the system clipboard
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            // Set the text to copy
            content.putString(textField5.getText());

            // Place the text in the clipboard
            clipboard.setContent(content);

            // Optional: Indicate that the text has been copied
            System.out.println("Text copied to clipboard.");
        });

        Region space = new Region();
        space.setMinHeight(20);

        Region infoSpace = new Region();
        infoSpace.setMinHeight(120);

        Label info = new Label("Note:- For decryption fill first Secret Key and IV then click on Decrypt button " +
                "and also make sure you fill the right Secret,IV and Wrap Key");
        info.setStyle("-fx-text-fill: red;");

        Region edSpace1 = new Region();
        edSpace1.setMinWidth(50);

        Region edSpace2 = new Region();
        edSpace2.setMinWidth(110);

        buttons.getChildren().addAll(edSpace1, encryptButton, edSpace2, decryptButton);
        root.getChildren().addAll(hBox ,space ,buttons ,secret_Key ,Iv ,Wrapping_key ,infoSpace ,info);

        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            key = generateKey();// Generate the encryption key
            wrappingKey = generateKey();
        } catch (NoSuchAlgorithmException e) {
            statusText.setText("Error generating key: " + e.getMessage());
        }
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    private void encryptFile(Button selectFileButton, Text statusText){
        File selectedFile = (File) selectFileButton.getUserData();
        if (selectedFile == null) {
            statusText.setText("No file selected to encrypt.");
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] data = new byte[(int) selectedFile.length()];
            int totalBytesRead = 0;
            int bytesRead;

            // Read in a loop until the entire array is filled or end of file is reached
            while (totalBytesRead < data.length) {
                // Read into the remaining part of the array
                bytesRead = fis.read(data, totalBytesRead, data.length - totalBytesRead);

                if (bytesRead == -1) { // If end of file is reached
                    break; // Exit the loop
                }

                totalBytesRead += bytesRead; // Update the total bytes read
            }

            byte[] new_data = encrypt(data);

            System.out.println("Secret Key: "+key);

            byte[] wrap_key = wrap.wrapKey(key,wrappingKey);
            textField1.setText(encode(wrap_key));
            textField2.setText(encode(IV));
            textField5.setText(encode(wrappingKey.getEncoded()));

            FileOutputStream fos = new FileOutputStream(selectedFile);
            fos.write(new_data);
            fos.close();
            fis.close();
            statusText.setText("File is encrypted");
        } catch (IOException e){
            statusText.setText("Error during encryption: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Encryption
    private byte[] encrypt(byte[] data) throws Exception{
        Cipher encryptionCypher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCypher.init(Cipher.ENCRYPT_MODE,key);
        IV = encryptionCypher.getIV();
        return encryptionCypher.doFinal(data);
    }

    private void decryptFile(Button selectFileButton, Text statusText){
        File selectedFile = (File) selectFileButton.getUserData();
        if (selectedFile == null) {
            statusText.setText("No file selected to encrypt.");
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] data = new byte[(int) selectedFile.length()];
            int totalBytesRead = 0;
            int bytesRead;

            // Read in a loop until the entire array is filled or end of file is reached
            while (totalBytesRead < data.length) {
                // Read into the remaining part of the array
                bytesRead = fis.read(data, totalBytesRead, data.length - totalBytesRead);

                if (bytesRead == -1) { // If end of file is reached
                    break; // Exit the loop
                }

                totalBytesRead += bytesRead; // Update the total bytes read
            }

            byte[] originalData = decrypt(data);

            System.out.println("Secret Key: "+key);

            textField1.setText("");
            textField2.setText("");

            FileOutputStream fos = new FileOutputStream(selectedFile);
            fos.write(originalData);
            fos.close();
            fis.close();
            statusText.setText("File is decrypted");
        } catch (IOException e){
            statusText.setText("Error during encryption: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Decryption
    private byte[] decrypt(byte[] data) throws Exception{
        Cipher decryptionCypher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,IV);
        decryptionCypher.init(Cipher.DECRYPT_MODE,key,spec);
        return decryptionCypher.doFinal(data);
    }

    private String encode(byte[] data){ return Base64.getEncoder().encodeToString(data); }
    private byte[] decode(String data) { return Base64.getDecoder().decode(data); }

    public static void main(String[] args) {
        launch(args);
    }
}