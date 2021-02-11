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

$event_id = $json_obj['event_id'];

$user_id = $_SESSION['id'];

global $mysqli;

$stmt = $mysqli->prepare("
    SELECT tag_id, tag_content
    FROM tags
    WHERE (user_id = ? and event_id = ?)
");

if(!$stmt) {
    echo json_encode(array(
        "success" => false,
        "code" => 2,
        "message" => $mysqli->error

    ));
    exit();
}

$stmt->bind_param('ss', $user_id, $event_id);

$stmt->execute();

$stmt->bind_result($tag_id, $tag_content);

$tag_id_list = [];
$tag_content_list = [];

$index = 0;

while ($stmt->fetch()){
    $tag_id_list[$index] = $tag_id;
    $tag_content_list[$index] = $tag_content;
    $index++;
}

$stmt->close();

$tag_length = count($tag_id_list);

echo json_encode(array(
    "success" => true,
    "user_id" => $user_id,
    "event_id" => $event_id,
    "tag_length" => $tag_length,
    "tag_id_list" => $tag_id_list,
    "tag_content_list" => $tag_content_list,
    "message" => "Great: getTag successfully"
));

exit();
