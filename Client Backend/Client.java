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
        try
        {
            Socket clientSocket = new Socket("localhost", 5000);

            Scanner keyboard = new Scanner(System.in);

            String str = keyboard.nextLine();

            PrintWriter clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            clientOutput.println(str);

            BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client RECEIVED : "+clientInput.readLine()+"\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }
}
