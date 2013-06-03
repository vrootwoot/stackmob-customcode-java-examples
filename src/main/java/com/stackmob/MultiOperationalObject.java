/**
 * Copyright 2012-2013 StackMob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackmob.example.crud;

import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.DatastoreException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    String carID = "";
    String year  = "";

    LoggerService logger = serviceProvider.getLoggerService(MultiOperationalObject.class);
    logger.debug(request.getBody());
    Map<String, String> errMap = new HashMap<String, String>();

    /* The following try/catch block shows how to properly fetch parameters for PUT/POST operations
     * from the JSON request body
     */
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(request.getBody());
      JSONObject jsonObject = (JSONObject) obj;

      // Fetch the values passed in by the user from the body of JSON
      create_list = (JSONArray)jsonObject.get("create");
      update_list = (JSONArray)jsonObject.get("update");
      delete_list = (JSONArray)jsonObject.get("delete");

    } catch (ParseException pe) {
      logger.error(pe.getMessage(), pe);
      return Util.badRequestResponse(errMap, pe.getMessage());
    }

    if (Util.hasNulls(create_list) && Util.hasNulls(update_list) && Util.hasNulls(delete_list)){
      return Util.badRequestResponse(errMap);
    }

    DataService ds = serviceProvider.getDataService();

    // Creation 
    // loop through each table which needs entries creating
    for (int i=0; create_list.length(); i++)
    {
        // loop through each entry which needs creating
        for (int k=0; create_list[i].length(); k++)
        {
            Map<String, SMValue> feedback = new HashMap<String, SMValue>();
            // loop through each column within array == table column
            for (int l=0; create_list[i][k].length(); l++)
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
            try {
              // Attempt to create object
              ds.createObject(create_list[i], new SMObject(feedback));
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