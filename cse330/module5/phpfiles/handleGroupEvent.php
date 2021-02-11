<?php
header("Content-Type: application/json");

require 'database.php';

$json_str = file_get_contents('php://input');
$json_obj = json_decode($json_str, true);

session_start();
if (!isset($_SESSION['id']) || $_SESSION['id'] == '') {
    echo json_encode(array(
        "success" => false,
        "code" => 0,
        "message" => "Have not logged in"
    ));
    session_destroy();
    exit();
}

$creator_id = $_SESSION['id'];
$index = 0;

// new Group Event, insert event_id with different member_ids
if (isset($json_obj['action']) && $json_obj['action'] == 'newGroupEvent') {
    $event_id = $json_obj['event-id'];
    $member_id_list = $json_obj['member-id-list'];
    $length = count($member_id_list);
    $member_id_list[$length] = $creator_id;
    $length++;
    $index = 0;

    global $mysqli;

    while ($index<$length) {
        $stmt = $mysqli->prepare("
            INSERT INTO groups (creator_id, member_id, event_id)
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
        $stmt->bind_param('sss', $creator_id, $member_id_list[$index], $event_id);
        $stmt->execute();
        $stmt->close();
        $index++;
    }
    echo json_encode(array(
        "success" => true,
        "code" => 2,
        "user_id" => $creator_id,
        "event_id" => $event_id,
        "message" => "Great: Post Successfully!"
    ));
    exit();
}

// get Event ID using member id
if (isset($json_obj['action']) && $json_obj['action'] == 'getEventId') {
    $user_id = $creator_id;
    $event_id_list = [];
    $index = 0;

    global $mysqli;
    $stmt = $mysqli->prepare("
        SELECT event_id FROM groups
        WHERE member_id=?
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
    $stmt->bind_result($event_id);

    while ($stmt->fetch()) {
        $event_id_list[$index] = $event_id;
        $index++;
    }

    $stmt->close();
    echo json_encode(array(
        "success" => true,
        "code" => 2,
        "user_id" => $user_id,
        "event_id_list" => $event_id_list,
        "message" => "Great: Post Successfully!"
    ));
    exit();

}