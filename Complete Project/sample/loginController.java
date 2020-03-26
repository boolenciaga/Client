package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class loginController{

    // LOGIN WINDOW

    @FXML
    private TextField usernameInput;


    @FXML
    void nextButtonClicked(Event event) throws IOException
    {
        userNameEntered(event);
    }

    @FXML
    void onEnterUserSelection(KeyEvent event) throws IOException
    {
        if (event.getCode() == KeyCode.ENTER)
        {
            userNameEntered(event);
        }
    }

    void userNameEntered(Event event) throws IOException
    {
        if(!usernameInput.getText().isEmpty())
        {
            //set user name
            Global.myUserName = usernameInput.getText();

            //send user name to ClientManager
            Global.toClientManager.writeUTF(usernameInput.getText());
            Global.toClientManager.flush();

            //if username input is successful -> go to next Window
            Parent chatSelectionWindow = FXMLLoader.load(getClass().getResource("ChooseChat.fxml"));
            Scene chatSelectionScene = new Scene(chatSelectionWindow);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle("Chat Selection Window: Internet Relay Chat");
            window.setScene(chatSelectionScene);
            window.show();
        }
    }
}

