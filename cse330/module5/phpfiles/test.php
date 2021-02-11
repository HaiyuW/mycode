<?php
header("Content-Type: application/json");

require 'database.php';

$json_str = file_get_contents('php://input');
$json_obj = json_decode($json_str, true);

$test = array("a", "b", "c");

echo json_encode(array(
    "success" => true,
    "array" => $test


));
exit();