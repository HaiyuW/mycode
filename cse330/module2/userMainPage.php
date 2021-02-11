<?php
session_start();
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>user main page</title>
</head>
<body>
<!-- Check if the user exists, if so show the mainpage -->
<?php
if (checkUserExist()) {
    showUpload($_GET['username']);
}
?>

<!-- Create new folder -->
<form action="<?php echo htmlentities($_SERVER['PHP_SELF'].'?'.'username='.$_GET['username']); ?>" method="POST">
    <p>
        <label for="newFolderName">Filename:</label>
        <input type="text" name="newFolderName" id="newFolderName">
        <input type="submit" value="new folder" name="newFolder"> 
    </p>
</form>
<?php
if (isset($_POST['newFolder'])) {
    $path = "/srv/uploads/" . $_GET['username'] . "/" . $_POST['newFolderName'] . "/";
    mkdir($path,0777);
}
?>

<!-- Go back to login page -->
<form action="login.php" method="POST">
    <p>
        <input type="submit" value="Back" name="back"> 
    </p>
</form>
<?php
if (isset($_POST['back'])) {
    header("Location: login.php");
    session_destroy();
    exit();
}
?>

</body>
</html>

<?php
function checkUserExist () {

    $username = $_GET['username'];
    echo $username;
    // users.txt stores the usernames
    $h = fopen("/srv/users.txt", "r");
    while(!feof($h)) {
        $check = trim(fgets($h));
        if($check == $username) {
            echo "Hello, " . $username . '<br>login success!<br>';
            // if there is the user, show the mainpage
            $isSuccess = showUsersFiles($username);
            break;
        }
    }
    if (!$isSuccess){
        echo "login failed, cannot find username";
    }
    fclose($h);

    return $isSuccess;
}

function showUsersFiles ( $username ) {
    if ($username === null) {
        echo "Error: no username";
        exit();
    }
    // show the user's files and make a form for download, remove and rename
    $path = "/srv/uploads/" . $username . "/";
    if (is_dir($path)) {
        if ($h = opendir($path)) {
            echo sprintf('<form action="" method="POST" name="form1">');
            while (($filename = readdir($h)) !== false) {
                echo sprintf('<input type="radio" name="file" value="%s" /><a href="view.php?username=%s&filename=%s">%s</a><br>',  $filename, $username, $filename,$filename);

            }
            closedir($h);

            // different buttons lead to different pages 
            $download = sprintf("form1.action='downloader.php?username=%s'", $username);
            $remove = sprintf("form1.action='remover.php?username=%s'", $username);
            $rename = sprintf("form1.action='renamer.php?username=%s'", $username);

            echo sprintf('<p>
                <input type="button" value="download" onClick="%s;form1.submit()"/>
                <input type="button" value="remove" onClick="%s;form1.submit()"/>
                <input type="button" value="rename" onClick="%s;form1.submit()"/>
                </p>
                </form>', $download, $remove, $rename);
        }
    }
    return true;

}

// show upload function
function showUpload($username) {
    $html = sprintf('
        <form enctype="multipart/form-data" action="uploader.php?username=%s" method="POST">
            <p>
                <input type="hidden" name="MAX_FILE_SIZE" value="20000000" />
                <label for="uploadfile_input">Choose a file to upload:</label>
                <input name="uploadedfile" type="file" id="uploadfile_input" />
            </p>
            <p>
                <input type="submit" value="Upload File" />
            </p>
        </form>
    
    ', $username);
    echo $html;

}

?>

