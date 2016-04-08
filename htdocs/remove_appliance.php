<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('homeserver', $con)) {

	$hse = $_POST["house"];
	$apl = $_POST["appliance"];
	
	$q = mysql_query("SELECT EXISTS(SELECT * FROM houses WHERE appliance = '$apl' AND house = '$hse')");

	if(mysql_result($q,0) == 1){
		$result = mysql_query("DELETE FROM houses WHERE appliance = '$apl' AND house = '$hse'");
			if($result)
				print("true");
			else
				print("false");
	}
	else
		print("appliance_not_added");
	
}	
mysql_close();
?>
