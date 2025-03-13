function initInputField(){
    console.log("initInputField");
    document.querySelectorAll('#main-input-form input')
        .forEach(input => input.value = '');
}
