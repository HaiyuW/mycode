function addModalButton(id, button_class, modal_id, innerText) {
    let newNode = document.createElement("button");
    newNode.setAttribute("id", id);
    newNode.setAttribute("type", "button");
    newNode.setAttribute("class", button_class);
    newNode.setAttribute("data-toggle", "modal");
    newNode.setAttribute("data-target", "#myModal" + modal_id);
    newNode.innerText = innerText;
    return newNode;
}

function addModal(modal_id) {
    let modalNode = document.createElement("div");
    modalNode.setAttribute("class", "modal fade");
    modalNode.setAttribute("id", "myModal" + modal_id);

    let dialogNode = document.createElement("div");
    dialogNode.setAttribute("class", "modal-dialog");

    let contentNode = document.createElement("div");
    contentNode.setAttribute("class", "modal-content");

    let headerNode = document.createElement("div");
    headerNode.setAttribute("id", modal_id + "-header");
    headerNode.setAttribute("class", "modal-header");

    let bodyNode = document.createElement("div");
    bodyNode.setAttribute("id", modal_id + "-body");
    bodyNode.setAttribute("class", "modal-body");

    let footerNode = document.createElement("div");
    footerNode.setAttribute("id", modal_id + "-footer");
    footerNode.setAttribute("class", "modal-footer");

    let closeButtonNode = document.createElement("button");
    closeButtonNode.setAttribute("id", modal_id + "-close-button");
    closeButtonNode.setAttribute("type", "button");
    closeButtonNode.setAttribute("class", "btn btn-secondary");
    closeButtonNode.setAttribute("data-dismiss", "modal");
    closeButtonNode.innerText = "close";

    footerNode.appendChild(closeButtonNode);

    contentNode.appendChild(headerNode);
    contentNode.appendChild(bodyNode);
    contentNode.appendChild(footerNode);

    dialogNode.appendChild(contentNode);

    modalNode.appendChild(dialogNode);

    return modalNode;
}

function addInput(type, className, name, id) {
    let inputNode = document.createElement("input");
    inputNode.setAttribute("type", type);
    inputNode.setAttribute("class", className);
    inputNode.setAttribute("name", name);
    inputNode.setAttribute("id", id);

    return inputNode;
}

function addLabel(forWhat, innerText) {
    let labelNode = document.createElement("label");
    labelNode.setAttribute("for", forWhat);
    labelNode.innerText = innerText;

    return labelNode;
}

function addFormGroup() {
    let formGroupNode = document.createElement("div");
    formGroupNode.setAttribute("class", "form-group");

    for (let i = 0; i < arguments.length; i++) {
        formGroupNode.appendChild(arguments[i]);
    }

    return formGroupNode;
}

function addLogoutButton() {
    return addNormalButton("logout-button", "btn btn-primary", "Log out");
}

function addShareButton() {
    return addNormalButton("share-button", "btn btn-primary", "Share");
}

function addPElement(id, text) {
    let newNode = document.createElement("p");
    newNode.setAttribute("id", id);
    newNode.innerText = text;

    return newNode;
}

function addNormalButton(id, className, innerText) {
    let buttonNode = document.createElement("button");
    buttonNode.setAttribute("id", id);
    buttonNode.setAttribute("type", "button");
    buttonNode.setAttribute("class", className);
    buttonNode.innerText = innerText;

    return buttonNode;

}

function addBasicCard(id, className) {
    let cardNode = document.createElement("div");
    cardNode.setAttribute("id", id);
    cardNode.setAttribute("class", className);

    let cardBodyNode = document.createElement("div");
    cardBodyNode.setAttribute("id", id + "-body");
    cardBodyNode.setAttribute("class", "card-body");

    cardNode.appendChild(cardBodyNode);

    return cardNode;
}

function addComplexCard(id, className) {
    let cardNode = document.createElement("div");
    cardNode.setAttribute("id", id);
    cardNode.setAttribute("class", className);

    let cardHeaderNode = document.createElement("div");
    cardHeaderNode.setAttribute("id", id + "-header");
    cardHeaderNode.setAttribute("class", "card-header");

    let cardBodyNode = document.createElement("div");
    cardBodyNode.setAttribute("id", id + "-body");
    cardBodyNode.setAttribute("class", "card-body");

    let cardFooterNode = document.createElement("div");
    cardFooterNode.setAttribute("id", id + "-footer");
    cardFooterNode.setAttribute("class", "card-footer");

    cardNode.appendChild(cardHeaderNode);
    cardNode.appendChild(cardBodyNode);
    cardNode.appendChild(cardFooterNode);

    return cardNode;
}

function clearNavigationBar() {
    let navigationBar = document.getElementById("navigation-bar");
    navigationBar.innerHTML = '';

}

function clearModal(modalName) {
    let navigationBarNode = document.getElementById("navigation-bar");
    navigationBarNode.removeChild(
        document.getElementById(modalName));
    document.getElementById("root").parentNode.removeChild(
        document.getElementsByClassName("modal-backdrop fade show")[0]);

}

function updateCalendar() {}
function updateContacts() {}

function getTodayDate() {
    let date = new Date();
    let currentDate = date.getFullYear().toString() + "-" + (date.getMonth() + 1).toString() + "-" + date.getDate().toString()
    return currentDate;
}

function postPrototype(url, data, handleFunctionName) {
    fetch(url, {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleFunctionName(data))
        .catch(err => console.error(err))
    
}


