package com.stackmob.example.crud;


import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

//import org.json.parser.*;
//import org.json.*;
 

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

    JSONArray create_list = new JSONArray();;
    JSONArray create_table_contents = new JSONArray();
    JSONArray create_tables = new JSONArray();
    JSONArray create_table_columns = new JSONArray();
    JSONObject create_list_inner;
    String table_column_name;
    String table_column_data_type;    
    Boolean do_not_save = false;
    
    JSONArray update_list = new JSONArray();
    JSONArray update_table_contents = new JSONArray();
    JSONArray update_tables = new JSONArray();
    JSONArray update_table_columns = new JSONArray();
    JSONObject updtae_list_inner;
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
    JSONParser parser = new JSONParser();
    try {
        
      //Object obj = parser.parse(request.getParams().get("object_operations"));
      //JSONObject jsonObject = (JSONObject) obj;
      
      //org.json.JSONObject jsonObject = (org.json.JSONObject) parser.parse(request.getParams().get("object_operations"));  
        
      //org.json.JSONObject jsonObject = (org.json.JSONObject) JSONSerializer.toJSON(request.getParams().get("object_operations"));          
        
      
      
    Object obj=JSONValue.parse(request.getParams().get("object_operations"));
    JSONArray array_mate=(JSONArray)obj;

  
      
      // Fetch the values passed in by the user from the body of JSON
      
      logger.debug("trying to grab json arrays");
      create_list = (JSONArray)array_mate.get("create");
      delete_list = (JSONArray)array_mate.get("delete");
      update_list = (JSONArray)array_mate.get("update");
        
    } catch (ParseException pe) {
      logger.error(pe.getMessage(), pe);
      return Util.badRequestResponse(errMap, pe.getMessage());
    } catch (JSONException pe) {
      logger.error(pe.getMessage(), pe);
      return Util.badRequestResponse(errMap, pe.getMessage());
    }
    
    
    if ((create_list.length()==0) && (update_list.length()==0) && (delete_list.length()==0)){
      return Util.badRequestResponse(errMap);
    }
    
    logger.debug("grabbed json arrays, proceeding to operations");

    DataService ds = serviceProvider.getDataService();
    

    
        
    // Creation 
    // loop through each table which needs entries creating

    for (int i=0; i <= create_list.size(); i++)
    {
            try {
              create_tables = (JSONArray)create_list.get(i);
            } catch (JSONException e) {
              return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
            }            
            // loop through each entry which needs creating
            for (int k=0; k <= create_tables.size(); k++)
            {
                // empty feedback map as new table entry is being creared
                creation.clear();
                do_not_save=false;
                // loop through each column within array == table column
                    try {
                      create_table_columns = (JSONArray)create_list.get(i).get(k);
                    } catch (JSONException e) {
                      return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
                    }
                    
                    for (int l=0; l <= create_table_columns.size(); l++)
                    {
                        try {
                            create_table_contents = (JSONArray)create_table_columns.get(l);
                            table_column_data_type = String.valueOf(create_table_contents.get(0));
                            table_column_name = String.valueOf(create_table_contents.get(1));
                        } catch (JSONException e) {
                          return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
                        }
                        
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
                            try {
                                feedback.put(table_column_name, new SMString(String.valueOf(create_table_contents.get(2))));    
                                creation.put(table_column_name, new SMString(String.valueOf(create_table_contents.get(2))));    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }
                        }  else if (table_column_data_type.equals("boolean")) {
                            try {
                                feedback.put(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents.get(2).toString())));    
                                creation.put(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents.get(2).toString())));    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }
                        }
                        else if (table_column_data_type.equals("integer")) {
                            try {
                                feedback.put(table_column_name, new SMInt(Long.parseLong(String.valueOf(create_table_contents.get(2)))));    
                                creation.put(table_column_name, new SMInt(Long.parseLong(String.valueOf(create_table_contents.get(2)))));    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }
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
                            try {
                                feedback.put(table_column_name, new SMLong(Long.parseLong(String.valueOf(create_table_contents.get(2)))));
                                creation.put(table_column_name, new SMLong(Long.parseLong(String.valueOf(create_table_contents.get(2)))));
                            }
                            catch (ParseException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }                            
                        } else if (table_column_data_type.equals("double")) {
                            try {
                                feedback.put(table_column_name, new SMDouble(Double.parseDouble(String.valueOf(create_table_contents.get(2)))));
                                creation.put(table_column_name, new SMDouble(Double.parseDouble(String.valueOf(create_table_contents.get(2)))));
                            }
                            catch (ParseException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }                                                        
                        } else {
                            feedback.put("invalid data type", new SMString(table_column_name) );
                            do_not_save = true;
                            break;
                        }
                     }
                    
                        try {
                          // Attempt to create object
                            if (!do_not_save) {
                                result = ds.createObject(String.valueOf(create_table_contents.get(3)), new SMObject(creation));
                                feedback.put("created object",result);
                            }
                        }
                        catch (InvalidSchemaException ise) {
                          return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
                        }
                        catch (DatastoreException dse) {
                          return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
                        }                        
                        catch (ParseException json) {
                          return Util.internalErrorResponse("json_exception", json, errMap);  // http 500 - internal server error
                        }                            
            }
    }
    

    
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);

  }

}