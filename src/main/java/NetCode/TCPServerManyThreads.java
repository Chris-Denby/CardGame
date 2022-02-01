/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NetCode;

/**
 *
 * @author chris
 */
/**
 * Java TCP sockets provide a reliable connected communication between the client and server.(Similar to making a phone call)
 * TCP establishes a connection before the client and server can begin to communicate.
 * Threading is required for concurrency to handle mutilple clients at the same time.
 * Server sits in an infnite loop waiting for client to connect and delegates a thread to handle each connection.(Thread-per-connection)
 * Run the server program first before running the client program.
 * ******************************************************
 */
import Interface.Message;
import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingWorker;

public class TCPServerManyThreads
{
    Socket clientSocket;
    ServerSocket listenSocket;
    Connection connection;
    boolean isConnected = false;
    
    public TCPServerManyThreads()
    {
        try
        {
            // the port number the process listens at.
            int serverPort = 8888;
            // TCP server socket assigned to the port number
            listenSocket = new ServerSocket(serverPort);
            System.out.println("TCP Server running...");
            
            //Server sitting in an infinite loop waiting for clients to connect.
            while(!isConnected)
            {
                //connecting with the client established
                clientSocket = listenSocket.accept();
                // assign a new thead to deal with the client and contine to accept more clients
                connection = new Connection(clientSocket);
                isConnected = true;
                
            }
        }//end of try//end of try
        catch(IOException e) //excpetion handling
        {
        System.out.println("Listen socket:"+e.getMessage());
        }
    }//end of main  
    
    public boolean isConnected()
    {
        return isConnected;
    }
} //end of TCPServer



//seperate class to handle each connection with thread capability.
class Connection extends Thread
{
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;

    public Connection (Socket aClientSocket)
    {
        try
        {
            clientSocket = aClientSocket;
            in = new ObjectInputStream( clientSocket.getInputStream());
            out =new ObjectOutputStream( clientSocket.getOutputStream());
            this.start();
        }
        catch(IOException e) 
        {
            System.out.println("Connection:"+e.getMessage());
        }
    }

    public void run()
    {
        try 
        {
            //contine dealing with client requests
            while(true)
            {
                // read the Message instance with text sent by client
                Message m = (Message)in.readObject();
                //interpret message
                if(m.getText().equals("HANDSHAKE_REQUEST"))
                {
                    m.setText("HANDSHAKE_ACCEPT");
                    //Write the Message instance to the stream
                    System.out.println("Server - Handshake accepted");
                    out.writeObject(m);  
                }
            }//end of while
        }// end of try
        catch (EOFException e)
        {
            System.out.println("EOF:"+e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("readline:"+e.getMessage());
        }
        catch(ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        finally
        { 
            try 
            {
                clientSocket.close();
            }
            catch (IOException e)
            {
                /*close failed*/
            }
        }
    }
}

