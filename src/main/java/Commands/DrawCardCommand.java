/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commands;

import Interface.Card;
import Interface.Deck;
import java.io.Serializable;

/**
 *
 * @author chris
 */
public class DrawCardCommand implements ICommand, Serializable 
{

    //private Card card;
    private Deck deck;
    
    public DrawCardCommand(Deck deck)
    {
        this.deck = deck;
    }
    
    public Deck getDeck()
    {
        return deck;
    }
    
    @Override
    public void execute() 
    {
        deck.drawCard();
    }
    
}
