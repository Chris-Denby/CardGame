/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

/**
 *
 * @author chris
 */
public class Constants 
{
    static int maxHandSize = 7;
    static int maxResourceAmount = 7;
    static int defaultPlayerHealth = 20;
    static int turnTimeLimit = 10;
    static int discardTimeLimit = 10;
    public static String JSONpath = "C:\\Users\\chris\\AppData\\Local\\CardGame\\cards.json";
    public static String imagePath = "C:\\Users\\chris\\AppData\\Local\\CardGame\\";
    
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
        DRAW_CARD  
    }
    
    
    
}
