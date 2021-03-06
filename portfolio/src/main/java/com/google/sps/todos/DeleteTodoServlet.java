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
import com.google.appengine.api.datastore.Key;
/* -------- */

/** Deletes all tasks. */
@WebServlet("/delete-todo")
public class DeleteTodoServlet extends HttpServlet {

    /** Given user task, store it in Datastore and redirect back to HTML. */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        final Query q = new Query("Task");
        q.setKeysOnly();
        PreparedQuery todosInStore = datastore.prepare(q);

        ArrayList<Key> keys = new ArrayList<Key>(); 

        for (Entity todo : todosInStore.asIterable()) {
            keys.add(todo.getKey());
        }

        // Batch delete keys
        datastore.delete(keys);

        response.getWriter().println();
    }
    
}