<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}

	$hse = $_POST["house"];
	//$apl = $_POST["appliance"];

	
		$myFile = $hse;
		$fh = fopen($myFile, 'r') or die("can't open file");
		while (!feof($fh)) {
		   $line = fgets($fh);
		   echo $line;
		}		
		fclose($fh);

		//print($line);
		
mysql_close();
?>
