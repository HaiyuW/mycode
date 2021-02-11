<?php
//download the file and head back to the mainpage
$filename = $_POST['file'];
$username = $_GET['username'];
$file = "/srv/uploads/".$username."/".$filename;
if (file_exists($file)) {
    header('Content-Description: File Transfer');
    header('Content-Type: application/octet-stream');
    header('Content-Disposition: attachment; filename="'.basename($file).'"');
    header('Expires: 0');
    header('Cache-Control: must-revalidate');
    header('Pragma: public');
    header('Content-Length: ' . filesize($file));
    readfile($file);
}
$back = sprintf("Location userMainPage.php?username=%s", $username);
header($back);
exit();
?>