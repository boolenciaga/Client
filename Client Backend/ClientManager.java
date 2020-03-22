import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientManager
{
    public static void main(String[] args)
    {
        new ClientManager();
    }

    private Socket socket;
    private ObjectOutputStream objectOutputToServerManager;
    private ObjectInputStream objectInputFromServerManager;

    private ArrayList<ConnectionToChatRoom> chatRoomConnections = new ArrayList<>();

    ClientManager()
    {
        try
        {
            //establish socket to ServerManager
            socket = new Socket("localhost", 7777);

            if(socket.isConnected())
                System.out.println("Connected to ServerManager...\n");
            else
                System.out.println("Connection failed...\n");

            wrapSocketStreams();

            //collect and send user name to ServerManager
            System.out.print("Enter your name: ");
            Scanner keyboard = new Scanner(System.in);
            final String userName = keyboard.nextLine();
            objectOutputToServerManager.writeUTF(userName);
            objectOutputToServerManager.flush();

            //DISABLED THE LOOP BECAUSE ONLY ONE CONSOLE == CONFLICTS
//            while(true)
//            {
                //collect and send chat room requests
                System.out.print("\nWhat chat room would you like to join: ");
                String chatRoomDesired = keyboard.nextLine();
                objectOutputToServerManager.writeUTF(chatRoomDesired);
                objectOutputToServerManager.flush();

                //receive chat room info from ServerManager
                ChatRoomInfoMsg roomMsg = (ChatRoomInfoMsg) objectInputFromServerManager.readObject();

                if(roomMsg.hadToBeCreated)
                    System.out.println("the room \"" + roomMsg.chatRoomName + "\" didn't exist, was created by server...");

                //make a client object to handle the connection and chatting
                chatRoomConnections.add(new ConnectionToChatRoom(roomMsg.chatRoomPort, roomMsg.chatRoomName, userName));
//            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("exception caught in ClientManager's constructor!\n");
            e.printStackTrace();
        }
    }


    private void wrapSocketStreams()
    {
        try {
            objectOutputToServerManager = new ObjectOutputStream(socket.getOutputStream());
            objectInputFromServerManager = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("exception caught in wrapSocketStream()\n\n");
            e.printStackTrace();
        }
    }
}
