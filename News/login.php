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
    <title>LOGIN</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</head>
<body>
<div class="row">
    <div class="col-1"></div>
    <div class="col-11">
        <br>
        <h1>Log In</h1>
        <br>
    </div>
</div>
<div class="row">
    <div class="col-1"></div>
    <div class="col-6">
        <form action="<?php echo htmlentities($_SERVER['PHP_SELF']); ?>" method="POST">
            <div class="form-group">
                <label for="email">E-mail:</label>
                <input type="email" class="form-control" name="email" id="email" />
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" class="form-control" name="password" id="password" />
            </div>
            <div class="form-group">
                <input type="submit" class="btn btn-primary" name="login" value="login">
                <a href="mainPage.php" class="btn btn-info" role="button">Cancel</a>
            </div>
        </form>
    </div>
</div>




</body>
</html>
<?php
// check the input of email and password and submit the form
if(isset($_REQUEST['login']) && isset($_POST['email']) && isset($_POST['password'])) {

    // to do: check valid
    $email_address = $_POST['email'];
    $guess_password = $_POST['password'];

    $str_length = strlen($email_address);
    if ($str_length  <= 0 || $str_length  > 50) {
        exit("email address cannot be null or larger than 50 characters. Your email address length is: " . $str_length );
    }

    require 'database.php';
    global $mysqli;


    $stmt = $mysqli->prepare("
        SELECT COUNT(*), id, username, hashed_password 
        FROM users WHERE email_address=?");
    if(!$stmt) {
        echoAlert(fprintf("Something wrong during preparing query: %s", $mysqli->error));
        exit();
    }


    $stmt->bind_param('s', $email_address);
    $stmt->execute();

    $stmt->bind_result($count, $id, $username, $hashed_password);
    $stmt->fetch();


    // Password pass the password_verify, then log in
    if ($count == 1 && password_verify($guess_password, $hashed_password)) {
        $_SESSION['id'] = $id;
        $_SESSION['email_address'] = $email_address;
        $_SESSION['username'] = $username;
        $_SESSION['token'] = bin2hex(openssl_random_pseudo_bytes(32));
        echoAlert("we all set");
        sleep(2);
        header('Location: mainPage.php');
        $stmt->close();
        exit();
    } else {
        echoAlert("login failed, please try it again");
        session_destroy();
        sleep(2);
        header("Location: " . $_SERVER['HTTP_REFERER']);
    }
    $stmt->close();

}
?>