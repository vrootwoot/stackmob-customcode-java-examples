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
import com.google.gson.Gson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

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
    Map<String, SMObject> feedback = new HashMap<String, SMObject>();
    Map<String, String> errMap = new HashMap<String, String>();
    LoggerService logger = serviceProvider.getLoggerService(UpdateObject.class);
	Type mapType = new TypeToken<Map<String, Map>>(){}.getType();  
	Map<String, String[]> son = new Gson().fromJson(request.getBody(), mapType);
    logger.debug(request.getBody());
    try {
		
    } catch (ParseException pe) {
      logger.error(pe.getMessage(), pe);
      return Util.badRequestResponse(errMap, pe.getMessage());
    }
    return new ResponseToProcess(HttpURLConnection.HTTP_OK, son);
  }

}