package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable
{
    // sockets connecting to ServerManager
    private Socket socket;
    private ObjectOutputStream objectOutputToServerManager;
    private ObjectInputStream objectInputFromServerManager;

    // sockets connecting to GUI
    ServerSocket serverSocketForGUI;
    private Socket primaryGUISocket;
    private ObjectOutputStream objectOutputToGUI;
    private ObjectInputStream objectInputFromGUI;

    private ArrayList<ConnectionToChatRoom> chatRoomConnections = new ArrayList<>();

    ClientManager(int port)
    {
        try
        {
            //create server socket for GUI to connect
            serverSocketForGUI = new ServerSocket(port);

            //establish socket connection to ServerManager
            socket = new Socket("localhost", 7777);

            if(socket.isConnected())
                System.out.println("Connected to ServerManager...\n");
            else
                System.out.println("Connection to ServerManager failed...\n");
        }
        catch (IOException e)
        {
            System.out.println("exception caught in ClientManager's constructor!");
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            //await socket connection to primary GUI
            primaryGUISocket = serverSocketForGUI.accept();

            if(primaryGUISocket.isConnected())
                System.out.println("Connected to GUI...\n");
            else
                System.out.println("Connection to GUI failed...\n");

            //wrap all streams
            wrapSocketStreams();

            //collect user name from GUI and send to ServerManager
            final String myUserName = objectInputFromGUI.readUTF();
            objectOutputToServerManager.writeUTF(myUserName);
            objectOutputToServerManager.flush();

            while(true)
            {
                //collect room requests from GUI
                String chatRoomDesired = objectInputFromGUI.readUTF();

                //check if this ClientManager already has a connection with requested room
                boolean roomAlreadyConnected = false;
                for(ConnectionToChatRoom connection : chatRoomConnections)
                {
                    if(chatRoomDesired.equals(connection.getChatRoomName()))
                    {
                        //maybe send "room exists" info back to GUI
                        //maybe handle the case where request room that exists and is already open on GUI side
                        roomAlreadyConnected = true;
                        break;
                    }
                }

                if(roomAlreadyConnected)
                {
                    //send do-not-open signal to GUI
                    objectOutputToGUI.writeBoolean(false);
                    objectOutputToGUI.flush();
                    continue; //skip the rest of loop body
                }
                else //send chat room requests to ServerManager
                {
                    objectOutputToGUI.writeBoolean(true); //send ok-to-open signal to GUI
                    objectOutputToGUI.flush();
                    objectOutputToServerManager.writeUTF(chatRoomDesired);
                    objectOutputToServerManager.flush();
                }

                //receive chat room info from ServerManager
                ChatRoomInfoMsg roomMsg = (ChatRoomInfoMsg) objectInputFromServerManager.readObject();

                if(roomMsg.hadToBeCreated)
                    System.out.println("the room \"" + roomMsg.chatRoomName + "\" didn't exist, was created by server...");

                //make an object to handle the connection and chatting
                ConnectionToChatRoom newChatRoom = new ConnectionToChatRoom(roomMsg.chatRoomPort, roomMsg.chatRoomName, myUserName);
                chatRoomConnections.add(newChatRoom);

                //send GUI the name/port to connect to the new ConnectionToChatRoom
                objectOutputToGUI.writeUTF(roomMsg.chatRoomName);
                objectOutputToGUI.writeInt(newChatRoom.getPortForRoomGUI());
                objectOutputToGUI.flush();
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void wrapSocketStreams()
    {
        try
        {
            //wrap ServerManager socket streams
            objectOutputToServerManager = new ObjectOutputStream(socket.getOutputStream());
            objectInputFromServerManager = new ObjectInputStream(socket.getInputStream());

            //wrap GUI socket streams
            objectInputFromGUI = new ObjectInputStream(primaryGUISocket.getInputStream());
            objectOutputToGUI = new ObjectOutputStream(primaryGUISocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("exception caught in wrapSocketStream()\n\n");
            e.printStackTrace();
        }
    }
}
