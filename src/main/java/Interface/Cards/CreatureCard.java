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
import javax.swing.JLabel;
import javax.swing.JPanel;

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
    
    public CreatureCard(String cardName, int imageID) 
    {
        super(cardName,imageID);
        
        
        powerLabel = new JLabel(power+"");
        powerLabel.setFont(headingFont);
        //powerLabel.setVerticalAlignment(SwingConstants.CENTER);
        toughnessLabel = new JLabel(toughness+"");
        toughnessLabel.setFont(headingFont);        

        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(powerLabel,BorderLayout.WEST);
        bottomPanel.add(toughnessLabel, BorderLayout.EAST);     
    }
     
    
    public int getPower() 
    {
        return power;
    }

    public void setPower(int power) 
    {
        this.power = power;
        powerLabel.setText(power+"");
        
    }

    public void setBuffed(boolean is, int buff) 
    {
        isBuffed = is;
        if(is)
        {
            this.power = power + buff;
            powerLabel.setText(power+"");
            powerLabel.setFont(modifiedStatFont);
            powerLabel.setForeground(Color.BLUE);
        }
        else
        {
            this.power = power - buff;
            powerLabel.setText(power+"");
            powerLabel.setFont(headingFont);
            powerLabel.setForeground(Color.BLACK);
        }
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
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        powerLabel.setFont(bodyFont);
        toughnessLabel.setFont(bodyFont);        
   
    }
    
    @Override
    public CreatureCard getClone(Image img)
    {
        //this method creates a deep copy of the card and returns it
        CreatureCard clone = new CreatureCard(getName(),getImageID());
        clone.setImage(img);
        clone.setPlayCost(getPlayCost());
        clone.setPower(power);
        clone.setToughness(toughness);
        //set picture box
        return clone;
    }
}
