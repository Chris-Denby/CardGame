/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

import Interface.Constants.CardLocation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
    
    public CreatureCard(String cardName) 
    {
        super(cardName);
        powerLabel = new JLabel(power+"",SwingConstants.CENTER);
        powerLabel.setFont(headingFont);
        powerLabel.setVerticalAlignment(SwingConstants.CENTER);
        toughnessLabel = new JLabel(toughness+"", SwingConstants.CENTER);
        toughnessLabel.setFont(headingFont);

        bottomPanel.add(powerLabel);
        bottomPanel.add(toughnessLabel); 
        
    }

    public int getPower() 
    {
        return power;
    }

    public void setPower(int power) 
    {
        this.power = power;
    }

    public int getToughness() 
    {
        return toughness;
    }

    public void setToughness(int toughness) 
    {
        this.toughness = toughness;
    }
    
    public void setLocation(CardLocation l)
    {
        super.setCardLocation(l);
        powerLabel.setFont(headingFont);
        toughnessLabel.setFont(headingFont);
        revalidate();
    }
    
    public void setFaceUp(boolean is)
    {
        super.setFaceUp(is);
        powerLabel.setVisible(is);
        toughnessLabel.setVisible(is);
    }

    public void takeDamage(int damage)
    {
        this.toughness = toughness - damage;
        this.toughnessLabel.setText(toughness+"");
        if(toughness<=0)
        {
            //if toughness is reduced to 0 or below - it dies
            playArea.removeCard(this);
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        powerLabel.setFont(headingFont);
        toughnessLabel.setFont(headingFont);        
   
    }
    
    @Override
    public CreatureCard getClone()
    {
        //this method creates a deep copy of the card and returns it
        CreatureCard clone = new CreatureCard(getName());
        clone.setPlayCost(getPlayCost());
        clone.setPower(power);
        clone.setToughness(toughness);
        //set picture box
        return clone;
    }
    
    
    
    
    
    
    
    
    
    
}