package src.main.java.project;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.HttpClientErrorException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONML;
import org.json.JSONArray;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import static src.main.java.project.Values.*;

@RestController
public class GreetingController {

    @Autowired
    RestTemplate restTemplate;
    JSONArray jsonArray;
    JSONObject json;
    JSONObject test;
    String wikiCode;
    int index;
    Values values;
    JSONTraverser traverser;
    String albumId;

    ResponseEntity<String> response;

    @RequestMapping(value = "/artist/{id}")
    public String getProductList(@PathVariable String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity <String> entity = new HttpEntity<String>(headers);
        traverser = new JSONTraverser();
        response =  restTemplate.exchange("https://musicbrainz.org/ws/2/artist/" + id + "?&inc=url-rels+release-groups&fmt=json", HttpMethod.GET, entity, String.class);

        json = new JSONObject(response.getBody());
        jsonArray = (JSONArray)json.get("relations");
        test = null;
        wikiCode = "";
        
        JSONObject album = new JSONObject(response.getBody());
        JSONArray albumArray = (JSONArray)album.get("release-groups");
        //System.out.println(albumArray.title);
       
        //for-loop that collects all ids for albums
        for(int i = 0; i<albumArray.length(); i++){
            test = (JSONObject)(albumArray).get(i);
            albumId=test.get("id").toString();
            try { 
            response = restTemplate.exchange("http://coverartarchive.org/release-group/" + albumId, HttpMethod.GET, entity, String.class);
            }
            catch(HttpClientErrorException e){
                response = null;
            }
            if (response != null){
            json = new JSONObject(response.getBody());
            JSONArray albumCovers = (JSONArray)(json.get("images"));
            for(int a = 0; a<albumCovers.length(); a++){
               String coverart = (albumCovers.getJSONObject(a)).getString("image"); 
                }     
            } 

            }

        for(int i = 0; i<jsonArray.length(); i++){
            test = (JSONObject)(jsonArray).get(i);
            if ( ((String)test.get("type")).equals("wikidata") ){
                //wikiCode = (String)json.get("url") ;
                json = test;
            }
        }
        wikiCode = ((json.getJSONObject("url")).get("resource")).toString();
        index = wikiCode.lastIndexOf('/');
        String code = wikiCode.substring(index+1,wikiCode.length());
// response from wikidata 
        response = restTemplate.exchange(Values.WIKIDATA_URL + code + "&format=json&props=sitelinks", HttpMethod.GET, entity, String.class);
        json = new JSONObject(response.getBody());
        wikiCode = (((((json.getJSONObject("entities")).getJSONObject(code)).getJSONObject("sitelinks")).getJSONObject("enwiki")).get("title")).toString();
        //wikiCode = traverser.getNestedValue(json, new String[]{"entities", code, "sitelinks", "enwiki","title"});
        response = restTemplate.exchange(Values.WIKI_URL + wikiCode, HttpMethod.GET, entity, String.class);
        
        JSONArray test = new JSONArray();
        test.put(albumArray);
        test.put(response.getBody());
        json = new JSONObject(response.getBody());
        return test.toString();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();


    }
}