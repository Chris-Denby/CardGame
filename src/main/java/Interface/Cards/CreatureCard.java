/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

import Interface.Constants;
import Interface.Constants.CardLocation;
import Interface.Constants.ETBeffect;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/**
 *
 * @author chris
 */
public class CreatureCard extends Card
{
    private int power = 1;
    private int toughness = 1;
    private JLabel powerLabel;
    private JLabel toughnessLabel;
    private Font modifiedStatFont = new Font("Arial",Font.BOLD,headingFontSize+2);
    private boolean isBuffed = false;
    private int buffedBy = 0;
    
    public CreatureCard(String cardName, int imageID) 
    {
        super(cardName,imageID);
        
        
        powerLabel = new JLabel();
        powerLabel.setFont(headingFont);
        toughnessLabel = new JLabel();
        toughnessLabel.setFont(headingFont);
        powerLabel.setForeground(Color.WHITE);
        toughnessLabel.setForeground(Color.WHITE);
        toughnessLabel.setOpaque(false);
        
        

        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(powerLabel,BorderLayout.WEST);
        bottomPanel.add(toughnessLabel, BorderLayout.EAST);     
    }
     
    
    public int getPower() 
    {
        return power + buffedBy;
    }

    public void setPower(int power) 
    {
        this.power = power;
        powerLabel.setText(power+"");
        
    }
    
    public boolean getIsBuffed()
    {
        return isBuffed;
    }

    public void setBuffed(int buff) 
    {
        buffedBy = buffedBy + buff;
        powerLabel.setText(getPower()+"");
        
        if(buffedBy>0)
        {
            isBuffed = true;
            powerLabel.setForeground(Color.ORANGE);
        }
        else
        {
            isBuffed = false;
            powerLabel.setForeground(Color.WHITE);
            
        }
        System.out.println("my power is now " + power + ", and is buffed by " + buffedBy);
    }

    public int getToughness() 
    {
        return toughness;
    }

    public void setToughness(int toughness) 
    {
        this.toughness = toughness;
        toughnessLabel.setText(toughness+"");
    }
    
    public void setLocation(CardLocation l)
    {
        super.setCardLocation(l);
    }
    
    public void setFaceUp(boolean is)
    {
        super.setFaceUp(is);
        powerLabel.setVisible(is);
        toughnessLabel.setVisible(is);
    }

    public void takeDamage(int damage)
    {
        playerHand.getGameWindow().playSound("attackLand");
        if(this.toughness-damage<0)
            this.toughness = 0;
        else
            this.toughness = toughness-damage;
        
        this.toughnessLabel.setText(toughness+"");
        
        if(toughness<=0)
        {
            //if toughness is reduced to 0 or below - it dies
            this.playArea.triggerDeathFffect(this);
            playArea.removeCard(this);
        }
        repaint();
        revalidate();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        powerLabel.setFont(bodyFont);
        toughnessLabel.setFont(bodyFont);        
   
    }
    
    public CreatureCard getClone(Image img)
    {
        //this method creates a deep copy of the card and returns it
        CreatureCard clone = new CreatureCard(getName(),getImageID());
        clone.setImage(img);
        clone.setPlayCost(getPlayCost());
        clone.setPower(power);
        clone.setToughness(toughness);
        clone.setETBeffect(getETBeffect());
        clone.setDeathEffect(getDeathEffect());
        //set picture box
        return clone;
    }
    
    public void setCardValue()
    {
        //determine card value
        /**
        max card value = 
        7 power
        7 toughness
        1 ETB
        1 DE
        = 16
        **/

        int cardValue = power + toughness;
        if(getETBeffect()!=ETBeffect.NONE)
        cardValue++;
        if(getDeathEffect()!=Constants.DeathEffect.NONE)
        cardValue++;
        
        int borderStroke = 1;
        this.cardValue = cardValue;
        LineBorder border;
        Color borderColor;
        if(cardValue<4)
            borderColor = Constants.commonColor;
        else if(cardValue>=5 && cardValue<=8)
            borderColor = Constants.uncommonColor;
        else if(cardValue>=9 && cardValue<=12)
            borderColor = Constants.rareColor;
        else if(cardValue>13 && cardValue<=16)
            borderColor = Constants.mythicColor;
        else
            borderColor = Color.white;
        
        border = new LineBorder(borderColor,borderStroke);
        innerPanel.setBorder(border);
        
        cardNameLabel.setForeground(borderColor);
    }
    

}
