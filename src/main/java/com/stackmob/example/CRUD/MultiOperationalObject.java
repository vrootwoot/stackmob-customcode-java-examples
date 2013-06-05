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
    String[] create_table_contents;
    String[] create_table_columns;
    String[] create_tables;
    
    Boolean do_not_save = false;
    
    //JSONArray update_list = new JSONArray();
    //JSONArray update_table_contents = new JSONArray();
    //JSONArray update_tables = new JSONArray();
    //JSONArray update_table_columns = new JSONArray();
    //JSONObject updtae_list_inner;
    String update_primary_key;
    
    //JSONArray delete_list = new JSONArray();
    //JSONArray delete_row = new JSONArray();
    //JSONArray table_columns = new JSONArray();
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
    String[] create_list = gson.fromJson(test[0], String[].class);
    String[] update_list = gson.fromJson(test[1], String[].class);
    String[] delete_list = gson.fromJson(test[2], String[].class);
    
    
    logger.debug("grabbed json arrays, proceeding to operations");

    DataService ds = serviceProvider.getDataService();
    


    for (int i=0; i <= create_list.length; i++)
    {
            
           create_tables = gson.fromJson(create_list[i], String[].class);
                       
            // loop through each entry which needs creating
            for (int k=0; k <= create_tables.length; k++)
            {
                // empty feedback map as new table entry is being creared
                creation.clear();
                do_not_save=false;
                // loop through each column within array == table column
                    
                    create_table_columns = gson.fromJson(create_tables[k], String[].class);         
                    
                    for (int l=0; l <= create_table_columns.length; l++)
                    {
                            
                        create_table_contents = gson.fromJson(create_table_columns[l], String[].class);         
                        table_column_data_type = create_table_contents[0];
                        table_column_name = create_table_contents[1];
                        
                        
                        if (table_column_data_type.equals("map")) {
                            /*
                            try {
                               feedback.put(table_column_name, new SMMap(String.valueOf(create_table_contents.get(2))));
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }  
                            */
                            
                        } else if (table_column_data_type.equals("string")) {
                            
                                feedback.put(table_column_name, new SMString(create_table_contents[2]));    
                                creation.put(table_column_name, new SMString(create_table_contents[2]));    
                            
                        }  else if (table_column_data_type.equals("boolean")) {
                            
                                feedback.put(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents[2])));    
                                creation.put(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents[2])));    
                            
                        }
                        else if (table_column_data_type.equals("integer")) {
                            
                                feedback.put(table_column_name, new SMInt(Long.parseLong(create_table_contents[2])));    
                                creation.put(table_column_name, new SMInt(Long.parseLong(create_table_contents[2])));    
                            
                            
                        }   
                        /*
                        else if (table_column_data_type.equals("list")) {
                            try {
                                feedback.put(table_column_name, new SMList(convertJsonToList(create_table_contents.get(2))));    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }
                        } 
                        */ 
                        
                       else if (table_column_data_type.equals("long")) {
                            
                                feedback.put(table_column_name, new SMLong(Long.parseLong(create_table_contents[2])));
                                creation.put(table_column_name, new SMLong(Long.parseLong(create_table_contents[2])));
                            
                                                        
                        } else if (table_column_data_type.equals("double")) {
                            
                                feedback.put(table_column_name, new SMDouble(Double.parseDouble(create_table_contents[2])));
                                creation.put(table_column_name, new SMDouble(Double.parseDouble(create_table_contents[2])));
                                                                
                        } else {
                            feedback.put("invalid data type", new SMString(table_column_name) );
                            do_not_save = true;
                            break;
                        }
                     }
                    
                        try {
                          // Attempt to create object
                            if (!do_not_save && create_table_contents[3]!=null) {
                                result = ds.createObject(create_table_contents[3], new SMObject(creation));
                                feedback.put("created object",result);
                            }
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