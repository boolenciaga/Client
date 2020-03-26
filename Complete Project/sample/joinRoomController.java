package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class joinRoomController implements Initializable {
    // JOIN ROOM WINDOW
    @FXML
    private TextField roomNameField;

    @FXML
    private Label usernameChatSelectionLabel;                       // Needs to be implemented by runLater


    // WINDOW INITIALIZER

    @Override
    public void initialize(URL x, ResourceBundle y)
    {
        usernameChatSelectionLabel.setText(Global.myUserName + ": ");
    }


    @FXML
    void joinButtonClicked(ActionEvent event)
    {
        roomNameEntered(event);
    }

    @FXML
    void onEnterChatSelection(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ENTER)
        {
            roomNameEntered(event);
        }
    }

    void roomNameEntered(Event event)
    {
        if(!roomNameField.getText().isEmpty())
        {
            try
            {
                //send room request to ClientManager
                Global.toClientManager.writeUTF(roomNameField.getText());
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
                    anotherStage.setTitle("Chat Room Window: Internet Relay Chat");
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
