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

public class UpdateObject implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "CRUD_Update";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("data");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
        LoggerService logger = serviceProvider.getLoggerService(UpdateObject.class);
    logger.debug(request.getBody());
    Map<String, String> errMap = new HashMap<String, String>();

    /* The following try/catch block shows how to properly fetch parameters for PUT/POST operations
     * from the JSON request body
     */
JSONParser parser = new JSONParser();
 
	
		Object obj = parser.parse(request.getBody());
 
		JSONObject jsonObject = (JSONObject) obj;
 
		// loop array
		JSONArray msg = (JSONArray) jsonObject.get("data");
		Iterator<String> iterator = msg.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}


    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}