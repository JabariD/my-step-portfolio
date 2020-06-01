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

/** Servlet that returns some example content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  // form 1
  private ArrayList<String> comments = new ArrayList<String>();
  // form 2
  private ArrayList<String> todos = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* Returned for FORM submit button */
    // response.setContentType("text/html;");
    // response.getWriter().println("<h1>Hello world!</h1>");
    // response.getWriter().println("<p>You just sent a request to the server and the server responded with this response!</p>");

    /* Check if null for ASYNC button */
    // if (request.getParameter("text") != null)
    //     response.getWriter().println("<p>Your input: " + request.getParameter("text") + "</p>");

    // reset comments. if this wasn't here we would keep appending to comments!
    comments.clear();

    String commentsInJSON = getComments(comments);

    //response.getWriter().println(commentsInJSON);

    // Send JSON as the response
    response.setContentType("application/json;");

    // send Todos
    Gson gson = new Gson();
    String json = gson.toJson(todos);

    // we can send either 2 things for testing out the functionality: comments or todos
    response.getWriter().println(json);

  }

  /**
  *  Converts Obect to JSON to return comments in JSON!
  **/
  private String getComments(ArrayList<String> comments) {
    // Converts Java object to JSON to return comments!
    comments.add("This site is cool!");
    comments.add("This site is pretty nice!");
    comments.add("Eh, this site could use some work.");

    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  /**
  *  1) The user just clicked submit on form #2. Now the computer comes here to get the current state and reloads the entire page.
  *  2) Then the JS function transforms the DOM to faciliate those changes. (everytime body loads again)
  **/
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // add to our todos
        todos.add(request.getParameter("todo"));

        // Usually will be working with JSON
        Gson gson = new Gson();
        String json = gson.toJson(todos);

        response.setContentType("application/json;");
        response.getWriter().println(json);
        response.sendRedirect("./index.html"); // redirects to init page load JS function
    }
}