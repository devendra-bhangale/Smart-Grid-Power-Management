<?php
//$con = mysql_connect("localhost","root","student");
mysql_connect("localhost","root","xyz123");
//mysql_connect("localhost","root","12020210749343");
mysql_select_db("homeserver");

$usr=$_POST["username"];
$pwdIn=$_POST["password"];
$q=mysql_query("SELECT pwd FROM users WHERE uname = '$usr'");

while($e=mysql_fetch_assoc($q)){
	$output[]=$e;
	$pwd = $e['pwd'];
}
if($pwd == $pwdIn)
	print("true");
else
	print("false");

mysql_close();
?>
