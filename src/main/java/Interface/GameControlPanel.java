/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public class GameControlPanel extends JPanel 
{
    private int height;
    private int width;
    private GameWindow gameWindow;
    private JButton passTurnButton;
    private JButton resolveButton;
    private JLabel turnLabel;
    private JLabel turnNumberLabel;
    private boolean isPlayerTurn = false;
    
    
    public GameControlPanel(int containerWidth, int containerHeight, GameWindow window)
    {
        gameWindow = window;
        height = containerHeight;
        width = Math.round(containerWidth/4);
        
        this.setPreferredSize(new Dimension(width,height));
        this.setOpaque(true);
        this.setBackground(Color.LIGHT_GRAY);
        
        passTurnButton = new JButton("Pass Turn");
        resolveButton = new JButton("Resolve Event");
        passTurnButton.setEnabled(false);
        resolveButton.setEnabled(false);
        turnLabel = new JLabel();
        turnNumberLabel = new JLabel();
        this.add(passTurnButton);
        this.add(resolveButton); 
        this.add(turnLabel);
        this.add(turnNumberLabel);
        
        resolveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                gameWindow.executeCardEvent();
            }
        });
        
        passTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                gameWindow.passTurn();
            }
        });
    }
    
    public void setTurnLabelText(String text)
    {
       turnLabel.setText(text);
    }  
    
    public void setIsPlayerTurn(boolean is)
    {
        this.isPlayerTurn = is;
        if(isPlayerTurn)
        {
            turnLabel.setText("YOUR\nTURN");
        }
        else
        {
            turnLabel.setText("OPPONENTS\nTURN");           
        }
        
        passTurnButton.setEnabled(isPlayerTurn);
        resolveButton.setEnabled(isPlayerTurn);
        turnNumberLabel.setText(gameWindow.getTurnNumber()+"");
    }
}
