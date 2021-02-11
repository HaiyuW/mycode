<?php
session_start();
// Get the username and filename
$username = $_GET['username'];
$filename = $_POST['file'];
?>

<!-- make a form for new filename -->
<form action="<?php echo htmlentities($_SERVER['PHP_SELF'].'?'.'username='.$username.'&'.'file=' . $filename); ?>" method="POST">
    <p>
        <label for="newFileName">New Filename:</label>
        <input type="text" name="newFileName" id="newFileName">
        <input type="submit" value="submit" name="newFile"> 
    </p>
</form>
<?php
// rename the selected file
if (isset($_POST['newFile'])) { 

    $newname = $_POST['newFileName'];

    $file = "/srv/uploads/".$username."/".$_GET['file'];
    $newfile = "/srv/uploads/".$username."/".$newname;

    if (file_exists($file)){
        rename($file, $newfile);
    }
    // head back to the userMainPage after renaming
    $back = sprintf("Location: userMainPage.php?username=%s", $username);
    header($back);
    exit();
}
?>


