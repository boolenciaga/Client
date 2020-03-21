import java.io.*;
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

            //Start a thread for listening to incoming messages
            listeningThread = new Thread(new listeningClass());
            listeningThread.start();

            //Begin message relaying
            while(true)
            {
                //collect input from client
                System.out.print("CHAT: ");
                String str = keyboard.nextLine();

                //wrap input in a message
                Messages.ChatMsg msg = new Messages.ChatMsg(str, "The Chat Room", "Client #" + String.valueOf(myClientNumber));

                //send client message to server
                objectOutputToServer.writeObject(msg);
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
        Messages.ChatMsg incomingMsg;

        @Override
        public void run()
        {
            try
            {
                //Continuously receive and print data from server
                while(true)
                {
                    incomingMsg = (Messages.ChatMsg) objectInputFromServer.readObject();
                    System.out.println("\t\t\t\t\t\t\t" + incomingMsg.sentByUser + ": " + incomingMsg.txt);
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
