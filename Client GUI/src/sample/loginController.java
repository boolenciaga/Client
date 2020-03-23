package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class loginController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Button nextButton;

    @FXML
    private TextField usernameInput;

    @FXML
    void nextButtonClicked(ActionEvent event) {
            if(!usernameInput.getText().isEmpty()) {

                // Input Milad's Code

                // If username input is successful -> go to next Window


            }
            else {
                // Duplicate username? or username input is empty
                userNameLabel.setText("INVALID INPUT - PLEASE TRY AGAIN");
                usernameInput.clear();
            }
    }

}

