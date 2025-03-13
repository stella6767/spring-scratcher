function initInputField(){
    console.log("initInputField");
    document.querySelectorAll('#main-input-form input')
        .forEach(input => input.value = '');
}


document.addEventListener('htmx:responseError', evt => {
    const xhr = evt.detail.xhr;
    const alertContainer = document.getElementById('error-alert-container');

    if (alertContainer) {

        console.log("!!!")
        console.error(xhr.responseText);
        const alert =
            document.createElement('div');
        alert.className = "fixed top-3 left-6 right-6 z-50 flex justify-center transition-opacity duration-1000";
        alert.innerHTML = xhr.responseText;

        alertContainer.appendChild(alert);

        //alertContainer.

        setTimeout(() => {
            alert.classList.add('opacity-0');
            // fade-out 애니메이션 지속시간(예: 1초) 후 요소 제거
            setTimeout(() => {
                alert.remove();
            }, 1000);
        }, 3000);
    }

});
