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

/* Gson */
import com.google.gson.Gson;
/* ---- */

import java.util.ArrayList;


/** Servlet that directs the client to a new page from the inputted data. */
@WebServlet("/addToDOM")
public class AddToDOMServlet extends HttpServlet {
    private ArrayList<String> foods = new ArrayList<String>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        foods.clear();
        // NOTE: If we repeatedly call this it will keep adding to our foods object. So, we clear it just in case.


        // Get input field called "text" from client
        String json = convertObjectToJSON(foods);

        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /* Converts Obect to JSON to return foods in JSON! */
    private String convertObjectToJSON(ArrayList<String> list) {
        list.add("Pizza");
        list.add("Ribs");
        list.add("Salads");

        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}