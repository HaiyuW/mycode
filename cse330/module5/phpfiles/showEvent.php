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

if (isset($json_obj['action']) && $json_obj['action'] == 'getEventsByDate') {

    $creator_id = $_SESSION['id'];
    $date = $json_obj['date'];

    global $mysqli;

    $stmt = $mysqli->prepare("
        SELECT event_id, event_title, event_description, event_date, event_time 
        FROM events
        WHERE (event_date = ? and creator_id = ?)
    ");

    if(!$stmt) {

        echo json_encode(array(
            "success" => false,
            "code" => 2,
            "message" => $mysqli->error

        ));
        exit();
    }

    $stmt->bind_param('ss', $date, $creator_id);
    $stmt->execute();

    $stmt->bind_result($event_id, $event_title, $event_description, $event_date, $event_time);

    $event_id_list = [];
    $event_title_list = [];
    $event_description_list = [];
    $event_date_list = [];
    $event_time_list = [];

    $index = 0;

    while ($stmt->fetch()){
        $event_id_list[$index] = $event_id;
        $event_title_list[$index] = $event_title;
        $event_description_list[$index] = $event_description;
        $event_date_list[$index] = $event_date;
        $event_time_list[$index] = $event_time;
        $index++;
    }

    $stmt->close();

// return JSON data.

    $event_length = count($event_id_list);

    echo json_encode(array(
        "success" => true,
        "id" => $creator_id,
        "event_length" => $event_length,
        "event_id_list" => $event_id_list,
        "event_title_list" => $event_title_list,
        "event_description_list" => $event_description_list,
        "event_date_list" => $event_date_list,
        "event_time_list" => $event_time_list,
        "message" => "Great: getEventsByDate successfully"
    ));


    exit();

}

if (isset($json_obj['action']) && $json_obj['action'] == 'getEventByEventID') {

    $creator_id = $_SESSION['id'];
    $event_id = $json_obj['event_id'];

    global $mysqli;

    $stmt = $mysqli->prepare("
        SELECT event_title, event_description, event_date, event_time
        FROM events
        WHERE (event_id = ?)
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
    $stmt->bind_result($event_title, $event_description, $event_date, $event_time);
    $stmt->fetch();

    echo json_encode(array(
        "success" => true,
        "event_id" => $event_id,
        "event_title" => $event_title,
        "event_description" => $event_description,
        "event_date" => $event_date,
        "event_time" => $event_time,
        "message" => "Great: getEventByEventID successfully",
        "mysql_message" => $stmt->num_rows
    ));

    $stmt->close();
    exit();

}

