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
    echoAlert("Error: missing user_id in manageComments.php");
}
require 'tools.php';

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Comments</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</head>
<body>

<div class="row">
    <div class="col-9">
        <?php
            $id = $_SESSION['id'];

            if (isset($_REQUEST['submit']) && isset($_POST['comment_id']) && isset($_POST['comment_content'])){
                $comment_id = $_POST['comment_id'];
                $comment_content = $_POST['comment_content'];
                require 'database.php';
                global $mysqli;

                $stmt = $mysqli->prepare("
                    UPDATE comments SET comment_content=?, comment_created_time=?
                    WHERE comment_id=?
                ");
                if(!$stmt) echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));

                $stmt->bind_param('sss', $comment_content, date("Y-m-d H:i:s"), $comment_id);
                $stmt->execute();
                $stmt->close();

                header( 'Location: ' . $_SERVER['HTTP_REFERER']);

            }

            if (isset($_REQUEST['delete']) && isset($_POST['comment_id'])) {
                $comment_id = $_POST['comment_id'];
                require 'database.php';
                global $mysqli;

                $stmt = $mysqli->prepare("
                    DELETE FROM comments WHERE comment_id = ?
                ");
                if(!$stmt) echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
                $stmt->bind_param('s', $comment_id);
                $stmt->execute();
                $stmt->close();

                header( 'Location: ' . $_SERVER['HTTP_REFERER']);
            }
            showComments($id);
        ?>

    </div>
    <div class="col-3">
        <?php showSideBar($isLoggedin); ?>
        <a href="mainPage.php" class="btn btn-info" role="button">Back</a>
    </div>
</div>

</body>
</html>
<?php

function showComments($id) {

    require 'database.php';
    global $mysqli;

    $stmt = $mysqli->prepare("
        SELECT comments.comment_id, comments.comment_content, comments.comment_created_time, news.news_id, news.news_title
        FROM comments LEFT JOIN news ON (comments.news_id = news.news_id)
        WHERE comments.comment_creator_id = ?
    ");
    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }
    $stmt->bind_param('s', $id);
    $stmt->execute();
    $stmt->bind_result($comment_id, $comment_content, $comment_created_time, $news_id, $news_title);
    while ($stmt->fetch()) {
        echo sprintf('
            <div class="col">
                <div class="row">
                    <div class="btn-group">
                    <form action="%s" method="POST">
                        <input type="hidden" name="comment_id" value="%s">
                        <button type="button" class="btn btn-info" data-toggle="modal" data-target="#myModal">Edit</button>
                        <div class="modal fade" id="myModal">
                            <div class="modal-dialog">
                                <div class="modal-content">
                   
                                <div class="modal-header">
                                    <h4 class="modal-title">Please Edit your comment:</h4>
                                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                                </div>
                   
                                <div class="modal-body">
                                    <div class="form-group">
                                        <label for="title">Comments:</label>
                                        <input type="text" class="form-control" name="comment_content" id="comment" value="%s">
                                    </div>
                                    <button type="submit" name="submit" class="btn btn-primary">Submit</button>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                </div>
                   
                                </div>
                            </div>
                        </div>  
                    </form>
                    <form action="viewNews.php" method="POST">
                        <input type="hidden" name="news_id" value="%s">
                        <input type="submit" class="btn btn-info" value="View News">
                    </form>
                    <form action="%s" method="POST">
                        <input type="hidden" name="comment_id" value="%s">
                        <input type="submit" class="btn btn-info" name="delete" value="Delete">
                    </form>
                    </div>
                </div>
                <div class="row">
                    <div class="card">
                        <div class="card-body">
                            <p class="card-text">
                                You have a comment under news: %s<br>
                                at %s<br>
                                Your comment is: %s
                            </p>     
                        </div>
                    </div>
                </div>

            </div>
            <br><br><br>            

            
            
        ',  htmlentities($_SERVER['PHP_SELF']), $comment_id, $comment_content, $news_id, htmlentities($_SERVER['PHP_SELF']),
            $comment_id, $news_title, $comment_created_time, $comment_content);

    }
    $stmt->close();

}

?>