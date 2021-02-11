<?php

header("Content-Type: application/json");

require 'database.php';

$json_str = file_get_contents('php://input');
$json_obj = json_decode($json_str, true);

session_start();

if (!isset($_SESSION['id']) || $_SESSION['id'] == "") {
    echo json_encode(array(
        "success" => false,
        "code" => 0,
        "message" => "Error: User has not logged in."
    ));
    session_destroy();
    exit();
}

$user_id = $_SESSION['id'];

global $mysqli;

$stmt = $mysqli->prepare("
    SELECT id, email_address, username
    FROM users
    WHERE (id!=?)
");

if(!$stmt) {

    echo json_encode(array(
        "success" => false,
        "code" => 2,
        "message" => $mysqli->error

    ));
    exit();
}

$stmt->bind_param('s', $user_id);
$stmt->execute();

$stmt->bind_result($id, $email, $username);

$id_list = [];
$email_list = [];
$username_list = [];

$index = 0;

while ($stmt->fetch()){
    $id_list[$index] = $id;
    $email_list[$index] = $email;
    $username_list[$index] = $username;
    $index++;
}

$stmt->close();

$user_length = count($id_list);

echo json_encode(array(
    "success" => true,
    "id" => $user_id,
    "user_length" => $user_length,
    "user_id_list" => $id_list,
    "email_list" => $email_list,
    "username_list" => $username_list,
    "message" => "Great: getUsers successfully"
));
