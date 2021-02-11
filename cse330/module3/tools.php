<?php
function echoAlert($message) {
    echo sprintf("
        <div class=\"alert alert-success alert-dismissible\">
            <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>
        %s</div>", $message);
    sleep(3);
}

function assembleLinks($linkList) {
    $html = '';
    foreach ($linkList as $aLink) {
        $html . fprintf('<a href="%s" class="card-link">%s</a><br>', $aLink, $aLink);
    }

}

function showSideBar($isLoggedin) {
    if ($isLoggedin) {
        echo '<br><h5>Hello: ' . $_SESSION['username'] . '</h5><br>';
        showManageBar();
        showLogOut();
    } else {
        echo '
            <br>
            <a href="login.php" class="btn btn-info" role="button">Log in</a><br>
            <p>Don\'t have an account?</p><br>
            <a href="register.php" class="btn btn-info" role="button"> Sign Up!</a>';

    }
}

function showManageBar() {
    $html = sprintf('
        <div class="btn-group-vertical">
            <a href="postNews.php" role="button" class="btn btn-primary">New Post</a>
        </div>
        <br>
        <div class="btn-group-vertical">
            <a href="manageNews.php" role="button" class="btn btn-primary">Manage Your News</a>
        </div>
        <br>
        <div class="btn-group-vertical">
            <a href=manageComments.php role="button" class="btn btn-primary">Manage Your Comments</a>
        </div>
        <br>
                <div class="btn-group-vertical">
            <a href=manageCollections.php role="button" class="btn btn-primary">Manage Your Collections</a>
        </div>
        <br>
        
    ');
    echo $html;
}

function showLogOut() {
    $html = '
        <br>
        <form action="' . htmlentities($_SERVER['PHP_SELF']) . '" method="POST">
            <p>
                <input type="submit" class="btn btn-info" role="button" value="log out" name="logout">
            </p>
        </form>
        ';
    echo $html;
    if (isset($_POST['logout'])) {
        session_destroy();
        header("Location: mainPage.php");
        exit();
    }
}
?>
