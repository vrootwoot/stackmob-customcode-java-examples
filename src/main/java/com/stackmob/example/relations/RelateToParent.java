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

package com.stackmob.example.relations;

import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.DatastoreException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.*;

import java.net.HttpURLConnection;
import java.util.*;

/**
 * This example will show a user how to write a custom code method
 * with two parameters that takes a unique car ID and then relates
 * that already-existing car to a parent `user`
 */

public class RelateToParent implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "Relate_Existing_To_Parent";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("car_ID", "user_name");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    Map<String, SMObject> feedback = new HashMap<String, SMObject>();
    Map<String, String> errMap = new HashMap<String, String>();
    LoggerService logger = serviceProvider.getLoggerService(RelateToParent.class);

    DataService ds = serviceProvider.getDataService();
    List<SMValue> valuesToAppend = new ArrayList<SMValue>();
    SMObject result;

    String carID = request.getParams().get("car_ID");
    String owner = request.getParams().get("user_name");
    if (Util.hasNulls(owner, carID)){
      return Util.badRequestResponse(errMap);
    }

    try {
      /* In the `user` schema we are going to add the car specified by ID to the `garage` (one-to-many) relation
       * specified by the input `user_name`
       */
      valuesToAppend.add(new SMString(carID));
      result = ds.addRelatedObjects("user", new SMString(owner), "garage", valuesToAppend);

      feedback.put("added " + carID + "to", result);
    } catch (InvalidSchemaException ise) {
      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
    } catch (DatastoreException dse) {
      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
    }

    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}
