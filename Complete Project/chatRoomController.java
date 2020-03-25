package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class chatRoomController implements Initializable
{
    @FXML
    private TextFlow messageDisplayArea;

    @FXML
    private TextArea messageLog;

    @FXML
    private TextField messagingBox;

    @FXML
    private Button sendButton;



    @Override
    public void initialize(URL x, ResourceBundle y)
    {
        String sceneName = Global.roomNames.pop();

        //TO DO:
        //set the chat window name

        Thread listeningThread = new Thread(new listeningClass(Global.socketMap.get(sceneName)));
        listeningThread.start();
    }



    @FXML
    void sendButtonClicked(ActionEvent event)
    {
        if(!messagingBox.getText().isEmpty())
        {
            //get scene name and use it to retrieve socket between this window and the ConnToChatRoom
//            Node source = (Node) event.getSource();
            String sceneName = (String) sendButton.getScene().getUserData();

//            System.out.println(sceneName + " vs " + sendButton.getScene() + " vs " + sendButton.getScene().getUserData());

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
                            messageDisplayArea.getChildren().add(theText);
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