/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Interface.Cards.Card;
import Interface.Cards.CreatureCard;
import NetCode.TCPClient;
import NetCode.TCPServer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import Interface.Constants.CardLocation;


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
    GameControlPanel gameControlPanel;
    private Deque<CardEvent> cardEventStack = new ArrayDeque<CardEvent>();
    private CardEvent cardEvent = null;
    JRootPane rootPane;
    MyGlassPane glassPane;
    private boolean isPlayerTurn = false;
    
    
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
        this.setBackground(Color.GREEN);
        
        //INITIALISE COMPONENTS
        
        playerPlayArea = new PlayArea(getWidth(),getHeight(),this, false);
        opponentsPlayArea = new PlayArea(getWidth(),getHeight(),this, true);
        playerHand = new PlayerHand(getWidth(),getHeight(), playerPlayArea, false,this);
        opponentsHand = new PlayerHand(getWidth(),getHeight(), opponentsPlayArea, true,this);
        opponentsHand.setEnabled(false);
        playerDeck = new Deck(playerHand, playerPlayArea, this,false); 
        opponentsDeck = new Deck(opponentsHand, opponentsPlayArea, this,true);  
        opponentsDeck.setEnabled(false);
        gameControlPanel = new GameControlPanel(this.getHeight(), this.getWidth(),this);
        
        
        //ADD COMPONENTS        
        this.add(opponentsHand, BorderLayout.PAGE_START);
        
        JPanel centrePanel = new JPanel();
        //centrePanel.setBackground(Color.);
        centrePanel.setOpaque(false);
        centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS));
        centrePanel.add(opponentsPlayArea, BorderLayout.CENTER);
        centrePanel.add(playerPlayArea);
        
        this.add(gameControlPanel, BorderLayout.WEST);
        this.add(centrePanel, BorderLayout.CENTER);
        this.add(playerHand, BorderLayout.PAGE_END);
        
        playerHand.setDeckArea(playerDeck);    
        
        opponentsHand.setDeckArea(opponentsDeck); 
        
        //MAKE THE JFRAME VISIBLE
        setVisible(true); 
    }
    
    public void createCardEvent(Card card)
    {        
        //@@ Parameter originCarnd - the player hand which fired the method
        
        //if card is clicked - create a new event for that card
        //then if another card is clicked as a target - trigger the source cards event on that card
        
        //select a card as event source only if it is not yet selected
        //dont allow origin card to be selected from opponents hand        
        if(cardEvent == null & !card.isActivated())
        { 
            if(this.isPlayerTurn & card.getCardLocation()==CardLocation.OPPONENT_PLAY_AREA)
            return;
            
            cardEvent = new CardEvent(card);
            //activate cards in the play areas
            card.activateCard(true);
        }
        else
        if(cardEvent != null && cardEvent.getTargetCard()==null)
        {           
            //activate cards in the play areas
            playerPlayArea.showSelectedCard(card);
            card.activateCard(true);
            cardEvent.addTargetCard(card);
            
            //assign event type based on the type of cards selected
            if(cardEvent.getOriginCard() instanceof CreatureCard & cardEvent.getTargetCard() instanceof CreatureCard)
            {
                cardEvent.setType("CREATURE_COMBAT");
            }
            
            System.out.println("event created: " + cardEvent.getOriginCard().getName() + "->" + cardEvent.getTargetCard().getName());
            //add card event to the stack
            cardEventStack.addFirst((CardEvent)cardEvent);
            
            //show glass pane so the arrow can be drawn
            drawPointer(cardEvent);
                    
            //System.out.println(cardEventStack.size()+" events on the stack");
            //reset current card event
            //cardEvent = null;
        }

        //***************
        //send message to connected server/client
        if(isPlayerTurn)
        {
            Message message = new Message();
            message.setText("OPPONENT_ACTIVATE_CARD");
            message.setCard(card);
            sendMessage(message);
        }
    }
    
    public void cancelCardEvent()
    {
        Card originCard = cardEvent.getOriginCard();
        Card targetCard = cardEvent.getTargetCard();
        
        //if target card objects dont match whats in players hand due to sending over stream
        //match by ID instead
        
        if(getLocalCard(targetCard)!=null)
        {
            targetCard = getLocalCard(targetCard);
        }
        
        /**
        for(Card c:playerPlayArea.getCardsInPlayArea())
        {
            if(c.getCardID()==cardEvent.getTargetCard().getCardID())
            {
                targetCard = c;
            }
        }
        **/
        
        originCard.activateCard(false);
        targetCard.activateCard(false);
        cardEvent = null;
        
       if(isPlayerTurn)
       {
           Message m = new Message();
           m.setText("CANCEL_CARD_EVENT");
           sendMessage(m);
       }
    }
    
    public Card getLocalCard(Card card)
    {
        for(Card c:playerPlayArea.getCardsInPlayArea())
        {
            if(c.getCardID()==card.getCardID())
            {
                return c;
            }
        }
        return null;
    }
    
    public void executeCardEvent(CardEvent event)
    {
        Card originCard = event.getOriginCard();
        Card targetCard = event.getTargetCard();
        
        //if target card objects dont match whats in players hand due to sending over stream
        //match by ID instead
        
        if(getLocalCard(targetCard)!=null)
        {
            targetCard = getLocalCard(targetCard);
        }
                
        if(event.getType()=="CREATURE_COMBAT")
        {            
            System.out.println("Creature combat resolved");              
            int n = Integer.parseInt(originCard.getName())-1;
            originCard.setName(n+"");
            
            if(originCard.getCardID()!=targetCard.getCardID())
            {
                n = Integer.parseInt(targetCard.getName())-1;
                targetCard.setName(n+"");      
            }
        }
        
        
        //return card state to normal
        event.execute();
        originCard.activateCard(false);
        targetCard.activateCard(false);
        //release current card event
        this.cardEvent = null;
        
        //***************
        //send message to connected server/client
        if(isPlayerTurn)
        {
            Message message = new Message();
            message.setText("OPPONENT_RESOLVED_CARDEVENT");
            //message.setCard(card);
            //message.setCardEvent(cardEvent);
            sendMessage(message);
        }   
    }
    
    public void executeCardEvent()
    {
        //if no parameters given
        if(cardEvent!=null)
        executeCardEvent(cardEvent);
    }
    
    public boolean getIsPlayerTurn()
    {
        return isPlayerTurn;
    }
        
    public void passTurn()
    {
        if(isPlayerTurn)
        {
            isPlayerTurn=false;      
            this.playerPlayArea.setIsPlayerTurn(isPlayerTurn);
            this.opponentsPlayArea.setIsPlayerTurn(isPlayerTurn);
            this.gameControlPanel.setIsPlayerTurn(isPlayerTurn);
            
            Message m = new Message();
            m.setText("OPPONENT_PASS_TURN");
            sendMessage(m);
        }
        else if(!isPlayerTurn)
        {
            isPlayerTurn=true;
            this.playerPlayArea.setIsPlayerTurn(isPlayerTurn);
            this.opponentsPlayArea.setIsPlayerTurn(isPlayerTurn);
            this.gameControlPanel.setIsPlayerTurn(isPlayerTurn);
        }
        
        if(cardEvent!=null)
            cancelCardEvent();
    }
    
    public void drawPointer(CardEvent cardEvent)
    {
        rootPane = this.getRootPane();
        glassPane = new MyGlassPane(cardEvent);
        rootPane.setGlassPane(glassPane);
        glassPane.setVisible(true);
        glassPane.repaint();                   
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
        //setOpponentInteractable(false);
    }
    
    public PlayerHand getPlayerHand()
    {
        return playerHand;
    }
    
    public PlayerHand getOpponentHand()
    {
        return opponentsHand;
    }
    
    public GameWindow(JTabbedPane pane,TCPClient client)
    {
        this(pane);
        netClient = client;
        client.setGameWindow(this);
        
        //host always goes first - for testing
        isPlayerTurn = true;
        passTurn();
    }
    
    public void sendMessage(Message message)
    {
        //System.out.println("Send message request - " + message.getText());
        
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
        //System.out.println("Message recieved by game - " +message.getText());
        
        if(message.getText().equals("OPPONENT_DRAW_CARD"))
        {
            opponentsDeck.drawCard();
        }
        else
        if(message.getText().equals("OPPONENT_ADD_CARD_TO_DECK"))
        {
            opponentsDeck.addCard(message.getCard());
        }
        else
        if(message.getText().equals("OPPONENT_PLAY_CARD"))
        {
            this.opponentsHand.playCard(message.getCard());
        }
        else
        if(message.getText().equals("OPPONENT_ACTIVATE_CARD"))
        {
            createCardEvent(message.getCard());
        }
        else
        if(message.getText().equals("OPPONENT_RESOLVED_CARDEVENT"))
        {
            executeCardEvent();
        }
        else
        if(message.getText().equals("OPPONENT_PASS_TURN"))
        {
            passTurn();
        }
    }
    
    public class MyGlassPane extends JComponent
    {
        Point originCardPoint;
        Point targetCardPoint;

        public MyGlassPane(CardEvent e)
        {
            int verticalSpacing = 0;
            int horizontalSpacing = 0;
            //verticalSpacing = gameWindow.getOpponentHand().getHeight() + playerPlayArea.getHeight(); 
            //verticalSpacing = gameWindow.getOpponentHand().getHeight(); 

            originCardPoint = new Point(cardEvent.getOriginCard().getX()
                    +(cardEvent.getOriginCard().getWidth()/2)
                    +horizontalSpacing,
                    cardEvent.getOriginCard().getY()
                            +(cardEvent.getOriginCard().getHeight()/2)
                            +verticalSpacing);
            
            targetCardPoint = new Point(cardEvent.getTargetCard().getX()
                    +(cardEvent.getTargetCard().getWidth()/2)+horizontalSpacing,
                    cardEvent.getTargetCard().getY()
                            +(cardEvent.getTargetCard().getHeight()/2)
                            +verticalSpacing);
            
            this.setVisible(true);
        }
        
        @Override
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            this.setForeground(Color.red);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setStroke(new BasicStroke(5));
            graphics.drawLine(originCardPoint.x,originCardPoint.y,targetCardPoint.x,targetCardPoint.y);
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
