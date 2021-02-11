<?php
    // show users files in their types
    $username = $_GET['username'];
    $filename = $_GET['filename'];

    //form the path to the file
    $path = "/srv/uploads/" . htmlentities($username) . "/".htmlentities($filename);

    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $mime = $finfo->file($path);

    header("Content-Type: ". $mime);
    header('content-disposition: inline; filename="'. $filename .'";');

    readfile($path)

    /*

    $h = fopen($path, "r") or die("Unable to open file!");
    while(!feof($h)){
        echo fgets($h);
        echo "<br/>";
    }
    fclose($h);
    */
?>