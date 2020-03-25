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

    // LOGIN WINDOW

    @FXML
    private Button nextButton;

    @FXML
    private TextField usernameInput;

    @FXML
    private Label userNameLabel;

    @FXML
    void nextButtonClicked(ActionEvent event) throws IOException
    {
        if(!usernameInput.getText().isEmpty())
        {
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
        else
        {
            //duplicate username? or username input is empty
            userNameLabel.setText("INVALID INPUT - PLEASE TRY AGAIN");
            usernameInput.clear();
        }
    }


    // JOIN ROOM WINDOW

    @FXML
    private Button joinButton;

    @FXML
    private TextField roomName;

    @FXML
    void joinButtonClicked(ActionEvent event)
    {
        if(!roomName.getText().isEmpty())
        {
            try
            {
                //send room request to ClientManager
                Global.toClientManager.writeUTF(roomName.getText());
                Global.toClientManager.flush();

                //if ClientManager signals valid request
                if(Global.fromClientManager.readBoolean())
                {
                    //read in connection info
                    String roomName = Global.fromClientManager.readUTF();
                    int portWithClientObject = Global.fromClientManager.readInt();

                    Global.pushConnectionPackage(roomName, portWithClientObject);

                    Global.roomNames.push(roomName);

                    //open a chat window with the name of the room
                    FXMLLoader anotherLoader = new FXMLLoader(getClass().getResource("ChatRoom.fxml")) ; // FXML for second stage
                    Parent anotherRoot = anotherLoader.load();
                    Scene anotherScene = new Scene(anotherRoot);
                    anotherScene.setUserData(roomName); //important to set the name
                    Stage anotherStage = new Stage();
                    anotherStage.setScene(anotherScene);
                    anotherStage.show();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

