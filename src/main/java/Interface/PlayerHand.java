/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Interface.Cards.Card;
import Interface.Constants.CardLocation;
import Interface.Constants.TurnPhase;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLayeredPane;

/**
 *
 * @author chris
 */
public class PlayerHand extends JLayeredPane
{
    boolean isOpponents = false;
    int layers = 0;
    int width;
    int height;
    int maxHandSize = Constants.maxHandSize;
    ArrayList<Card> cardsInHand = new ArrayList<>();
    PlayArea playArea;
    Deck deck;
    //this is the origin of hte first card added
    Point origin;
    GameWindow gameWindow;
    ResourcePanel resourcePanel;
    private int discardTimeLimit = 10;
    
    public PlayerHand(int containerWidth, int containerHeight, PlayArea area, boolean isOpponents, GameWindow window, ResourcePanel panel)
    {
        resourcePanel = panel;
        gameWindow = window;
        this.isOpponents = isOpponents;
        playArea = area;
        width = containerWidth;
        height = (int) Math.round(containerHeight/4); 
        this.setPreferredSize(new Dimension(width, height));
        this.setOpaque(true);
        this.setBackground(Color.DARK_GRAY);
    }
    
    public boolean addCard(Card card)
    {
        if(!cardsInHand.contains(card))
        {
            cardsInHand.add(card);
            int height = (int) Math.round(this.height *0.8);
            card.applySize(height);
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            //set the position of the cards added
            card.setBounds(origin.x, origin.y,card.getWidth(),card.getHeight());
            if(!isOpponents)
                card.setFaceUp(true);
            this.add(card,layers);
            layers++;
            resizeHand();
            card.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e){
                }

                @Override
                public void mousePressed(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) 
                {
                    if(e.getButton()==MouseEvent.BUTTON1)
                    {
                        if(gameWindow.getIsPlayerTurn() && card.getCardLocation()==CardLocation.PLAYER_HAND)
                        {
                            if(gameWindow.getTurnPhase()==TurnPhase.END_PHASE)
                            {
                                discardCard(card);
                                if(checkHandSizeForEndTurn())
                                    gameWindow.passTurn();     
                            }
                            else
                                playCard(card,false);
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {}

                @Override
                public void mouseExited(MouseEvent e) {}
            });
            
            return true;
        }
        return false;
    }
    
    public List<Card> getCardsInHand()
    {
        return cardsInHand;
    }
    
    public void discardCard(Card card)
    {
        if(cardsInHand.contains(card))
        {
            int index = cardsInHand.indexOf(card);
            cardsInHand.remove(card);
            this.remove(card);
            resizeHand();
            layers--;
            //remove mouse listener assigned when card was added
            card.removeMouseListener(card.getMouseListeners()[0]);
            playArea.addToDiscardPile(card);
            
            if(!isOpponents)
            {
                Message message = new Message();
                message.setText("PLAYER_DISCARD_CARD");
                message.setCard(card);
                gameWindow.sendMessage(message);
            }
        }    
    }
    
    public void removeCard(Card card)
    {
        if(cardsInHand.contains(card))
        {
            int index = cardsInHand.indexOf(card);
            cardsInHand.remove(card);
            this.remove(card);
            resizeHand();
            layers--;
            //remove mouse listener assigned when card was added
            card.removeMouseListener(card.getMouseListeners()[0]);
        }
    }
    
    public void setDeckArea(Deck deck)
    {
        this.deck = deck;
        int spacing = Math.round((height-deck.getHeight())/2);
        deck.setBounds(spacing,spacing,deck.getWidth(),deck.getHeight());
        //set the points where cards are added relative to the deck area
        origin = new Point(deck.getWidth()+(spacing*2), deck.getY());
        Integer num = 0;
        //this.add(deck);
        this.add(deck,num);
    }
    
    public void resizeHand()
    {
        origin.x = (deck.getWidth()+(Math.round((height-deck.getHeight())/2)*2));
        
        //@index parameter is the hard that was taken from the hand
        for(int x = 0; x<cardsInHand.size();x++)
        {
            Card card = (Card) cardsInHand.get(x);
            this.remove(card);
            layers--;
            card.setBounds(origin.x, origin.y,card.getWidth(),card.getHeight());
            this.add(card,layers);
            origin.x += card.getWidth()-10;
            layers++;
            repaint();
            revalidate();
        }  
    }
    
    public void playCard(Card card, boolean isOpponents)
    {
        if(card.getPlayCost()>resourcePanel.getAmount())
            return;
         
        if(!isOpponents)
            card.setCardLocation(CardLocation.PLAYER_HAND);
            
        else
            card.setCardLocation(CardLocation.OPPONENT_HAND);           

        resourcePanel.useResources(card.getPlayCost());
        playArea.addCard(card);
        removeCard(card);
        
        //***************
        //send message to connected server/client
        if(!isOpponents)
        {
            Message message = new Message();
            message.setText("OPPONENT_PLAY_CARD");
            message.setCard(card);
            gameWindow.sendMessage(message);
        }
        
    }
    
    public Deck getDeck()
    {
        return deck;
    }
        
    public void dealHand()
    {
        for(int x=0;x<maxHandSize;x++)
            deck.drawCard();
    }
    
    public int getNumCards()
    {
        return this.cardsInHand.size();
    }
    
    public int getMaxHandSize()
    {
        return maxHandSize;
    }
    
    public boolean checkHandSizeForEndTurn()
    {        
        gameWindow.setTurnPhase(TurnPhase.END_PHASE);
        
        int numCardsOver = cardsInHand.size()-maxHandSize;
  
        if(numCardsOver>0)
        {
            gameWindow.getGameControlPanel().setNotificationLabel("Discard " + numCardsOver + " cards");
            return false;
        }
        else
        {
            gameWindow.getGameControlPanel().setNotificationLabel("");    
            return true;
        }
    }
        
}

    
    
    
    
    
    

