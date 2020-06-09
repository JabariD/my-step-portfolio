/** For now, we will simply store a boolean of true/false if the User is signed in. That's all we want. 
In the future, we can expand on this by ADDING a separate user's kind that stores email, username, nickname. */

/** Once we click the 'Sign In' button, we sign the user in. */
async function signIn() {
    
        // Try to log the user in.
        const signedIn =  await checkIfSignedIn();

        if (!signedIn) {
            // The user is not signed in, so sign them in!
            const response = await fetch('/login');
            const data = await response.json();
        
            window.location.href = data;
        }

        await checkIfSignedIn();
}


/** Everytime we load the Body, we want to check if the User is signed in or not. */
async function checkIfSignedIn() {
    // Try to get the Email
    const response = await fetch('/user');
    const data = await response.json();
    const signedIn = data[0];

    if (signedIn === "true") {
        const email = data[1];
        greetingMessage(true, email);
        return true;
    } else {
        greetingMessage(false);
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
function greetingMessage(user, data = "Guest") {
    document.getElementById("email").innerHTML = `Welcome, ${data}.`;
}