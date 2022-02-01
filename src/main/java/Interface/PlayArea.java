/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
    JPanel playerPlayArea;
    JPanel opponentsPlayArea;
            
    ArrayList<Card> cardsInPlayArea = new ArrayList<Card>();

    
    public PlayArea(int containerWidth, int containerHeight, GameWindow window, boolean isOpponent)
    {
        gameWindow = window;
        this.isOpponent = isOpponent;
        width = containerWidth;
        //height is the container minus the who player and opponents hands
        height = (int) containerHeight-Math.round(containerHeight/2); 
        this.setPreferredSize(new Dimension(width,height));
        this.setOpaque(false);
        //BoxLayout box = new BoxLayout(this, BoxLayout.X_AXIS);
        //this.setLayout(box);
    }
    
    public void addCard(Card card)
    {
        if(!cardsInPlayArea.contains(card))
        {
            cardsInPlayArea.add(card);
            int height = (int) Math.round(this.height *0.25);
            card.applySize(height);
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(card, BorderLayout.PAGE_END);
            this.revalidate();
            this.repaint();
            MyMouseListener mouseListener = new MyMouseListener();
            card.addMouseListener(mouseListener);           
        }
    }
    
    public void removeCard(Card card)
    {
        if(cardsInPlayArea.contains(card))
        {
            cardsInPlayArea.remove(card);
            this.remove(card);
            this.revalidate();
            this.repaint();
        }
    }
    
    public void activateCard(Card card)
    {
        System.out.println("Card activated - " + card.getName());
        
        //***************
        //send message to connected server/client
        if(!isOpponent)
        {
            Message message = new Message();
            message.setText("OPPONENT_ACTIVATE_CARD");
            message.setCard(card);
            gameWindow.sendMessage(message);
        }
    }
    
    public class MyMouseListener implements MouseListener
    {

        @Override
        public void mouseClicked(MouseEvent e) {
            activateCard((Card) e.getSource());
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
        
    }
    
}
