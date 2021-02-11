<?php
// Content of database.php

global $mysqli;
$mysqli = new mysqli('localhost', 'wustl_inst', 'wustl_pass', 'wustl_module3');

if($mysqli->connect_errno) {
    printf("Connection Failed: %s\n", $mysqli->connect_error);
    exit;
}
?>