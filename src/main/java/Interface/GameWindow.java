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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingConstants;


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
    private JPanel centrePanel;
    private Deque<CardEvent> cardEventStack = new ArrayDeque<CardEvent>();
    private CardEvent cardEvent = null;
    private JRootPane rootPane;
    private DrawLineGlassPane drawLineGlassPane;
    private CardZoomGlassPane cardZoomGlassPane;
    private boolean isPlayerTurn = false;
    private int turnPasses = 0;
    private int turnNumber = 1;
    private TurnPhase turnPhase;
    private int turnCycleIncrementor = 0;
    private Color overlayColor = new Color(223,223,223,200);
    
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
        
        centrePanel = new JPanel();
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
            
            setTurnPhase(TurnPhase.COMBAT_PHASE);
            cardEvent = new CardEvent(card);
            //activate cards in the play areas
            card.setIsSelected(true);
            
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
        else
        //if a card event exists and target card has not yet been set
        if(cardEvent != null && cardEvent.getTargetCard()==null && cardEvent.getTargetPlayerBox()==null && turnPhase!=TurnPhase.DECLARE_BLOCKERS)
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
            //card.setIsSelected(true);
            cardEvent.addTargetCard(card);
            
            //assign event type based on the type of cards selected
            if(cardEvent.getOriginCard() instanceof CreatureCard & cardEvent.getTargetCard() instanceof CreatureCard)
            {
                cardEvent.setType("CREATURE_COMBAT");
            }
            
            
            cardEvent.getTargetCard().setIsSelected(true);
                        
            //add card event to the stack
            cardEventStack.addFirst((CardEvent)cardEvent);
            
            //show glass pane so the arrow can be drawn
            //drawPointer();
            drawPointer(cardEvent.getOriginCard(), cardEvent.getTargetCard());
                    
            //System.out.println(cardEventStack.size()+" events on the stack");
            //reset current card event
            //cardEvent = null;
            
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
        //if turn phase is delcare blockers
        //the card added is to be the blocker
        else
        if(turnPhase==TurnPhase.DECLARE_BLOCKERS)
        {
            //add blocker to card event
            cardEvent.addBlockingCard(card);
            
            //show blocker selected
            card.setIsSelected(true);
            
            if(!isPlayerTurn)
            {
            //send message to connected server/client
            Message message = new Message();
            message.setText("OPPONENT_DECLARED_BLOCKER");
            message.setCard(card);
            sendMessage(message);
            }
            
            //after blocker declared
            //execute the card event
            executeCardEvent();
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
    {   if(cardEvent!=null)
        {
            setTurnPhase(TurnPhase.MAIN_PHASE);
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

            if(drawLineGlassPane!=null)
                drawLineGlassPane.setVisible(false);
            
            drawLineGlassPane=null;

            if(isPlayerTurn)
            {
                Message m = new Message();
                m.setText("CANCEL_CARD_EVENT");
                sendMessage(m);
            }
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
    
    public void requestResolveCombat()
    {
        if(cardEvent.getType()=="CREATURE_COMBAT")
            executeCardEvent();
        else
        if(cardEvent.getType()=="CREATURE_ATTACK_PLAYER")
        {
            setTurnPhase(TurnPhase.DECLARE_BLOCKERS);  
            
            if(!isPlayerTurn)
            {
                gameControlPanel.enableResolveButton(true);
                gameControlPanel.setResolveButtonText("NO BLOCKERS");
            }
        }
        //***************
        //send message to connected server/client
        if(isPlayerTurn)
        {
            Message message = new Message();
            message.setText("REQUEST_RESOLVE_COMBAT");
            sendMessage(message);
        } 
    }

    public void executeCardEvent(CardEvent event)
    {   
        setTurnPhase(TurnPhase.COMBAT_PHASE);
        
        Card originCard = event.getOriginCard();
        Card targetCard = event.getTargetCard();
        PlayerBox targetPlayer = event.getTargetPlayerBox();
        Card blockingCard = event.getBlockingCard();
        
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
            
            System.out.println(origin.getName() + " and " + target.getName() + " are fighting!");
            
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
            CreatureCard blocker = (CreatureCard) event.getBlockingCard();
            final int originPower = origin.getPower();
            final int blockerPower;
            final int blockerToughness;
            if(blocker!=null)
            {
                blockerPower = blocker.getPower();
                blockerToughness = blocker.getToughness();
            }
            else
            {
                blockerPower = 0;
                blockerToughness = 0;
            }

            
            Timer timer = new Timer();
            
            TimerTask creatureDamageTask = new TimerTask(){
                @Override
                public void run(){
                    if(blocker!=null)
                    {
                        blocker.takeDamage(originPower);
                        origin.takeDamage(blockerPower);
                    }
                } 
            };
            if(blocker!=null)
                timer.schedule(creatureDamageTask, 1000);  
            
            TimerTask playerDamageTask = new TimerTask() {
                @Override
                public void run() {
                    
                    target.takeDamage(originPower-blockerToughness);
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
            timer.schedule(playerDamageTask, 1000);  
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
        if(blockingCard!=null)
        {
            blockingCard.setIsSelected(false);
            blockingCard.setIsActivated(true);
        }
        
        //release current card event
        this.cardEvent = null;
        drawLineGlassPane.setVisible(false);
        drawLineGlassPane = null; 
        
        //progress turn phase
        setTurnPhase(TurnPhase.MAIN_PHASE);
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
        setTurnPhase(TurnPhase.END_PHASE);
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
            this.opponentsPlayArea.setIsPlayerTurn(true);
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
            this.opponentsPlayArea.setIsPlayerTurn(false);
            this.gameControlPanel.setIsPlayerTurn(isPlayerTurn);
            
            //deactivate all activated cards in the players play area
            playerPlayArea.unActivateAllCards();
            
            //draw card at the start of the turn - except the first turn of the game
            if(turnCycleIncrementor>0)
                playerDeck.drawCard();
            
            
            
        }
        
        //replenish resources back to turn amount
        resourcePanel.resetResources(turnNumber);  
        
        //cancel any half created events
        if(cardEvent!=null)
            cancelCardEvent();
        
        //progress to next turn phase
        setTurnPhase(TurnPhase.MAIN_PHASE);
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
    
    public void passOnBlocking()
    {
        executeCardEvent();
        
        Message message = new Message();
        message.setText("OPPONENT_PASS_ON_BLOCKING");
        sendMessage(message);
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
        
        drawLineGlassPane = new DrawLineGlassPane(originPoint,targetPoint);

        if(cardZoomGlassPane==null)
        {
            rootPane.setGlassPane(drawLineGlassPane);
            drawLineGlassPane.setVisible(true);  
        }
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
            opponentsHand.discardCard(message.getCard());
        }
        else
        if(message.getText().equals("OPPONENT_LOSE_GAME"))
        {
            this.winGame();
        }
        else
        if(message.getText().equals("REQUEST_RESOLVE_COMBAT"))
        {
            requestResolveCombat();
        }
        else
        if(message.getText().equals("OPPONENT_DECLARED_BLOCKER"))
        {
            //send received blocking card to the create card event method
            //this method asigns the card as a blocker in the card event
            createCardEvent(message.getCard());
        }
        else
        if(message.getText().equals("OPPONENT_PASS_ON_BLOCKING"))
        {
            executeCardEvent();            
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
    
    public void setTurnPhase(TurnPhase phase)
    {
        turnPhase = phase;
        gameControlPanel.setTurnPhaseLabelText(turnPhase);
        
        if(phase==TurnPhase.MAIN_PHASE)
        {
            gameControlPanel.setResolveButtonText("Resolve"); 
            gameControlPanel.enableResolveButton(false);
        }
        else
        if(phase==TurnPhase.COMBAT_PHASE)
        {
            if(isPlayerTurn)
                gameControlPanel.enableResolveButton(true);
                
            else
                gameControlPanel.enableResolveButton(false);
        }
        else
        if(phase==TurnPhase.DECLARE_BLOCKERS)
        {
            if(isPlayerTurn)
                gameControlPanel.enableResolveButton(false);
            else
                gameControlPanel.enableResolveButton(true);    
        }
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
    
    public void zoomInCard(Card card)
    {
        if(cardZoomGlassPane==null)
        {
            Card zoomedCard = zoomedCard = card.getClone();
            //if glass pane is currently hidden
            //show zoomed in card glass pane
            rootPane = this.getRootPane();
            cardZoomGlassPane = new CardZoomGlassPane(zoomedCard);
            rootPane.setGlassPane(cardZoomGlassPane);
            cardZoomGlassPane.setVisible(true); 
        }
        else
        if(cardZoomGlassPane!=null)
        {
            //remove zoomed in card glass pane
            //restore draw line glass pane
            cardZoomGlassPane.setVisible(false); 
            cardZoomGlassPane = null;
            if(drawLineGlassPane!=null)
            {
                rootPane.setGlassPane(drawLineGlassPane);
                drawLineGlassPane.setVisible(true);
            }
        }        
    }
           
    public class DrawLineGlassPane extends JComponent
    {
        Point originCardPoint;
        Point targetCardPoint;

        public DrawLineGlassPane(Point origin, Point target)
        {
            originCardPoint = origin;
            targetCardPoint = target; 
            setVisible(true);
        }
        
        @Override
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            setForeground(Color.red);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setStroke(new BasicStroke(5));
            graphics.drawLine(originCardPoint.x,originCardPoint.y,targetCardPoint.x,targetCardPoint.y);
        } 
    }
    
    public class CardZoomGlassPane extends JComponent
    {
        public CardZoomGlassPane(Card card)
        { 
            //make size of glass pane the same as the game window
            setSize(centrePanel.getWidth(), centrePanel.getHeight());
            setVisible(true);
            setBackground(overlayColor);
            //resize clone of card to be zoomed            
            card.applySize((int) Math.round(centrePanel.getHeight()*0.5));
            //set card location on screen
            card.setBounds((gameControlPanel.getWidth() + (int) Math.round(card.getWidth()/5)), 
                    (int) Math.round((centrePanel.getHeight()-card.getHeight())/2) + playerHand.getHeight(), 
                    card.getWidth(), card.getHeight());
            
            //add card to glass pane
            add(card);
            card.setFaceUp(true);
            card.setIsActivated(false);
            card.setIsSelected(false);
            card.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}

                @Override
                public void mousePressed(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) {
                    zoomInCard(card);
                }

                @Override
                public void mouseEntered(MouseEvent e) {}

                @Override
                public void mouseExited(MouseEvent e) {}
            });
        }
        
    }
}
