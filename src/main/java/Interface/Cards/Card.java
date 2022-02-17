/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

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
import Interface.PlayArea;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;



/**
 *
 * @author chris
 */
public class Card extends JPanel implements Serializable, Cloneable
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
    private int playCost = 1;
    transient private CardLocation location;
    transient private boolean isFaceUp = false;
    transient PlayArea playArea;
    transient Dimension dimension = new Dimension(width,height);
    transient int headingFontSize =8;
    transient int bodyFontSize = 6;
    transient Font headingFont = new Font("Arial",Font.BOLD,headingFontSize);
    transient Font bodyFont = new Font("Arial",Font.BOLD,bodyFontSize);
    transient private boolean isSelected = false;
    JLabel cardNameLabel;
    JPanel topPanel;
    JPanel pictureBox;
    JPanel bottomPanel;
    JLabel playCostLabel;
    

    public Card(String cardName)
    {
        this.cardName = cardName;
                
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        pictureBox = new JPanel(); 
        
        topPanel.setVisible(isFaceUp);
        topPanel.setBackground(Color.BLUE);
        pictureBox.setVisible(isFaceUp);
        pictureBox.setBackground(Color.PINK);
        bottomPanel.setBackground(Color.BLUE);
        topPanel.setBounds(strokeSize,strokeSize,getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/5));
        pictureBox.setBounds(strokeSize,topPanel.getY()+topPanel.getHeight(),getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/5)*3);
        bottomPanel.setBounds(strokeSize,pictureBox.getY()+pictureBox.getHeight(),topPanel.getWidth(), topPanel.getHeight());           
        add(topPanel); 
        add(pictureBox);
        add(bottomPanel);

        cardNameLabel = new JLabel(this.cardName,SwingConstants.LEFT);
        playCostLabel = new JLabel(""+playCost,SwingConstants.RIGHT);
                
        headingFont = new Font("Arial",Font.BOLD,headingFontSize);
        bodyFont = new Font("Arial",Font.BOLD,bodyFontSize);
        cardNameLabel.setFont(headingFont);
        playCostLabel.setFont(headingFont);
        
        topPanel.add(cardNameLabel);
        topPanel.add(playCostLabel); 
       
        
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
    
    public void setFaceUp(boolean is)
    {
        this.isFaceUp = is;
        if(isFaceUp)
            this.backgroundColor = Color.WHITE;
        else
            this.backgroundColor = Color.GRAY; 
        
        
        topPanel.setVisible(isFaceUp);
        pictureBox.setVisible(isFaceUp);
        bottomPanel.setVisible(isFaceUp);
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
        repaint();
        //set card height as 40% of board height
        
        topPanel.setBounds(strokeSize,strokeSize,getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/5));
        pictureBox.setBounds(strokeSize,topPanel.getY()+topPanel.getHeight(),getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/5)*3);
        bottomPanel.setBounds(strokeSize,pictureBox.getY()+pictureBox.getHeight(),topPanel.getWidth(), topPanel.getHeight());        
              
    }        

    public void setIsSelected(boolean is)
    {
        isSelected = is;
        repaint();
        revalidate();
    }
    
    public void setIsActivated(boolean is)
    {
        isActivated = is;
        repaint();
        revalidate();
    }
    
    public boolean getIsActivated()       
    {
        return isActivated;
    }
    
    public boolean getIsSelected()
    {
        return isSelected;
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
        if(location==CardLocation.PLAYER_PLAY_AREA | location==CardLocation.OPPONENT_PLAY_AREA)
        {
            headingFontSize = 8;
            bodyFontSize = 6;
        }
        else
        {
            headingFontSize = 8;
            bodyFontSize = 6;
        }
        revalidate(); 
        //repaint();
    }
    
    public void setPlayArea(PlayArea area)
    {
        this.playArea = area;
    }
     
    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        
        this.setForeground(Color.black);
        Color strokeColor = getForeground();
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
        //draw shadow
        if(dropShadow){

            graphics.setColor(shadowColor);
            graphics.fillRoundRect(shadowOffset,shadowOffset,width-strokeSize-shadowOffset,height-strokeSize-shadowOffset,arcSize,arcSize);
        } 
        
        //draw fill
        if(isSelected){   
            strokeColor = Color.RED;
            strokeSize = 5;
        }
        else
        if(isActivated){
            strokeColor = Color.GRAY;
            strokeSize = 5;
        }
        else
            strokeSize = 1;  
        
        //draw inside
        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        
        //draw outline
        graphics.setColor(strokeColor);
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.drawRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);

        topPanel.setBounds(strokeSize,strokeSize,getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/5));
        pictureBox.setBounds(strokeSize,topPanel.getY()+topPanel.getHeight(),getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/5)*3);
        bottomPanel.setBounds(strokeSize,pictureBox.getY()+pictureBox.getHeight(),topPanel.getWidth(), topPanel.getHeight());        
                
        headingFont = new Font("Arial",Font.BOLD,headingFontSize);
        bodyFont = new Font("Arial",Font.BOLD,bodyFontSize);
        cardNameLabel.setFont(headingFont);
        playCostLabel.setFont(headingFont);
    
    }
    
    @Override
    public boolean equals(Object object)
    {
        if(object instanceof Card)
        {
            Card other = (Card) object;
            if(this.cardID == other.getCardID())
                return true;
  
        }
        return false;
    }
        
    public CardLocation getCardLocation()
    {
        return location;
    }   
    
    public Card getClone()
    {
        //this method creates a deep copy of the card and returns it
        Card clone = new Card(this.getName());
        clone.setPlayCost(playCost);
        //set picture box
        return clone;
    }
}
