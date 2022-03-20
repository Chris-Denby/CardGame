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
import Interface.Constants.DeathEffect;
import Interface.Constants.ETBeffect;
import Interface.InnerCardPanel;
import Interface.PlayArea;
import Interface.PlayerHand;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;



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
    private Color shadowColor = Constants.shadowColor;
    private boolean dropShadow = true;
    private boolean highQuality = true;
    private int shadowGap = 4;
    private int shadowOffset = 4;
    private int shadowAlpha = 150; //transparency from (0-255)
    private Color cardBaseColor = Constants.cardBaseColor;
    private Color backgroundColor = cardBaseColor;
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
    transient int bodyFontSize = 8;
    transient Font headingFont = new Font("Arial",Font.BOLD,headingFontSize);
    transient Font bodyFont = new Font("Arial",Font.PLAIN,bodyFontSize);
    transient private boolean isSelected = false;
    private int imageID;
    private ETBeffect etbEffect;
    private DeathEffect deathEffect;
    private boolean isPlayable;
    JLabel cardNameLabel;
    JPanel topPanel;
    ImagePanel pictureBox;
    JPanel bottomPanel;
    JLabel playCostLabel;
    JTextPane bodyBox;
    InnerCardPanel innerPanel;
    Image cardBack;
    Image cardImage;
    boolean zoomed = false;

    public Card(String cardName, int imageId)
    {
        this.cardName = cardName;
        this.imageID = imageID;
        
        innerPanel = new InnerCardPanel();
                
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        bodyBox = new JTextPane();
        setBodyText("Basic Creature");
        bodyBox.setEditable(false);
        pictureBox = new ImagePanel();

        topPanel.setBackground(new Color(0,0,0,100));
        bottomPanel.setBackground(new Color(0,0,0,100));
        pictureBox.setBackground(new Color(255,255,255,0));
        bodyBox.setBackground(new Color(255,255,255,180));
        innerPanel.setBackground(new Color(0,102,102));
        
        topPanel.setVisible(isFaceUp);
        pictureBox.setVisible(isFaceUp);
        bodyBox.setVisible(isFaceUp);        
        bottomPanel.setVisible(isFaceUp);

        
        cardNameLabel = new JLabel(this.cardName,SwingConstants.LEFT);
        cardNameLabel.setForeground(Color.WHITE);
        playCostLabel = new JLabel(""+playCost,SwingConstants.RIGHT);
        playCostLabel.setForeground(Color.WHITE);
                

        

        innerPanel.setLayout(new BoxLayout(innerPanel,BoxLayout.Y_AXIS));

        this.add(innerPanel);

        topPanel.setLayout(new BorderLayout());
        topPanel.add(cardNameLabel,BorderLayout.WEST);
        topPanel.add(playCostLabel, BorderLayout.EAST);
        
        innerPanel.add(topPanel); 
        innerPanel.add(pictureBox);
        innerPanel.add(bodyBox);
        innerPanel.add(bottomPanel);

    }
    
    public PlayerHand getPlayerHand()
    {
        return playerHand;
    }

    public DeathEffect getDeathEffect() {
        return deathEffect;
    }

    public void setDeathEffect(DeathEffect deathEffect) {
        this.deathEffect = deathEffect;
        if(this.getDeathEffect()!=DeathEffect.NONE)
            setBodyText(this.getDeathEffect().toString());
        
    }

    public ETBeffect getETBeffect()
    {
        return etbEffect;
    }
    
    public void setETBeffect(ETBeffect effect) {
        etbEffect = effect;        
        if(this.getETBeffect()!=ETBeffect.NONE)
            setBodyText(this.getETBeffect().toString());
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
        if(isFaceUp){
            this.backgroundColor =  cardBaseColor;
        }
        else{
            this.backgroundColor = Color.GRAY;
        }

        innerPanel.setVisible(is);
        topPanel.setVisible(isFaceUp);
        pictureBox.setVisible(isFaceUp);
        bodyBox.setVisible(isFaceUp);
        bottomPanel.setVisible(isFaceUp);
    }
    
    public void applySize(int h)
    {
        //parameter 'h' is the container height of hte card.
        
        //SET RECTANGLE 
        this.height = (int) Math.round(h*0.75); //resize the card to be a fraction of its container height
        this.setOpaque(false); //makes this panel transparent
        this.width = (int) Math.round(height*Constants.cardAspectRatio);
        arcSize = (int) Math.round(height/20);
        this.setMinimumSize(new Dimension(width,height));
        this.setPreferredSize(new Dimension(width,height));
        this.setSize(new Dimension(width,height));
        
        this.setLayout(null);



        innerPanel.setBounds(
                arcSize,
                arcSize,
                width-shadowGap-arcSize-arcSize,    //width
                height-shadowGap-arcSize-arcSize);  //height
        
        int innerWidth = innerPanel.getBounds().width;
        int innerHeight = innerPanel.getBounds().height;
        
        if(cardImage!=null){
            cardImage = cardImage.getScaledInstance(innerWidth, innerHeight, Image.SCALE_DEFAULT);
            this.setImage(cardImage);
        }
        
        
        topPanel.setPreferredSize(new Dimension(innerWidth,(int) Math.round((innerHeight/10)*1)));
        pictureBox.setPreferredSize(new Dimension(innerWidth,Math.round((innerHeight/10)*4)));
        bodyBox.setPreferredSize(new Dimension(innerWidth,(int) Math.round((innerHeight/10)*3.5)));
        bottomPanel.setPreferredSize(new Dimension(innerWidth,Math.round(innerHeight/10)*1));
        
        
        if(zoomed){
            headingFont = new Font("Arial",Font.BOLD,20);
            bodyFont = new Font("Arial",Font.PLAIN,20); 
        }
        
        cardNameLabel.setFont(headingFont);
        playCostLabel.setFont(headingFont);
        bodyBox.setFont(bodyFont);
        
        repaint();
     
    }      

    public void setIsSelected(boolean is)
    {
        if(is)
        backgroundColor = Color.orange;
        else
            backgroundColor = cardBaseColor;
        
        isSelected = is;
        repaint();
        revalidate();
    }
    
    public void setIsActivated(boolean is)
    {
        if(is)
        backgroundColor = Color.GRAY;
        else
            backgroundColor = cardBaseColor;
        
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
            this.remove(topPanel);
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
        cardImage = img;
        innerPanel.setImage(cardImage);
    }
    
    public void setBodyText(String text)    
    {
        

        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(attribs, "SansSerif");
        StyleConstants.setFontSize(attribs, bodyFontSize);
        bodyBox.setParagraphAttributes(attribs, true);

        if(text.equals("Basic Creature"))
        {
            bodyBox.setText(text);
            return;
        }
        
        if(bodyBox.getText().equals("Basic Creature"))
            bodyBox.setText("");
            
        String textToAdd = text.replace('_', ' ');
        StringBuilder sb = new StringBuilder(bodyBox.getText());
        if(!bodyBox.getText().equals(""))
            sb.append("\n");
        
        sb.append(textToAdd);
 
        bodyBox.setText(sb.toString());
    }
    
    public void setIsPlayable(boolean is)
    {
        isPlayable = is; 
        
        if(is && isFaceUp)
            backgroundColor = Color.GREEN;
        else if (!is)
        {
            if(isFaceUp)
                this.setFaceUp(true);
            else
                setFaceUp(false);
        }
        repaint();
        revalidate();
    }

    public PlayArea getPlayArea() {
        return playArea;
    }
    
    public void setZoomed(boolean is)
    {
        zoomed = is;
        if(is)
        {
            SimpleAttributeSet attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontFamily(attribs, "SansSerif");
            StyleConstants.setFontSize(attribs, bodyFontSize);
            bodyBox.setParagraphAttributes(attribs, true);
            
            
            String etbString = etbEffect.toString().replace('_', ' ');
            String deathString = deathEffect.toString().replace('_', ' ');
            String etbDescription;
            String deathDescription;
            StringBuilder sb = new StringBuilder();
            
            //ETB EFFECT
            if(etbEffect==ETBeffect.Buff_Power)
            {
                int buffValue = Math.round(getPlayCost()/Constants.buffModifier);
                if(buffValue<1)
                    buffValue = 1;
                
                etbDescription = "Increases the power of the two left minions by " + buffValue + " while in play";
                sb.append(etbString);
                sb.append("\n");
                sb.append(etbDescription);
                sb.append("\n");
                sb.append("\n");

            }
            else if(etbEffect==ETBeffect.Taunt)
            {
                etbDescription = "Is not yet implemented";
                sb.append(etbString);
                sb.append("\n");
                sb.append(etbDescription);
                sb.append("\n");
                sb.append("\n");
            }

            //DEATH EFFECT
            if(deathEffect==DeathEffect.Gain_Life)
            {
                deathDescription = "When destroyed, gain " + getPlayCost() + " life";
                sb.append("\n");
                sb.append(deathDescription);
            }
            
            if(etbEffect==ETBeffect.NONE && deathEffect == DeathEffect.NONE)
            {
                sb.append("Basic Minion");
                sb.append("\n");
                sb.append("This minion has no abilities");    
            }
            bodyBox.setText(sb.toString());
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
        
        //draw inside
        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        
        //draw outline
        graphics.setColor(strokeColor);
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.drawRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
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
    
    public void getCardSize()
    {
        int w = width-shadowGap;
        int h = height-shadowGap;
        
        System.out.println(innerPanel.getHeight() + ", " + innerPanel.getWidth());
        
    }
}
