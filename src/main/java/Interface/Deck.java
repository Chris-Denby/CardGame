/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JLayeredPane;

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
        this.setBackground(Color.LIGHT_GRAY);
        this.setOpaque(true);
        this.setSize(new Dimension(width,height));
            
        if(!isOpponents)
        {
            Timer timer = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    populateDeck();
                }
            };
            timer.schedule(tt, 5000);
        }

    }
    
    public void addCard(Card card)
    { 
        card.setPlayArea(playArea);
        cardsInDeck.add(card);
        card.setBounds(origin.x+offset,origin.y,card.getWidth()-1,card.getHeight()-1);  
        card.setSize(Math.round(height*0.75f),height);
        card.applySize(card.getHeight());
        offset+=1;
        this.add(card,layer);
        layer++;
        
        //***************
        //send message to connected server/client
        if(!isOpponents)
        {
            Message m = new Message();
            m.setText("OPPONENT_ADD_CARD_TO_DECK");
            m.setCard(card);
            gameWindow.sendMessage(m); 
        }
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
        }       
        return card;
    }
    
    public void populateDeck()
    {
        int deckSize = 60;
        int x=0;
        while(x<=deckSize)
        {
            Card card = new Card(ThreadLocalRandom.current().nextInt(0,61)+"");
            addCard(card);
            x++;
        }
    }
  
    
    
    
}
