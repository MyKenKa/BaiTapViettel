
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ken Ka
 */
import java.util.HashMap;
import java.util.Map;

public class Record {
    private Map<String, String> values;
    public static String s;
    public Record(String id) {
        this.values = new HashMap<String, String>();
    }
    public Record(Map<String, String> v)
    {
        values = v;
    }
    public Map<String, String> getValues() {
        return values;
    }
    public void setValues(Map<String, String> values) {
        this.values = values;
    }
    public void put(String key, String value) {
        values.put(key, value);
        
    }
    public String get(String key) {
        return values.get(key);
    }
   // redundant
    public String getKey(String key) {
        return values.get(key);
    }
    
    public String toString(){
        String r = "";
        for (Map.Entry entry : values.entrySet())
        {
            //r += "keys: " + entry.getKey() + "; value: " +entry.getValue() + " \r\n " ;
            r +=   entry.getKey() + ", " +entry.getValue() + " \r\n " ;
        }
        return r;
    }   
}

