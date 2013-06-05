package com.stackmob.example.crud;


import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.*;

import com.google.gson.*;

//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONValue;

//import org.json.parser.*;
//import org.json.*;
 

import java.net.HttpURLConnection;
import java.util.*;
import java.util.List;
import java.util.Map;

/**
 * This example will show a user how to write a custom code method
 * with two parameters that updates the specified object in their schema
 * when given a unique ID and a `year` field on which to update.
 */

public class MultiOperationalObject implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "crud_multi_object";
  }
  
  @Override
  public List<String> getParams() {
    // Please note that the strings `user` and `username` are unsuitable for parameter names
    return Arrays.asList("object_operations");
  }
  /*
  private List<String> convertJsonToList(JSONObject j) {
        JSONArray temp = j.getJSONArray(0);
        ArrayList<String> list = new ArrayList<String>();     
        if (temp != null) { 
           int len = temp.length();
           for (int i=0;i<len;i++){ 
            list.add(temp.get(i).toString());
           } 
        }
        return list;
  }
*/
  
  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {

    //JsonArray create_list = new JSONArray();;
    //JsonArray create_table_contents = new JSONArray();
    //JsonArray create_tables = new JSONArray();
    //JsonArray create_table_columns = new JSONArray();
    //JSONObject create_list_inner;
    String table_column_name;
    String table_column_data_type;    
    Boolean do_not_save = false;
    
    JSONArray update_list = new JSONArray();
    JSONArray update_table_contents = new JSONArray();
    JSONArray update_tables = new JSONArray();
    JSONArray update_table_columns = new JSONArray();
    //JSONObject updtae_list_inner;
    String update_primary_key;
    
    JSONArray delete_list = new JSONArray();
    JSONArray delete_row = new JSONArray();
    JSONArray table_columns = new JSONArray();
    Map<String, SMValue> feedback = new HashMap<String, SMValue>();
    Map<String, SMValue> creation = new HashMap<String, SMValue>();
    Map<String, String> errMap = new HashMap<String, String>();
    List<SMUpdate> update = new ArrayList<SMUpdate>();
    SMObject result;
    
    LoggerService logger = serviceProvider.getLoggerService(MultiOperationalObject.class);
    logger.debug(request.getParams().get("object_operations"));
    

    /* The following try/catch block shows how to properly fetch parameters for PUT/POST operations
     * from the JSON request body
     */
    
    Gson gson = new Gson();  
    String[] test = gson.fromJson(request.getParams().get("object_operations"), String[].class);
    
    
      //Object obj = parser.parse(request.getParams().get("object_operations"));
      //JSONObject jsonObject = (JSONObject) obj;
      
      //org.json.JSONObject jsonObject = (org.json.JSONObject) parser.parse(request.getParams().get("object_operations"));  
        
      //org.json.JSONObject jsonObject = (org.json.JSONObject) JSONSerializer.toJSON(request.getParams().get("object_operations"));          
        
      
      /*
    Object obj = parser.parse(request.getParams().get("object_operations"));
    org.json.simple.JSONObject jsonjObject = (org.json.simple.JSONObject) obj;
    org.json.simple.JSONArray array_mate=(org.json.simple.JSONArray)jsonjObject;
    */
  
      
      // Fetch the values passed in by the user from the body of JSON
      /*
      logger.debug("trying to grab json arrays");
      create_list = (JSONArray)array_mate.get(0);
      delete_list = (JSONArray)array_mate.get(1);
      update_list = (JSONArray)array_mate.get(2);
        */
    
    
    
    
    logger.debug("grabbed json arrays, proceeding to operations");

    DataService ds = serviceProvider.getDataService();
    

    
    

    
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);

  }

}