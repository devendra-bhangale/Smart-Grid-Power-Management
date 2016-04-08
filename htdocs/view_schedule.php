<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");
//$con = mysql_connect("localhost","root","");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('utilityserver', $con)) {

	$hse = $_POST["house"];
	
	
	$result = mysql_query("SELECT EXISTS(SELECT * FROM houses WHERE house = '$hse')");
	$result1 = mysql_query("SELECT * FROM houses WHERE house = '$hse'");
	
	if(!mysql_result($result,0) == 1) {

		print("null");
		
	}
	else {
	
		while($f=mysql_fetch_assoc($result1)){
			$output[]=$f;
		}
		
		print(json_encode($output));

	}
	
}	
mysql_close();
?>
