// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

import com.google.sps.data.Task;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Additons */
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
/* -------- */

/* Datastore */
// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.PreparedQuery;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.api.datastore.Query.SortDirection;
// import com.google.appengine.api.datastore.Key;
// import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.*;

/* -------- */

/** Servlet that returns the number of todos. */
@WebServlet("/todo-quantity")
public class StoreQuantityServlet extends HttpServlet {
    private int numberOfTodos = 3;
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public void init(ServletConfig servletconfig) throws ServletException {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    /** Simply get number of the numberofTodos. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            // Retrieve from Datastore if avaliable.
            Query query = new Query("Quantity");
            PreparedQuery results = datastore.prepare(query);
            for (Entity entity : results.asIterable())
                numberOfTodos = (Integer) entity.getProperty("num");
        } catch (Exception e) {
            
        }

        // convert to JSON
        Gson gson = new Gson();
        String json = gson.toJson(numberOfTodos);
        
        // send number back to client
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /** Simply change amount of todos displayed in the Datastore. */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read body from POST request and store it in numberofTodos //
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
       
        try {
            numberOfTodos = Integer.parseInt(data.substring(13,14));
        } catch (Exception e) {
            numberOfTodos = 3;
        }
        // ------------------------------------- //

        // Put in Datastore
        Entity quan = new Entity("Quantity", "amount");
        quan.setProperty("num", numberOfTodos);


        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(quan);
        
        // Convert to JSON
        Gson gson = new Gson();
        String json = gson.toJson(numberOfTodos);
        
        // Send number back to client
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    
}