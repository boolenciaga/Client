package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionToChatRoom implements Runnable
{
    //DATA MEMBERS
    private final int chatRoomPort;
    private final String chatRoomName;
    private final String myUserName;
    private Thread mainThread;
    private Thread listeningThread;

    //socket connecting to chat room object
    private Socket socketWithChatRoom;
    private ObjectOutputStream objectOutputToChatRoom;
    private ObjectInputStream objectInputFromChatRoom;

    //socket connecting to messaging GUI
    private ServerSocket serverSocketForRoomGUI;
    private Socket socketWithRoomGUI;
    private ObjectOutputStream objectOutputToRoomGUI;
    private ObjectInputStream objectInputFromRoomGUI;

    //METHODS
    ConnectionToChatRoom(int portOfTheChatRoom, String nameOfTheChatRoom, String userName)
    {
        chatRoomPort = portOfTheChatRoom;
        chatRoomName = nameOfTheChatRoom;
        myUserName = userName;

        try
        {
            //establish GUI server socket on a port
            serverSocketForRoomGUI = new ServerSocket(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //start this object on its own thread
        mainThread = new Thread(this);
        mainThread.start();
    }


    @Override
    public void run()
    {
        try
        {
            //establish connection to chat room object
            socketWithChatRoom = new Socket("localhost", chatRoomPort);

            //establish connection with chat room GUI
            socketWithRoomGUI = serverSocketForRoomGUI.accept();

            if(socketWithChatRoom.isConnected() && socketWithRoomGUI.isConnected())
                System.out.println(myUserName + " is now connected to \"" + chatRoomName + "\" and its GUI...\n");
            else
                System.out.println(myUserName + "'s connection failed...\n");


            System.out.println("local port for connectObj: " + socketWithRoomGUI.getLocalPort());
            System.out.println("remote port for connectObj " + socketWithRoomGUI.getPort() + "\n");




            //wrap all the IO streams
            wrapSocketStreams();

            //output user name to chat room object
            objectOutputToChatRoom.writeUTF(myUserName);
            objectOutputToChatRoom.flush();

            //start a thread for listening to incoming messages
            listeningThread = new Thread(new listeningToChatRoomClass());
            listeningThread.start();

            System.out.println("BEGIN CHATTING...");

            //Begin message relaying
            while(true)
            {
                //collect chat input from GUI
                String chatText = objectInputFromRoomGUI.readUTF();

            System.out.println("client received : " + chatText + " from GUI***");

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


    private class listeningToChatRoomClass implements Runnable
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

                    System.out.println("received a message in listener\n");

                    if(incomingMsg.messageType.equals("ChatMsg")) //change to instanceof
                    {
                        System.out.println("chat message received!!!\n");

                        System.out.println("listener about to write to GUI socket\n");

                        ChatMsg chat = (ChatMsg) incomingMsg;

                        System.out.println("this string is being sent: " + chat.sentBy + ": " + chat.txt);

                        objectOutputToRoomGUI.writeUTF(chat.sentBy + ": " + chat.txt);
                        objectOutputToRoomGUI.flush();
//                        System.out.printf("%30s: ", chat.sentBy);
//                        System.out.println(chat.txt);
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
                        System.out.println("chat history msg received!!!\n");

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
        objectOutputToChatRoom = new ObjectOutputStream(socketWithChatRoom.getOutputStream());
        objectInputFromChatRoom = new ObjectInputStream(socketWithChatRoom.getInputStream());

        objectOutputToRoomGUI = new ObjectOutputStream(socketWithRoomGUI.getOutputStream());
        objectInputFromRoomGUI = new ObjectInputStream(socketWithRoomGUI.getInputStream());
    }


    String getChatRoomName()
    {
        return chatRoomName;
    }


    int getPortForRoomGUI()
    {
        return serverSocketForRoomGUI.getLocalPort();
    }
}
