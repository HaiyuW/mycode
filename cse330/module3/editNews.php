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

<?php
    findNews();
?>

</body>
</html>
<?php
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
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
        ', htmlentities($_SERVER['PHP_SELF']), $news_title, $link, $news_content);
        $stmt->close();
    } else if (isset($_SESSION['news_id'])) {
        $news_id = $_SESSION['news_id'];
    } else {
        echoAlert("Error: missing news_id in findNews()");
        return;
    }
    editNews($news_id);
}

function editNews($news_id) {
    if (isset($_POST['title']) && isset($_POST['link'])
        && isset($_POST['content'])) {
        $title = $_POST['title'];
        $link = $_POST['link'];
        $content = $_POST['content'];

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

        $stmt->bind_param('ssss', $title, $content, date("Y-m-d H:i:s"), $news_id);
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
        $stmt->execute();
        $stmt->close();

        header("Location: manageNews.php");

    }

}
?>