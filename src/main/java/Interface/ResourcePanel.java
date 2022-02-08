/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public class ResourcePanel extends JPanel
{
    GameWindow gameWindow;
    ArrayList<Resource> resources = new ArrayList<Resource>();
    //original amount of tokens - used for resetting after consumption
    int originalAmount;
    int width;
    int height;
    
    public ResourcePanel(int width, int height,GameWindow window)
    {
        gameWindow = window;
        originalAmount = gameWindow.getTurnNumber();
        this.setBackground(Color.WHITE);
        this.width = Math.round(width/16);
        this.height = height;
        //set layout to add items vertically from top to bottom
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setPreferredSize(new Dimension(width,height));   
    }
    
    public int getAmount()
    {
        return resources.size();
    }
    
    public void increaseAmount()
    {
        Resource r = new Resource();
        resources.add(r);
        this.add(r);
        originalAmount++;
    }
    
    public void decreaseAmount(int amount)
    {
        for(int x=amount;x>0;x--)
        {
            //remove the last component (token) added
            this.remove(resources.get(resources.size()-1));
            resources.remove(resources.size()-1);
            gameWindow.revalidate();
            gameWindow.repaint();
        }
    }
    
    public void resetResources()
    {
        //add resources back - increasing amount back to original
        for(int x=resources.size();x<=originalAmount;x++)
        {
            Resource r = new Resource();
            resources.add(r);
            this.add(r); 
        }
    }  
    
    //class to draw the resource token
    public class Resource extends JPanel
    {
        int tokenWidth;
        int tokenHeight;
        int drawOrigin;
        int spacing;
        //size of the token as a factor of hte width of the resource window
        int sizeFraction = 6;
        
        public Resource()
        {
            setAlignmentX(Component.CENTER_ALIGNMENT);
            //set token width to 80% of container
            tokenWidth = Math.round(width-((width/10)*sizeFraction));
            //determine start x.y pos of token from width
            spacing = Math.round((width-tokenWidth)/2);
            this.setPreferredSize(new Dimension(width,width));
        }
        
        @Override
        public Dimension getMaximumSize() {
        return getPreferredSize();
        }

        @Override
        public void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setStroke(new BasicStroke());
            setForeground(Color.BLACK);
            //setBackground(Color.BLACK);
            graphics.drawOval(spacing, spacing, tokenWidth,tokenWidth);
            graphics.fillOval(spacing, spacing, tokenWidth,tokenWidth);
            
            
            
        }  
    } 
}


