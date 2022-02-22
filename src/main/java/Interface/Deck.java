/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Database.JSONHelper;
import Interface.Cards.Card;
import Interface.Cards.CreatureCard;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JLayeredPane;
import Interface.Constants.CardLocation;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
public class Deck extends JLayeredPane
{
    
    int width;
    int height;
    PlayerHand playerHand;
    PlayArea playArea;
    Point origin;
    int offset = 0;
    List<Card> cardsInDeck = new ArrayList<Card>(); 
    GameWindow gameWindow;
    boolean isOpponents = false;
    Integer layer = 0;  
    
    public Deck(PlayerHand hand, PlayArea area, GameWindow window, boolean isOpponents)
    {
        gameWindow = window;
        this.isOpponents = isOpponents;
        origin = new Point(2,2);
        playerHand = hand;
        playArea = area;
        height = (int) Math.round(hand.height * 0.8);
        width = height*2;
        //this.setBackground(Color.LIGHT_GRAY);
        this.setOpaque(true);
        this.setSize(new Dimension(width,height));
        
            

    }
    
    public void addCard(Card card)
    { 
        

        card.setBounds(origin.x+offset,origin.y,card.getWidth()-1,card.getHeight()-1);  
        card.setSize(Math.round(height*0.75f),height);
        card.applySize(card.getHeight());
        offset+=1;
        
        //***************
        //send message to connected server/client
        if(!isOpponents)
        {
            Message m = new Message();
            m.setText("OPPONENT_ADD_CARD_TO_DECK");
            m.setCard(card);
            gameWindow.sendMessage(m); 
            card.setCardLocation(CardLocation.PLAYER_HAND);
        }
        else
        if(isOpponents)
            card.setCardLocation(CardLocation.OPPONENT_HAND);
            card.setFaceUp(false);
        
        cardsInDeck.add(card);
        this.add(card,layer);
        layer++;
    }
    
    public void removeCard (Card card)
    {
        cardsInDeck.remove(card);
        this.remove(card);
        layer--;
    }
    
    public Card drawCard()
    {
        Card card = null;
        //add new card to play area from the top of the deck
        if(playerHand.addCard(cardsInDeck.get(cardsInDeck.size()-1)))
        {
            removeCard(cardsInDeck.get(cardsInDeck.size()-1));
            //System.out.println(cardsInDeck.size() + " cards remaining");  
            if(!isOpponents)
            {
                Message m = new Message();
                m.setText("OPPONENT_DRAW_CARD");
                gameWindow.sendMessage(m);
            }
        }       
        return card;
    }
        
    public void populateDeckAndDeal(boolean isPlayer1)
    {
        JSONHelper h = new JSONHelper(); 
        
        if(isPlayer1)
        {        
            //read cards from JSON DB
            //populate users deck  from JSON  
            
            for(Card c:h.readJSONFile("player1Cards"))
            {
                c.setImage(gameWindow.getImageFromCache(c.getImageID()));
                //c.setCardBack((gameWindow.getImageFromCache(999)));
                addCard(c);
            }
        }
        else 
        {
            //read cards from JSON DB
            //populate users deck  from JSON         
            for(Card c:h.readJSONFile("player2Cards"))
            {
                c.setImage(gameWindow.getImageFromCache(c.getImageID()));
                //c.setCardBack((gameWindow.getImageFromCache(999)));
                addCard(c);
            }
        }
        shuffleDeck();
        //deal out first hand
        playerHand.dealHand();
    }
    
    public void shuffleDeck()
    {
        layer = 0;
        this.removeAll();
        Collections.shuffle(cardsInDeck);
        for(Card c:cardsInDeck)
        {
            add(c,layer);
            layer++;
        }
    }
    
    public List<Card> getCardsInDeck()
    {
        return cardsInDeck;
    }  
}
