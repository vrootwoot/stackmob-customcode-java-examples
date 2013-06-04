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

    JSONArray create_list;
    JSONArray create_table_contents;
    JSONArray create_tables;
    JSONArray create_table_columns;
    JSONObject create_list_inner;
    JSONObject update_list;
    JSONObject delete_list;
    JSONArray table_columns;
    String table_column_name;
    String table_column_data_type;
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

    for (int i=0; i <= create_list.length(); i++)
    {
        if (create_list.getJSONArray(i) instanceof JSONArray)
        {
            try {
              create_tables = create_list.getJSONArray(i);
            } catch (JSONException e) {
              return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
            }            
            // loop through each entry which needs creating
            for (int k=0; k <= create_tables.length(); k++)
            {
                // loop through each column within array == table column
                if (create_list.getJSONArray(i).getJSONArray(k) instanceof JSONArray)
                {
                    try {
                      create_table_columns = create_list.getJSONArray(i).getJSONArray(k);
                    } catch (JSONException e) {
                      return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
                    }
                    
                    for (int l=0; l <= create_table_columns.length(); l++)
                    {
                        try {
                            create_table_contents = create_list.getJSONArray(i).getJSONArray(k).getJSONArray(l);
                            table_column_data_type = String.valueOf(create_table_contents.get(0));
                            table_column_name = String.valueOf(create_table_contents.get(1));
                        } catch (JSONException e) {
                          return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
                        }
                        
                        if (table_column_data_type.equals("list")) {
                         //   feedback.put(String.valueOf(create_table_contents.get(1)), new SMList(String.valueOf(create_table_contents.get(2))));
                        } else if (table_column_data_type.equals("map")) {
                         //   feedback.put(String.valueOf(create_table_contents.get(1)), new SMMap(String.valueOf(create_table_contents.get(2))));
                        } else if (table_column_data_type.equals("string")) {
                            try {
                                feedback.put(table_column_name, new SMString(String.valueOf(create_table_contents.get(2))));    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }
                        } else if (table_column_data_type.equals("long")) {
                        //    feedback.put(table_column_name, new SMLong(Long.parseLong(String.valueOf(create_table_contents.get(2)))));
                        } else if (table_column_data_type.equals("double")) {
                        //    feedback.put(table_column_name, new SMDouble(Double.parseDouble(String.valueOf(create_table_contents.get(2)))));
                        }

                     }
                    try {
                      // Attempt to create object
                      ds.createObject(String.valueOf(create_table_contents.get(3)), new SMObject(feedback));
                    }
                    catch (InvalidSchemaException ise) {
                      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
                    }
                    catch (DatastoreException dse) {
                      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
                    }
                }
            }
        }
    }
    

    
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);

  }

}