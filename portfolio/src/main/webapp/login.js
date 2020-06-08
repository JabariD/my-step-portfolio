
/** Once we click the 'Sign In' button, we sign the user in. */
async function signIn() {
    // Determine if user is already logged in.
    if (document.getElementById("email").innerHTML.localeCompare("Welcome, Guest.") === 0) {
        // Try to log the user in.
        const response = await fetch('/login');
        const data = await response.json();
        
        window.location.href = data;
    }
}


/** Everytime we load the Body, we want to check if the User is signed in or not. */
async function checkIfSignedIn() {
    // Try to get the Email
    const response = await fetch('/user');
    const data = await response.json();

    document.getElementById("email").innerHTML = `Welcome, ${data}.`;
}


/** Once we click the 'Sign Out' button, we address the user. */
async function signOut() {
    const response = await fetch('/logout');
    const data = await response.json();

    document.getElementById("email").innerHTML = "Welcome, Guest."
}