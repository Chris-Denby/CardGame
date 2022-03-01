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
public interface IAttackable {
    
    public void takeDamage(int damage);
    
    public void setIsSelected(boolean selected);
    
    public void setIsActivated(boolean activated);
    
}
