<?php
//$con = mysql_connect("localhost","root","student");
$con = mysql_connect("localhost","root","xyz123");

if (!$con){ die('Could not connect: ' . mysql_error());}

if (mysql_select_db('homeserver', $con)) {
	$house = $_POST["house"];
	
	$q = mysql_query("SELECT * FROM houses WHERE house = '$house'");
	print($q);
	//if($q == 0){
	//	print("does_not_exist");
	//}
	//else 

//	if (!$q || !mysql_num_rows($q)) {
//		print("does_not_exist");
//	}
//	else if(mysql_result($q,0) == 1){
		//$output[] = "";
		//while($e=mysql_fetch_assoc($q))
		//	$output[]=$e;

		//print(json_encode($output));
//	}
//	else
//		print("does_not_exist");
}
		
mysql_close();
?>
