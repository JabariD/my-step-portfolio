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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Additons */
import com.google.gson.Gson;
import java.util.ArrayList;
/* -------- */
import com.google.appengine.api.datastore.*;

/** Determines if the todo was made by a Guest or an Email | Also, determines if any todos were made by an email. */
@WebServlet("/guest-delete")
public class GuestDeleteServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        response.setContentType("application/json;");

        Boolean deleteOneTodo = Boolean.valueOf(request.getParameter("oneTodo"));

        // User wants to try and remove 1 todo.
        if (deleteOneTodo) {
            long todoID = Long.parseLong(request.getParameter("key"));
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Key todoKey = KeyFactory.createKey("Task", todoID);
            try {
                Entity got = datastore.get(todoKey);
                String email = (String) got.getProperty("email");

                sendTodosToClient(response, email);
            } catch (Exception e) {
                System.out.println("Unable to get todo that the user wanted to delete -- " + e);
            }

           
        } else {
            // User wants to try and remove all todos. Loop through todos and find if a email is not guest
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            final Query q = new Query("Task");
            PreparedQuery todosInStore = datastore.prepare(q);

            Gson gson = new Gson();

            for (Entity todo : todosInStore.asIterable()) {
                String email = (String) todo.getProperty("email");
                try {
                    if (email.equals("Guest")) continue;
                    else {
                        sendTodosToClient(response, "true");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            sendTodosToClient(response, "false");
        }
        
    }

    private void sendTodosToClient(HttpServletResponse response, String user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);

        response.setContentType("application/json;");
        try {
            response.getWriter().println(json);
        } catch (Exception e) {
            System.out.println("Error trying to send todos to client" + e);
        }
    }
}

