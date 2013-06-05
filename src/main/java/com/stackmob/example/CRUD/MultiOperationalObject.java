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
    JsonArray create_table_contents = new JsonArray();
    JsonArray create_table_columns;
    JsonArray create_tables;
    
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
    /*
    
    List<String> test = gson.fromJson(request.getParams().get("object_operations"), String.class);
    List<String> create_list = gson.fromJson(test[0], String.class);
    List<String> update_list = gson.fromJson(test[1], String.class);
    List<String> delete_list = gson.fromJson(test[2], String.class);
    */
    
    Gson gson = new Gson();  
    JsonParser parser = new JsonParser();
    JsonArray array = parser.parse(request.getParams().get("object_operations")).getAsJsonArray();    
    
    logger.debug(array);
    
    JsonArray create_list = array.get(0).getAsJsonArray();
    //JsonArray create_list = array.get(0);
    
    
    
    logger.debug("grabbed json arrays, proceeding to operations");

    DataService ds = serviceProvider.getDataService();
    


    for (int i=0; i <= create_list.size(); i++)
    {
            
           create_tables = create_list.get(i).getAsJsonArray();
                       
            // loop through each entry which needs creating
            for (int k=0; k <= create_tables.size(); k++)
            {
                // empty feedback map as new table entry is being creared
                creation.clear();
                do_not_save=false;
                // loop through each column within array == table column
                    
                    create_table_columns = create_tables.get(k).getAsJsonArray();
                    
                    for (int l=0; l <= create_table_columns.size(); l++)
                    {
                            
                        create_table_contents = create_table_columns.get(1).getAsJsonArray();
                        table_column_data_type = create_table_contents.get(2).getAsJsonArray().toString();
                        table_column_name = create_table_contents.get(1).getAsJsonArray().toString();
                        
                        
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
                            
                                feedback.put(table_column_name, new SMString(create_table_contents.get(2).toString()));    
                                creation.put(table_column_name, new SMString(create_table_contents.get(2).toString()));    
                            
                        }  else if (table_column_data_type.equals("boolean")) {
                            
                           //     feedback.put(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents[2])));    
                          //      creation.put(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents[2])));    
                            
                        }
                        else if (table_column_data_type.equals("integer")) {
                            
                             //   feedback.put(table_column_name, new SMInt(Long.parseLong(create_table_contents[2])));    
                             //   creation.put(table_column_name, new SMInt(Long.parseLong(create_table_contents[2])));    
                            
                            
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
                            
                              //  feedback.put(table_column_name, new SMLong(Long.parseLong(create_table_contents[2])));
                             //   creation.put(table_column_name, new SMLong(Long.parseLong(create_table_contents[2])));
                            
                                                        
                        } else if (table_column_data_type.equals("double")) {
                            
                              //  feedback.put(table_column_name, new SMDouble(Double.parseDouble(create_table_contents[2])));
                              //  creation.put(table_column_name, new SMDouble(Double.parseDouble(create_table_contents[2])));
                                                                
                        } else {
                            feedback.put("invalid data type", new SMString(table_column_name) );
                            do_not_save = true;
                            break;
                        }
                     }
                    
                        try {
                          // Attempt to create object
                            if (!do_not_save && create_table_contents.get(3)!=null) {
                                result = ds.createObject(create_table_contents.get(3).toString(), new SMObject(creation));
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