/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Interface.Cards.PlayerBox;
import Interface.Cards.Card;
import Interface.Cards.CreatureCard;
import Interface.Cards.SpellCard;
import Interface.Constants.CardLocation;
import Interface.Constants.TurnPhase;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public class PlayArea extends JPanel
{
    GameWindow gameWindow;
    
    boolean isOpponent = false;
    int width;
    int height;
    JPanel playerPlayArea = this;  
    JPanel opponentPlayArea;
    private CardEvent cardEvent;
    private boolean isPlayerTurn;
    JPanel cardSubPanel;
    JPanel playerSubPanel;
    PlayerBox playerBoxPanel;
            
    //ArrayList<Card> cardsInPlay = new ArrayList<Card>();
    private HashMap<Integer,Card> cardsInPlay = new HashMap<Integer,Card>();
    private Deque<CardEvent> cardEventStack = new ArrayDeque<CardEvent>();
    ArrayList<Card> discardPile = new ArrayList<Card>();
 
    public PlayArea(int containerWidth, int containerHeight, GameWindow window, boolean isOpponent)
    {
        gameWindow = window;
        this.isOpponent = isOpponent;
        width = containerWidth;
        
        //height is the container minus the who player and opponents hands
        height = (int) containerHeight-Math.round((containerHeight/16)*3); 
        this.setPreferredSize(new Dimension(width,height));
        this.setOpaque(false); 
        
        cardSubPanel = new JPanel();
        Dimension cardSubPanelDimension = new Dimension(width,Math.round(height/10)*6);
        cardSubPanel.setPreferredSize(cardSubPanelDimension);
        cardSubPanel.setSize(cardSubPanelDimension);
        
        playerSubPanel = new JPanel();
        Dimension playerSubPanelDimension = new Dimension(width,Math.round(height/10)*4);
        playerSubPanel.setPreferredSize(playerSubPanelDimension);
        playerSubPanel.setSize(playerSubPanelDimension);
                
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
        if(!isOpponent)
        {
            this.add(cardSubPanel);
            this.add(playerSubPanel);
        }
        else
        {
            this.add(playerSubPanel); 
            this.add(cardSubPanel);
        }   
        
        playerBoxPanel = new PlayerBox(playerSubPanel.getHeight(),this.isOpponent);
        playerBoxPanel.addMouseListener(new PlayerBoxMouseListener(playerBoxPanel,this));
        playerSubPanel.add(playerBoxPanel,Component.CENTER_ALIGNMENT);
        
        
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                /**
                 * DISABLED MANUAL DRAW - draw happens automatically at start of turn
                if(gameWindow.getIsPlayerTurn())
                {
                    drawCard();
                }
                **/
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(gameWindow.getIsPlayerTurn())
                    gameWindow.cancelCardEvent();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    
    public PlayerBox getPlayerBoxPanel()
    {
        return playerBoxPanel;
    }
    
    public void setIsPlayerTurn(boolean is)
    {
        isPlayerTurn = is;
        if(isPlayerTurn)
            unActivateAllCards();
    }

    public void addCard(Card card)
    {       
        if(!cardsInPlay.containsKey(card.getCardID()))
        {
            //set card location
            if(isOpponent)
                card.setCardLocation(CardLocation.OPPONENT_PLAY_AREA);
            else
                card.setCardLocation(CardLocation.PLAYER_PLAY_AREA);
            
            card.setPlayArea(this);
            
            //card.applySize(cardSubPanel.getHeight());
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.setAlignmentY(Component.CENTER_ALIGNMENT);
            card.setFaceUp(true);
            cardSubPanel.add(card);
            CardMouseListener mouseListener = new CardMouseListener(card,this);
            card.addMouseListener(mouseListener); 
            cardsInPlay.put(card.getCardID(), card);
            //this.revalidate();
            //this.repaint();
            
            triggerETeffect(card);
            
            if(card instanceof SpellCard)
            {
                SpellCard scard = (SpellCard) card;
                
                //do activate on enter the battlefield
                Timer timer = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run()
                    {
                        timer.cancel();
                        selectCard(scard);  
                    }
                }; 
                timer.schedule(tt, 1000);
                
            }
        }
    }
    
    public void removeCard(Card card)
    {
        if(cardsInPlay.containsKey(card.getCardID()))
        {
            cardSubPanel.remove(cardsInPlay.get(card.getCardID()));
            addToDiscardPile(card);
            cardsInPlay.remove(card.getCardID()); 
            revalidate();
            repaint();
        }
    }
    
    public void addToDiscardPile(Card card)
    {
        discardPile.add(card);
    }
        
    public void selectCard(Card card)
    {
        gameWindow.createCardEvent(card);   
    }
    
    public HashMap<Integer,Card> getCardsInPlayArea()
    {
        return cardsInPlay;
    }
    
    public void triggerETeffect(Card card)
    {
        if(card.getETBeffect()==null)
            return;

        switch(card.getETBeffect())
        {
            case TAUNT:
            break;
                
            case BUFF_1:
                int buffBy = Integer.parseInt(card.getETBeffect().toString().split("_")[1]);
                BiConsumer<Integer,Card> buffConsumer = (i,c)-> {
                    //for each creature card in play, increase power by buff value
                    if(c instanceof CreatureCard && c.getCardID()!=c.getCardID())
                                ((CreatureCard) c).setPower(((CreatureCard) c).getPower()+buffBy);};
                cardsInPlay.forEach(buffConsumer);
            break;
        }        
    }
    
    public class CardMouseListener implements MouseListener
    {
        private Container container;
        private Card card;
        
        public CardMouseListener(Card card, Container container)
        {
            this.card = card;
            this.container = container;
        }
        
        
        @Override
        public void mouseClicked(MouseEvent e) 
        {

        }

        @Override
        public void mousePressed(MouseEvent e) 
        {
        }

        @Override
        public void mouseReleased(MouseEvent e) { 
            //only allow mouse events while its the players turn, or if its the declare blockers phase
            if(e.getButton()==MouseEvent.BUTTON1 && !gameWindow.getIsPlayerTurn() && gameWindow.getTurnPhase()==TurnPhase.DECLARE_BLOCKERS && !card.getIsActivated() && card.getCardLocation()==CardLocation.PLAYER_PLAY_AREA)
            {
                //if mouse 1 clicked
                //and is not your turn
                //and its declare blockers turn phase
                //and card is not already activated
                //add the clicked card is in your play area
                selectCard(card);
            }
            else
            if(e.getButton()==MouseEvent.BUTTON1 && gameWindow.getIsPlayerTurn() && gameWindow.getTurnPhase()!=TurnPhase.DECLARE_BLOCKERS)
            {
                //if mouse 1 clicked
                //and it is your turn
                //and the turn phase is NOT declare blockers
                selectCard(card);    
            }
            else
            if(e.getButton()==MouseEvent.BUTTON1 && gameWindow.getIsPlayerTurn() && gameWindow.getTurnPhase()==TurnPhase.COMBAT_PHASE)
            {
                //if mouse 1 clicked
                //and it is your turn
                //and its combat phase
                selectCard(card);
                
            }
            else
            if(e.getButton()==MouseEvent.BUTTON3)
            {
                //if mouse 3 (right button) clicked
                //gameWindow.zoomInCard(card);
                System.out.println(card.getCardID());
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
    }
    
    public class PlayerBoxMouseListener implements MouseListener
    {
        private Container container;
        private PlayerBox playerBox;
        
        public PlayerBoxMouseListener(PlayerBox box, Container container)
        {
            this.playerBox = box;
            this.container = container;
        }
        
        
        @Override
        public void mouseClicked(MouseEvent e) 
        {

        }

        @Override
        public void mousePressed(MouseEvent e) 
        {
        }

        @Override
        public void mouseReleased(MouseEvent e) { 
            //only allow mouse events while its the players
            if(gameWindow.getIsPlayerTurn())
            {
                gameWindow.createCardEvent(playerBox);           
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
    }
    
    public void unActivateAllCards()
    {
        BiConsumer <Integer,Card> consumer = (i,card)->{card.setIsActivated(false);};
        cardsInPlay.forEach(consumer);
        
    }
    
    public boolean checkForAvailableBlockers()
    {
        //returns true if any cards in play area are available to block
        //else returns false
        for(Map.Entry<Integer,Card> entry:cardsInPlay.entrySet())
        {
            if(entry.getValue() instanceof CreatureCard)
            {
                CreatureCard c = (CreatureCard) entry.getValue();
                if(!c.getIsActivated())
                    return true;
                else
                    return false;             
            }      
        }
        return false;        
    } 
}
