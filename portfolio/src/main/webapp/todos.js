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
    // Refresh Comments
    refreshTodos();

    // Grab the # of Comments
    const todoQuantity = document.getElementById("quantity").value;

    // authenticate quantity given
    if (authenticateQuantityGiven(todoQuantity)) {

        // Fetch that Quantity
        const response = await fetch(`/todo?amount=${todoQuantity}`);
        const data = await response.json();
        console.log(data);

        createTodos(data);

    } else {
        console.log("Error! Please enter another value!");
    }
}

/** Create Todos */
function createTodos(data) {
    const TodoNode = document.getElementById('todos'); 
    for (todo of data) {
        // Create li element
        const liElement = document.createElement('li');
        liElement.innerText = todo;

        TodoNode.appendChild(liElement);
    }
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
    // POST request using fetch() to delete store in database
    await fetch("/delete-todo", { 
      
        // Adding method type 
        method: "POST", 
      
        // Adding body or contents to send ** Not necessary for this but for practice?
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

    // Re-Update Todos
    await updateTodos();
}