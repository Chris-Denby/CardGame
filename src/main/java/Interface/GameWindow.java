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
import Interface.Constants.TurnPhase;
import java.awt.Component;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * @author chris
 */
public class GameWindow extends JPanel
{
    private JTabbedPane parentTabbedPane;
    private TCPServer netServer = null;
    private TCPClient netClient = null;
    private PlayArea opponentsPlayArea;
    private PlayArea playerPlayArea;
    private PlayerHand playerHand;
    private PlayerHand opponentsHand;
    private Deck playerDeck;
    private Deck opponentsDeck;
    private GameControlPanel gameControlPanel;
    private ResourcePanel resourcePanel;
    private Deque<CardEvent> cardEventStack = new ArrayDeque<CardEvent>();
    private CardEvent cardEvent = null;
    private JRootPane rootPane;
    private MyGlassPane glassPane;
    private boolean isPlayerTurn = false;
    private int turnNumber = 1;
    private TurnPhase turnPhase;
    private int turnCycleIncrementor = 0;
    
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
        resourcePanel = new ResourcePanel(getWidth(),getHeight(),this);
        resourcePanel.setOpaque(true);
        resourcePanel.setPreferredSize(new Dimension(Math.round(getWidth()/16),getHeight()));
        playerPlayArea = new PlayArea(getWidth(),getHeight(),this, false);
        opponentsPlayArea = new PlayArea(getWidth(),getHeight(),this, true);
        playerHand = new PlayerHand(getWidth(),getHeight(), playerPlayArea, false,this,resourcePanel);
        opponentsHand = new PlayerHand(getWidth(),getHeight(), opponentsPlayArea, true,this,resourcePanel);
        opponentsHand.setEnabled(false);
        playerDeck = new Deck(playerHand, playerPlayArea, this,false); 
        opponentsDeck = new Deck(opponentsHand, opponentsPlayArea, this,true);  
        opponentsDeck.setEnabled(false);
        gameControlPanel = new GameControlPanel(this.getHeight(), this.getWidth(),this);
        
        
        //ADD COMPONENTS        
        this.add(opponentsHand, BorderLayout.PAGE_START);
        
        JPanel centrePanel = new JPanel();
        centrePanel.setOpaque(false);
        centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS));
        centrePanel.add(opponentsPlayArea, BorderLayout.CENTER);
        centrePanel.add(playerPlayArea);

        this.add(gameControlPanel, BorderLayout.WEST);
        this.add(resourcePanel,BorderLayout.EAST);
        this.add(centrePanel, BorderLayout.CENTER);
        this.add(playerHand, BorderLayout.PAGE_END);
        
        playerHand.setDeckArea(playerDeck);    
        opponentsHand.setDeckArea(opponentsDeck);
        //set decks disabled until after each player is dealt
        playerDeck.setEnabled(false);
        opponentsDeck.setEnabled(false);
        //MAKE THE JFRAME VISIBLE
        setVisible(true); 
        
        //TRIGGER ACTIONS
        
        Timer timer = new Timer();
        
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                playerDeck.populateDeckAndDeal();
                if(netClient!=null)
                {
                    isPlayerTurn = true;
                    passTurn();
                }
            }
        };
        timer.schedule(tt, 1500); 
    }
    
    public GameControlPanel getGameControlPanel()
    {
        return gameControlPanel;
    }
    
    public void createCardEvent(Card card)
    {        
        //@@ Parameter originCarnd - the player hand which fired the method
        
        //if card is clicked - create a new event for that card
        //then if another card is clicked as a target - trigger the source cards event on that card
        
        //if the cost of the selected card is greater than the available resources - exit the method       
        
        //select a card as event source only if it is not yet selected
        //dont allow origin card to be selected from opponents hand        
        if(cardEvent == null && !card.getIsSelected() && !card.getIsActivated())
        { 
            if(this.isPlayerTurn & card.getCardLocation()==CardLocation.OPPONENT_PLAY_AREA)
            return;
            

            cardEvent = new CardEvent(card);
            //activate cards in the play areas
            card.setIsSelected(true);
        }
        else
        if(cardEvent != null && cardEvent.getTargetCard()==null & cardEvent.getTargetPlayerBox()==null)
        {
            //set the target card location if its the opponents turn
            //because location information is lost when sending over the stream
            if(getLocalCard(card)==null)
                card.setCardLocation(CardLocation.OPPONENT_PLAY_AREA);
            else
                card.setCardLocation(CardLocation.PLAYER_PLAY_AREA);   
            
            //if target card is the same as the origin - abandon method
            if(cardEvent.getOriginCard().getCardID()==card.getCardID())
                return;
            
            //activate cards in the play areas
            playerPlayArea.selectCard(card);
            card.setIsSelected(true);
            cardEvent.addTargetCard(card);
            
            //assign event type based on the type of cards selected
            if(cardEvent.getOriginCard() instanceof CreatureCard & cardEvent.getTargetCard() instanceof CreatureCard)
            {
                cardEvent.setType("CREATURE_COMBAT");
            }
            
            //add card event to the stack
            cardEventStack.addFirst((CardEvent)cardEvent);
            
            //show glass pane so the arrow can be drawn
            //drawPointer();
            drawPointer(cardEvent.getOriginCard(), cardEvent.getTargetCard());
                    
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
    
    public void createCardEvent(PlayerBox playerBox)
    {
        //if the event already created
        //if a target card has not been set in the event
        //if a target player has not been set in th event        
        if(cardEvent != null && cardEvent.getTargetCard()==null & cardEvent.getTargetPlayerBox()==null)
        {
            playerBox.setIsSelected(true);
            cardEvent.addTargetPlayerBox(playerBox);
            
            //assign event type based on the type of cards selected
            cardEvent.setType("CREATURE_ATTACK_PLAYER");
            
            //add card event to the stack
            cardEventStack.addFirst((CardEvent)cardEvent);
            
            //show glass pane so the arrow can be drawn
            drawPointer(cardEvent.getOriginCard(),cardEvent.getTargetPlayerBox());
            
            //***************
            //send message to connected server/client
            if(isPlayerTurn)
            {
                Message message = new Message();

                if(playerBox.getIsOpponent())
                    message.setText("CARD_EVENT_ON_OPPONENT");             
                else
                    message.setText("CARD_EVENT_ON_PLAYER");                
                sendMessage(message);
            }
        }
    }
    
    public void cancelCardEvent()
    {       
        Card originCard = cardEvent.getOriginCard();
        Card targetCard = cardEvent.getTargetCard();
        PlayerBox targetPlayer = cardEvent.getTargetPlayerBox();
        
        //if target card objects dont match whats in players hand due to sending over stream
        //match by ID instead
        if(targetCard!=null && getLocalCard(targetCard)!=null)
        {
            targetCard = getLocalCard(targetCard);
        }
        
        
        //unselect any selected cards or opponent
        originCard.setIsSelected(false);
        if(targetPlayer!=null)
            targetPlayer.setIsSelected(false);
        if(targetCard!=null)
            targetCard.setIsSelected(false);
        
        cardEvent = null;
        
        if(glassPane!=null)
            glassPane.setVisible(false);
        
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
        PlayerBox targetPlayer = event.getTargetPlayerBox();
        
        //if target card objects dont match whats in players hand due to sending over stream
        //match by ID instead        
        if(targetCard!=null && getLocalCard(targetCard)!=null){
            targetCard = getLocalCard(targetCard);
        }
        
        //DO ACTIONS
        //
        //                
        if(event.getType()=="CREATURE_COMBAT")
        {   
            //exchange damage between origin and target
            CreatureCard origin = (CreatureCard) originCard;
            CreatureCard target = (CreatureCard) targetCard;
            Timer timer = new Timer();
            TimerTask targetDamageTask = new TimerTask() {
                @Override
                public void run() {
                    origin.takeDamage(target.getPower());
                }
            };
            TimerTask originDamageTask = new TimerTask() {
                @Override
                public void run() {
                    target.takeDamage(origin.getPower()); 
                    timer.schedule(targetDamageTask, 500);
                }
            };
            timer.schedule(originDamageTask, 500);
        }
        
        if(event.getType()=="CREATURE_ATTACK_PLAYER")
        {
            //creature does damage to target player
            CreatureCard origin = (CreatureCard) originCard;
            PlayerBox target = event.getTargetPlayerBox();
            Timer timer = new Timer();
            TimerTask playerDamageTask = new TimerTask() {
                @Override
                public void run() {
                    target.takeDamage(origin.getPower());
                    
                    System.out.println("Players health is " + playerPlayArea.getPlayerBoxPanel().getPlayerHealth());
                    //if card event execution reduced player health to 0 or below
                    if(playerPlayArea.getPlayerBoxPanel().getPlayerHealth()<=0)
                    {
                        System.out.println("YOU LOSE THE GAME!!!");
                        //if player is <=0 health and opponent is >0
                        //player loses the game
                        loseGame();
        }
                }
            };
            timer.schedule(playerDamageTask, 500);  
        }
        
        //AFTER EVENT RESOLVED
        //return card state to normal
        event.execute();
        
        if(originCard!=null)
        {
            originCard.setIsSelected(false);
            originCard.setIsActivated(true);
        }
        if(targetCard!=null)
        {
            targetCard.setIsSelected(false);
            targetCard.setIsActivated(true);
        }
        if(targetPlayer!=null)
        {
            targetPlayer.setIsSelected(false);
        }
        
        //release current card event
        this.cardEvent = null;
        glassPane.setVisible(false);
        
        //***************
        //send message to connected server/client
        if(isPlayerTurn)
        {
            Message message = new Message();
            message.setText("OPPONENT_RESOLVED_CARDEVENT");
            sendMessage(message);
        }   
    }
    
    public void executeCardEvent()
    {
        //if no parameters given
        //execute card event in memory
        if(cardEvent!=null)
        executeCardEvent(cardEvent);
    }
    
    public boolean getIsPlayerTurn()
    {
        return isPlayerTurn;
    }
    
    public TurnPhase getTurnPhase()
    {
        return turnPhase;
    }

    public void passTurn()
    {   
        this.turnPhase = TurnPhase.END_PHASE;
        gameControlPanel.setTurnPhaseLabelText(turnPhase);
        
        //increment turn number          
        if(isPlayerTurn)
        {
            //if its the players turn, check if any cards need to be discarded before passing turn
            //if player hand is over max hand size - prevent passing turn
            if(!playerHand.checkHandSizeForEndTurn()) 
                return;
            
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
            //it becomes the players turn
            isPlayerTurn=true;
            
            this.playerPlayArea.setIsPlayerTurn(isPlayerTurn);
            this.opponentsPlayArea.setIsPlayerTurn(isPlayerTurn);
            this.gameControlPanel.setIsPlayerTurn(isPlayerTurn);
            
            //draw card at the start of the turn - except the first turn of the game
            if(turnCycleIncrementor>1)
                playerDeck.drawCard();
            
        }
        
        //replenish resources back to turn amount
        resourcePanel.resetResources(turnNumber);  
        
        //cancel any half created events
        if(cardEvent!=null)
            cancelCardEvent();
        
        //progress to next turn phase
        turnPhase = TurnPhase.MAIN_PHASE;
        gameControlPanel.setTurnPhaseLabelText(turnPhase);
        
        //increment turn number 
        turnCycleIncrementor++;
        System.out.println("INCREMENTOR "+ turnCycleIncrementor);
        if(turnCycleIncrementor==(turnNumber*2))
        {
            turnNumber++;
            System.out.println("TURN "+ turnNumber);
        }     
    }
    
    public void drawPointer(Component origin, Component target)
    {
        rootPane = this.getRootPane();
        
        int horizontalSpacing = gameControlPanel.getWidth();
        int originVerticalSpacing = 0;
        int targetVerticalSpacing = 0;
        
        if(target instanceof PlayerBox)
        {
            PlayerBox targetPlayer = (PlayerBox) target;

            if(isPlayerTurn)
                //on the players turn, origin is always from players hand
                originVerticalSpacing = opponentsHand.getHeight()
                        + opponentsPlayArea.getHeight()
                        + target.getHeight()/2;
            else if(!isPlayerTurn)
                //if its the opponents turn, origin is always from the opponents hand
                originVerticalSpacing = opponentsHand.getHeight()
                        + (opponentsPlayArea.getHeight()/2)
                        + target.getHeight()/2;
            
            
            //if the target player is the player (me)
            if(!targetPlayer.getIsOpponent())             
                 //spacing includes opponents hand
                targetVerticalSpacing = 
                        opponentsHand.getHeight()
                        + opponentsPlayArea.getHeight()
                        + playerPlayArea.getHeight()/2
                        + target.getHeight()/2;
            
            
            //if the target player is the opponent
            if(targetPlayer.getIsOpponent())                             
                 //spacing includes opponents hand
                targetVerticalSpacing = 
                        opponentsHand.getHeight()
                        + target.getHeight()/2;
        }
        
        
        if(target instanceof Card)
        {
            Card originCard = (Card) origin;
            Card targetCard = (Card) target;
            if(isPlayerTurn)
            {
                //on the players turn, origin is always from players hand
                originVerticalSpacing = opponentsHand.getHeight() + opponentsPlayArea.getHeight();


                //if target is in player hand, spacing includes opponents hand + play area
                if(targetCard.getCardLocation()==CardLocation.PLAYER_PLAY_AREA)
                    targetVerticalSpacing = 
                            opponentsHand.getHeight()
                            + opponentsPlayArea.getHeight();

                //if target is in opponents hand, spacing includes opponents hand only
                if(targetCard.getCardLocation()==CardLocation.OPPONENT_PLAY_AREA)
                    targetVerticalSpacing = opponentsHand.getHeight()
                    + (opponentsPlayArea.getHeight()/2);
            }
            else if(!isPlayerTurn)
            {
                //if its the opponents turn, origin is always from the opponents hand
                originVerticalSpacing = opponentsHand.getHeight()
                         + (opponentsPlayArea.getHeight()/2);


                //if the target is in the opponents hand, spacing includes opponents hand only
                if(targetCard.getCardLocation()==CardLocation.OPPONENT_PLAY_AREA)
                    targetVerticalSpacing = opponentsHand.getHeight()
                    + (opponentsPlayArea.getHeight()/2);


                //if the target is in the players hand, spacing includes
                if(targetCard.getCardLocation()==CardLocation.PLAYER_PLAY_AREA)
                    targetVerticalSpacing = 
                              opponentsHand.getHeight()
                            + opponentsPlayArea.getHeight();
            }            
        }
        

        //create points for origin and target cards
        //x = middle of card + horizontal spacing
        //y = middle of card + vertical spacing determined above
        Point originPoint = new Point(origin.getX()+(origin.getWidth()/2)+horizontalSpacing,
                origin.getY()+(origin.getHeight()/2)+originVerticalSpacing);
        
        Point targetPoint = new Point(target.getX()+(target.getWidth()/2)+horizontalSpacing,
                target.getY()+(target.getHeight()/2)+targetVerticalSpacing);
        
        glassPane = new MyGlassPane(originPoint,targetPoint);

        rootPane.setGlassPane(glassPane);
        glassPane.setVisible(true);                   
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
    }
    
    public void sendMessage(Message message)
    {        
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
            this.opponentsHand.playCard(message.getCard(),true);
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
        else
        if(message.getText().equals("CANCEL_CARD_EVENT"))
        {
            if(cardEvent!=null)
                cancelCardEvent();
        }
        else
        if(message.getText().equals("CARD_EVENT_ON_PLAYER"))
        {
            //"player" meaning the opponents (who sent the message) self
            this.createCardEvent(opponentsPlayArea.getPlayerBoxPanel());
        }
        else
        if(message.getText().equals("CARD_EVENT_ON_OPPONENT"))
        {
            //"opponent" meaning this player (who recieved the message) self
            this.createCardEvent(playerPlayArea.getPlayerBoxPanel());
        }
        else
        if(message.getText().equals("PLAYER_DISCARD_CARD"))
        {
            //"player" meaning the active player who's turn it is
            //therefore on receipt, the player would be this applications opponent
            opponentsHand.removeCard(message.getCard());
        }
        else
        if(message.getText().equals("OPPONENT_LOSE_GAME"))
        {
            this.winGame();
        }
    }
    
    public class MyGlassPane extends JComponent
    {
        Point originCardPoint;
        Point targetCardPoint;

        public MyGlassPane(Point origin, Point target)
        {
            originCardPoint = origin;
            targetCardPoint = target; 
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
    
    public int getTurnNumber()
    {
        return turnNumber;
    }
    
    public void disablePlay()
    {
        //disable interaction with play area
        //disable interaction with players game
        
        //player
        playerPlayArea.setEnabled(false);
        playerHand.setEnabled(false); 
        playerDeck.setEnabled(false);
        
        //opponent
        opponentsPlayArea.setEnabled(false);
        opponentsHand.setEnabled(false);
        opponentsDeck.setEnabled(false);
        
     
    }
    
    public void loseGame()
    {
        //you lose the game
        
        //disable interaction
        this.disablePlay();
        gameControlPanel.endGame(false);
        
        //send message to connected server/client
        Message message = new Message();
        message.setText("OPPONENT_LOSE_GAME");
        sendMessage(message);
    }
    
    public void winGame()
    {
        //you win the games
        
        //disable interaction
        this.disablePlay();
        gameControlPanel.endGame(true);
    }
    
    public void drawGame()
    {
        
    }
}
