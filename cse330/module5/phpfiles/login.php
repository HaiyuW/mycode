<?php
header("Content-Type: application/json");

require 'database.php';

$json_str = file_get_contents('php://input');
$json_obj = json_decode($json_str, true);

if (isset($json_obj['checkLogin']) && $json_obj['checkLogin'] == 'check') {
    session_start();
    if (isset($_SESSION['id'])) {
        echo json_encode(array(
            "loggedin" => true,
            "username" => $_SESSION['username'],
            "message" => "session['id'] has been activated"
        ));
        exit();
    } else {
        echo json_encode(array(
            "loggedin" => false,
            "message" => "Have not logged in"
        ));
        session_destroy();
        exit();
    }
} elseif (isset($json_obj['event']) && $json_obj['event'] == 'login') {

    $email_address = $json_obj['email'];
    $guess_password = $json_obj['password'];

    $str_length = strlen($email_address);
    if ($str_length  <= 0 || $str_length  > 50) {
        $error_message = "email address cannot be null or larger than 50 characters. Your email address length is: " . $str_length;
        echo json_encode(array(
            "success" => false,
            "message" => $error_message
        ));
        exit();
    }

    global $mysqli;

    $stmt = $mysqli->prepare("
        SELECT COUNT(*), id, username, hashed_password 
        FROM users WHERE email_address=?");
    if(!$stmt) {
        $error_message = sprintf("Something wrong during preparing query: %s", $mysqli->error);
        echo json_encode(array(
            "success" => false,
            "message" => $error_message
        ));
        exit();
    }


    $stmt->bind_param('s', $email_address);
    $stmt->execute();

    $stmt->bind_result($count, $id, $username, $hashed_password);
    $stmt->fetch();


    // Password pass the password_verify, then log in
    if ($count == 1 && password_verify($guess_password, $hashed_password)) {
        session_start();
        $_SESSION['id'] = $id;
        $_SESSION['email_address'] = $email_address;
        $_SESSION['username'] = $username;
        $_SESSION['token'] = bin2hex(openssl_random_pseudo_bytes(32));
        echo json_encode(array(
            "success" => true,
            "id" => $id,
            "username" => $username,
            "message" => "Great: login has been finished"
        ));
        $stmt->close();
        exit();
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "Error: login failed, please try it again."
        ));
        $stmt->close();
        exit();
    }
}
?>