/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

import Interface.Constants;
import Interface.Constants.CardLocation;
import Interface.Constants.ETBeffect;
import Interface.PlayArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
        playTakeDamageSound();
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
    
        public void playTakeDamageSound()
    {
        AudioInputStream audioInputStream = null;
        try {
            String soundName = "sounds/attackLand.wav";
            audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } 
        catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(PlayArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(PlayArea.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                audioInputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(PlayArea.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
}
