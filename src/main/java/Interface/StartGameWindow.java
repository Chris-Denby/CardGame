/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Database.JSONHelper;
import NetCode.TCPServer;
import NetCode.TCPClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;


/**
 *
 * @author chris
 */
public class StartGameWindow extends JPanel
{
    StartGameWindow self;
    JTabbedPane parentTabbedPane;
    private TCPClient netClient;
    private TCPServer netServer;
    
    JButton startLocalButton = new JButton("Start local game");
    JButton startClientButton = new JButton("Join net game");
    JButton startServerButton = new JButton("Host net game");
    
    public StartGameWindow(JTabbedPane pane)
    {
        self = this;
        parentTabbedPane = pane;
        this.add(startLocalButton);
        this.add(startClientButton);
        this.add(startServerButton);
        
        JSONHelper jHelper = new JSONHelper();
        //jHelper.createCardLists();
        
        startClientButton.addActionListener((new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                showIPAddressDialog();
            }
        }));   
        
        startServerButton.addActionListener((new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
               startServer(); 
               startServerButton.setText("Server Started");
            }
        }));
        
        startLocalButton.addActionListener((new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
               startGame(); 
            }
        }));
    }
    
    private void startServer()
    {
        SwingWorker workerThread = new SwingWorker<TCPServer,Void>() 
        {
            
            @Override
            protected TCPServer doInBackground() throws Exception 
            {
                netServer = new TCPServer();
                System.out.println("Server is doInBackground");
                return netServer;
            }
            
            
            @Override
            public void done()
            {
                System.out.println("Server is done");
                try 
                {
                    netServer = (TCPServer) get();
                    if(netServer.isConnected())
                    {
                        startGame(netServer);
                    }
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(StartGameWindow.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (ExecutionException ex) 
                {
                    Logger.getLogger(StartGameWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
            
        };
        workerThread.execute();
    }
    
    private void startClient(String ipAddress)
    {
        SwingWorker workerThread = new SwingWorker<TCPClient,Void>()
        {
            @Override
            protected TCPClient doInBackground() throws Exception 
            {
                netClient = new TCPClient(ipAddress); 
                System.out.println("client is doInBackground");
                return netClient;
            }
            
            @Override
            public void done()
            {
                System.out.println("client is done");
                try 
                {
                    netClient = (TCPClient) get();
                    if(netClient.isConnected())
                    {
                        startGame(netClient);    
                    } 
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(StartGameWindow.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(self,"Failed to connect: " + ex.getMessage(),"Failed to connect",JOptionPane.ERROR_MESSAGE);
                } 
                catch (ExecutionException ex) 
                {
                    Logger.getLogger(StartGameWindow.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(self,"Failed to connect: " + ex.getMessage(),"Failed to connect",JOptionPane.ERROR_MESSAGE);
                }
            }
        };  
        workerThread.execute();
    }
    
    private void startGame()
    {
        parentTabbedPane.addTab("Game", new GameWindow(parentTabbedPane));
        parentTabbedPane.setSelectedIndex(1);
    }
    
    private void startGame(TCPClient client)
    {
        parentTabbedPane.addTab("Game", new GameWindow(parentTabbedPane, client));
        parentTabbedPane.setSelectedIndex(1);    
    }
    
    private void startGame(TCPServer server)
    {
        parentTabbedPane.addTab("Game", new GameWindow(parentTabbedPane, server));
        parentTabbedPane.setSelectedIndex(1);    
    }
    
    public void showIPAddressDialog()
    {
        //String ipAddress = (String) JOptionPane.showInputDialog(this, "Enter IP Address","xxxx:8888");
        String ipAddress = (String) JOptionPane.showInputDialog(this, "Enter IP Address","localhost");
        startClient(ipAddress);
        
    }


            

    
}
