package src.main.java.project;

import org.json.JSONObject;
import org.json.JSONML;
import org.json.JSONArray;

public class JSONTraverser{

    /*

     for(int i = 0; i<jsonArray.length(); i++){
            test = (JSONObject)(jsonArray).get(i);
            if ( ((String)test.get("type")).equals("wikidata") ){
                //wikiCode = (String)json.get("url") ;
                json = test;
            }
        }
        searchJSONArray
        param - JSONarray to be searched, String key to look for, String value to find
        return - JSONOBject found or null
     */
    public JSONObject searchJSONArray(JSONArray array, String key, String value){
        return null;
    }
    /*
    * getNestedValue
    * param - JSOBject to find nested value of, String[] of values to look for
    * return - String res
    * */

    public String getNestedValue(JSONObject json, String[] string){
        JSONObject temp = json;
        for (String name : string) {
            temp = temp.getJSONObject(name);
        }
        
        return temp.toString();
    }
}