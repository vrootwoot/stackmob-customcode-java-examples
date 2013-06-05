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

import java.util.ArrayList;

/**
 * This example will show a user how to write a custom code method
 * with two parameters that updates the specified object in their schema
 * when given a unique ID and a `year` field on which to update.
 */



public class GlobalOperations implements CustomCodeMethod {
  
    static class TableFields {
        public String data_type;
        public String column_name;
        public String data_value;
        public String table_name;
    }
    
    static class TableEntry {
        
       public TableFields[] table_fields;
       
       public TableEntry(TableFields[] table_fields) {
           this.table_fields = table_fields;
       }
       
        public TableFields[] getFields() {
            return this.table_fields;
        }  
        
        
    }
    
    static class TableOperation {
        public TableEntry[] table_entry;
        
        public TableOperation(TableEntry[] table_entry) {
            this.table_entry = table_entry;
        }
        
        public TableEntry[] getEntries() {
            return this.table_entry;
        }        
    }
    
   static class TableOperations {
       
        public TableOperation[] operation;

        public TableOperations(TableOperation[] operation) {
          this.operation = operation;
        }
        
        public TableOperation getOperation(Integer op) {
            return this.operation[op];
        }
        
    }   
    

  @Override
  public String getMethodName() {
    return "crud_global_operations";
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
      logger = serviceProvider.getLoggerService(GlobalOperations.class);
      logger.debug(request.getParams().get("object_operations"));
    

  
    
    Gson gson = new Gson();  
    JsonParser parser = new JsonParser();
    logger.debug(parser);
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);

  }

}