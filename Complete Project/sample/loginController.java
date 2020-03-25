package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class loginController{

    // LOGIN WINDOW

    @FXML
    private TextField usernameInput;

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField roomName;

    @FXML
    private Label chatLabel;

    @FXML
    StackPane windowID;

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
            userNameLabel.setText("ERROR");
            usernameInput.clear();
        }
    }

    @FXML
    void onEnterUserSelection(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            if (!usernameInput.getText().isEmpty()) {
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
            } else {
                //duplicate username? or username input is empty
                userNameLabel.setText("ERROR");
                usernameInput.clear();
            }
        }
    }


    // JOIN ROOM WINDOW



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
        else {
            chatLabel.setText("ERROR");
            roomName.clear();
        }
    }
        @FXML
        void onEnterChatSelection(KeyEvent event) {
            if(event.getCode() == KeyCode.ENTER)
            {
                if(!roomName.getText().isEmpty()) {
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
                else {
                    chatLabel.setText("ERROR");
                    roomName.clear();
                }
            }

        }


}
