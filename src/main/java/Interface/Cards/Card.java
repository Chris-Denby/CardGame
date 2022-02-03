/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

import Interface.Constants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import Interface.Constants.CardLocation;



/**
 *
 * @author chris
 */
public class Card extends JPanel implements Serializable
{
    private String objectID;
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
    private boolean isActivated = false;
    private String cardName;
    private int cardID;
    private int playCost;
    transient private CardLocation location;
    
          
    
    
    JLabel cardNameLabel = new JLabel();
    Dimension dimension = new Dimension(width,height);
    
    public Card(String cardName)
    {    
        this.cardName = cardName;
        cardNameLabel.setText(this.cardName);
        add(cardNameLabel); 
    }
    
    public void setCardID(int id)
    {
        cardID = id;
    }
    
    public int getCardID()
    {
        return cardID;
    }
        
    public String getName()
    {
        return cardName;
    }
    
    public void setName(String name)
    {
        cardName = name;
        cardNameLabel.setText(cardName);
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
        
        Color strokeColor = getForeground();
        
        if(isActivated)
        {   
            strokeColor = Color.RED;
            strokeSize = 5;
        }
        else
        {
            strokeSize = 1;
        }
        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        graphics.setColor(strokeColor);
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.drawRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        graphics.setStroke(new BasicStroke());   
    }
        
    public void activateCard(boolean activated)
    {
        this.isActivated = activated;
        this.repaint();
        this.revalidate();
    }
    
    public boolean isActivated()       
    {
        return isActivated;
    }
    
    public void setPlayCost(int cost)
    {
        playCost = cost;
    }
    
    public int getPlayCost()
    {
        return playCost;
    }
    
    public void setCardLocation(CardLocation l)
    {
        location = l;
    }
    
    public CardLocation getCardLocation()
    {
        return location;
    }
    
    /**
    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Card)
        {
            sameSame = this.cardID == ((Card) object).getCardID();
        }

        return sameSame;
    }
    **/
    
   
}
