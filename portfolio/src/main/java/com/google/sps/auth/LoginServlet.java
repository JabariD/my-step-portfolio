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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private ArrayList<String> data = new ArrayList<String>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        // Log the user in.        
        UserService userService = UserServiceFactory.getUserService();

        String urlToRedirectToAfterUserLogsIn = "/";
        String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

        Gson gson = new Gson();
        String json = gson.toJson(loginUrl);

        response.setContentType("application/json;");
        response.getWriter().println(json);

        // Update isLoggedIn to true!
        Entity user = new Entity("IsLoggedIn", "User");
        user.setProperty("user", "true");

        // Put in datastore.
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(user);
    }
}
