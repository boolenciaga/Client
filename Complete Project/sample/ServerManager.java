package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerManager
{
    public static void main(String[] args)
    {
        new ServerManager();
    }

    // DATA MEMBERS

    //the port this ServerManager is on
    private final int serverPort = 9000;

    //an ArrayList of the different ClientManagers connected to this server
    private ArrayList<ClientConnection> clientList = new ArrayList<>();

    //a map which holds the currently existing chat rooms
    private HashMap<String, ChatRoomServer> chatRoomMap = new HashMap<>();

    // METHODS

    private ServerManager()
    {
        System.out.println("ServerManager turned on\n");

        try
        {
            //establish server on a port
            ServerSocket ss = new ServerSocket(serverPort);

            //ServerManager open for connections to ClientManagers
            while(true)
            {
                Socket socket = ss.accept(); //connections to cMs

                if(socket.isConnected())
                {
                    System.out.println("ServMan connected to a ClientManager");
                    System.out.println(socket.getInetAddress().getHostAddress() + " (port " + socket.getPort() + ")\n");
                }
                else
                    System.out.println("ServMan didn't connect !!!\n");

                clientList.add(new ClientConnection(socket));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ClientConnection implements Runnable
    {
        //DATA MEMBERS
        private Socket socket;
        private String clientsUserName;
        private Thread clientThread;

        //this connection's socket streams
        private ObjectInputStream objectInputFromClientManager;
        private ObjectOutputStream objectOutputToClientManager;

        //METHODS
        ClientConnection(Socket socket)
        {
            this.socket = socket;

            wrapSocketStreams();

            //start this object's thread
            clientThread = new Thread(this);
            clientThread.start();
        }

        @Override
        public void run()
        {
            try
            {
                //get user name from cM
                clientsUserName = objectInputFromClientManager.readUTF();

                //process chat room requests
                while(true)
                {
                    //read in a chat room name request
                    String chatRoomName = objectInputFromClientManager.readUTF();
                    System.out.println("got chat room request \"" + chatRoomName + "\" from " + clientsUserName); //test print

                    boolean isNewRoom = false;

                    //if requested room doesn't exist create it
                    if(!chatRoomMap.containsKey(chatRoomName))
                    {
                        chatRoomMap.put(chatRoomName, new ChatRoomServer(chatRoomName));
                        System.out.println(chatRoomName + " was created");
                        isNewRoom = true;
                    }

                    ChatRoomServer theRequestedRoom = chatRoomMap.get(chatRoomName);

                    System.out.println("The room's port # is ... " + theRequestedRoom.getChatRoomPort() + "\n");

                    //send back room's info to cM
                    ChatRoomInfoMsg roomMsg = new ChatRoomInfoMsg(theRequestedRoom.getChatRoomPort(), chatRoomName, isNewRoom);
                    objectOutputToClientManager.writeObject(roomMsg);
                    objectOutputToClientManager.flush();
                }
            }
            catch (IOException e)
            {
                System.out.println("exception caught in one of sM's ClientConnection object's run()");
                e.printStackTrace();
            }
        }

        private void wrapSocketStreams()
        {
            try {
                objectOutputToClientManager = new ObjectOutputStream(socket.getOutputStream());
                objectInputFromClientManager = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("exception caught in wrapSocketStream()\n\n");
                e.printStackTrace();
            }
        }
    }
}
