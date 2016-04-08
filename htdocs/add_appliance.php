<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('homeserver', $con)) {

	$watt = $_POST["wattage"];
	$strt = $_POST["starttime"];
	$dead = $_POST["deadline"];
	$run = $_POST["runtime"];
	$house = $_POST["house"];
	$apl = $_POST["appliance"];
	$typ = $_POST["type"];
	$rps = $_POST["rps"];
	
	$q = mysql_query("SELECT EXISTS(SELECT * FROM houses WHERE appliance = '$apl' AND house = '$house')");

	if(mysql_result($q,0) == 1)
//	if($q == 1)
		print("appliance_already_added");
	else{
//	if(mysql_result($q,0) == 0){
	
	//$numResults = mysql_num_rows($q);

	//if($numResults == 0){
		$result = mysql_query("INSERT INTO houses (house, appliance, wattage, starttime, deadline, runtime, type, rps) 
			VALUES ('$house', '$apl', '$watt', '$strt', '$dead', '$run', '$typ', '$rps')");
			if($result)
				print("true");
			else
				print("false");
	}
	//else
	//	print("appliance_already_added");
	
}	
mysql_close();
?>
