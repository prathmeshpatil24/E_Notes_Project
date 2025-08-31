/**
 * 
 */

function reset(){

     // Clear text inputs
//     document.getElementById("name").value = "";
//    document.getElementById("email").value = "";
//    document.getElementById("address").value = "";
//    document.getElementById("myPassword").value = "";


//     // Uncheck all radio buttons
//     const radioButtons = document.querySelectorAll('input[name="gender"]');
//     radioButtons.forEach(radio => {
//         radio.checked = false;
//     });
    
//     // Uncheck show password checkbox
//     document.getElementById("togglePassword").checked = false;


// Reset the form (this will clear all form fields including radio buttons)
document.getElementById("registrationForm").reset();
    
// Manually uncheck the show password checkbox since it might not be part of the form
document.getElementById("togglePassword").checked = false;

// Ensure password is hidden after reset
document.getElementById("myPassword").type = "password";
     
}