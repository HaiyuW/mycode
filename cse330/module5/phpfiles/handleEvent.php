<?php
/*
 * errorcode:
 *  0: not log in
 *  1: wrong input
 *  2: error in process
 */
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

if (isset($json_obj['action']) && $json_obj['action'] == 'newEvent') {

    $creator_id = $_SESSION['id'];
    $event_title = $json_obj['event-title'];
    $event_description = $json_obj['event-description'];
    $event_date = $json_obj['event-date'];
    $event_time = $json_obj['event-time'] . ":00";

    $str_length = strlen($event_title);
    if ($str_length  <= 0 || $str_length  > 255) {
        $error_message = "title cannot be null or larger than 255 characters. Your email address length is: " . $str_length;
        echo json_encode(array(
            "success" => false,
            "code" => 1,
            "message" => $error_message
        ));
        exit();
    }

    global  $mysqli;

    $stmt = $mysqli->prepare("
        INSERT INTO events (creator_id, event_title, event_description, event_date, event_time)
        VALUES (?, ?, ?, ?, ?)
    ");

    if(!$stmt) {
        echo json_encode(array(
            "success" => false,
            "code" => 2,
            "message" => $mysqli->error
        ));
        exit();
    }

    $stmt->bind_param('sssss', $creator_id, $event_title, $event_description, $event_date, $event_time);
    $stmt->execute();
    $stmt->close();

    $stmt = $mysqli->prepare("
        SELECT max(event_id) FROM events
    ");

    if(!$stmt) {
        echo json_encode(array(
            "success" => false,
            "code" => 2,
            "message" => $mysqli->error
        ));
        exit();
    }

    $stmt->execute();
    $stmt->bind_result($event_id);
    $stmt->fetch();

    echo json_encode(array(
        "success" => true,
        "code" => 2,
        "event_date" => $event_date,
        "event_id" => $event_id,
        "message" => "Great: Post Successfully!"
    ));
    exit();
}
if (isset($json_obj['action']) && $json_obj['action'] == 'modifyEvent') {

    $creator_id = "" . $_SESSION['id'];
    $event_id = "" . $json_obj['event-id'];
    $event_title = "" . $json_obj['event-title'];
    $event_description = "" . $json_obj['event-description'];
    $event_date = "" . $json_obj['event-date'];
    $event_time = "" . $json_obj['event-time'] . ":00";

    $str_length = strlen($event_title);
    if ($str_length  <= 0 || $str_length  > 255) {
        $error_message = "title cannot be null or larger than 255 characters. Your title length is: " . $str_length;
        echo json_encode(array(
            "success" => false,
            "code" => 1,
            "message" => $error_message
        ));
        exit();
    }

    global $mysqli;

    $stmt = $mysqli->prepare("
        UPDATE events SET event_title=?, event_description=?, event_date =?, event_time=?
        WHERE events.event_id = ?
    ");

    if(!$stmt) {
        echo json_encode(array(
            "success" => false,
            "code" => 2,
            "message" => $mysqli->error
        ));
        exit();
    }

    $stmt->bind_param('sssss', $event_title, $event_description, $event_date, $event_time, $event_id);
    $stmt->execute();
    $stmt->close();

    echo json_encode(array(
        "success" => true,
        "event_id" => $json_obj['event-id'],
        "event_title" => $json_obj['event-title'],
        "event_description" => $json_obj['event-description'],
        "event_date" => $json_obj['event-date'],
        "event_time" => $json_obj['event-time'],
        "message" => $mysqli->affected_rows
    ));
    //"Great: Modify Successfully!"
    exit();
}
if (isset($json_obj['action']) && $json_obj['action'] == 'deleteEvent') {

    $event_id = $json_obj['event_id'];

    global $mysqli;

    $stmt = $mysqli->prepare("
        DELETE FROM events
        WHERE event_id = ?
    ");

    if(!$stmt) {
        echo json_encode(array(
            "success" => false,
            "code" => 2,
            "message" => $mysqli->error
        ));
        exit();
    }

    $stmt->bind_param('s', $event_id);
    $stmt->execute();

    echo json_encode(array(
        "success" => true,
        "event_id" => $event_id,
        "message" => "Great: deletEvent successfully"
    ));

    $stmt->close();

    exit();

}



