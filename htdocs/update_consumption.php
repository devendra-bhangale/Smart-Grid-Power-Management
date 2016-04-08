<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}


if (mysql_select_db('utilityserver', $con)) {

	$house = $_POST["house"];
	$apl = $_POST["appliance"];
	//$time = $_POST["time"];
	
	//$result = mysql_query("SELECT EXISTS(SELECT '$time' FROM houses WHERE appliance = '$apl' AND house = '$hse')");
	$result = mysql_query("SELECT EXISTS(SELECT * FROM houses WHERE house = '$house')");

	if($result == 0)
		print("empty");
	else if(mysql_result($result,0) == 1){
		$myFile = "updateConsumption.txt";
		$fh = fopen($myFile, 'a') or die("can't open file");
		$nextline = "\n";
//		$stringData = "$house";
		$stringData = $house.$nextline;		
		fwrite($fh, $stringData);
		fclose($fh);

		print("true");
	}
	else
		print("false");

	
		
}	
mysql_close();
?>
