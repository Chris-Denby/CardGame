/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Cards;

/**
 *
 * @author chris
 */
public class CreatureCard extends Card 
{
    private int power;
    private int toughness;
    
    public CreatureCard(String cardName) 
    {
        super(cardName);
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
    
    
    
    
    
    
    
}
