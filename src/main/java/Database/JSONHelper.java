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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    String filePath = "C:\\Users\\chris\\AppData\\Local\\CardGame\\";
    String fileName = "cards.json";
    
    
    public JSONHelper()
    {
        
    }

    public JSONObject getJSONCardList (List<Card> cardList)
    {
        //to pack multiple child json items
        //put child jasons in parent json
        //if I want to append, add more json objects to existing key

        //temp holder for iterating
        JSONObject cardJSON;
        //array is the 3rd level child of 2nd level child 'cardsJSON'
        JSONArray allCardsJSONArray = new JSONArray();
        //2nd level child
        JSONObject cardsJSON = new JSONObject();
        
        for(Card c:cardList)
        {
        //create json object
            cardJSON = new JSONObject();
            //add key/value pairs for the card
            if(c instanceof CreatureCard)
            {
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("power",((CreatureCard) c).getPower());
                cardJSON.put("toughness",((CreatureCard) c).getToughness());
            }
            if(c instanceof SpellCard)
            {
                //cardJSON.put("effect", ((SpellCard) c).getEffect());
            }
            cardJSON.put("id",c.getCardID());
            cardJSON.put("name",c.getName());
            cardJSON.put("cost",c.getPlayCost());

           //add card to parent json record
           allCardsJSONArray.add(cardJSON);
        }
        cardsJSON.put("cards", allCardsJSONArray);
        return cardsJSON;
    }
    
    public void writeJSONFile(JSONObject jObject)
    {
        try{
            file = new FileWriter(filePath + fileName);
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
        } catch (IOException ex) 
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
                sCard.setEffect((String)o.get("effect"));
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
    
    public void createCardLists()
    {
        JSONObject cardsJSON = new JSONObject();
        JSONArray player1CardsJSONArray = new JSONArray();
        JSONArray player2CardsJSONArray = new JSONArray();
        
        //player 1 card list
        for(int x=0;x<60;x++)
        {
            JSONObject cardJSON = null;
            
            if(x<50)
            {
                CreatureCard c = new CreatureCard("",1);
                c.setName("Creature");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPower(ThreadLocalRandom.current().nextInt(1,8));
                c.setToughness(ThreadLocalRandom.current().nextInt(1,8));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8));            

                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("power",((CreatureCard) c).getPower());
                cardJSON.put("toughness",((CreatureCard) c).getToughness());
                cardJSON.put("imageID",c.getImageID());
            }
            if(x>=50)
            {
                SpellCard c = new SpellCard("",1);
                c.setName("Spell");
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
            
            if(x<50)
            {
                CreatureCard c = new CreatureCard("",1);
                c.setName("Creature");
                c.setImageID(ThreadLocalRandom.current().nextInt(1,7));
                c.setCardID(System.identityHashCode(c));
                c.setPower(ThreadLocalRandom.current().nextInt(1,8));
                c.setToughness(ThreadLocalRandom.current().nextInt(1,8));
                c.setPlayCost(ThreadLocalRandom.current().nextInt(1,8));            

                //create json object
                cardJSON = new JSONObject();

                cardJSON.put("id",c.getCardID());
                cardJSON.put("name",c.getName());
                cardJSON.put("cost",c.getPlayCost());
                cardJSON.put("type",c.getClass().toString());
                cardJSON.put("power",((CreatureCard) c).getPower());
                cardJSON.put("toughness",((CreatureCard) c).getToughness());
                cardJSON.put("imageID",c.getImageID());
            }
            if(x>=50)
            {
                SpellCard c = new SpellCard("",1);
                c.setName("Spell");
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

}
