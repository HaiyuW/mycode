<?php

// if session has not existed start session
// else copy username
session_start();
$isLoggedin = false;
if (isset($_SESSION['username'])
    && isset($_SESSION['id'])
    && isset($_SESSION['email_address'])) {
    $username = $_SESSION['username'];
    $id = $_SESSION['id'];
    $email = $_SESSION['email_address'];
    $isLoggedin = true;
}
require 'tools.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>main page</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
</head>
<body>
<?php showNavigationBar($isLoggedin); ?>
<div class="row">
    <div class="col-9">
        <?php
            showMainBlock($isLoggedin);
            showContent();
        ?>

    </div>
    <div class="col-3">
        <?php
            showSideBar($isLoggedin);
        ?>


    </div>
</div>




</body>
</html>

<?php

function showNavigationBar($isLoggedin) {
    if (!$isLoggedin) return;
    $html = sprintf('

    ');


}

function showContent() {
    require 'database.php';
    global $mysqli;

    $stmt = $mysqli->prepare("
        SELECT news.news_id, news_creator_id, news_title, news_created_time, users.username, links.link
        FROM news LEFT JOIN users ON news.news_creator_id = users.id 
        LEFT JOIN links ON news.news_id = links.news_id
    ");
    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }

    $stmt->execute();
    $stmt->bind_result($news_id, $news_creator_id, $news_title, $news_created_time, $creator_name, $link);
    while($stmt->fetch()) {
        echo sprintf('
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">%s</h5>
                    <p class="card-text">
                        Source Link: <a href="%s">%s</a><br>
                        Created by: %s at %s.
                    </p>
                    <form action="viewNews.php" method="POST">
                        <input type="hidden" name="news_id" value="%s">
                        <input type="submit" class="btn btn-info" value="view">
                    </form>
                    
                </div>
            </div>
        ', $news_title, $link, $link, $creator_name, $news_created_time, $news_id);
    }
    $stmt->close();
}

function showMainBlock($isLoggedin) {

}



function showTitleCard($title, $news_id) {
    $html = sprintf('
        <div class="card">
            <div class="card-body">
                <p class="card-text">%s</p>
                <form action="viewNews.php">
                    <input type="hidden" name="news_id" value="%s">
                    <input type="submit" class="btn btn-info" value="view">
                </form>              
            </div>
        </div>
    ', $title, $news_id);
    echo $html;
}






?>