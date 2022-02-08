/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

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
        
    public void populateDeckAndDeal()
    {
        int deckSize = 60;
        int x=0;
        while(x<=deckSize)
        {
            Card card = new CreatureCard(ThreadLocalRandom.current().nextInt(0,61)+"");
            card.setCardID(System.identityHashCode(card));
            addCard(card);
            x++;
        }
        
        //deal out first hand
        playerHand.dealHand();

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
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
  
    
    
    
}
