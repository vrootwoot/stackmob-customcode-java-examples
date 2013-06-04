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

import org.json.JSONArray;
import org.json.JSONException;
//import org.json.JSONObject;
import org.json.simple.JSONObject;
import com.googlecode.json-simple;
 

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
      /*
        org.json.simple.JSONObject jsonObject = null;
       
        Object obj = parser.parse(request.getParams().get("object_operations"));
        jsonObject=(org.json.simple.JSONObject)obj;       
*/
        
          String s=request.getParams().get("object_operations");
          
          JSONArray obj = JSONArray.parse(s);
          
          //JSONArray arrayZZ =(JSONArray)obj;
          
          
      
      // Fetch the values passed in by the user from the body of JSON
      
      logger.debug("trying to grab json arrays");
      

      
      //create_list = jsonObject.getJSONArray("create");
      //delete_list = jsonObject.getJSONArray("delete");
      //update_list = jsonObject.getJSONArray("update");
        
      
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

    for (int i=0; i <= create_list.length(); i++)
    {
            try {
              create_tables = create_list.getJSONArray(i);
            } catch (JSONException e) {
              return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
            }            
            // loop through each entry which needs creating
            for (int k=0; k <= create_tables.length(); k++)
            {
                // empty feedback map as new table entry is being creared
                creation.clear();
                do_not_save=false;
                // loop through each column within array == table column
                    try {
                      create_table_columns = create_list.getJSONArray(i).getJSONArray(k);
                    } catch (JSONException e) {
                      return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
                    }
                    
                    for (int l=0; l <= create_table_columns.length(); l++)
                    {
                        try {
                            create_table_contents = create_table_columns.getJSONArray(l);
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
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }                            
                        } else if (table_column_data_type.equals("double")) {
                            try {
                                feedback.put(table_column_name, new SMDouble(Double.parseDouble(String.valueOf(create_table_contents.get(2)))));
                                creation.put(table_column_name, new SMDouble(Double.parseDouble(String.valueOf(create_table_contents.get(2)))));
                            }
                            catch (JSONException e) {
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
                        catch (JSONException json) {
                          return Util.internalErrorResponse("json_exception", json, errMap);  // http 500 - internal server error
                        }                            
            }
    }
    

    // Update
    // loop through each table which needs updating

    for (int i=0; i <= update_list.length(); i++)
    {
            try {
              update_tables = update_list.getJSONArray(i);
            } catch (JSONException e) {
              return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
            }            
            // loop through each entry which needs updating
            for (int k=1; k <= update_tables.length(); k++)
            {
                // empty feedback map as new table entry is being updated
                update.clear();
                // loop through each column within array == table column
                    try {
                      update_primary_key = update_list.getJSONArray(i).getJSONArray(0).toString();
                      update_table_columns = update_list.getJSONArray(i).getJSONArray(k);
                    } catch (JSONException e) {
                      return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
                    }
                    
                    for (int l=0; l <= create_table_columns.length(); l++)
                    {
                        try {
                            create_table_contents = create_table_columns.getJSONArray(l);
                            table_column_data_type = String.valueOf(update_table_contents.get(0));
                            table_column_name = String.valueOf(update_table_contents.get(1));
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
                                update.add(new SMSet(table_column_name, new SMString(String.valueOf(create_table_contents.get(2)) )));
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }
                        }  else if (table_column_data_type.equals("boolean")) {
                            try {
                                feedback.put(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents.get(2).toString())) );
                                update.add(new SMSet(table_column_name, new SMBoolean(Boolean.valueOf(create_table_contents.get(2).toString())) ));    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }
                        }
                        else if (table_column_data_type.equals("integer")) {
                            try {
                                feedback.put(table_column_name, new SMInt(Long.parseLong(String.valueOf(create_table_contents.get(2)))) );                                
                                update.add(new SMSet(table_column_name, new SMInt(Long.parseLong(String.valueOf(create_table_contents.get(2)))) ));                                    
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
                                feedback.put(table_column_name, new SMLong(Long.parseLong(String.valueOf(create_table_contents.get(2)))) );                                                                
                                update.add(new SMSet(table_column_name, new SMLong(Long.parseLong(String.valueOf(create_table_contents.get(2)))) ));                                                                    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }                            
                        } else if (table_column_data_type.equals("double")) {
                            try {
                                feedback.put(table_column_name, new SMDouble(Double.parseDouble(String.valueOf(create_table_contents.get(2)))) );
                                update.add(new SMSet(table_column_name, new SMDouble(Double.parseDouble(String.valueOf(create_table_contents.get(2)))) ));                                                                    
                            }
                            catch (JSONException e) {
                                return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error
                            }                                                        
                        }

                     }
                        try {
                          // Attempt to update object
                            result = ds.updateObject(String.valueOf(create_table_contents.get(3)), new SMString(update_primary_key), update);
                            feedback.put("updated object", result);
                        }
                        catch (InvalidSchemaException ise) {
                          return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
                        }
                        catch (DatastoreException dse) {
                          return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
                        }                        
                        catch (JSONException json) {
                          return Util.internalErrorResponse("json_exception", json, errMap);  // http 500 - internal server error
                        }                            
            }
    }
    

    // Delete
    // loop through each row which needs deleting

    for (int i=0; i <= delete_list.length(); i++)
    {
        try {
            delete_row = delete_list.getJSONArray(i);
            ds.deleteObject(delete_row.get(1).toString(), delete_row.get(0).toString()); // Finally the object gets deleted by ID.
        } catch (JSONException e) {
          return Util.internalErrorResponse("invalid_json", e, errMap);  // http 500 - internal server error                        
        }
        catch (InvalidSchemaException ise) {
          return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
        }        
        catch (DatastoreException dse) {
          return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
        }        
    }    
    
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);

  }

}
