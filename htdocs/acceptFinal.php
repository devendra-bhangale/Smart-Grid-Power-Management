<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('utilityserver', $con)) {

	$hse = $_POST["house"];
	$apl = $_POST["appliance"];
	$strt = $_POST["starttime"];
	$dead = $_POST["deadline"];
	$run = $_POST["runtime"];
	$typ = $_POST["type"];
	
	$q = mysql_query("SELECT EXISTS(SELECT * FROM houses WHERE house = '$hse')");
	
	if($q == 0)
		print("does_not_exist");
	else if(mysql_result($q,0) == 1){
	
		$result = mysql_query("UPDATE houses SET starttime = '$strt', deadline = '$dead', runtime = '$run', type = '$typ' 
			WHERE appliance = '$apl' AND house = '$hse'");
			if($result)
				print("true");
			else
				print("false");
	}
	else
		print("does_not_exist");
	
}	
mysql_close();
?>
