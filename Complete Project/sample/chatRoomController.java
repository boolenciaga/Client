package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class chatRoomController implements Initializable
{
    // WINDOW INITIALIZER

    @Override
    public void initialize(URL x, ResourceBundle y)
    {
        String roomName = Global.roomNames.pop();

        chatRoomNameLabel.setText(roomName);

        //TO DO:
        //display the user name somewhere nice (Global.myUserName)

        Thread listeningThread = new Thread(new listeningClass(Global.socketMap.get(roomName)));
        listeningThread.start();
        messagingBox.requestFocus();
    }



    // GUI CONTROLS

    @FXML
    private Label chatRoomNameLabel;

    @FXML
    private TextFlow displayMessagesArea;

    @FXML
    private TextField messagingBox;

    @FXML
    private Button sendButton;


    @FXML
    void sendButtonClicked(ActionEvent event)
    {
        messageBoxEntered();
    }

    @FXML
    void onEnterChatMessage(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ENTER)
        {
            messageBoxEntered();
        }
    }

    void messageBoxEntered()
    {
        if (!messagingBox.getText().isEmpty())
        {
            //get scene name and use it to retrieve socket between this window and the ConnToChatRoom
            String sceneName = (String) sendButton.getScene().getUserData();

            Global.connectionPackage connectionWithClientObj = Global.socketMap.get(sceneName);

            try
            {
                connectionWithClientObj.out.writeUTF(messagingBox.getText());
                connectionWithClientObj.out.flush();

                messagingBox.clear(); //clear the messaging box
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    // THE LISTENING CLASS

    class listeningClass implements Runnable
    {
        Global.connectionPackage myConnection;

        listeningClass(Global.connectionPackage x)
        {
            myConnection = x;
        }

        @FXML
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    String str = myConnection.in.readUTF();
                    Text theText = new Text(str + "\n\n");

                    //Image image = new Image(new FileInputStream("C:\\Users\\chabo\\Desktop\\kanye.png")); this did not work
                    //ImageView imageView = new ImageView("https://www.nme.com/wp-content/uploads/2019/12/GettyImages-1186150147.jpg"); this worked

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayMessagesArea.getChildren().add(theText);
                            //messageDisplayArea.getChildren().add(imageView);
                        }
                    });

                    //messageLog.appendText(str + "\n\n");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}