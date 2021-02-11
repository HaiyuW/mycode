<?php
// to do list:
// * find a method about news_id
session_start();
$isLoggedin = false;
if (isset($_SESSION['username'])
    && isset($_SESSION['id'])
    && isset($_SESSION['email_address'])) {
    $username = $_SESSION['username'];
    $id = $_SESSION['id'];
    $email = $_SESSION['email_address'];
}
require 'tools.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
</head>
<body>
<div class="row">
    <br><br>
</div>
<div class="row">
<div class="container-fluid">
    <div class="row">
        <div class="col-1"></div>
        <div class="col-9">
            <form action="<?php echo htmlentities($_SERVER['PHP_SELF']); ?>" method="POST">
                <div class="form-group">
                    <label for="title">Title:</label>
                    <input type="text" class="form-control" name="title" id="title">
                </div>
                <div class="form-group">
                    <label for="link">Source Link:</label>
                    <input type="text" class="form-control" name="link" id="link">
                </div>
                <div class="form-group">
                    <label for="content">Contents:</label>
                    <textarea class="form-control" rows="5" name="content" id="content"></textarea>
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
                <a href="mainPage.php" class="btn btn-info" role="button">Cancel</a>

            </form>

        </div>
        <div class="col-3">

        </div>

    </div>

</div>
</div>

</body>
</html>
<?php
if (isset($_POST['title']) && isset($_POST['link'])
    && isset($_POST['content'])) {
    $title = $_POST['title'];
    $link = $_POST['link'];
    $content = $_POST['content'];

    require  'database.php';
    global $mysqli;

    // insert (news_creator_id, news_title, news_created_time) into news table
    $stmt = $mysqli->prepare("
        INSERT INTO news (news_creator_id, news_title, news_content, news_created_time)
        VALUES (?, ?, ?, ?)");

    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }

    $stmt->bind_param('ssss', $id, $title, $content, date("Y-m-d H:i:s"));
    $stmt->execute();
    $stmt->close();

    //search news_id
    $stmt = $mysqli->prepare("
        SELECT news_id FROM news WHERE (news_creator_id = ? and news_title = ?)
    ");

    if(!$stmt) {
        echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
    }

    $stmt->bind_param('ss', $id, $title);
    $stmt->execute();
    $stmt->bind_result($news_id);
    $stmt->fetch();
    $stmt->close();

    // insert
    $stmt = $mysqli->prepare("
        INSERT INTO links (news_id, link) VALUES (?, ?)
    ");
     if (!$stmt) {
         echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
     }
     $stmt->bind_param('ss', $news_id, $link);
     $stmt->execute();
     $stmt->close();

     header("Location: mainPage.php");
     exit();
}
?>
