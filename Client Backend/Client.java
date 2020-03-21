import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    public static void main(String args[])
    {
        new Client();
    }


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

            while(true)
            {
                //Collect input from client
                Scanner keyboard = new Scanner(System.in);
                System.out.print("Please enter a string: ");
                String str = keyboard.nextLine();

                //Send client data to server
                outputToServer.println(str);

                //Receive and print data from server
                System.out.println("\nClient RECEIVED : " + inputFromServer.readLine() + "\n\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
