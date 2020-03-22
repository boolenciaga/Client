import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionToChatRoom implements Runnable
{
    //DATA MEMBERS
    Socket socketToChatRoom;
    private final int chatRoomPort;
    private final String chatRoomName;
    private final String myUserName;

    private Thread mainThread;
    private Thread listeningThread;

    ObjectOutputStream objectOutputToServer;
    ObjectInputStream objectInputFromServer;

    Scanner keyboard = new Scanner(System.in);

    //METHODS
    public ConnectionToChatRoom(int portOfTheChatRoom, String nameOfTheChatRoom, String userName)
    {
        chatRoomPort = portOfTheChatRoom;
        chatRoomName = nameOfTheChatRoom;
        myUserName = userName;

        //start this object on its own thread
        mainThread = new Thread(this);
        mainThread.start();
    }


    @Override
    public void run()
    {
        try
        {
            //Establish connection to server
            socketToChatRoom = new Socket("localhost", chatRoomPort);

            if(socketToChatRoom.isConnected())
                System.out.println(myUserName + " is now connected to \"" + chatRoomName + "\"...\n");
            else
                System.out.println(myUserName + "'s connection failed...\n");

            //Wrap the IO streams
            wrapSocketStreams();

            //output user name to chat room
            objectOutputToServer.writeUTF(myUserName);
            objectOutputToServer.flush();

            //Start a thread for listening to incoming messages
            listeningThread = new Thread(new listeningClass());
            listeningThread.start();

            System.out.println("BEGIN CHATTING...");

            //Begin message relaying
            while(true)
            {
                //collect chat input from client
                String chatText = keyboard.nextLine();

                //wrap input in a message
                ChatMsg chatMsg = new ChatMsg(chatText, chatRoomName, myUserName);

                //send chat message to chat room server
                objectOutputToServer.writeObject(chatMsg);
                objectOutputToServer.flush();
            }
        }
        catch (IOException e) {
            System.out.println("exception caught in the run() of " + myUserName + "'s ConnectionToChatRoom\n");
            e.printStackTrace();
        }
    }


    private class listeningClass implements Runnable
    {
        ChatMsg incomingMsg;

        @Override
        public void run()
        {
            try
            {
                //Continuously receive and print data from server
                while(true)
                {
                    incomingMsg = (ChatMsg) objectInputFromServer.readObject();
                    System.out.printf("%30s: ", incomingMsg.sentBy);
                    System.out.println(incomingMsg.txt);
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                System.out.println("exception caught in the run() of " + myUserName + "'s ConnectionToChatRoom.listeningClass\n");
                e.printStackTrace();
            }
        }
    }


    private void wrapSocketStreams() throws IOException
    {
        objectOutputToServer = new ObjectOutputStream(socketToChatRoom.getOutputStream());
        objectInputFromServer = new ObjectInputStream(socketToChatRoom.getInputStream());
    }
}
