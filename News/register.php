<?php
// To Do Lists:
// * security
session_start();
require 'tools.php';
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SIGN UP</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</head>
<body>
<!--The navigation part with a explicitly title-->
<div class="row">
    <div class="col-1"></div>
    <div class="col-11">
        <br>
        <h1>Sign Up</h1>
        <br>
    </div>
</div>
<!--The navigation part end-->
<div class="row">
    <div class="col-1"></div>
    <!--The part of form of registing-->
    <div class="col-6">
        <form action="<?php echo htmlentities($_SERVER['PHP_SELF']); ?>" method="POST">
            <div class="form-group">
                <label for="new_email_address">E-mail:</label>
                <input type="email" class="form-control" name="new_email_address" id="new_email_address" />
            </div>
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" class="form-control" name="new_username" id="username" />
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" class="form-control" name="new_password" id="password" />
            </div>
            <div class="form-group">
                <input type="submit" class="btn btn-primary" name="new_user_submit" value="submit"/>
                <a href="mainPage.php" class="btn btn-info" role="button">Cancel</a>
            </div>
        </form>
    </div>
    <!--end part-->
</div>

</body>
</html>

<?php
    require 'database.php';
    global $mysqli;
    // handle the form of signing up
    if (isset($_REQUEST['new_user_submit'])
        && isset($_POST['new_user_submit'])
        && isset($_POST['new_username'])
        && isset($_POST['new_password'])
        && isset($_POST['new_email_address'])) {

        $new_username = $_POST['new_username'];
        $new_password = $_POST['new_password'];
        $new_email_address = $_POST['new_email_address'];

        $str_length = strlen($new_email_address);
        if ($str_length  <= 0 || $str_length  > 50) {
            exit("email address cannot be null or larger than 50 characters. Your email address length is: " . $str_length );
        }

        $str_length = strlen($new_username);
        if ($str_length  <= 0 || $str_length  > 20) {
            exit("username cannot be null or larger than 20 characters. Your username length is: " . $str_length );
        }

        if (!isExist($new_email_address)) {
            createAccount($new_username, $new_password, $new_email_address);
        } else {
            echoAlert('This E-mail address has been used, please try another one!');
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
            echoAlert(sprintf("Something wrong during preparing query: %s", $mysqli->error));
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
            echoAlert("Something wrong during creating account in createAccount");
        } else {
            $_SESSION['id'] = $id;
            $_SESSION['email'] = $new_email_address;
            $_SESSION['username'] = $new_username;
            $_SESSION['token'] = bin2hex(openssl_random_pseudo_bytes(32));
            echoAlert("we all set");
        }

        header('Location: mainPage.php');




    }



?>
