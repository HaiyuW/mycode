<?php
session_start();
$isLoggedin = false;
if (isset($_SESSION['username'])
    && isset($_SESSION['id'])
    && isset($_SESSION['email_address'])) {
    $username = $_SESSION['username'];
    $id = $_SESSION['id'];
    $email = $_SESSION['email_address'];
    $isLoggedin = true;
} else {
    echoAlert("Error: missing user_id in manageNews.php");
    exit();
}
require 'tools.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Your News</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-1"></div>
        <div class="col-11">
            <br>
            <h1>Manage Your News</h1>
            <br>
        </div>
    </div>
    <div class="row">
        <div class="col-1"></div>
        <div class="col-8">
            <?php
            if (isset($_REQUEST['delete']) && isset($_POST['news_id'])) {

                if(!hash_equals($_SESSION['token'], $_POST['token'])){
                    die("Request forgery detected");
                }

                $news_id = $_POST['news_id'];
                require 'database.php';
                global $mysqli;

                // delete news_id from likes
                $stmt = $mysqli->prepare("
                    DELETE FROM likes WHERE news_id = ?
                ");
                if(!$stmt) {
                    echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
                }
                $stmt->bind_param('s', $news_id);
                $stmt->execute();
                $stmt->close();

                // delete news_id from collections
                $stmt = $mysqli->prepare("
                    DELETE FROM collections WHERE news_id = ?
                ");
                if(!$stmt) {
                    echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
                }
                $stmt->bind_param('s', $news_id);
                $stmt->execute();
                $stmt->close();

                // delete news_id from links
                $stmt = $mysqli->prepare("
                    DELETE FROM links WHERE news_id = ?
                ");
                if(!$stmt) {
                    echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
                }
                $stmt->bind_param('s', $news_id);
                $stmt->execute();
                $stmt->close();

                // delete news_id from comments
                $stmt = $mysqli->prepare("
                    DELETE FROM comments WHERE news_id = ?
                ");
                if(!$stmt) {
                    echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
                }
                $stmt->bind_param('s', $news_id);
                $stmt->execute();
                $stmt->close();

                // delete news_id from news
                $stmt = $mysqli->prepare("
                    DELETE FROM news WHERE news_id = ?
                ");
                if(!$stmt) {
                    echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
                }
                $stmt->bind_param('s', $news_id);
                $stmt->execute();
                $stmt->close();

                header("refresh:1;url=" . $_SERVER['HTTP_REFERER']);
                exit();
                }
                showContent($id);
            ?>

        </div>
        <div class="col-3">
            <?php showSideBar($isLoggedin); ?>
            <a href="mainPage.php" class="btn btn-info" role="button">Back</a>
        </div>


    </div>
</div>

</body>
</html>

<?php

function showContent($id) {
    require 'database.php';
    global $mysqli;

    $stmt = $mysqli->prepare("
        SELECT news.news_id, news_creator_id, news_title, news_created_time, users.username, links.link
        FROM news LEFT JOIN users ON news.news_creator_id = users.id 
        LEFT JOIN links ON news.news_id = links.news_id
        WHERE news.news_creator_id = ?
    ");
    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }
    $stmt->bind_param('s', $id);
    $stmt->execute();
    $stmt->bind_result($news_id, $news_creator_id, $news_title, $news_created_time, $creator_name, $link);
    // show news card
    while($stmt->fetch()) {
        echo sprintf('
            <div class="row">
            <div class="col-7">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">%s</h5>
                        <p class="card-text">
                            Source Link: <a href="%s">%s</a><br>
                            Created by: %s at %s.
                        </p>

                        
                    </div>
                </div>
            </div>
            <div class="col-2">
                <div class="btn-group-vertical">
                <!--Edit News Button -->
                <form action="editNews.php" method="POST">
                    <input type="hidden" name="news_id" value="%s">
                    <input type="submit" class="btn btn-info" value="Edit">
                </form>
                <!--View News Button -->
                <form action="viewNews.php" method="POST">
                    <input type="hidden" name="news_id" value="%s">
                    <input type="submit" class="btn btn-info" value="View">
                </form>
                <!--Delete News Button -->
                <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#deleteModal_%s">
                    delete
                </button>
                </div>
            </div>
            </div>
        ', $news_title, $link, $link, $creator_name, $news_created_time, $news_id, $news_id, $news_id);

        // show delete Modal and ask confirmation
        echo sprintf('
            <div class="modal fade" id="deleteModal_%s">
                <div class="modal-dialog">
                    <div class="modal-content">
       
                    <div class="modal-header">
                        <h4 class="modal-title">! YOU WILL DELETE THIS NEWS. ARE YOU SURE?</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
       
                    <div class="modal-body">
                        <div class="btn-group">
                            <form action="%s" method="POST">
                                <input type="hidden" name="news_id" value="%s">
                                <input type="hidden" name="token" value="%s" />
                                <button type="submit" name="delete" class="btn btn-primary">Confirm</button>                            
                            </form>
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                    </div>
                </div>
            </div>
        ',$news_id, htmlentities($_SERVER['PHP_SELF']), $news_id, $_SESSION['token']);
    }
    $stmt->close();
}
?>