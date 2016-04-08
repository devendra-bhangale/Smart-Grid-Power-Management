<?php
//$con = mysql_connect("localhost","root","student");
mysql_connect("localhost","root","xyz123");
mysql_select_db("homeserver");

$try = "users";
$usr=$_POST["username"];

$q=mysql_query("SELECT house FROM $try WHERE uname = '$usr'");

while($e=mysql_fetch_assoc($q)){
	$output[]=$e;
	$hse = $e['house'];

}

print("$hse");


mysql_close();
?>
