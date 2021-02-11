<?php
// remove the selected files and head back to the mainpage
$username = $_GET['username'];
$filename = $_POST['file'];

$file = "/srv/uploads/".$username."/".$filename;

if (file_exists($file)){
    unlink($file);
}

header('Location: userMainPage.php?username='.$username);
exit;

?>

