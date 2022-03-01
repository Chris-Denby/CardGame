/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

import Interface.Constants.CardLocation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
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
    
    public CreatureCard(String cardName, int imageID) 
    {
        super(cardName,imageID);
        
        powerLabel = new JLabel(power+"");
        powerLabel.setFont(headingFont);
        //powerLabel.setVerticalAlignment(SwingConstants.CENTER);
        toughnessLabel = new JLabel(toughness+"");
        toughnessLabel.setFont(headingFont);
        powerLabel.setBackground(Color.PINK);
        toughnessLabel.setBackground(Color.ORANGE);
        
        
        JPanel fillPanel = new JPanel();
        fillPanel.setSize(bottomPanel.getWidth()-powerLabel.getWidth()-toughnessLabel.getWidth(), bottomPanel.getHeight());
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(powerLabel,BorderLayout.WEST);
        bottomPanel.add(fillPanel,BorderLayout.CENTER);
        bottomPanel.add(toughnessLabel, BorderLayout.EAST);
        
        
        
        
        /**
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        bottomPanel.add(powerLabel);
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(toughnessLabel);
        **/
        
        /**
        GridBagConstraints gbConstraints = new GridBagConstraints();
        bottomPanel.setLayout(new GridBagLayout());
        
        //gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.ipadx = 0;
        gbConstraints.ipady = 0;
        int insetWidth = bottomPanel.getWidth()-powerLabel.getWidth()-toughnessLabel.getWidth();
        //gbConstraints.insets = new Insets(0,0,insetWidth,4);
        gbConstraints.ipady = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.anchor = GridBagConstraints.LINE_END;
        bottomPanel.add(powerLabel, gbConstraints);

        //gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.ipadx = 0;
        gbConstraints.ipady = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;
        gbConstraints.anchor = GridBagConstraints.LINE_START;
        bottomPanel.add(toughnessLabel, gbConstraints);
        **/
        
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
        if(this.toughness-damage<0)
            this.toughness = 0;
        else
            this.toughness = toughness-damage;
        
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
