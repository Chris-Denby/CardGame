/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Interface.Cards.Card;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
public class CardEvent implements Serializable
{
    private Card originCard = null;
    private Card targetCard = null;
    private boolean isResolved = false;
    private String type;
    
    public CardEvent(Card originCard)
    {
        this.originCard = originCard;      
    }
    
    public void setType(String t)
    {
        type = t;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void addTargetCard(Card card)
    {
        targetCard = card;
    }
    
    public Card getTargetCard()
    {
        return targetCard;
    }
    
    public Card getOriginCard()
    {
        return originCard;
    }
    
    public void execute()
    {
        isResolved = true;
    }
    
    
    
    
}
