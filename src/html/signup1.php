<?php
// Start the session
session_start();
?>
<!DOCTYPE html>
<html lang="en-us">
<meta charset="utf-8" />
<head>
<title>ARM</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
@import url("http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css");
@import url("http://fonts.googleapis.com/css?family=Source+Sans+Pro:400,700");

*{margin:0; padding:0}
body{background:#294072; font-family: 'Source Sans Pro', sans-serif}
.form{width:400px; margin:0 auto; background:#1C2B4A; margin-top:150px}
.header{height:44px; background:#17233B}
.header h2{height:44px; line-height:44px; color:#fff; text-align:center}
.login{padding:0 20px}
.login span.un{width:10%; text-align:center; color:#0C6; border-radius:3px 0 0 3px}
.text{background:#12192C; width:90%; border-radius:0 3px 3px 0; border:none; outline:none; color:#999; font-family: 'Source Sans Pro', sans-serif} 
.text,.login span.un{display:inline-block; vertical-align:top; height:40px; line-height:40px; background:#12192C;}

.btn{height:40px; border:none; background:#0C6; width:100%; outline:none; font-family: 'Source Sans Pro', sans-serif; font-size:20px; font-weight:bold; color:#eee; border-bottom:solid 3px #093; border-radius:3px; cursor:pointer}
ul li{height:40px; margin:15px 0; list-style:none}
.span{display:table; width:100%; font-size:14px;}
.ch{display:inline-block; width:50%; color:#CCC}
.ch a{color:#CCC; text-decoration:none}
.ch:nth-child(2){text-align:right}
/*social*/
.social{height:30px; line-height:30px; display:table; width:100%}
.social div{display:inline-block; width:42%; color:#eee; font-size:12px; text-align:center; border-radius:3px}
.social div i.fa{font-size:16px; line-height:30px}
.fb{background:#3B5A9A; border-bottom:solid 3px #036} .tw{background:#2CA8D2; margin-left:16%; border-bottom:solid 3px #3399CC}
/*bottom*/
.sign{width:90%; padding:0 5%; height:50px; display:table; background:#17233B}
.sign div{display:inline-block; width:50%; line-height:50px; color:#ccc; font-size:14px}
.up{text-align:right}
.up a{display:block; background:#096; text-align:center; height:35px; line-height:35px; width:50%; font-size:16px; text-decoration:none; color:#eee; border-bottom:solid 3px #006633; border-radius:3px; font-weight:bold; margin-left:50%}
@media(max-width:480px){ .form{width:100%}}
</style>
</head>
<body>
<div class="form">
<div class="header"><h2>Sign Up</h2></div>
<div class="login">
<form action="signup2.html" method="post">
<ul>

<?php
$myfile = fopen("/home/rahul/webserver.cfg", "r") or die("Unable to open file!");
$myyfile = fgets($myfile,filesize("/home/rahul/webserver.cfg"));
$myyyfile = fopen("/home/rahul/webserver1.cfg", "r") or die("Unable to open file!");
$myyyyfile=fgets($myyyfile,filesize("/home/rahul/webserver1.cfg"));
$servername = "localhost";
$dbname = "Class_".$_SESSION["b"];
$username=$myyfile;
$password=$myyyyfile;
// Create connection
$conn = new mysqli($servername,  $username,  $password,$dbname);

// Check connection
if (mysqli_connect_error()) {
    die("Database connection failed: " . mysqli_connect_error());
}
$f=$_SESSION["s"];
$sql = "SELECT * FROM Namelist where IDNo='$f'";
$result = $conn->query($sql);
if ($result->num_rows >0){
while($row= $result->fetch_assoc()) {
  if(strcmp($row["IDNo"],$_SESSION["s"])==0 && ($row["Password"]==NULL)){
$t=1;
echo "<br>"."<div align='center'>"."<font color='white' size='4'>"."<b>"."Are You"."</b>"."</font>"."</div>";
echo "<br>"."<div align='center'>"."<font color='orange' size='4'>"."<b>".$row["Name"]." - ".$row["IDNo"]." ?</b>"."</font>"."</div>";
break;
}
else
{
 header("Refresh: 0;signedinn.html");
 break;
}
}
if($t==0)
{
}
}
$conn->close();
?>
<li>
<input type="submit" value="Yes! Continue" class="btn">
</li>
<li>
<div align="right"><a href="signup"><b><font color="red">Not you?</font></b></a></div>
</li>
</ul>
</form>

</div>
</div>
</div>
</body>
</html>

