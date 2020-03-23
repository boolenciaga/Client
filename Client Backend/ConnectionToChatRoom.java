import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionToChatRoom implements Runnable
{
    //DATA MEMBERS
    private Socket socketToChatRoom;
    private final int chatRoomPort;
    private final String chatRoomName;
    private final String myUserName;

    private Thread mainThread;
    private Thread listeningThread;

    private ObjectOutputStream objectOutputToChatRoom;
    private ObjectInputStream objectInputFromChatRoom;

    private Scanner keyboard = new Scanner(System.in);

    //METHODS
    ConnectionToChatRoom(int portOfTheChatRoom, String nameOfTheChatRoom, String userName)
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
            objectOutputToChatRoom.writeUTF(myUserName);
            objectOutputToChatRoom.flush();

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
                ChatMsg chatMsg = new ChatMsg(chatText, myUserName);

                //send chat message to chat room server
                objectOutputToChatRoom.writeObject(chatMsg);
                objectOutputToChatRoom.flush();
            }
        }
        catch (IOException e) {
            System.out.println("exception caught in the run() of " + myUserName + "'s ConnectionToChatRoom");
            e.printStackTrace();
        }
    }


    private class listeningClass implements Runnable
    {
        Messages incomingMsg;

        @Override
        public void run()
        {
            try
            {
                //Continuously receive and process data from server
                while(true)
                {
                    incomingMsg = (Messages) objectInputFromChatRoom.readObject();

                    if(incomingMsg instanceof ChatMsg)
                    {
                        ChatMsg chat = (ChatMsg) incomingMsg;
                        System.out.printf("%30s: ", chat.sentBy);
                        System.out.println(chat.txt);
                    }
                    else if(incomingMsg instanceof JoinedChatMsg)
                    {
                        JoinedChatMsg newUser = (JoinedChatMsg) incomingMsg;
                        System.out.printf("%40s ", "{" + newUser.sentBy + " has joined}\n");
                    }
                    else if(incomingMsg instanceof LeftChatMsg)
                    {
                        LeftChatMsg userLeft = (LeftChatMsg) incomingMsg;
                        System.out.printf("%40s ", "{" + userLeft.sentBy + " has left}\n");
                    }
                    else if(incomingMsg instanceof ChatHistoryMsg)
                    {
                        ChatHistoryMsg msg = (ChatHistoryMsg) incomingMsg;
                        for (ChatMsg chat : msg.chatHistory) //print chat history
                        {
                            System.out.printf("%30s: ", chat.sentBy);
                            System.out.println(chat.txt);
                        }
                    }
                    else
                    {
                        System.out.println(myUserName + "'s listeningClass could not process received message\n");
                    }
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                System.out.println("exception caught in the run() of " + myUserName + "'s ConnectionToChatRoom.listeningClass");
                e.printStackTrace();
            }
        }
    }


    private void wrapSocketStreams() throws IOException
    {
        objectOutputToChatRoom = new ObjectOutputStream(socketToChatRoom.getOutputStream());
        objectInputFromChatRoom = new ObjectInputStream(socketToChatRoom.getInputStream());
    }
}
