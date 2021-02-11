<?php
header("Content-Type: application/json");

require 'database.php';

$json_str = file_get_contents('php://input');
$json_obj = json_decode($json_str, true);


if (isset($json_obj['event']) && $json_obj['event'] == 'signup') {

    $username = $json_obj['username'];
    $password = $json_obj['password'];
    $email = $json_obj['email'];

    $str_length = strlen($email);
    if ($str_length  <= 0 || $str_length  > 50) {
        $error_message = "email address cannot be null or larger than 50 characters. Your email address length is: " . $str_length;
        echo json_encode(array(
            "success" => false,
            "message" => $error_message
        ));
        exit();
    }

    $str_length = strlen($username);
    if ($str_length  <= 0 || $str_length  > 20) {
        $error_message = "username cannot be null or larger than 20 characters. Your username length is: " . $str_length;
        echo json_encode(array(
            "success" => false,
            "message" => $error_message
        ));
        exit();
    }

    if (!isExist($email)) {
        createAccount($username, $password, $email);
    } else {
        echo json_encode(array(
            "success" => false,
            "message" => "Error, this email has been used"
        ));
        exit();
    }
}
// get if the email address has been used
function isExist($new_email_address) {
    //check if new_mail_address has existed in db

    global $mysqli;
    $existed = true;
    $stmt = $mysqli->prepare("SELECT COUNT(*) FROM users WHERE email_address = ?");
    // bind with parameter
    $stmt->bind_param('s', $new_email_address);
    $stmt->execute();
    // bind with results
    $result = 0;
    $stmt->bind_result($result);
    $stmt->fetch();
    $stmt->close();

    //check if result > 0, if so, email_address existed
    if ($result == 0) $existed = false;

    return $existed;
}
// if email address has not been used, create new account for user
function createAccount($new_username, $new_password, $new_email_address) {
    global $mysqli;
    $hashed_password = password_hash($new_password, PASSWORD_DEFAULT);
    $create_account = $mysqli->prepare("
            INSERT INTO users (email_address, username, hashed_password)
            VALUES (?, ?, ?)
            ");
    if (!$create_account) {
        $error_message = sprintf("Something wrong during preparing query: %s", $mysqli->error);
        echo json_encode(array(
            "success" => false,
            "message" => $error_message
        ));
        exit();
    }
    $create_account->bind_param('sss', $new_email_address, $new_username, $hashed_password);
    $create_account->execute();
    $create_account->close();


    // save user id into session
    $find_id = $mysqli->prepare("
            SELECT COUNT(*), id FROM users WHERE email_address = ?
        ");
    $find_id->bind_param('s', $new_email_address);
    $find_id->execute();
    // bind with results
    $count = 0;
    $id = null;
    $find_id->bind_result($count ,$id);
    $find_id->fetch();
    $find_id->close();
    if ($count == 0 && $id == null) {
        echo json_encode(array(
            "success" => false,
            "message" => "Error: Something Wrong during creating account"
        ));
        exit();
    } else {
        session_start();
        $_SESSION['id'] = $id;
        $_SESSION['email'] = $new_email_address;
        $_SESSION['username'] = $new_username;
        $_SESSION['token'] = bin2hex(openssl_random_pseudo_bytes(32));
        echo json_encode(array(
            "success" => true,
            "id" => $id,
            "username" => $new_username,
            "message" => "Great: sign up has been finished"
        ));
        exit();
    }
}