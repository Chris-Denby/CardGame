/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.awt.Color;

/**
 *
 * @author chris
 */
public class Constants 
{
    public static final int windowWidth = 800;
    public static final int windowHeight = 800;
    public static final int maxHandSize = 7;
    public static final int maxResourceAmount = 7;
    public static final int defaultPlayerHealth = 20;
    public static final int turnTimeLimit = 10;
    public static final int discardTimeLimit = 10;
    public static final int maxCaradsInPlayArea = 14;
    public static final int buffDistance = 2;
    public static final int buffModifier = 2;
    public static final int DECK_SIZE = 60;
    public static final double cardAspectRatio = 0.715;
    public static String imagePath = "C:\\Users\\chris\\AppData\\Local\\CardGame\\";
    public static final Color cardBaseColor = new Color(38,38,38,255);
    
    public static enum CardLocation
    {
        PLAYER_HAND,
        PLAYER_PLAY_AREA,
        PLAYER_DECK,
        PLAYER_DISCARD_PILE,
        OPPONENT_HAND,
        OPPONENT_PLAY_AREA,
        OPPONENT_DECK,
        OPPONENT_DISCARD_PILE
    }
    
    public static enum TurnPhase
    {
        UPKEEP_PHASE,
        MAIN_PHASE,
        COMBAT_PHASE,
        DECLARE_BLOCKERS,        
        END_PHASE
    }
    
    public static enum SpellEffect
    {
        DRAW_CARD,
        DEAL_DAMAGE,
        //HEAL_DAMAGE
    }
    
    public static enum ETBeffect
    {
        NONE,
        Taunt,
        Buff_Power;
    }
    
    public static enum DeathEffect
    {
        Gain_Life,
        NONE;
    }
    
    
    
}
