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


/** Once we click the 'Sign In' button, we sign the user in. */
async function signIn() {
    
    // Try to log the user in.
    const signedIn =  await checkIfSignedIn();

    if (!signedIn) {
        // The user is not signed in, so sign them in!
        const response = await fetch('/login');
        const data = await response.json();
        setTimeout(500);
        
        window.location.href = data;
    }
    await checkIfSignedIn();
}


/** Everytime we load the Body, we want to check if the User is signed in or not. */
async function checkIfSignedIn() {
    // Try to get the Email
    const response = await fetch('/user');
    const data = await response.json();

    if (data.isLoggedIn) {
        greetingMessage(data.email);
        return true;
    } else {
        greetingMessage();
        return false;
    }
}

/** Once we click the 'Sign Out' button, we log the user out. */
async function signOut() {
    const response = await fetch('/logout');

    // Check if still signed in.
    await checkIfSignedIn();
}

/** Update message to user */
function greetingMessage(data = "Guest") {
    document.getElementById("email").innerHTML = `Welcome, ${data}.`;
}
