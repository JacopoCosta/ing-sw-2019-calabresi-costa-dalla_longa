package it.polimi.ingsw;

import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import org.json.simple.JSONArray;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        DecoratedJSONObject o = DecoratedJSONObject.getFromFile("src\\main\\json\\weapons.json");
        String s = o.getArray("weapons").get(0).getString("name");
        System.out.println(s);
    }
}
