<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('utilityserver', $con)) {

	$watt = $_POST["wattage"];
	$strt = $_POST["starttime"];
	$dead = $_POST["deadline"];
	$run = $_POST["runtime"];
	$house = $_POST["house"];
	$apl = $_POST["appliance"];
	$typ = $_POST["type"];
	
	$q = mysql_query("SELECT EXISTS(SELECT * FROM houses WHERE appliance = '$apl' AND house = '$house')");

	if(mysql_result($q,0) == 1){
		$result = mysql_query("UPDATE houses SET starttime = '$strt', deadline = '$dead', runtime = '$run', type = '$typ' 
			WHERE appliance = '$apl' AND house = '$house'");
			if($result)
				print("true");
			else
				print("false");
	}
	else if(mysql_result($q,0) == 0){
		$result = mysql_query("INSERT INTO houses (house, appliance, wattage, starttime, deadline, runtime, type)
			VALUES ('$house', '$apl', '$watt', '$strt', '$dead', '$run', '$typ')");
			if($result)
				print("true");
			else
				print("false");
	}
}	
mysql_close();
?>
