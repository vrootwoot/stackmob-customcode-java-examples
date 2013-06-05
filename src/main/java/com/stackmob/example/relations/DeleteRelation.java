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
 * with one parameter that creates an object in the `car` schema
 * and relates it to a parent `User` object. This relation is
 * a property of the `user` and it is called `garage` in this example.
 */

public class DeleteRelation implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "Delete_Relation";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("car_ID", "user_name");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    Map<String, SMObject> feedback = new HashMap<String, SMObject>();
    Map<String, String> errMap = new HashMap<String, String>();
    LoggerService logger = serviceProvider.getLoggerService(DeleteRelation.class);

    DataService ds = serviceProvider.getDataService();
    List<SMValue> valuesToRemove = new ArrayList<SMValue>();

    String carID = request.getParams().get("car_ID");
    String owner = request.getParams().get("user_name");
    if (Util.hasNulls(carID, owner)){
      return Util.badRequestResponse(errMap);
    }

    try {
      valuesToRemove.add(new SMString(carID));

      /**
       * This function will remove any values present in `valuesToRemove` from the garage
       * relationship in the user specified by the parameter `user_name`.
       * The boolean False signifies that we do not want to engage in a cascade deletion,
       * which not only removes the relationship, but deletes the object as well.
       */
      ds.removeRelatedObjects("user", new SMString(owner), "garage", valuesToRemove, false);
    } catch (InvalidSchemaException ise) {
      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
    } catch (DatastoreException dse) {
      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
    }

    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}
