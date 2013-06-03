package com.stackmob.example.crud;


import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.DatastoreException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 

import java.net.HttpURLConnection;
import java.util.*;

/**
 * This example will show a user how to write a custom code method
 * with two parameters that updates the specified object in their schema
 * when given a unique ID and a `year` field on which to update.
 */

public class MultiOperationalObject implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "Multi Operational";
  }
  
  @Override
  public List<String> getParams() {
    // Please note that the strings `user` and `username` are unsuitable for parameter names
    return Arrays.asList("model","make","year");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {

    JSONObject create_list;
    JSONObject update_list;
    JSONObject delete_list;
    Map<String, SMValue> feedback = new HashMap<String, SMValue>();
    Map<String, String> errMap = new HashMap<String, String>();
    
    LoggerService logger = serviceProvider.getLoggerService(MultiOperationalObject.class);
    logger.debug(request.getBody());
    

    /* The following try/catch block shows how to properly fetch parameters for PUT/POST operations
     * from the JSON request body
     */
    JSONParser parser = new JSONParser();
    try {
        
      Object obj = parser.parse(request.getBody());
      JSONObject jsonObject = (JSONObject) obj;
      // Fetch the values passed in by the user from the body of JSON
      
      //update_list = (JSONArray)jsonObject.get("update");
      //delete_list = (JSONArray)jsonObject.get("delete");
      
       create_list = jsonObject.getJSONArray("create");
        
    } catch (ParseException pe) {
      logger.error(pe.getMessage(), pe);
      return Util.badRequestResponse(errMap, pe.getMessage());
    } catch (JSONException pe) {
      logger.error(pe.getMessage(), pe);
      return Util.badRequestResponse(errMap, pe.getMessage());
    }
    
    /*
    if (Util.hasNulls(create_list) && Util.hasNulls(update_list) && Util.hasNulls(delete_list)){
      return Util.badRequestResponse(errMap);
    }
    * 
    */
    

    DataService ds = serviceProvider.getDataService();
    

    
        
    // Creation 
    // loop through each table which needs entries creating
    
    for (int i=0; i <= create_list.length; i++)
    {
        // loop through each entry which needs creating
        for (int k=0; k <= create_list[i].length; k++)
        {
            // loop through each column within array == table column
            for (int l=0; l <= create_list[i][k].length; l++)
            {
                if (create_list[i][k][l][0] instanceof String )
                {
                    if (create_list[i][k][l][0].equals("list")) {
                        //feedback.put(create_list[i][k][l][1], new SMList(List.parseList(create_list[i][k][l][2])));
                    } else if (create_list[i][k][l][0].equals("map")) {
                        //feedback.put(create_list[i][k][l][1], new SMMap(Map.parseMap(create_list[i][k][l][2])));
                    } else if (create_list[i][k][l][0].equals("string")) {
                        //feedback.put(create_list[i][k][l][1], new SMString(create_list[i][k][l][2]));    
                    } else if (create_list[i][k][l][0].equals("long")) {
                        //feedback.put(create_list[i][k][l][1], new SMInt(Long.parseLong(create_list[i][k][l][2])));
                    } else if (create_list[i][k][l][0].equals("double")) {
                        //feedback.put(create_list[i][k][l][1], new SMDouble(Double.parseLong(create_list[i][k][l][2])));
                    }
                }
            }
            try {
              // Attempt to create object
              ds.createObject(create_list[i][k][l][3], new SMObject(feedback));
            }
            catch (InvalidSchemaException ise) {
              return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
            }
            catch (DatastoreException dse) {
              return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
            }           
        }
    }
    
    
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);

  }

}