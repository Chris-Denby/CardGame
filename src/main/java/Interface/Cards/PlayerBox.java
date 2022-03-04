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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author chris
 */
public class PlayerBox extends JPanel
{
    private int playerHealth = 0;
    private int width;
    private int height;
    private JLabel playerNameLabel;
    private JLabel playerHealthLabel;
    private boolean isOpponent;
    
    private int arcSize;
    private int strokeSize = 1;
    private Color shadowColor = Color.DARK_GRAY;
    private int shadowGap = 4;
    private int shadowOffset = 4;
    private int shadowAlpha = 150; //transparency from (0-255)
    private Color backgroundColor = Color.WHITE;
    private boolean isSelected = false;
    
    public PlayerBox(int containerHeight, boolean isOpponent)
    {
        this.isOpponent = isOpponent;
        this.height = (int) Math.round(containerHeight*0.5);
        this.width = this.height;
        this.setMinimumSize(new Dimension(width,height));
        this.setPreferredSize(new Dimension(width,height));
        this.setSize(new Dimension(width,height));
        arcSize = (int) Math.round(height/10);
        playerNameLabel = new JLabel();
        playerHealthLabel = new JLabel();
        this.add(playerHealthLabel);
        setPlayerHealth(Constants.defaultPlayerHealth);
    }
    
    public boolean getIsOpponent()
    {
        return isOpponent;
    }
    
    public void setPlayerHealth(int health)
    {
        this.playerHealth = health;
        this.playerHealthLabel.setText(playerHealth+"");
        repaint();
        revalidate();
    }
    
    public void setIsSelected(boolean selected)
    {
        this.isSelected = selected;
        this.repaint();
        this.revalidate();
    }
    
    public boolean getIsSelected()       
    {
        return isSelected;
    }
    
    public void takeDamage(int damage)
    {
        setPlayerHealth(playerHealth-damage);
    }
    
    public int getPlayerHealth()
    {
        return playerHealth;
    }
    
    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        
        //draw shap of JPanel
        
        //draw shadow
        this.setForeground(Color.black);
        Color strokeColor = getForeground();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(shadowColor);
        graphics.fillRoundRect(shadowOffset,shadowOffset,width-strokeSize-shadowOffset,height-strokeSize-shadowOffset,arcSize,arcSize);     
                
        //draw fill
        if(isSelected)
        {   
            strokeColor = Color.RED;
            strokeSize = 5;
        }
        else
            strokeSize = 1;
        
        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        graphics.setColor(strokeColor);
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.drawRoundRect(0,0,width-shadowGap,height-shadowGap,arcSize,arcSize);
        graphics.setStroke(new BasicStroke());    
    }
    
    
    
}
