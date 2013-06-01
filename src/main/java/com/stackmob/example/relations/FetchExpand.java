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

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.*;

import java.net.HttpURLConnection;
import java.util.*;

/**
 * This example will show a user how to write a custom code method
 * that will do an expanded (deep) fetch of `user` objects. This
 * expanded fetch will cause any relationship property found in `user` to return
 * entire objects instead of just a list of strings.
 */

public class FetchExpand implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "Expanded_Fetch";
  }

  @Override
  public List<String> getParams() {
    return new ArrayList<String>();
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    LoggerService logger = serviceProvider.getLoggerService(FetchExpand.class);
    Map<String, List<SMObject>> feedback = new HashMap<String, List<SMObject>>();
    Map<String, String> errMap = new HashMap<String, String>();

    DataService ds = serviceProvider.getDataService();
    List<SMObject> results;

    try {
      /**
       * In this case, when we read objects from the `user` schema, we have
       * an extra parameter '1' that defines the expand depth. What a depth of 1 means
       * is that a fetch will be called on all related children to the initial object
       * up to 1 level deep. An expand-depth of 2 will make a fetch for the
       * children of the children of the initial object, that is, 2 levels deep. The maximum
       * value for expand depth is 3, which means the fetch will only be called up to 3 levels
       * recursively. Imagine 1, 2, 3 to mean children, grandchildren, and great-grandchildren
       * of the initial object, respectively.
       */
      results = ds.readObjects("user", new ArrayList<SMCondition>(), 1);
      feedback.put("results", results);

    } catch (InvalidSchemaException ise) {
      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
    } catch (DatastoreException dse) {
      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
    }

    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}
