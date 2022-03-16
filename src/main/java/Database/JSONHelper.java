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
import Interface.Constants.DeathEffect;
import Interface.Constants.ETBeffect;
import Interface.Constants.SpellEffect;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author chris
 */
public class JSONHelper 
{
    
    FileWriter file;
    String filePath = "";
    String fileName = "cards.json";
    
    
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
    
    public List<Card> readJSONFile(String recordName)
    {
        JSONParser jsonParser = new JSONParser();
        JSONArray array = null;
        List<Card> cardsList = new ArrayList<Card>();
        
        
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(filePath+fileName));
            array = (JSONArray) jsonObject.get(recordName);
            
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) 
        {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        Iterator iter = array.iterator();
        while(iter.hasNext())
        {
            
            JSONObject o = (JSONObject) iter.next(); 

            if(o.get("type").equals("class Interface.Cards.CreatureCard"))
            {
                CreatureCard cCard = new CreatureCard("",1);
                cCard.setImageID(Integer.parseInt(Long.toString((Long)o.get("imageID"))));
                cCard.setName((String)o.get("name"));
                cCard.setCardID(Integer.parseInt(Long.toString((Long)o.get("id"))));
                cCard.setPlayCost(Integer.parseInt(Long.toString((Long)o.get("cost")))); 
                cCard.setPower(Integer.parseInt(Long.toString((Long)o.get("power")))); 
                cCard.setToughness(Integer.parseInt(Long.toString((Long)o.get("toughness")))); 
                cCard.setETBeffect(ETBeffect.valueOf((String)o.get("etbEffect")));
                cCard.setDeathEffect(DeathEffect.valueOf((String)o.get("deathEffect")));
                cardsList.add(cCard);
            }
            else
            if(o.get("type").equals("class Interface.Cards.SpellCard"))
            {
                SpellCard sCard = new SpellCard("",1);
                sCard.setImageID(Integer.parseInt(Long.toString((Long)o.get("imageID"))));
                sCard.setName((String)o.get("name"));
                sCard.setCardID(Integer.parseInt(Long.toString((Long)o.get("id"))));
                sCard.setPlayCost(Integer.parseInt(Long.toString((Long)o.get("cost")))); 
                sCard.setEffect(Constants.SpellEffect.valueOf((String)o.get("effect")));
                cardsList.add(sCard);
            }  
            else
            {
                Card card = new Card("",1);
                card.setImageID(Integer.parseInt(Long.toString((Long)o.get("imageID"))));
                card.setName((String)o.get("name"));
                card.setCardID(Integer.parseInt(Long.toString((Long)o.get("id"))));
                card.setPlayCost(Integer.parseInt(Long.toString((Long)o.get("cost"))));    
                cardsList.add(card);
            }            
        }
        return cardsList;
    }
    
    public List<Card> createCardLists()
    {
        List<Card> cardList = new ArrayList<Card>();
        List<ETBeffect> ETBeffectsList = Arrays.asList(ETBeffect.values());
        List<DeathEffect> deathEffectsList = Arrays.asList(DeathEffect.values());
        List<SpellEffect> spellEffectsList = Arrays.asList(SpellEffect.values());
        
        for(int x=0;x<Constants.DECK_SIZE;x++)
        {
            Card card = null;
            if(x<=40)
            {
                card = new CreatureCard("",1);
                CreatureCard c = (CreatureCard) card;
                c.setName("Minion");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPower(ThreadLocalRandom.current().nextInt(1,8));
                c.setToughness(ThreadLocalRandom.current().nextInt(1,8));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8)); 
                c.setETBeffect(ETBeffectsList.get(ThreadLocalRandom.current().nextInt(0,ETBeffectsList.size())));
                c.setDeathEffect(deathEffectsList.get(ThreadLocalRandom.current().nextInt(0,deathEffectsList.size())));
            }
            else
            if(x>40 && x<=60)
            {
                card = new SpellCard("",1);
                SpellCard c = (SpellCard) card;
                c.setName("Spell");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8));
                c.setEffect(spellEffectsList.get(ThreadLocalRandom.current().nextInt(0,spellEffectsList.size())));              
            }
           cardList.add(card);
        }
        return cardList;
    }
    
    public void createCardListsOLD()
    {
        JSONObject cardsJSON = new JSONObject();
        JSONArray player1CardsJSONArray = new JSONArray();
        JSONArray player2CardsJSONArray = new JSONArray();
        
        List<ETBeffect> ETBeffectsList = Arrays.asList(ETBeffect.values());
        List<DeathEffect> deathEffectsList = Arrays.asList(DeathEffect.values());
        
        //player 1 card list
        for(int x=0;x<Constants.DECK_SIZE;x++)
        {
            JSONObject cardJSON = null;
            
            if(x<40)
            {
                CreatureCard c = new CreatureCard("",1);
                c.setName("Creature");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPower(ThreadLocalRandom.current().nextInt(1,8));
                c.setToughness(ThreadLocalRandom.current().nextInt(1,8));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8)); 
                c.setETBeffect(ETBeffectsList.get(ThreadLocalRandom.current().nextInt(0,ETBeffectsList.size())));
                c.setDeathEffect(deathEffectsList.get(ThreadLocalRandom.current().nextInt(0,deathEffectsList.size())));

                
                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("power",((CreatureCard) c).getPower());
                cardJSON.put("toughness",((CreatureCard) c).getToughness());
                cardJSON.put("imageID",c.getImageID());
                cardJSON.put("etbEffect",c.getETBeffect().toString());
                cardJSON.put("deathEffect",c.getDeathEffect().toString());
            }
            else
            if(x>=40&& x<50)
            {
                SpellCard c = new SpellCard("",1);
                c.setName("Burn Spell");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8));
                c.setEffect(Constants.SpellEffect.DEAL_DAMAGE);

                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("imageID",c.getImageID()); 
                cardJSON.put("effect",c.getEffect().toString());               
            }
            else
            if(x>=50)
            {
                SpellCard c = new SpellCard("",1);
                c.setName("Draw Cards");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8));
                c.setEffect(Constants.SpellEffect.DRAW_CARD);

                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("imageID",c.getImageID()); 
                cardJSON.put("effect",c.getEffect().toString());
            }

           player1CardsJSONArray.add(cardJSON);
        }
        
        //player 2 card list
        for(int x=0;x<60;x++)
        {
            JSONObject cardJSON = null;
            
            if(x<40)
            {
                CreatureCard c = new CreatureCard("",1);
                c.setName("Creature");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPower(ThreadLocalRandom.current().nextInt(1,8));
                c.setToughness(ThreadLocalRandom.current().nextInt(1,8));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8)); 
                c.setETBeffect(ETBeffectsList.get(ThreadLocalRandom.current().nextInt(0,ETBeffectsList.size())));
                c.setDeathEffect(deathEffectsList.get(ThreadLocalRandom.current().nextInt(0,deathEffectsList.size())));
                
                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("power",((CreatureCard) c).getPower());
                cardJSON.put("toughness",((CreatureCard) c).getToughness());
                cardJSON.put("imageID",c.getImageID());
                cardJSON.put("etbEffect",c.getETBeffect().toString());
                cardJSON.put("deathEffect",c.getDeathEffect().toString());
            }
            else
            if(x>=40&& x<50)
            {
                SpellCard c = new SpellCard("",1);
                c.setName("Burn Spell");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8));
                c.setEffect(Constants.SpellEffect.DEAL_DAMAGE);

                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("imageID",c.getImageID()); 
                cardJSON.put("effect",c.getEffect().toString());               
            }
            else
            if(x>=50)
            {
                SpellCard c = new SpellCard("",1);
                c.setName("Draw Cards");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8));
                c.setEffect(Constants.SpellEffect.DRAW_CARD);

                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("imageID",c.getImageID()); 
                cardJSON.put("effect",c.getEffect().toString());
            }
            
           player2CardsJSONArray.add(cardJSON);
        }
        
        cardsJSON.put("player1Cards", player1CardsJSONArray);
        cardsJSON.put("player2Cards", player2CardsJSONArray);

        this.writeJSONFile(cardsJSON);
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
            cardJSON.put("etbEffect", c.getETBeffect().toString());
            cardJSON.put("deathEffect", c.getDeathEffect().toString());
        }
        if(c instanceof SpellCard)
        {
            cardJSON.put("effect", ((SpellCard) c).getEffect());
        }
        cardJSON.put("id",(int) c.getCardID());
        cardJSON.put("name",c.getName());
        cardJSON.put("cost",c.getPlayCost());
        cardJSON.put("type",c.getClass().toString());
        

        
        
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
            cCard.setETBeffect(ETBeffect.valueOf((String)o.get("etbEffect")));
            cCard.setDeathEffect(DeathEffect.valueOf((String)o.get("deathEffect")));
        }
        else
        if(o.get("type").equals("class Interface.Cards.SpellCard"))
        {
            card = new SpellCard("",1);
            SpellCard sCard = (SpellCard) card;
            sCard.setEffect(SpellEffect.valueOf(o.get("effect").toString()));
        } 
        
        //card.setImageID((int) o.get("imageID"));
        card.setName((String)o.get("name"));
        card.setCardID((int) o.get("id"));
        card.setPlayCost((int) o.get("cost")); 

        return card;
    }
    

}
