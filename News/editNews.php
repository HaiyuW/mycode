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
    <title>Edit News</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</head>
<body>
<!--show title-->
<div class="row">
    <div class="col-1"></div>
    <div class="col-11">
        <br>
        <h1>Edit Your News</h1>
        <br><br>
    </div>
</div>
<div class="row">
    <div class="container-fluid">
        <div class="row">
            <div class="col-1"></div>
            <div class="col-9">
<?php
if (isset($_REQUEST['submit']) && isset($_POST['title'])
    && isset($_POST['link']) && isset($_SESSION['news_id'])
    && isset($_POST['content'])) {

    // detect request forgery
    if(!hash_equals($_SESSION['token'], $_POST['token'])){
        die("Request forgery detected");
    }

    // get title, link, content from the form, get news_id from session
    $title = $_POST['title'];
    $link = $_POST['link'];
    $content = $_POST['content'];
    $news_id = $_SESSION['news_id'];

    $str_length = strlen($title);
    if ($str_length  <= 0 || $str_length  > 255) {
        exit("title cannot be null or larger than 255 characters. Your title length is: " . $str_length );
    }
    $str_length = strlen($link);
    if ($str_length  <= 0 || $str_length  > 255) {
        exit("link cannot be null or larger than 255 characters. Your link length is: " . $str_length );
    }

    require  'database.php';
    global $mysqli;

    // update news
    $stmt = $mysqli->prepare("
            UPDATE news SET news_title=?, news_content=?, news_created_time=?
            WHERE news.news_id=?
        ");

    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }
    $date = date("Y-m-d H:i:s");
    $stmt->bind_param('ssss', $title, $content, $date, $news_id);
    $stmt->execute();
    $stmt->close();

    //update links
    $stmt = $mysqli->prepare("
            UPDATE links SET link=?
            WHERE links.news_id=?
        ");

    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }
    $stmt->bind_param('ss', $link, $news_id);
    if(!$stmt->execute()) echoAlert($mysqli->error);
    $stmt->close();

    header("refresh:1;url=http://ec2-13-58-248-18.us-east-2.compute.amazonaws.com/~lbx/module3-group-475532-475533/manageNews.php");
    exit();

}
findNews();
?>
            </div>
        </div>
    </div>
</div>

</body>
</html>
<?php
// query the database and find the news by news_id
function findNews() {
    if (isset($_POST['news_id'])) {

        require 'database.php';
        global $mysqli;
        $news_id = $_POST['news_id'];
        $_SESSION['news_id'] = $news_id;

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
        if (!$stmt->fetch()) {
            echoAlert("Error: didn't find news");
            return;
        }
        echo sprintf('
            <form action="%s" method="POST">
                <div class="form-group">
                    <label for="title">Title:</label>
                    <input type="text" class="form-control" name="title" value="%s" id="title">
                </div>
                <div class="form-group">
                    <label for="link">Source Link:</label>
                    <input type="text" class="form-control" name="link" value="%s" id="link">
                </div>
                <div class="form-group">
                    <label for="content">Contents:</label>
                    <textarea class="form-control" rows="5" name="content" id="content">%s</textarea>
                </div>
                <input type="hidden" name="token" value="%s" />
                <button type="submit" name="submit" class="btn btn-primary">Submit</button>
                <a href="manageNews.php" class="btn btn-info" role="button">Cancel</a>
            </form>
        ', htmlentities($_SERVER['PHP_SELF']), $news_title, $link, $news_content, $_SESSION['token']);
        $stmt->close();
    } else if (isset($_SESSION['news_id'])) {
        $news_id = $_SESSION['news_id'];
    } else {
        echoAlert("Error: missing news_id in findNews()");
        return;
    }
}

?>