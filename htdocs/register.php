<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('homeserver', $con)) {

	$name = $_POST["uname"];
	$pwd = $_POST["pwd"];
	$fname = $_POST["firstname"];
	$lname = $_POST["lastname"];
	$hse = $_POST["house"];
	$email = $_POST["email"];
	$phone = $_POST["phone"];
	
	$q = mysql_query("SELECT EXISTS(SELECT * FROM users WHERE uname = '$name')");
	$g =  mysql_query("SELECT EXISTS(SELECT * FROM users WHERE email = '$email')");
	$h =  mysql_query("SELECT EXISTS(SELECT * FROM users WHERE house = '$hse')");

	if(mysql_result($q,0) == 1)
		print("false_user");
	else if (mysql_result($g,0) ==1)
		print("false_email");
	else if (mysql_result($h,0) ==1)
		print("false_house");
	else {
		$result = mysql_query("INSERT INTO users (uname, pwd, firstname, lastname, house, email, phone)
		VALUES ('$name','$pwd','$fname','$lname', '$hse', '$email','$phone')");
		if($result)
			print("true");
		else
			print("false");
	}
}	
mysql_close();
?>
