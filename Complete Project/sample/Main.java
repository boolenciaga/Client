package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        final int portForGUIandCM = 1111; //change this and run in parallel to simulate another computer

        //start this instance's ClientManager
        new Thread(new ClientManager(portForGUIandCM)).start();

        //connect GUI side with ClientManager
        Global.connectWithClientManager(portForGUIandCM);

        //start login window
        Parent root = FXMLLoader.load(getClass().getResource("ClientLogin.fxml"));
        primaryStage.setTitle("Login Window: Internet Relay Chat");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
