/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Interface.Constants.Location;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public class Card extends JPanel implements ActionListener,MouseMotionListener, Serializable
{
    private int width;
    private int height;
    private int arcSize;
    private int strokeSize = 1;
    private Color shadowColor = Color.DARK_GRAY;
    private boolean dropShadow = true;
    private boolean highQuality = true;
    private int shadowGap = 4;
    private int shadowOffset = 4;
    private int shadowAlpha = 150; //transparency from (0-255)
    private Color backgroundColor = Color.WHITE;
    transient private PlayArea playArea;
    private PlayerHand playerHand;
    private Location cardLocation;
    private boolean isTapped = false;
    private int defaultHeight;
    
    JLabel cardNameLabel = new JLabel();
    Dimension dimension = new Dimension(width,height);
    
    public Card(String cardName)
    {    
        cardNameLabel.setText(cardName);
        addMouseMotionListener(this);
        add(cardNameLabel); 
    }

        
    public void setPlayerHand(PlayerHand hand)
    {
        this.playerHand = hand;
    }

    public void applySize(int height)
    {        
        this.height = height;
        this.setOpaque(false); //makes this panel transparent
        width = (int) Math.round(height*0.75);
        arcSize = (int) Math.round(height/20);
        this.setMinimumSize(new Dimension(width,height));
        this.setPreferredSize(new Dimension(width,height));
        this.setSize(new Dimension(width,height));
        //set card height as 40% of board height    
    }
    
    
    public void setPlayArea(PlayArea playArea)
    {
        this.playArea = playArea; 
    }
    
    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        this.setForeground(Color.black);
        
        Graphics2D graphics = (Graphics2D) g;

        if(highQuality)
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
        if(dropShadow)
        {
            graphics.setColor(shadowColor);
            graphics.fillRoundRect(shadowOffset,shadowOffset,width-strokeSize-shadowOffset,height-strokeSize-shadowOffset,arcSize,arcSize);
        }   
        
        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        graphics.setColor(getForeground());
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.drawRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        graphics.setStroke(new BasicStroke());   
    }
    
    public void setCardLocation(Location loc)
    {
        cardLocation = loc;
    }  

    @Override
    public void actionPerformed(ActionEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
    
   
}
