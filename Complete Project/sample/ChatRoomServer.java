package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatRoomServer implements Runnable
{
//    //FOR TESTING
      //NOTE: i couldnt figure out whats wrong with this
//    public static void main(String[]x)
//    {
////        Thread mainThread = Thread.currentThread();
////        mainThread.setName("mainThread");
//
//        ChatRoomServer room = new ChatRoomServer("testRoom");
//
////        System.out.println("entering while loop\n");
////
////        while(true)
////        {
//////            System.out.println("loop");
////
//////            if(mainThread.getState().equals(Thread.State.BLOCKED))
//////                System.out.println("blocked.............");
////
////            if(room.membersInTheRoom.size() == 2)
////            {
////                System.out.println("printing member array list:");
////                System.out.println(room.membersInTheRoom);
////                break;
////            }
////        }
////
////        System.out.println("out of loop");
//    }

    //DATA MEMBERS
    private ServerSocket ss;
    private int chatRoomPort;
    private final String chatRoomName;
    private Thread chatRoomThread;
    private int numInRoom = 0;

    private Lock lock = new ReentrantLock(true);

    private ArrayList<MemberConnection> membersInTheRoom = new ArrayList<>();
    private ArrayBlockingQueue<Messages> msgQueue = new ArrayBlockingQueue<>(1000);
    private ArrayList<ChatMsg> chatHistory = new ArrayList<>();


    //METHODS
    ChatRoomServer(String roomName)
    {
        chatRoomName = roomName;

        try
        {
            //establish chat room on an available port
            ss = new ServerSocket(0);
            chatRoomPort = ss.getLocalPort();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        chatRoomThread = new Thread(this);
        chatRoomThread.start();
    }

    @Override
    public void run()
    {
        //turn on message publisher object for the whole room
        new Thread(new Publisher()).start();

        try
        {
            //chat room open for connections to new members
            while(true)
            {
                Socket socket = ss.accept(); //blocking thread

                ++numInRoom;

                displayConnectionStatus(socket);

                membersInTheRoom.add(new MemberConnection(socket));
            }
        }
        catch (IOException e) {
            System.out.println("exception caught in " + chatRoomName + "'s run()");
            e.printStackTrace();
        }
    }


    private class MemberConnection implements Runnable
    {
        private Socket socketToMember;
        private String memberName;
        private Thread thisThread;

        ObjectInputStream objectInputFromMember;
        ObjectOutputStream objectOutputToMember;

        MemberConnection(Socket socket)
        {
            socketToMember = socket;

            //Wrap the IO streams
            try
            {
                objectInputFromMember = new ObjectInputStream(socketToMember.getInputStream());
                objectOutputToMember = new ObjectOutputStream(socketToMember.getOutputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            //Send this connection on its own thread
            thisThread = new Thread(this);
            thisThread.start();
        }

        @Override
        public void run()
        {
            try
            {
                //read in new client's name
                memberName = objectInputFromMember.readUTF();

                //push member-has-joined and chat-history messages
                msgQueue.add(new JoinedChatMsg(memberName));
                msgQueue.add(new ChatHistoryMsg(chatHistory, chatRoomName, memberName));

                //Receive messages from clients
                while(true)
                {
                    //receive messages
                    ChatMsg receivedMsg = (ChatMsg) objectInputFromMember.readObject();

                    System.out.println("Chat Room \"" + chatRoomName + "\" RECEIVED : " + receivedMsg.txt + " {from " + memberName + "}\n");

                    //repackage msg to ensure proper sentBy value
                    receivedMsg = new ChatMsg(receivedMsg, memberName);

                    //store message in blocking queue for publisher
                    msgQueue.add(receivedMsg);
                }
            }
            catch(IOException | ClassNotFoundException e)
            {
                if(e instanceof IOException)
                {
                    msgQueue.add(new LeftChatMsg(memberName));
                    membersInTheRoom.remove(this);
                }

                System.out.println("exception caught in run() of "  + memberName + "'s MemberConnection object");
                e.printStackTrace();
            }
        }

        void sendMessageFromPublisher(Messages msg)
        {
            try
            {
                objectOutputToMember.writeObject(msg);
            }
            catch (IOException e) {
                System.out.println("exception caught in sendMessageFromPub() of " + memberName + "'s MemberConnection object");
                e.printStackTrace();
            }
        }
    }


    private class Publisher implements Runnable
    {
        @Override
        public void run()
        {
            while(true)
            {
                try
                {
                    //pull message off queue
                    Messages nextMsg = msgQueue.take();

                    //handle chat history messages differently
                    if(nextMsg instanceof ChatHistoryMsg)
                    {
                        ChatHistoryMsg historyMsg = (ChatHistoryMsg) nextMsg;

                        for (MemberConnection client : membersInTheRoom)
                        {
                            if(historyMsg.receivingName.equals(client.memberName) && client.thisThread.isAlive())
                            {
                                client.sendMessageFromPublisher(nextMsg);
                                break;
                            }
                        }
                    }
                    else
                    {
                        //propagate message to appropriate clients
                        for (MemberConnection client : membersInTheRoom)
                        {
                            if(/*!client.memberName.equals(nextMsg.doNotSendTo) && */client.thisThread.isAlive())
                                client.sendMessageFromPublisher(nextMsg);
                        }
                    }

                    //store chat messages in chatHistory
                    if(nextMsg instanceof ChatMsg)
                        chatHistory.add((ChatMsg) nextMsg);
                }
                catch (InterruptedException e) {
                    System.out.println("exception caught in Publisher's run()");
                    e.printStackTrace();
                }
            }
        }
    }


    /*********************************************************************************/


    private void displayConnectionStatus(Socket socket)
    {
        if(socket.isConnected())
        {
            System.out.println("Room-Member connection established...");
            System.out.println("Room: " + chatRoomName);
            System.out.println("(Connected to " + socket.getPort() + " [IP: " + socket.getInetAddress().getHostAddress() + "])\n");
        }
        else
            System.out.println("-- SOCKET NOT CONNECTED --\n");
    }


    int getChatRoomPort() {return chatRoomPort;}
}
