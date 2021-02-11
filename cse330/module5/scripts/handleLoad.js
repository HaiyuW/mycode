document.addEventListener("DOMContentLoaded", function () {
    checkLogin(handleCheckResponse);
}, false);

function checkLogin(functionName) {
    return fetch('phpfiles/login.php', {
        method: 'POST',
        body: JSON.stringify({ 'checkLogin': 'check'}),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => functionName(data))
        .catch(err => console.error(err));
}

function handleCheckResponse(data) {
    console.log(data.message);
    if (data.loggedin) {

        clearNavigationBar();

        updateEvents(getTodayDate(), handleUpdateEvents);
        let navigationBarNode = document.getElementById("navigation-bar");

        // TODO: 这里加了一句
        postGroupEvents();

        // add new event button
        navigationBarNode.appendChild(addModalButton("newEvent-button", "btn btn-primary",
            "newEvent-modal", "New Event"));

        //TODO: 这里我加了个判断是否为group event
        document.getElementById("newEvent-button").addEventListener("click", function () {
            showNewEventModal();
            let isGroup = false;
            // check if it is a group event
            document.getElementById("isGroup-checkbox").addEventListener("change", function () {
                if (this.checked){
                    isGroup = true;
                    showUsers();
                } else {
                    document.getElementById("newEvent-modal-body").removeChild(
                        document.getElementById("usersLine")
                    );
                    isGroup = false;
                }
            }, false);

            // handle both event and group event
            document.getElementById("newEvent-modal-footer-submit").addEventListener("click", function () {
                if (isGroup){
                    handleNewGroupEvent();
                } else {
                    postNewEvent();
                }
            }, false);

            // TODO: 以上内容有添加或修改

            document.getElementById("newEvent-modal-close-button").addEventListener("click", function () {
                document.getElementById("navigation-bar").removeChild(
                    document.getElementById("myModalnewEvent-modal")
                );
                document.getElementById("root").parentNode.removeChild(
                    document.getElementsByClassName("modal-backdrop fade show")[0]);
            })
        }, false);

        // add text
        navigationBarNode.appendChild(addPElement("helloText", "Hello! " + data.username));

        // add logout button
        navigationBarNode.appendChild(addLogoutButton());
        document.getElementById("logout-button").addEventListener("click", postLogout, false);
        navigationBarNode.appendChild(addShareButton());



        // updateCalendar();
        // updateContacts();

    } else {

        document.getElementById("events-view").innerHTML = '';

        let rootNode = document.getElementById("navigation-bar");

        // add login button
        rootNode.appendChild(addModalButton("login-button", "btn btn-primary",
            "login-modal", "Log in"));
        // add eventlistener
        document.getElementById("login-button").addEventListener("click", function () {
            showLoginModal();
            document.getElementById("login-modal-footer-submit").addEventListener("click", function () {
                postLoginInfo();
            }, false);
            document.getElementById("login-modal-close-button").addEventListener("click", function () {
                document.getElementById("navigation-bar").removeChild(
                    document.getElementById("myModallogin-modal")
                );
                document.getElementById("root").parentNode.removeChild(
                    document.getElementsByClassName("modal-backdrop fade show")[0]);
            })
        }, false);


        // add sign up button
        rootNode.appendChild(addModalButton("signup-button", "btn btn-primary",
            "signup-modal", "Sign up"));
        // add eventlistener
        document.getElementById("signup-button").addEventListener("click", function () {
            showSignupModal();
            document.getElementById("signup-modal-footer-submit").addEventListener("click",function () {
                postSignupInfo()
            }, false);
            document.getElementById("signup-modal-close-button").addEventListener("click", function () {
                document.getElementById("navigation-bar").removeChild(
                    document.getElementById("myModalsignup-modal")
                );
                document.getElementById("root").parentNode.removeChild(
                    document.getElementsByClassName("modal-backdrop fade show")[0]);
            })

        }, false);

    }
}

// TODO:以下内容有添加或修改

// handle group event, first insert the new event into TABLE events
function handleNewGroupEvent() {
    const event_title = document.getElementById("newEvent-title").value;
    const event_description = document.getElementById("newEvent-description").value;
    const event_date = document.getElementById("newEvent-date").value;
    const event_time = document.getElementById("newEvent-time").value;
    const data = {
        'action': 'newEvent',
        'event-title': event_title,
        'event-description': event_description,
        'event-date': event_date,
        'event-time': event_time
    };

    // alert('action: ' + 'newEvent' +
    //     'event-title' + event_title +
    //     'event-description' + event_description +
    //     'event-date' + event_date +
    //     'event-time' + event_time);
    //
    // //debug
    // alert("??");

    fetch('phpfiles/handleEvent', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleNewGroupEventResponse(data))
        .catch(err => console.error(err))
}

// insert event_id and member_id into TABLE groups
function handleNewGroupEventResponse(data) {
    let event_id = data.event_id;
    let event_date = data.event_date;

    let member_id_list = getMemberID();
    clearModal("myModalnewEvent-modal");
    const groupData = {
        "action": "newGroupEvent",
        "event-id": event_id,
        "member-id-list": member_id_list
    };

    fetch('phpfiles/handleGroupEvent', {
        method: 'POST',
        body: JSON.stringify(groupData),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => postGroupEvents())
        .catch(err => console.log(err))
}

// post the new group events
function postGroupEvents() {
    const groupData = {
        "action": "getEventId"
    };

    fetch('phpfiles/handleGroupEvent', {
        method: 'POST',
        body: JSON.stringify(groupData),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handlePostGroupEvents(data))
        .catch(err => console.log(err))
}

// handle the data get from the TABLE events and display them at groups-view
function handlePostGroupEvents(data) {

    let event_id_list = data.event_id_list;

    let rootNode = document.getElementById("groups-view");
    rootNode.innerHTML = "<div class=\"col\" id=\"groups-view\">--- group ---</div>";

    let containerNode = document.createElement("div");
    containerNode.setAttribute("id", "groups-view-container");
    containerNode.setAttribute("class", "container");

    rootNode.appendChild(containerNode);

    for (let i=0;i<event_id_list.length;i++){
        getEventByEventID(event_id_list[i], postGroupResponse);
    }
}

// form the group event card
function postGroupResponse(data) {
    if (data.success){
        let newGroupEventNode = addBasicCard("group-" + data.event_id, "card bg-info text-white");
        newGroupEventNode.setAttribute("group-id", data.event_id);
        let containerNode = document.getElementById("groups-view-container");
        containerNode.appendChild(newGroupEventNode);

        let bodyNode = document.getElementById("group-"+data.event_id+"-body");
        bodyNode.setAttribute("name", data.event_id);

        let titleNode = document.createElement("h4");
        titleNode.setAttribute("group-id", data.event_id);
        titleNode.setAttribute("class", "card-title");
        titleNode.innerText = data.event_title;

        let tagNode = document.createElement("p");
        tagNode.innerText="Tag";
        tagNode.setAttribute("group-id", data.event_id);
        tagNode.setAttribute("id", "tag-"+data.event_id+"-body");
        showTag(data.event_id);

        let textNode = document.createElement("p");
        textNode.setAttribute("id", "group-" + data.event_id + "-date");
        textNode.setAttribute("group-date", data.event_date);
        textNode.setAttribute("group-id",data.event_id);
        textNode.setAttribute("class", "card-text");
        textNode.innerText = data.event_date + " " + data.event_time;

        bodyNode.appendChild(titleNode);
        bodyNode.appendChild(tagNode);
        bodyNode.appendChild(textNode);

        containerNode.appendChild(document.createElement("br"));

        bodyNode.addEventListener("click", function (event) {
            getEventByEventID(event.target.getAttribute("group-id"), handleGroupCard);
        }, false);
    }
}

// handle the group event card and delete button
function handleGroupCard(data) {
    if (data.success) {
        let cardNode = document.getElementById("group-"+data.event_id);
        if (document.getElementById("group-" + data.event_id + "-footer")) {
            let footerNode = document.getElementById("group-" + data.event_id + "-footer");
            footerNode.removeChild(document.getElementById("group-" + data.event_id + "-delete"));
            cardNode.removeChild(footerNode);
        }

        let cardFooterNode = document.createElement("div");
        cardFooterNode.setAttribute("id", "group-" + data.event_id + "-footer");
        cardFooterNode.setAttribute("class", "card-footer");

        cardFooterNode.appendChild(addNormalButton("group-" + data.event_id + "-delete",
            "btn btn-danger", "Delete"));

        cardNode.appendChild(cardFooterNode);

        document.getElementById("group-" + data.event_id + "-delete")
            .setAttribute("event-id", data.event_id);

        let bodyNode = document.getElementById("group-" + data.event_id + "-body");
        if (document.getElementById("group-" + data.event_id + "-body-description"))
            bodyNode.removeChild(document.getElementById("group-" + data.event_id + "-body-description"));

        let textNode = document.createElement("p");
        textNode.setAttribute("id", "group-" + data.event_id + "-body-description");
        textNode.setAttribute("class", "card-text");
        textNode.innerText = data.event_description;
        bodyNode.appendChild(textNode);

        document.getElementById("group-" + data.event_id + "-delete").addEventListener("click", function (event) {
            const data = {
                'action': 'deleteEvent',
                'event_id': event.target.getAttribute("event-id")
            };
            postPrototype('phpfiles/handleEvent', data, handleClickDelete);
        }, false);
    } else {
        alert(data.message);
    }
}

// get the member_id_list from the checkbox of the users name
function getMemberID() {

    let members = document.getElementsByName("username_list");
    let member_id_list = [];
    let index = 0;

    for (let i=0;i<members.length;i++) {
        if (members[i].checked) {
            member_id_list[index] = members[i].value;
            index++
        }
    }
    return member_id_list;

}

// show users when checked the isGroup checkbox
function showUsers() {
    const data = {};

    fetch('phpfiles/showUsers.php',{
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleShowUsers(data))
}

// get the data from the users and show in the modal
function handleShowUsers(data) {
    let userLine = document.createElement("div");
    userLine.setAttribute("id", "usersLine");

    let index = 0;
    while (index<data.user_length){
        let userCheckbox = document.createElement("input");
        userCheckbox.setAttribute("type", "checkbox");
        userCheckbox.setAttribute("name", "username_list");
        // userCheckbox.setAttribute("class", "form-control");
        userCheckbox.setAttribute("id", "userCheckbox-"+data.user_id_list[index]);
        userCheckbox.innerText = data.username_list[index];
        userCheckbox.setAttribute("value", data.user_id_list[index]);


        let userLabel = document.createElement("label");
        userLabel.setAttribute("for", "username_list");
        userLabel.innerText = data.username_list[index];
        userLine.appendChild(userCheckbox);
        userLine.appendChild(userLabel);
        userLine.appendChild(document.createTextNode('\u00A0\u00A0\u00A0'));
        index++;
    }

    document.getElementById("newEvent-modal-body").appendChild(userLine);
}

// TODO: 以上内容有添加或修改

function showNewEventModal() {
    document.getElementById("navigation-bar").appendChild(addModal("newEvent-modal"));

    // form group for title
    let bodyNode = document.getElementById("newEvent-modal-body");
    bodyNode.appendChild(
        addFormGroup(
            addLabel("title", "Title: "),
            addInput("text", "form-control", "title", "newEvent-title")
        )
    );

    // form group for description
    let inputNode = document.createElement("textarea");
    inputNode.setAttribute("class", "form-control");
    inputNode.setAttribute("rows", "5");
    inputNode.setAttribute("name", "description");
    inputNode.setAttribute("id", "newEvent-description");
    bodyNode.appendChild(
        addFormGroup(
            addLabel("description", "Description: "), inputNode
        )
    );

    bodyNode.appendChild(
        addFormGroup(
            addLabel("isGroup", "Is it a group event?"),
            addInput("checkbox", "", "isGroup", "isGroup-checkbox")
        )
    );

    // form group for date
    bodyNode.appendChild(
        addFormGroup(
            addLabel("date", "Date: "),
            addInput("date", "form-control", "date", "newEvent-date")
        )
    );

    // form group for time
    bodyNode.appendChild(
        addFormGroup(
            addLabel("time", "Time: "),
            addInput("time", "form-control", "time", "newEvent-time")
        )
    );

    // add submit button
    let footerNode = document.getElementById("newEvent-modal-footer");
    footerNode.insertBefore(
        addFormGroup(
            addNormalButton("newEvent-modal-footer-submit", "btn btn-primary", "submit")
        ),
        document.getElementById("newEvent-modal-close-button")
    );
}

function showLoginModal() {
    document.getElementById("navigation-bar").appendChild(
        addModal("login-modal"));
    let bodyNode = document.getElementById("login-modal-body");
    bodyNode.appendChild(
        addFormGroup(
            addLabel("e-mail", "E-mail:"),
            addInput("email", "form-control", "email", "login-email")
        )
    );
    bodyNode.appendChild(
        addFormGroup(
            addLabel("password", "Password:"),
            addInput("password", "form-control", "password", "password")
        )
    );

    let footerNode = document.getElementById("login-modal-footer");
    footerNode.insertBefore(
        addFormGroup(
            addNormalButton("login-modal-footer-submit", "btn btn-primary",
                "Submit")
        ),
        document.getElementById("login-modal-close-button")
    );

}

function showSignupModal() {
    document.getElementById("navigation-bar").appendChild(
        addModal("signup-modal"));

    let bodyNode = document.getElementById("signup-modal-body");
    bodyNode.appendChild(
        addFormGroup(
            addLabel("username", "Username:"),
            addInput("text", "form-control", "username", "username")
        )
    );
    bodyNode.appendChild(
        addFormGroup(
            addLabel("e-mail", "E-mail:"),
            addInput("email", "form-control", "email", "login-email")
        )
    );
    bodyNode.appendChild(
        addFormGroup(
            addLabel("password", "Password:"),
            addInput("password", "form-control", "password", "password")
        )
    );
    bodyNode.appendChild(
        addFormGroup(
            addLabel("re-password", "Re-Password:"),
            addInput("password", "form-control", "re-password", "re-password")
        )
    );

    let footerNode = document.getElementById("signup-modal-footer");
    footerNode.insertBefore(
        addFormGroup(
            addNormalButton("signup-modal-footer-submit", "btn btn-primary",
                "Submit")
        ),
        document.getElementById("signup-modal-close-button")
    );


}

function postLoginInfo() {
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("password").value;

    const data = {
        'event': 'login',
        'email': email,
        'password': password
    };
    fetch('phpfiles/login.php', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleLoginResponse(data))
        .catch(err => console.error(err));
}
function handleLoginResponse(data) {
    console.log(data.message);
    if (data.success) {

        let navigationBarNode = document.getElementById("navigation-bar");
        navigationBarNode.removeChild(
            document.getElementById("myModallogin-modal"));
        document.getElementById("root").parentNode.removeChild(
            document.getElementsByClassName("modal-backdrop fade show")[0]);

        checkLogin(handleCheckResponse);
    } else {
        alert(data.message);
    }
}

function postSignupInfo() {
    const username = document.getElementById("username").value;
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("password").value;
    let re_password = document.getElementById("re-password").value;
    if (password !== re_password) {
        alert("Error: Not identical password");
        return;
    }
    const data = {
        'event': 'signup',
        'username': username,
        'email': email,
        'password': password
    };
    fetch('phpfiles/signup.php', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleSignupResponse(data))
        .catch(err => console.error(err));
}

function handleSignupResponse(data){
    console.log(data.message);
    if (data.success) {
        let navigationBarNode = document.getElementById("navigation-bar");
        navigationBarNode.removeChild(
            document.getElementById("myModalsignup-modal"));
        document.getElementById("root").parentNode.removeChild(
            document.getElementsByClassName("modal-backdrop fade show")[0]);

        checkLogin(handleCheckResponse);

    } else {
        alert(data.message);
    }
}

function postLogout() {
    const data = {
        'event': 'logout'
    };
    fetch('phpfiles/logout.php', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleLogoutResponse(data))
        .catch(err => console.error(err));
}
function handleLogoutResponse(data) {
    console.log(data.message);
    if (data.success) {
        clearNavigationBar();
        document.getElementById("groups-view").innerHTML="";
        checkLogin(handleCheckResponse);
    } else {
        alert(data.message);
    }

}

function postNewEvent() {
    const event_title = document.getElementById("newEvent-title").value;
    const event_description = document.getElementById("newEvent-description").value;
    const event_date = document.getElementById("newEvent-date").value;
    const event_time = document.getElementById("newEvent-time").value;
    const data = {
        'action': 'newEvent',
        'event-title': event_title,
        'event-description': event_description,
        'event-date': event_date,
        'event-time': event_time
    };

    // alert('action: ' + 'newEvent' +
    //     'event-title' + event_title +
    //     'event-description' + event_description +
    //     'event-date' + event_date +
    //     'event-time' + event_time);
    //
    // //debug
    // alert("??");

    fetch('phpfiles/handleEvent', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleNewEventResponse(data))
        .catch(err => console.error(err))
}
function handleNewEventResponse(data) {
    console.log(data.message);
    if (data.success) {
        clearModal("myModalnewEvent-modal");
        updateEvents(data.event_date, handleUpdateEvents);
    } else {
        alert(data.message);
    }
}

// show tag, query the database
function showTag(event_id) {
    const data = {'event_id': event_id};

    fetch('phpfiles/showTag.php', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleShowTag(data,event_id))
        .catch(err => console.error(err))
}

// show list of data
function handleShowTag(data, event_id) {
    let tagLine = document.getElementById("tag-"+event_id+"-body");
    tagLine.innerHTML =  "";
    let addTag = addModalButton("addTag-button", "btn btn-primary", "addTag-modal", "+");
    addTag.addEventListener("click", function (event) {
        handleAddTag(event_id);
    },false);
    tagLine.appendChild(addTag);

    let tag_id_list = data.tag_id_list;
    let tag_content_list = data.tag_content_list;
    let length = tag_id_list.length;

    let index = 0;
    while (index<length){
        let singleTag = document.createElement("span");
        singleTag.innerText = "#"+tag_content_list[index]+" ";
        singleTag.setAttribute("event_id", event_id);
        singleTag.setAttribute("tag_id", tag_id_list[index]);

        // when click at certain tag, you can delete that tag.
        singleTag.addEventListener("click", function (event) {
            alert("Delete Tag");
            const data = {
                'action': 'deleteTag',
                'event_id': event_id,
                'tag_id': event.target.getAttribute('tag_id')
            };

            fetch('phpfiles/handleTag.php', {
                method: 'POST',
                body: JSON.stringify(data),
                headers: { 'content-type': 'application/json' }
            })
                .then(response => response.json())
                .then(jsondata => showTag(jsondata.event_id))
                .catch(err => console.error(err))
        }, false);
        tagLine.appendChild(singleTag);
        index++;
    }

}

// add tag function
function handleAddTag(event_id) {

    document.getElementById("navigation-bar").appendChild(addModal("addTag-modal"));

    let bodyNode = document.getElementById("addTag-modal-body");
    bodyNode.appendChild(
        addFormGroup(
            addLabel("tag", "Tag: "),
            addInput("text", "form-control", "tag", "addTag-tag")
        )
    );

    let footNode = document.getElementById("addTag-modal-footer");
    footNode.insertBefore(
        addFormGroup(
            addNormalButton("addTag-modal-footer-submit", "btn btn-primary", "submit")
        ),
        document.getElementById("addTag-modal-close-button")
    );

    document.getElementById("addTag-modal-footer-submit").addEventListener("click",
        function (event) {
            const tag_content = document.getElementById("addTag-tag").value;
            const data = {
                'action': 'addTag',
                'event_id': event_id,
                'tag_content': tag_content
            };

            fetch('phpfiles/handleTag.php', {
                method: 'POST',
                body: JSON.stringify(data),
                headers: { 'content-type': 'application/json' }
            })
                .then(response => response.json())
                .then(jsondata => showTag(jsondata.event_id))
                .catch(err => console.error(err))
            clearModal("myModaladdTag-modal");
        }, false);

    document.getElementById("addTag-modal-close-button").addEventListener("click", function () {
        document.getElementById("navigation-bar").removeChild(document.getElementById("myModaladdTag-modal"));
    },false);

}

function updateEvents(date, handleFunctionName) {

    const data = {
        'action': 'getEventsByDate',
        'date': date
    };

    fetch('phpfiles/showEvent.php', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => handleFunctionName(data))
        .catch(err => console.error(err))
}



function handleUpdateEvents(data) {
    if(data.success) {
        // alert(data.array);
        // return;

        let event_length = data.event_length;
        let index = 0;

        let rootNode = document.getElementById("events-view");
        rootNode.innerHTML = "<div class=\"col\" id=\"events-view\">--- event ---</div>";

        let containerNode = document.createElement("div");
        containerNode.setAttribute("id", "events-view-container");
        containerNode.setAttribute("class", "container");

        rootNode.appendChild(containerNode);

        while (index < event_length) {
            let newEventNode = addBasicCard("event-" + data.event_id_list[index], "card bg-info text-white");
            newEventNode.setAttribute("event-id",data.event_id_list[index]);
            containerNode.appendChild(newEventNode);

            let bodyNode = document.getElementById("event-" + data.event_id_list[index] + "-body");
            bodyNode.setAttribute("name",data.event_id_list[index]);

            let titleNode = document.createElement("h4");
            titleNode.setAttribute("event-id",data.event_id_list[index]);
            titleNode.setAttribute("class", "card-title");
            titleNode.innerText = data.event_title_list[index];


            let tagNode = document.createElement("p");
            tagNode.innerText="Tag";
            tagNode.style.visibility="visible";
            tagNode.setAttribute("event-id", data.event_id_list[index]);
            tagNode.setAttribute("id", "tag-"+data.event_id_list[index]+"-body");
            showTag(data.event_id_list[index]);

            let textNode = document.createElement("p");
            textNode.setAttribute("id", "event-" + data.event_id_list[index] + "-date");
            textNode.setAttribute("event-date", data.event_date_list[index]);
            textNode.setAttribute("event-id",data.event_id_list[index]);
            textNode.setAttribute("class", "card-text");
            textNode.innerText = data.event_date_list[index] + " " + data.event_time_list[index];

            bodyNode.appendChild(titleNode);
            bodyNode.appendChild(tagNode);
            bodyNode.appendChild(textNode);

            // click bodyNode to change the visibility of tag
            // bodyNode.addEventListener("click", function () {
            //     if (tagNode.style.visibility == "hidden"){
            //         tagNode.style.visibility = "visible";
            //     } else {
            //         tagNode.style.visibility = "hidden";
            //     }
            // }, false);

            containerNode.appendChild(document.createElement("br"));

            bodyNode.addEventListener("click", function (event) {
                getEventByEventID(event.target.getAttribute("event-id"), handleClickCard);
            }, false);

            bodyNode.addEventListener("mouseleave", function (event) {
                let id = event.target.getAttribute("event-id");

                let cardNode = document.getElementById("event-" + id);

                if (document.getElementById("event-" + id + "-footer")) {
                    let footerNode = document.getElementById("event-" + id + "-footer");
                    footerNode.removeChild(document.getElementById("event-" + id + "-modify"));
                    footerNode.removeChild(document.getElementById("event-" + id + "-delete"));
                    cardNode.removeChild(footerNode);
                }
            },false);
            newEventNode.addEventListener("mouseleave", function (event) {
                let id = event.target.getAttribute("event-id");

                let cardNode = document.getElementById("event-" + id);

                if (document.getElementById("event-" + id + "-footer")) {
                    let footerNode = document.getElementById("event-" + id + "-footer");
                    footerNode.removeChild(document.getElementById("event-" + id + "-modify"));
                    footerNode.removeChild(document.getElementById("event-" + id + "-delete"));
                    cardNode.removeChild(footerNode);
                }

                if (document.getElementById("event-" + id + "-body-description")) {
                    document.getElementById("event-" + id + "-body")
                        .removeChild(document.getElementById("event-" + id + "-body-description"));
                }
            },false);

            index++;
        }
    }
}

function getEventByEventID(event_id, responseFunctionName) {
    if (event_id == null) {
        console.log("event_id is null");
        return;
    }
    const data = {
        'action': 'getEventByEventID',
        'event_id': event_id
    };
    fetch('phpfiles/showEvent.php', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'content-type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => responseFunctionName(data))

}

function handleClickCard(data) {
    if (data.success) {

        let cardNode = document.getElementById("event-" + data.event_id);
        if (document.getElementById("event-" + data.event_id + "-footer")) {
            let footerNode = document.getElementById("event-" + data.event_id + "-footer");
            footerNode.removeChild(document.getElementById("event-" + data.event_id + "-modify"));
            footerNode.removeChild(document.getElementById("event-" + data.event_id + "-delete"));
            cardNode.removeChild(footerNode);
        }


        let cardFooterNode = document.createElement("div");
        cardFooterNode.setAttribute("id", "event-" + data.event_id + "-footer");
        cardFooterNode.setAttribute("class", "card-footer");

        cardFooterNode.appendChild(addModalButton("event-" + data.event_id + "-modify",
            "btn btn-primary", "modifyEvent-modal", "Modify"));


        cardFooterNode.appendChild(addNormalButton("event-" + data.event_id + "-delete",
            "btn btn-danger", "Delete"));

        cardNode.appendChild(cardFooterNode);

        document.getElementById("event-" + data.event_id + "-modify")
            .setAttribute("event-id", data.event_id);
        document.getElementById("event-" + data.event_id + "-delete")
            .setAttribute("event-id", data.event_id);

        let bodyNode = document.getElementById("event-" + data.event_id + "-body");
        if (document.getElementById("event-" + data.event_id + "-body-description"))
            bodyNode.removeChild(document.getElementById("event-" + data.event_id + "-body-description"));

        let textNode = document.createElement("p");
        textNode.setAttribute("id", "event-" + data.event_id + "-body-description");
        textNode.setAttribute("class", "card-text");
        textNode.innerText = data.event_description;
        bodyNode.appendChild(textNode);

        showModifyEventModal(data);

        document.getElementById("event-" + data.event_id + "-delete").addEventListener("click", function (event) {
            const data = {
                'action': 'deleteEvent',
                'event_id': event.target.getAttribute("event-id")
            };
            postPrototype('phpfiles/handleEvent', data, handleClickDelete);
        }, false);



    } else {
        alert(data.message);
    }
}
function showModifyEventModal(data) {
    if (document.getElementById("myModalmodifyEvent-modal")) return;

    document.getElementById("navigation-bar").appendChild(addModal("modifyEvent-modal"));


    // form group for title
    let bodyNode = document.getElementById("modifyEvent-modal-body");
    bodyNode.appendChild(
        addFormGroup(
            addLabel("title", "Title: "),
            addInput("text", "form-control", "title", "modifyEvent-title")
        )
    );
    document.getElementById("modifyEvent-title").setAttribute("value", data.event_title);

    // form group for description
    let inputNode = document.createElement("textarea");
    inputNode.setAttribute("class", "form-control");
    inputNode.setAttribute("rows", "5");
    inputNode.setAttribute("name", "description");
    inputNode.setAttribute("id", "modifyEvent-description");
    bodyNode.appendChild(
        addFormGroup(
            addLabel("description", "Description: "), inputNode
        )
    );
    document.getElementById("modifyEvent-description").innerText = data.event_description;

    // form group for date
    bodyNode.appendChild(
        addFormGroup(
            addLabel("date", "Date: "),
            addInput("date", "form-control", "date", "modifyEvent-date")
        )
    );
    document.getElementById("modifyEvent-date").setAttribute("value",
        String(data.event_date));

    // form group for time
    bodyNode.appendChild(
        addFormGroup(
            addLabel("time", "Time: "),
            addInput("time", "form-control", "time", "modifyEvent-time")
        )
    );
    let time = String(data.event_time).split(':');
    document.getElementById("modifyEvent-time").setAttribute("value", time[0] + ":" + time[1]);

    // add submit button
    let footerNode = document.getElementById("modifyEvent-modal-footer");
    footerNode.insertBefore(
        addFormGroup(
            addNormalButton("modifyEvent-modal-footer-submit", "btn btn-primary", "submit")
        ),
        document.getElementById("modifyEvent-modal-close-button")
    );
    document.getElementById("modifyEvent-modal-footer-submit").setAttribute("event-id", data.event_id);

    document.getElementById("modifyEvent-modal-footer-submit").addEventListener("click", function (event) {
        const event_title = document.getElementById("modifyEvent-title").value;
        const event_description = document.getElementById("modifyEvent-description").value;
        const event_date = document.getElementById("modifyEvent-date").value;
        const event_time = document.getElementById("modifyEvent-time").value;
        const data = {
            'action': 'modifyEvent',
            'event-id': event.target.getAttribute("event-id"),
            'event-title': event_title,
            'event-description': event_description,
            'event-date': event_date,
            'event-time': event_time
        };
        postPrototype('phpfiles/handleEvent.php', data, handleClickModify);
    }, false);

    document.getElementById("modifyEvent-modal-close-button").addEventListener("click", function () {
        document.getElementById("navigation-bar").removeChild(
            document.getElementById("myModalmodifyEvent-modal"))
    }, false);
}


function handleClickModify(data) {
    if (data.success) {
        clearModal("myModalmodifyEvent-modal");
        updateEvents(data.event_date, handleUpdateEvents);

    } else {
        alert("?" + data.message);
    }


}
function handleClickDelete(data) {
    if (data.success) {
        let node = document.getElementById("event-" + data.event_id + "date");
        alert("delete");
        //TODO: 这里加了一句
        postGroupEvents();
        updateEvents(node.getAttribute("event-date"), handleUpdateEvents);
        return;
    } else {
        alert(data.message);
    }


}

