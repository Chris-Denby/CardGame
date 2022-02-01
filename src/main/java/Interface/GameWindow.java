/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import NetCode.TCPClient;
import NetCode.TCPServer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;


/**
 *
 * @author chris
 */
public class GameWindow extends JPanel
{
    private JTabbedPane parentTabbedPane;
    private TCPServer netServer = null;
    private TCPClient netClient = null;
    PlayArea opponentsPlayArea;
    PlayArea playerPlayArea;
    PlayerHand playerHand;
    PlayerHand opponentsHand;
    Deck playerDeck;
    Deck opponentsDeck;
    
    //constructor
    public GameWindow(JTabbedPane pane)
    {      
        parentTabbedPane = pane;
        BorderLayout borderLayout = new BorderLayout();
        
        //SET JFRAME PARAMETERS
        int width = 400;
        int height = 400;
        Dimension dimensions = new Dimension(width,height);
        this.setSize(width, height);
        this.setMinimumSize(dimensions);
        this.setLayout(borderLayout);
        
        //INITIALISE COMPONENTS
        
        playerPlayArea = new PlayArea(getWidth(),getHeight(),this, false);
        opponentsPlayArea = new PlayArea(getWidth(),getHeight(),this, true);
        playerHand = new PlayerHand(getWidth(),getHeight(), playerPlayArea, false,this);
        opponentsHand = new PlayerHand(getWidth(),getHeight(), opponentsPlayArea, true,this);
        playerDeck = new Deck(playerHand, playerPlayArea, this,false); 
        opponentsDeck = new Deck(opponentsHand, opponentsPlayArea, this,true);               
        
        //ADD COMPONENTS        
        this.add(opponentsHand, BorderLayout.PAGE_START);
        JPanel centrePanel = new JPanel();
        centrePanel.setBackground(Color.GREEN);
        centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS));
        centrePanel.add(opponentsPlayArea);
        centrePanel.add(playerPlayArea);
        this.add(centrePanel, BorderLayout.CENTER);
        this.add(playerHand, BorderLayout.PAGE_END);
        
        playerHand.setDeckArea(playerDeck);        
        opponentsHand.setDeckArea(opponentsDeck);       
        
        playerDeck.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playerDeck.drawCard();
                Message m = new Message();
                m.setText("OPPONENT_DRAW_CARD");
                sendMessage(m);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        
        //MAKE THE JFRAME VISIBLE
        setVisible(true); 
    }
    
    public void setOpponentInteractable(boolean enabled)
    {
        opponentsHand.setEnabled(enabled);
        opponentsPlayArea.setEnabled(enabled);
        opponentsDeck.setEnabled(enabled);
    }
    
    public GameWindow(JTabbedPane pane,TCPServer server)
    {
        this(pane);
        netServer = server;
        server.setGameWindow(this);
        setOpponentInteractable(false);
    }
    
    public GameWindow(JTabbedPane pane,TCPClient client)
    {
        this(pane);
        netClient = client;
        client.setGameWindow(this);
        setOpponentInteractable(false);
    }
    
    public void sendMessage(Message message)
    {
        System.out.println("Send message request - " + message.getText());
        
        if(netServer!=null)
        {
            netServer.sendMessage(message);
        }
        else if (netClient!=null)
        {
            netClient.sendMessage(message);
        }
    }
    
    public void recieveMessage(Message message)
    {
        System.out.println("Message recieved by game - " +message.getText());
        
        if(message.getText().equals("OPPONENT_DRAW_CARD"))
        {
            opponentsDeck.drawCard();
        }
        else
        if(message.getText().equals("OPPONENT_ADD_CARD_TO_DECK"))
        {
            opponentsDeck.addCard(message.getCard());
        }
        if(message.getText().equals("OPPONENT_PLAY_CARD"))
        {
            this.opponentsHand.playCard(message.getCard());
        }
        if(message.getText().equals("OPPONENT_ACTIVATE_CARD"))
        {
            opponentsPlayArea.activateCard(message.getCard());
            
        }
    }
    
        
    public class MessageListener extends SwingWorker<Void, Message>
    {      
        @Override
        protected Void doInBackground() 
        {
            Message message;
            while(true)
            {
                message = null;
                
                if(message!=null)
                {
                    publish(message);
                }
            }
        }
        
        //@Override
        protected void publish()
        {
            
        }
    }
    
    
    
    
    
        
    
    
    
}
