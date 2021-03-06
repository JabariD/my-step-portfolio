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


/** Adds a random greeting to the page. */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!', 'Привет мир!', 'Hello Wêreld!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/** Changes color of the ONLY the main header on the page. */
function applyColor() {
    const colors =
      ['red', 'green', 'blue', 'yellow', 'black', 'purple', 'orange', 'pink', 'gray', 'aqua', 'tomato', 'teal'];
    
    // Pick a random color.
    const color = colors[Math.floor(Math.random() * colors.length)];

    // Add it to the page.
    document.getElementById("header").style.color = color;
}

/** Async function that fetches the data URL mapped by the server and prints it to console and gets the first element and prints it on form. */
async function addFoodToDOM() {
    const response = await fetch('/addToDOM');
    const data = await response.json();

    document.getElementById('fooditems').innerText = data + "!"; 
}