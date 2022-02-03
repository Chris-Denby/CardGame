/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Interface.Cards.Card;
import Interface.Constants.CardLocation;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
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
            
    ArrayList<Card> cards = new ArrayList<Card>();
    private Deque<CardEvent> cardEventStack = new ArrayDeque<CardEvent>();
 
    public PlayArea(int containerWidth, int containerHeight, GameWindow window, boolean isOpponent)
    {
        gameWindow = window;
        this.isOpponent = isOpponent;
        width = containerWidth;
        
        //height is the container minus the who player and opponents hands
        height = (int) containerHeight-Math.round(containerHeight/2); 
        this.setPreferredSize(new Dimension(width,height));
        this.setOpaque(false); 
    }
    
    public void setIsPlayerTurn(boolean is)
    {
        isPlayerTurn = is;
    }

    public void addCard(Card card)
    {       
        //
        if(!cards.contains(card))
        {
            //set card location
            if(isOpponent)
                card.setCardLocation(CardLocation.OPPONENT_PLAY_AREA);
            else
                card.setCardLocation(CardLocation.PLAYER_HAND);
            
            cards.add(card);
            int height = (int) Math.round(this.height *0.25);
            card.applySize(height);
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(card, BorderLayout.PAGE_END);
            this.revalidate();
            this.repaint();
            MyMouseListener mouseListener = new MyMouseListener(card,this);
            card.addMouseListener(mouseListener);  
        }
    }
    
    public void removeCard(Card card)
    {
        if(cards.contains(card))
        {
            cards.remove(card);
            this.remove(card);
            this.revalidate();
            this.repaint();
        }
    }
    
    public void showSelectedCard(Card card)
    {          
        for(Card c:cards)
        {
            if(c.getCardID()==card.getCardID())
                c.activateCard(true);
        }    
    }
    
    public void activateCard(Card card)
    {
        System.out.println(this.getName());
        gameWindow.createCardEvent(card);   
    }
    
    public ArrayList<Card> getCardsInPlayArea()
    {
        return cards;
    }
    
    public class MyMouseListener implements MouseListener
    {
        private Container container;
        private Card card;
        
        public MyMouseListener(Card card, Container container)
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
            //only allow mouse events while its the players 
            if(isPlayerTurn)
            {
                activateCard(card);            
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
    }
    
}
