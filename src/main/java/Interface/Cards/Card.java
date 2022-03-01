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
import Interface.PlayerHand;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Shape;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;



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
    int playCost = 1;
    transient private CardLocation location;
    transient private boolean isFaceUp = false;
    transient PlayArea playArea;
    transient PlayerHand playerHand;
    transient Dimension dimension = new Dimension(width,height);
    transient int headingFontSize =8;
    transient int bodyFontSize = 6;
    transient Font headingFont = new Font("Arial",Font.BOLD,headingFontSize);
    transient Font bodyFont = new Font("Arial",Font.BOLD,bodyFontSize);
    transient private boolean isSelected = false;
    private int imageID;
    JLabel cardNameLabel;
    JPanel topPanel;
    ImagePanel pictureBox;
    JPanel bottomPanel;
    JLabel playCostLabel;
    JTextPane bodyBox;
    Image cardBack;
    

    public Card(String cardName, int imageId)
    {
        this.cardName = cardName;
        this.imageID = imageID;
                
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        bodyBox = new JTextPane();
        bodyBox.setEditable(false);
        pictureBox = new ImagePanel();

        topPanel.setBackground(Color.WHITE);
        bodyBox.setBackground(Color.GREEN);
        pictureBox.setBackground(Color.PINK);
        bottomPanel.setBackground(Color.WHITE);
        
        topPanel.setVisible(isFaceUp);
        pictureBox.setVisible(isFaceUp);
        bodyBox.setVisible(isFaceUp);        
        bottomPanel.setVisible(isFaceUp);
        
        cardNameLabel = new JLabel(this.cardName,SwingConstants.LEFT);
        playCostLabel = new JLabel(""+playCost,SwingConstants.RIGHT);
                
        headingFont = new Font("Arial",Font.BOLD,headingFontSize);
        bodyFont = new Font("Arial",Font.BOLD,bodyFontSize);
        cardNameLabel.setFont(headingFont);
        playCostLabel.setFont(headingFont);
        
        GridBagConstraints gbConstraints = new GridBagConstraints();
        topPanel.setLayout(new GridBagLayout());
        
        //gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.ipadx = 0;
        gbConstraints.ipady = 0;
        //int insetWidth = topPanel.getWidth()-cardNameLabel.getWidth()-playCostLabel.getWidth();
        //gbConstraints.insets = new Insets(0,0,insetWidth,4);
        gbConstraints.ipady = 0;
        gbConstraints.weightx =1;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.anchor = GridBagConstraints.LINE_END;
        topPanel.add(cardNameLabel, gbConstraints);
        
        //gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.ipadx = 0;
        gbConstraints.ipady = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;
        gbConstraints.anchor = GridBagConstraints.LINE_START;
        topPanel.add(playCostLabel, gbConstraints);
        
        add(topPanel); 
        add(pictureBox);
        add(bodyBox);
        add(bottomPanel);
        
        
       
        
    }
    
    public PlayerHand getPlayerHand()
    {
        return playerHand;
    }
    
    public void setCardBack(Image img)
    {
        cardBack = img;
    }
    
    public void setImageID(int id)
    {
        imageID = id;
    }

    public void setCardID(int id)
    {
        cardID = id;
    }
    
    public int getCardID()
    {
        return cardID;
    }
    
    public int getImageID()
    {
        return imageID;
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
        bodyBox.setVisible(isFaceUp);
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
        playCostLabel.setText(cost+"");
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
            this.remove(topPanel);
        }
        else
        {
            headingFontSize = 8;
            bodyFontSize = 6;
        }
        revalidate(); 
        repaint();
    }
    
    public void setPlayArea(PlayArea area)
    {
        this.playArea = area;
    }
    
    public void setPlayerHand(PlayerHand hand)
    {
        this.playerHand = hand;
    }
    
    public void removeFromPlayerHand()
    {
        playerHand.removeCard(this);
    }
    
    public void removeFromPlayArea()
    {
        playArea.removeCard(this);
    }
    
    public void setImage(Image img)
    {
        pictureBox.setImage(img);
    }
    
    public void setBodyText(String text)    
    {
        bodyBox.setText(text);        

        StyledDocument doc = (StyledDocument) bodyBox.getDocument();
        Style style = doc.addStyle("style", null);
        
        StyleConstants.setFontSize(style , bodyFontSize);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
        
        //doc.setParagraphAttributes(0, doc.getLength(), set , true);
        //doc.setCharacterAttributes(0, doc.getLength(), set, false);
        
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            strokeSize = 2;
        }
        else
        if(isActivated){
            strokeColor = Color.GRAY;
            strokeSize = 2;
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

        topPanel.setBounds(strokeSize,strokeSize,getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/10));
        pictureBox.setBounds(strokeSize,topPanel.getY()+topPanel.getHeight(),getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/10)*4);
        bodyBox.setBounds(strokeSize,pictureBox.getY()+pictureBox.getHeight(),getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/10)*4);
        bottomPanel.setBounds(strokeSize,bodyBox.getY()+bodyBox.getHeight(),getWidth()-shadowGap-(strokeSize*2), Math.round((getHeight()-shadowOffset-(strokeSize*2))/10));
        
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
    
    public Card getClone(Image img)
    {
        //this method creates a deep copy of the card and returns it
        Card clone = new Card(cardName,imageID);
        clone.setPlayCost(playCost);
        clone.setImage(img);
        //set picture box
        return clone;
    }
}
