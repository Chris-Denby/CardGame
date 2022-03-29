/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Interface.Cards.Card;
import Interface.Cards.CreatureCard;
import Interface.Cards.SpellCard;
import Interface.Constants;
import Interface.Constants.CreatureEffect;
import Interface.Constants.SpellEffect;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author chris
 */
public class JSONHelper 
{
    
    FileWriter file;
    String filePath = "";
    String fileName = "decklist.json";
    
    
    public JSONHelper()
    {
        
    }
        
    
    public void writeJSONFile(JSONObject jObject)
    {        
        File existingFile = new File(filePath+fileName);
        existingFile.delete();

        try{
            file = new FileWriter(filePath + fileName,false);
            file.write(jObject.toJSONString());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try{
                file.flush();
                file.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

    }
    
    
    
    public List<Card> readCardListJSON(JSONObject jsonObject)
    {
        JSONArray array = (JSONArray) jsonObject.get("playerCards");
        //System.out.println(array);
        
        List<Card> cardsList = new ArrayList<Card>();

        Iterator iter = array.iterator();
        while(iter.hasNext())
        {
            JSONObject o = (JSONObject) iter.next(); 
            //System.out.println(o.get("id").toString() + o.get("name").toString() + o.get("imageID").toString());
            
            
            if(o.get("type").equals("class Interface.Cards.CreatureCard"))
            {
                CreatureCard cCard = new CreatureCard("",1);
                cCard.setImageID(Integer.parseInt(o.get("imageID").toString()));
                cCard.setName((String)o.get("name"));
                cCard.setCardID(Integer.parseInt(o.get("id").toString()));
                cCard.setPlayCost(Integer.parseInt(o.get("cost").toString())); 
                cCard.setPower(Integer.parseInt(o.get("power").toString()));; 
                cCard.setToughness(Integer.parseInt(o.get("toughness").toString()));
                cCard.setCreatureEffect(CreatureEffect.valueOf(o.get("creatureEffect").toString()));
                        
                cardsList.add(cCard);
            }
            else
            if(o.get("type").equals("class Interface.Cards.SpellCard"))
            {
                SpellCard sCard = new SpellCard("",1);
                sCard.setImageID(Integer.parseInt(o.get("imageID").toString()));
                sCard.setName((String)o.get("name"));
                sCard.setCardID(Integer.parseInt(o.get("id").toString()));
                sCard.setPlayCost(Integer.parseInt(o.get("cost").toString())); 
                sCard.setEffect(SpellEffect.valueOf(o.get("effect").toString()));
                cardsList.add(sCard);
            }  
        }
        return cardsList;
    }
    
    public List<Card> createCardLists()
    {
        
        List<Card> cardList = new ArrayList<Card>();
        List<CreatureEffect> creatureEffectsList = Arrays.asList(CreatureEffect.values());
        List<SpellEffect> spellEffectsList = Arrays.asList(SpellEffect.values());
        int statLowerLimit = 1;
        int statUpperLimit = Constants.maxResourceAmount;
        
        for(int x=0;x<Constants.DECK_SIZE;x++)
        {
            Card card = null;
            if(x<=40)
            {
                card = new CreatureCard("",1);
                CreatureCard c = (CreatureCard) card;
                
                
                //SET MANA CURVE
                if(x<=20){
                    statLowerLimit = 1;
                    statUpperLimit = 3;
                }
                else{
                    statLowerLimit = 3;
                    statUpperLimit = Constants.maxResourceAmount;
                }
                    
                c.setPower(ThreadLocalRandom.current().nextInt(statLowerLimit,statUpperLimit));
                c.setToughness(ThreadLocalRandom.current().nextInt(statLowerLimit,statUpperLimit));
                
                
                //SET CARD EFFECTS (25% chance of getting an effect)
                int numOfEffects = creatureEffectsList.size()-1;
                int y = ThreadLocalRandom.current().nextInt(0,numOfEffects*3);
                if(y<=numOfEffects)
                    c.setCreatureEffect(creatureEffectsList.get(y));                        

                c.setName("Creature");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,46));
                c.setCardID(System.identityHashCode(c));                
                
                //play cost is calculated by
                /**
                 * = ((power + toughness)/2)-2
                 * minimum of 1
                 * +1 for each effect
                 */
                                
                int playCost = Math.round((c.getPower()+c.getToughness())/2);
                if(c.getCreatureEffect()!=CreatureEffect.NONE)
                    playCost++;
                if(playCost>Constants.maxResourceAmount)
                    playCost=Constants.maxResourceAmount;
                
                c.setPlayCost(playCost);
            }
            else
            if(x>40 && x<=60)
            {
                card = new SpellCard("",1);
                SpellCard sc = (SpellCard) card;
                sc.setName("Spell");
                sc.setCardID(System.identityHashCode(sc));
                sc.setPlayCost(ThreadLocalRandom.current().nextInt(1,Constants.maxResourceAmount));
                sc.setEffect(spellEffectsList.get(ThreadLocalRandom.current().nextInt(0,spellEffectsList.size())));  
                if(sc.getEffect()==SpellEffect.DRAW_CARD)                                        
                    sc.setImageID(999);
                if(sc.getEffect()==SpellEffect.DEAL_DAMAGE)
                    sc.setImageID(666);
            }
            cardList.add(card);
        }
        Collections.shuffle(cardList);
        return cardList;
    }
    
    public JSONObject getCardListJSON(List<Card> list)
    {
        JSONObject cardsJSON = new JSONObject();
        JSONArray playerCardsJSONArray = new JSONArray();

        for(Card c:list)
            playerCardsJSONArray.add(convertCardToJSON(c));
        
        cardsJSON.put("playerCards", playerCardsJSONArray);
        this.writeJSONFile(cardsJSON);
        return cardsJSON;
    }
    
    public JSONObject convertCardToJSON(Card c)
    {
        //convert card object to JSON object
        JSONObject cardJSON = new JSONObject();
        //add key/value pairs for the card
        if(c instanceof CreatureCard)
        {
            cardJSON.put("power",((CreatureCard) c).getPower());
            cardJSON.put("toughness",((CreatureCard) c).getToughness());
            cardJSON.put("creatureEffect", c.getCreatureEffect().toString());
        }
        if(c instanceof SpellCard)
        {
            cardJSON.put("effect", ((SpellCard) c).getEffect());
        }
        cardJSON.put("id",c.getCardID());
        cardJSON.put("name",c.getName());
        cardJSON.put("cost",c.getPlayCost());
        cardJSON.put("type",c.getClass().toString());
        cardJSON.put("imageID", c.getImageID());
        
        return cardJSON;
    }
    
    public Card convertJSONtoCard(JSONObject o)
    {
        Card card = null;

        if(o.get("type").equals("class Interface.Cards.CreatureCard"))
        {
            card = new CreatureCard("",1);
            CreatureCard cCard = (CreatureCard) card; 
            cCard.setPower((int) o.get("power")); 
            cCard.setToughness((int) o.get("toughness")); 
            cCard.setCreatureEffect(CreatureEffect.valueOf((String)o.get("creatureEffect")));
        }
        else
        if(o.get("type").equals("class Interface.Cards.SpellCard"))
        {
            card = new SpellCard("",1);
            SpellCard sCard = (SpellCard) card;
            sCard.setEffect(SpellEffect.valueOf(o.get("effect").toString()));
        } 
        
        card.setImageID((int) o.get("imageID"));
        card.setName((String)o.get("name"));
        card.setCardID((int) o.get("id"));
        card.setPlayCost((int) o.get("cost")); 

        return card;
    }
    

}
