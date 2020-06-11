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


/** Function that fetches the state of the todo array and appends them to unordered list todos. */
async function updateTodos() {
    // Refresh todos by deleting
    refreshTodos();

    // Grab the # of Todos and set them
    let todoQuantityResponse;
    try {
        todoQuantityResponse = await fetch("/todo-quantity");
    } catch (e) {
        console.log(e);
    }
    
    const todoQuantity = await todoQuantityResponse.json();
    
    document.getElementById("quantity").value = todoQuantity;

    // Grab sort direction
    const sortDirection = document.getElementById("SortDirection").value;

    // authenticate quantity given
    if (authenticateQuantityGiven(todoQuantity)) {

        // Fetch that Quantity
        const response = await fetch(`/todo?amount=${todoQuantity}&sort=${sortDirection}`);
        const data = await response.json();

        await createTodos(data);

    } else {
        console.log("Error! Please enter another value!");
    }
}

/** Everytime we reload the page we want to update the value! */
async function updateTodoQuantity() {
    let number = document.getElementById("quantity").value;

    if (!authenticateQuantityGiven(number)) number = 3; // DEFAULT to 3.

    // Update Number of Todos
    const response = await fetch("/todo-quantity", { 
    
                        // Adding method type 
                        method: "POST", 
      
                        // Adding body or contents to send 
                        body: JSON.stringify({ 
                            quantity: number, 
                        }), 
      
                        // Adding headers to the request 
                        headers: { 
                            "Content-type": "application/json; charset=UTF-8"
                        } 
    }); 
    


    // Reload Todos
    await updateTodos();
}



/** Create Todos */
function createTodos(data) {

    const TodoNode = document.getElementById('todos'); 
    for (todo of data) {
        // Create li element
        const liElement = document.createElement('li');

        // Check email not undefined
        if (todo.email === undefined) todo.email = "Guest";

        liElement.id = todo.email;
        liElement.innerText = todo.task;
        liElement.setAttribute("onmouseover", `addEmailToSpan(event)`);
        liElement.setAttribute("onmouseout", "removeEmailFromSpan(event)");
        
        // Create Delete Button
        const spanElement = document.createElement('span');

        const buttonElement = document.createElement('button');
        buttonElement.innerHTML = "X";
        buttonElement.id = todo.key;
        buttonElement.setAttribute("onclick", "removeThisTodo(event)");

        // The button element will inherit the mouseover property from it's parent liElement.
        // We need to prevent it from showing it's ID.
        buttonElement.setAttribute("onmouseover", "removeEmailFromSpan(event)");
        

        spanElement.appendChild(buttonElement);
        liElement.appendChild(spanElement);

        TodoNode.appendChild(liElement);
    }
}

/** Adds email to invisible span element. */
function addEmailToSpan(event) {
    const span = document.getElementById("todo_created_by");
    span.innerHTML = event.target.id;
}

function removeEmailFromSpan(event) {
    event.stopPropagation();
    document.getElementById("todo_created_by").innerHTML = " ";
}

/** Refresh Comments */ 
function refreshTodos() {
    const TodoNode = document.getElementById('todos');
    while (TodoNode.firstChild) {
        TodoNode.removeChild(TodoNode.firstChild);
    }
}

/** Authenticate quantity */ 
function authenticateQuantityGiven(todoQuantity) {
    // set MAX allowed value here
    if (todoQuantity > 0 && todoQuantity <= 5) {
        return true;
    } else
        return false;
}

/** Using fetch to have a POST request and RE-UPDATE todos. */ 
async function deleteTodos() {
    // Prevent guests from deleting a todo.
    let isLoggedIn = await checkIfNotGuest();

    if (!isLoggedIn) {
        if(confirm("Please sign in to complete this action.")) {
            document.getElementById('signIn').click();
        } else {
            return;
        }
    }

    // Make sure the user is has logged in.
    isLoggedIn = await checkIfNotGuest();

    if (isLoggedIn) {


        // POST request using fetch() to delete store in database
        await fetch("/delete-todo", { 
        
            // Adding method type 
            method: "POST", 
        
            // Adding body or contents to send ** Not necessary for this but for practice
            body: JSON.stringify({ 
                title: "foo", 
                body: "bar", 
                userId: 1 
            }), 
            
            // Adding headers to the request 
            headers: { 
                "Content-type": "application/json; charset=UTF-8"
            } 
        }) 

        // Clear UL child by deleting all the Todos we are showing
        await refreshTodos();

        // Sometimes if the user is over a todo and then suddenly clicks delete all, the span will stay. Let's make sure we delete that.
        document.getElementById("todo_created_by").innerHTML = " ";
    }
}

async function removeThisTodo(event) {
    // Prevent guests from deleting a todo.
    let isLoggedIn = await checkIfNotGuest();
    if (!isLoggedIn) {
        if(confirm("Please sign in to complete this action.")) {
            document.getElementById('signIn').click();
        } else {
            return;
        }
    }

    // Make sure the user is has logged in.
    isLoggedIn = await checkIfNotGuest();

    if (isLoggedIn) {
        const key = event.target.id;

        // Remove From Datastore
        await fetch(`/delete-one-todo?id=${key}`);

        // Update todos again
        await updateTodos();
    }
}


/** Making some sacrifices here...
    I could export the functions previously but I deemed it to be to much of a headachace for this reason:

    To use import/export you have to convert JS files to modules. Which limits the code in that file to ONLY that file.
    So now, we would have to manually change and edit DOM one by one. Since HTML code doesn't recongize the JS modules.

    For the sake of time, I thought it would just be worth duplicating just a tad bit of code that obviously could be reused. Let me know if you want me to change it :)!
    Even though this function is shorter than the one in login.js
 */

 async function checkIfNotGuest() {
    // Try to get the Email
    const response = await fetch('/user');
    const data = await response.json();

    return data.isLoggedIn ? true : false;

}