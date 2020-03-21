import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    public static void main(String args[])
    {
        new Client();
    }

    private int myClientNumber;

    public Client()
    {
        try
        {
            //Establish connection to server
            Socket clientSocket = new Socket("localhost", 6000);

            if(clientSocket.isConnected())
                System.out.println("Connected to server...\n");
            else
                System.out.println("Connection failed...\n");

            //Wrap the IO streams
            PrintWriter outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            DataInputStream primInputFromServer = new DataInputStream(clientSocket.getInputStream());

            ObjectOutputStream objectOutputToServer = new ObjectOutputStream(clientSocket.getOutputStream());

            myClientNumber = primInputFromServer.read();
            System.out.println("MY NUM IS "+myClientNumber+"\n\n");

            while(true)
            {
                //Collect input from client
                Scanner keyboard = new Scanner(System.in);
                System.out.print("CHAT: ");
                String str = keyboard.nextLine();

                Messages.ChatMsg msg = new Messages.ChatMsg();
                msg.txt = str;
                msg.channelToPublishTo = "The Chat Room";
                msg.sentByUser = "Client #" + String.valueOf(myClientNumber);

                //Send client data to server
                objectOutputToServer.writeObject(msg);
//                outputToServer.println(str);

                //Receive and print data from server
//                System.out.println("\nClient RECEIVED : " + inputFromServer.readLine() + "\n\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
