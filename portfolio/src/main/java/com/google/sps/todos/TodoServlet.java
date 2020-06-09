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
import java.util.ArrayList;
/* -------- */

/* Datastore */
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
/* -------- */

/** Servlet that returns todo content. */
@WebServlet("/todo")
public class TodoServlet extends HttpServlet {
    private  ArrayList<Task> todos = new ArrayList<Task>();
    private int numberOfComments = 3; // DEFAULT: 3

    /** */
    public void init(ServletConfig servletconfig) throws ServletException {
        
    }

    /** Load Todos from Datastore and return them to client in JSON. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Accepts the max number of comments
        numberOfComments = Integer.parseInt(request.getParameter("amount"));

        // Accept Sort Direction
        String sortDirection = request.getParameter("sort");

        // Get Todos from Datastore
        todos = loadTodos(sortDirection);

        // convert to JSON
        Gson gson = new Gson();
        String json = gson.toJson(todos);
        
        // send todos to client
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /** Given user task, store it in Datastore and redirect back to HTML. */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Add in DataStore
        String task = request.getParameter("todo");
        long timestamp = System.currentTimeMillis();

        Entity taskEntity = new Entity("Task");
        taskEntity.setProperty("task", task);
        taskEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(taskEntity);

        // Grab Sort Direction from HTML Form
        String sortDirection = request.getParameter("SortDirection");

        // Reload Todos from Datastore
        todos = loadTodos(sortDirection);

        // Most applications send messages using JSON
        Gson gson = new Gson();
        String json = gson.toJson(todos);

        response.setContentType("application/json;");
        response.getWriter().println(json);

        response.sendRedirect("./index.html"); // redirects to init page load JS function
    }

    /** Extract todos from Datastore */
    private ArrayList<Task> loadTodos(String sortDirection) {
        // Change Search Direction based on user input
        Query.SortDirection SortChoice = sortDirection.equals("newest") ? Query.SortDirection.DESCENDING : Query.SortDirection.ASCENDING;
        
        // create query instance with the 'Task' kind to load it's instances
        /* NOTE: that addSort is a function that automatically sorts based on the property */
        Query query = new Query("Task").addSort("timestamp", SortChoice);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // contains all the results of the KIND that you want to receive
        PreparedQuery results = datastore.prepare(query);

        ArrayList<Task> todoList = new ArrayList<Task>();

        // loop through the entities and put them in our todoList to be ready to be sent to the server .
        int counter = 0;
        for (Entity entity : results.asIterable()) {
            if (counter == numberOfComments) break;
            ++counter;

            long id = entity.getKey().getId();
            String task = (String) entity.getProperty("task");

            Task item = new Task(task, id);

            todoList.add(item);
        }

        return todoList;
    }
}