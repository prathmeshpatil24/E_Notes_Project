/**
 * 
 */


function showPassword() {
	let password = document.getElementById("myPassword");
	if (password.type === "password") {
		password.type = "text";
	} else {
		password.type = "password";
	}
}
