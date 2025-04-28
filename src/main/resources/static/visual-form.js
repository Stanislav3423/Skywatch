const pwShowHide = document.querySelectorAll(".showHidePw"),
    loginPwFields = document.querySelectorAll("#password");

pwShowHide.forEach((eyeIcon) => {
    eyeIcon.addEventListener("click", () => {
        loginPwFields.forEach((pwField) => { // !!! І тут
            if (pwField.type === "password") {
                pwField.type = "text";
                eyeIcon.classList.replace("uil-eye-slash", "uil-eye");
            } else {
                pwField.type = "password";
                eyeIcon.classList.replace("uil-eye", "uil-eye-slash");
            }
        });
    });
});