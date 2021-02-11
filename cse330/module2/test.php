<!DOCTYPE html>
<html>
<head><title>Bold Printer</title></head>
<body>
<form action="<?php echo htmlentities($_SERVER['PHP_SELF']); ?>" method="POST">
	<p>
		<label for="name">Name:</label>
		<input type="text" name="name" id="name" />
	</p>
	<p>
		<input type="submit" value="Print in Bold" />
    </p>
    <p>
    <form name="myForm" action="javascript:void(0)" method="post"> 
<input type="button" value="提交" onclick="myForm.action='login.php';myForm.submit()">  
<input type="button" value="预览" onclick="myForm.action='b.php';myForm.submit()">  
<input type="button" value="更新" onclick="myForm.action='c.php';myForm.submit()">
</form>
</p>
</form>

<?php
if(isset($_POST['name'])){
	printf("<p><strong>%s</strong></p>\n",
		htmlentities($_POST['name'])
	);
}
?>
</body>
</html>