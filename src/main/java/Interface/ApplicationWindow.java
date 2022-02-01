/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import NetCode.TCPClient;
import NetCode.TCPServerManyThreads;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


/**
 *
 * @author chris
 */
public class ApplicationWindow extends JFrame
{
    private TCPClient netClient;
    private TCPServerManyThreads netServer;
    JTabbedPane tabbedPane;
    
    //constructor
    public ApplicationWindow()
    {
        //netClient = new TCPClient();
        //netServer = new TCPServer();
        

        tabbedPane = new JTabbedPane();
        StartGameWindow startGameWindow = new StartGameWindow(tabbedPane);
        //GameWindow gameWindow = new GameWindow(tabbedPane);
        tabbedPane.addTab("Game Type", startGameWindow);
        //tabbedPane.addTab("Game",gameWindow);
        this.add(tabbedPane);
        //tabbedPane.setEnabledAt(1, false);
        
        
        
                
        //SET JFRAME PARAMETERS
        setTitle("Card Game");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        Dimension dimensions = new Dimension(800,600);
        this.setMinimumSize(dimensions);
        
        //MAKE THE JFRAME VISIBLE
        pack();
        setVisible(true);        
    }
    
    
    
}
