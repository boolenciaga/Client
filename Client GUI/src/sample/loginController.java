package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Button nextButton;

    @FXML
    private TextField usernameInput;

    @FXML
    void nextButtonClicked(ActionEvent event) throws IOException {
            if(!usernameInput.getText().isEmpty()) {


                // Input Milad's Code


                // If username input is successful -> go to next Window
                Parent chatSelectionWindow = FXMLLoader.load(getClass().getResource("ChooseChat.fxml"));
                Scene chatSelectionScene = new Scene(chatSelectionWindow);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setTitle("Chat Selection Window: Internet Relay Chat");
                window.setScene(chatSelectionScene);
                window.show();


            }
            else {
                // Duplicate username? or username input is empty
                userNameLabel.setText("INVALID INPUT - PLEASE TRY AGAIN");
                usernameInput.clear();
            }
    }
}

