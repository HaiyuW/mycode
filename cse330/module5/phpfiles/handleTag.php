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
$event_id = $json_obj['event_id'];

// delete tag.
if (isset($json_obj['action']) && $json_obj['action'] == 'deleteTag') {
    $tag_id = $json_obj['tag_id'];

    global $mysqli;

    $stmt = $mysqli->prepare("
        DELETE FROM tags
        WHERE tag_id=?
    ");

    if (!$stmt) {
        echo json_encode(array(
            "success" => false,
            "code" => 2,
            "message" => $mysqli->error
        ));
        exit();
    }

    $stmt->bind_param('s', $tag_id);
    $stmt->execute();

    echo json_encode(array(
        "success" => true,
        "event_id" => $event_id,
        "tag_id" => $tag_id,
        "message" => "Great: deletEvent successfully"
    ));

    $stmt->close();
    exit();
}

// add tag
if (isset($json_obj['action']) && $json_obj['action'] == 'addTag') {

    $tag_content = $json_obj['tag_content'];

    $str_length = strlen($tag_content);
    if ($str_length  <= 0 || $str_length  > 255) {
        $error_message = "tag cannot be null or larger than 255 characters. Your email address length is: " . $str_length;
        echo json_encode(array(
            "success" => false,
            "code" => 1,
            "message" => $error_message
        ));
        exit();
    }

    global $mysqli;

    $stmt = $mysqli->prepare("
        INSERT INTO tags (user_id, event_id, tag_content) 
        VALUES (?, ?, ?)
    ");

    if(!$stmt) {
        echo json_encode(array(
            "success" => false,
            "code" => 2,
            "message" => $mysqli->error
        ));
        exit();
    }

    $stmt->bind_param('sss', $user_id, $event_id, $tag_content);
    $stmt->execute();
    $stmt->close();

    echo json_encode(array(
        "success" => true,
        "event_id" => $event_id,
        "message" => "Great: deletEvent successfully"
    ));
    exit();
}

