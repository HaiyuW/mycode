<?php
header("Content-Type: application/json");

session_start();

require 'database.php';

$json_str = file_get_contents('php://input');
$json_obj = json_decode($json_str, true);

if (isset($json_obj['event']) && $json_obj['event'] == 'logout') {
    session_destroy();
    if (session_status() !== PHP_SESSION_ACTIVE) {
        echo json_encode(array(
            "success" => true,
            "message" => "logout successfully"
        ));
        exit();
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "Error: Something wrong during destroying session"
        ));
        exit();
    }
} else {
    echo json_encode(array(
        "success" => false,
        "message" => "Error: maybe fetch wrong place"
    ));
    exit();
}