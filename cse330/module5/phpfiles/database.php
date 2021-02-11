<?php

global $mysqli;
$mysqli = new mysqli('localhost', 'hanhan', 'hanniba', 'module5');

if($mysqli->connect_errno) {
    printf("Connection Failed: %s\n", $mysqli->connect_error);
    exit;
}
?>