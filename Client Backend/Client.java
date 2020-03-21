import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    public static void main(String args[])
    {
        new Client();
    }

    Socket clientSocket;
    private int myClientNumber;
    private String myClientName;

    private Thread listeningThread;

    ObjectOutputStream objectOutputToServer;
    ObjectInputStream objectInputFromServer;


    public Client()
    {
        Scanner keyboard = new Scanner(System.in);

        try
        {
            //Establish connection to server
            clientSocket = new Socket("localhost", 6000);

            if(clientSocket.isConnected())
                System.out.println("Connected to server...\n");
            else
                System.out.println("Connection failed...\n");

            //Wrap the IO streams
            wrapSocketStreams();

            //Read in client number sent by server
            myClientNumber = objectInputFromServer.readInt();
            System.out.println("MY NUM IS " + myClientNumber + "\n\n");


//            System.out.print("NAME: ");
//            myClientName = keyboard.nextLine();

            switch(myClientNumber) //auto assign names for testing
            {
                case 1:
                    myClientName = "John";
                    break;
                case 2:
                    myClientName = "Kate";
                    break;
                case 3:
                    myClientName = "Marlo";
                    break;
                default:
                    myClientName = "lush";
            }

            //output client name to server
            objectOutputToServer.writeUTF(myClientName);
            objectOutputToServer.flush();


            //Start a thread for listening to incoming messages
            listeningThread = new Thread(new listeningClass());
            listeningThread.start();

            System.out.println("BEGIN CHATTING...");

            //Begin message relaying
            while(true)
            {
                //collect input from client
                String str = keyboard.nextLine();

                //wrap input in a message
                ChatMsg chatMsg = new ChatMsg(str, "The Chat Room", myClientName);

                //send client message to server
                objectOutputToServer.writeObject(chatMsg);
            }
        }
        catch (IOException e) {
            System.out.println("exception caught in Client()***\n");
            e.printStackTrace();
        }
    }


    private void wrapSocketStreams() throws IOException
    {
        objectOutputToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputFromServer = new ObjectInputStream(clientSocket.getInputStream());
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
                    System.out.printf("%30s: ", incomingMsg.sentByUser);
                    System.out.println(incomingMsg.txt);
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                System.out.println("exception caught in listeningClass-run()\n");
                //e.printStackTrace();
            }
        }
    }
}
