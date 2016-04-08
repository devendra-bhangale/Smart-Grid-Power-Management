<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('homeserver', $con)) {

	$hse = $_POST["house"];
	$apl = $_POST["appliance"];
	
	
	
	$result = mysql_query("SELECT EXISTS(SELECT * FROM houses WHERE appliance = '$apl' AND house = '$hse')");
	$result1 = mysql_query("SELECT * FROM houses WHERE appliance = '$apl' AND house = '$hse'");
	
	if(!mysql_result($result,0) == 1) {

		print("null");
		
	}
	
	while($f=mysql_fetch_assoc($result1)){
		$output[]=$f;
	
		print(json_encode($output));
	}

	
	
}	
mysql_close();
?>
