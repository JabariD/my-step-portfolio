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

/** Simply check if the user is signed in and return the email. */
@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private ArrayList<String> data = new ArrayList<String>();
    private Boolean x = false;


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        response.setContentType("application/json;");

        // Retrieve the entity that stores our boolean value.
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key loggedInKey = KeyFactory.createKey("IsLoggedIn", "User");
        
        try {
            // Try to see if user is logged in.
            Entity got = datastore.get(loggedInKey);
            String loggedIn = (String) got.getProperty("user");

            // convert to JSON
            ArrayList<String> user = new ArrayList<String>();
            user.add(loggedIn);
            user.add(userService.getCurrentUser().getEmail());

            Gson gson = new Gson();
            String json = gson.toJson(user);
            response.getWriter().println(json);
        } catch (Exception e) {
            // If user is not logged in, just return false.
            System.out.println("Unable to determine if user is logged in. Default to user not logged in. " + e);

            Gson gson = new Gson();
            String json = gson.toJson("false");
            response.getWriter().println(json);
        }
        
    }
}
