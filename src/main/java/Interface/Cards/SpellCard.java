/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

import Interface.Constants.CardLocation;
import Interface.Constants.SpellEffect;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
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
public class SpellCard extends Card
{
    private SpellEffect effect;
    private JLabel effectLabel;
    
    public SpellCard(String cardName, int imageID) 
    {
        super(cardName, imageID);
        effectLabel = new JLabel();

    }

    public void setLocation(CardLocation l)
    {
        super.setCardLocation(l);
        revalidate();
    }
    
    public void setFaceUp(boolean is)
    {
        super.setFaceUp(is);
    }
    
    public void setEffect(SpellEffect effect)
    {
        this.effect=effect;
    }
    
    public void setEffect(String effect)
    {
        this.effect = SpellEffect.valueOf(effect);
    }
    
    public SpellEffect getEffect()
    {
        return effect;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);   
    }
    
    @Override
    public SpellCard getClone(Image img)
    {
        //this method creates a deep copy of the card and returns it
        SpellCard clone = new SpellCard(getName(), getImageID());
        clone.setPlayCost(getPlayCost());
        clone.setEffect(effect);
        clone.setImage(img);
        //set picture box
        return clone;
    }
    
    
    
    
    
    
    
    
    
    
}
