document.addEventListener("DOMContentLoaded", addbutton, false)
function addbutton() {
    let rootNode = document.getElementById("root");
    let newNode = document.createElement("button");
    newNode.setAttribute("id", "login_button");
    newNode.setAttribute("type", "button");
    newNode.setAttribute("class", "btn btn-primary");
    newNode.setAttribute("data-toggle", "modal");
    newNode.setAttribute("data-target", "#myModal1");
    newNode.innerText = "Log in";
    rootNode.appendChild(newNode);
    document.getElementById("login_button").addEventListener("click", handleLoginButton, false);
}

function handleLoginButton() {
    let rootNode = document.getElementById("root");
    let newNode = document.createElement("div");
    newNode.setAttribute("class", "modal fade");
    newNode.setAttribute("id", "myModal1");
    newNode.innerHTML = "<div class=\"modal-dialog\">\n" +
        "      <div class=\"modal-content\">\n" +
        "   \n" +
        "        <!-- 模态框头部 -->\n" +
        "        <div class=\"modal-header\">\n" +
        "          <h4 class=\"modal-title\">模态框头部</h4>\n" +
        "          <button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>\n" +
        "        </div>\n" +
        "   \n" +
        "        <!-- 模态框主体 -->\n" +
        "        <div class=\"modal-body\">\n" +
        "          模态框内容..\n" +
        "        </div>\n" +
        "   \n" +
        "        <!-- 模态框底部 -->\n" +
        "        <div class=\"modal-footer\">\n" +
        "          <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">关闭</button>\n" +
        "        </div>\n" +
        "   \n" +
        "      </div>\n";
    rootNode.appendChild(newNode);




}