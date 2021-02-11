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
}
require 'tools.php';
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <title>Title</title>
</head>
<body>
<div class="row">
    <div class="col-9">
        <?php
            showContent($isLoggedin, $id);
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

function showContent($isLoggedin, $id) {
    if (isset($_POST['news_id'])) {
        $news_id =  $_POST['news_id'];
        $_SESSION['current_news_id'] = $news_id;
    } else if (isset($_SESSION['current_news_id'])){
        $news_id = $_SESSION['current_news_id'];
    } else {
        echoAlert("Error: missing news_id");
        return;
    }
    require 'database.php';
    global $mysqli;


    // show news
    $stmt = $mysqli->prepare("
        SELECT news.news_id, news_creator_id, news_title, news_content, news_created_time, users.username, links.link
        FROM news LEFT JOIN users ON news.news_creator_id = users.id 
        LEFT JOIN links ON news.news_id = links.news_id
        WHERE (news.news_id = ?)
    ");
    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }

    $stmt->bind_param('s', $news_id);
    $stmt->execute();
    $stmt->bind_result($news_id, $news_creator_id, $news_title, $news_content, $news_created_time, $creator_name, $link);
    $stmt->fetch();
    echo sprintf('
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">%s</h5>
                <p class="card-text">
                    Source Link: <a href="%s">%s</a><br>
                    Created by: %s at %s.
                </p><br>
                <p class="card-text">%s</p>
                
            </div>
        </div>
    ', $news_title, $link, $link, $creator_name, $news_created_time, $news_content);
    $stmt->close();



    if ($isLoggedin) {

        echo sprintf('
            <div class="btn-group">
            <button type="button" role="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal">
                New Comment
            </button>
            <div class="like">
                <form action="%s" method="POST">
                    <input type="hidden" name="news_id" value="%s">
                    <input type="submit" role="button" class="btn btn-info" value="Like: %s" name="like">
                </form> 
            </div>
            <div class="collection">
                <form action="%s" method="POST">
                    <input type="hidden" name="news_id" value="%s">
                    <input type="submit" role="button" class="btn btn-info" value="Collection: %s" name="collection">
                </form> 
            </div>
            </div>
            
        ', htmlentities($_SERVER['PHP_SELF']), $news_id, likeNum($id,$news_id), htmlentities($_SERVER['PHP_SELF']),$news_id, collectionNum($id,$news_id));

        echo sprintf('
            <div class="modal fade" id="myModal">
                <div class="modal-dialog">
                    <div class="modal-content">
       
                    <div class="modal-header">
                        <h4 class="modal-title">Please input your comment:</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
       
                    <div class="modal-body">
                        <form action="%s" method="POST">
                            <div class="form-group">
                                <label for="title">Comments:</label>
                                <input type="text" class="form-control" name="comment_content" id="comment">
                            </div>
                            <button type="submit" name="submit" class="btn btn-primary">Submit</button>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    </div>
       
                    </div>
                </div>
            </div>
        ', htmlentities($_SERVER['PHP_SELF']));

        postNewComment($news_id, $id);

//        echo sprintf('
//            <form action="%s" method="POST">
//                <input type="hidden" name="news_id" value="%s">
//                <button type="submit" class="btn btn-primary">New Comment</button>
//            </form>
//        ',  htmlentities($_SERVER['PHP_SELF']), $news_id);
    }
    // show comments
    $stmt = $mysqli->prepare("
        SELECT COUNT(*)
        FROM comments LEFT JOIN news ON (news.news_id = comments.news_id)
        WHERE (comments.news_id = ?)
    ");


    if(!$stmt) {
        echoAlert(fprintf("Something wrong during preparing query: %s", $mysqli->error));
        exit();
    }
    $stmt->bind_param('s', $news_id);
    $stmt->execute();
    $stmt->bind_result($count);
    $stmt->fetch();
    $stmt->close();


    $stmt = $mysqli->prepare("
        SELECT comments.comment_id, comments.comment_creator_id, comments.comment_content, comments.comment_created_time, users.username
        FROM comments LEFT JOIN news ON (news.news_id = comments.news_id)
        LEFT JOIN users ON (comments.comment_creator_id = users.id)
        WHERE (comments.news_id = ?)
    ");


    if(!$stmt) {
        echoAlert(fprintf("Something wrong during preparing query: %s", $mysqli->error));
        exit();
    }
    $stmt->bind_param('s', $news_id);
    $stmt->execute();


    $stmt->bind_result( $comment_id, $comment_creator_id, $comment_content, $comment_created_time, $comment_creator_username);

    while ($stmt->fetch()) {
        if ($count == 0) {
            $stmt->close();
            return;
        }
        echo sprintf('
            <div class="card">
                <div class="card-body">
                    <p class="card-text">%s: %s</p>
                    <p class="card-text">(%s)</p>
                </div>
            </div>
        
        ', $comment_creator_username, $comment_content, $comment_created_time);
    }
    $stmt->close();

}

function postNewComment($news_id, $id) {

    if (isset($_POST['submit']) && isset($_POST['comment_content'])) {
        $comment_content = $_POST['comment_content'];
        require 'database.php';
        global $mysqli;
        $stmt = $mysqli->prepare("
            INSERT INTO comments (comment_creator_id, news_id, comment_content, comment_created_time)
            VALUES (?, ?, ?, ?)
        ");

        if(!$stmt) {
            echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
        }

        $date = sprintf(date("Y-m-d H:i:s"));

        $stmt->bind_param('ssss', $id, $news_id, $comment_content, $date);
        $stmt->execute();
        $stmt->close();
        header("Location: " . $_SERVER['HTTP_REFERER']);
    }
}

function likeNum($id, $news_id){
    require 'database.php';
    global $mysqli;


    $stmt = $mysqli->prepare("select
                                        like_created_time
                                    from likes
                                    where news_id=? and who_like_id=?");
    $stmt->bind_param('ss',$news_id,$id);

    $stmt->execute();
    $stmt->bind_result($like_created_time);

    $stmt->fetch();
    $stmt->close();


    if ($like_created_time==null && isset($_POST['like'])) {
        handleLike($id,$news_id,false);
    }else if ($like_created_time!=null && isset($_POST['like'])){
        handleLike($id,$news_id,true);
    }

    $stmt = $mysqli->prepare("SELECT COUNT(*)
                                    FROM likes 
                                    WHERE news_id=?");

    $stmt->bind_param('s',$news_id);
    $stmt->execute();
    $stmt->bind_result($count);

    $stmt->fetch();
    $stmt->close();

    return $count;
}

function handleLike($id,$news_id,$isLike){
    require 'database.php';
    global $mysqli;
    if (!$isLike){
        $stmt = $mysqli->prepare("insert into likes (news_id, who_like_id, like_created_time) values (?,?,now())");
    } else {
        $stmt = $mysqli->prepare("delete from likes where news_id=? and who_like_id=?");
    }

    $stmt->bind_param('ss',$news_id,$id);

    $stmt->execute();
    $stmt->close();
}

function collectionNum($id,$news_id) {
    require 'database.php';
    global $mysqli;


    $stmt = $mysqli->prepare("select
                                        collection_created_time
                                    from collections
                                    where news_id=? and collection_owner_id=?");
    $stmt->bind_param('ss',$news_id,$id);

    $stmt->execute();
    $stmt->bind_result($collection_created_time);

    $stmt->fetch();
    $stmt->close();


    if ($collection_created_time==null && isset($_POST['collection'])) {
        handleCollection($id,$news_id,false);
    }else if ($collection_created_time!=null && isset($_POST['collection'])){
        handleCollection($id,$news_id,true);
    }

    $stmt = $mysqli->prepare("SELECT COUNT(*)
                                    FROM collections 
                                    WHERE news_id=?");

    $stmt->bind_param('s',$news_id);
    $stmt->execute();
    $stmt->bind_result($count);

    $stmt->fetch();
    $stmt->close();

    return $count;
}

function handleCollection($id, $news_id, $isCollection) {
    require 'database.php';
    global $mysqli;
    if (!$isCollection){
        $stmt = $mysqli->prepare("insert into collections (news_id, collection_owner_id, collection_created_time) values (?,?,now())");
    } else {
        $stmt = $mysqli->prepare("delete from collections where news_id=? and collection_owner_id=?");
    }

    $stmt->bind_param('ss',$news_id,$id);

    $stmt->execute();
    $stmt->close();
}


function showUserSideBar() {

}
function showGuestSideBar() {

}
?>