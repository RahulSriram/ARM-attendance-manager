<?php
// Start the session
session_start();
?>

<!DOCTYPE html>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>PERSONAL DETAILS</title>
		<style type="text/css">
		
		body {
			margin: 0;
			padding: 0;
			overflow: hidden;
			height: 100%; 
			max-height: 100%; 
			font-family:Sans-serif;
			line-height: 1.5em;
		}
		
		#header {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100px; 
			overflow: hidden;  
			background: #BCCE98;
		}
		
		#nav {
			position: absolute; 
			top: 100px; 
			left: 0; 
			bottom: 0;
			width: 230px;
			overflow: auto;  
			background: #DAE9BC; 		
		}
		
		#logo {
			padding:10px;
		}
		
		main {
			position: fixed;
			top: 100px; 
			left: 230px; 
			right: 0;
			bottom: 0;
			overflow: auto; 
			background:#fff;
		}
		
		.innertube {
			margin: 15px;
		}
		
		p {
			color: #555;
		}

		nav ul {
			list-style-type: none;
			margin: 0;
			padding: 0;
		}
		
		nav ul a {
			color: darkgreen;
			text-decoration: none;
		}
				
		/*IE6 fix*/
		* html body{
			padding: 100px 0 0 230px; 
		}
		
		* html main{ 
			height: 100%; 
			width: 100%; 
		}
		
		</style>
		
		<script type="text/javascript">
			var bodyText=[ (sentenceCount){
				for (var i=0; i<sentenceCount; i++)
				document.write(bodyText[Math.floor(Math.random()*7)]+" ")
			}
		</script>	
	
	</head>
	
	<body>		

		<header id="header">
			<div id="logo">
				<h1>ARM</h1>
			</div>
		</header>
				
		<main>
			<div class="innertube">
				
				<h1>PERSONAL DETAILS</h1>
				<?php
$myfile = fopen("/home/rahul/webserver.cfg", "r") or die("Unable to open file!");
$myyfile = fgets($myfile,filesize("/home/rahul/webserver.cfg"));
$myyyfile = fopen("/home/rahul/webserver1.cfg", "r") or die("Unable to open file!");
$myyyyfile=fgets($myyyfile,filesize("/home/rahul/webserver1.cfg"));
$servername = "localhost";
$dbname = "Class_".$_SESSION["a"];
$username=$myyfile;
$password=$myyyyfile;
// Create connection
$con = new mysqli($servername,  $username,  $password,$dbname);
// Check connection
if (mysqli_connect_error()) {
    die("SERVER NOT FOUND");
}

$so=$_SESSION["v"];	
$sql = "SELECT * FROM Namelist where IDNo='$so' ;";
$sql .="SELECT round(sum(Attended)/sum(Total),0)*100  FROM Percentage GROUP BY IDNo HAVING IDNo='$so'";
if (mysqli_multi_query($con, $sql))
{
    $result1 = mysqli_store_result($con);
    $result2 = null;

    if (mysqli_more_results($con))
    {
        mysqli_next_result($con);
        $result2 = mysqli_store_result($con);
    }

    // do something with both result sets.

    if ($result1){
          while($row = $result1->fetch_row())
             {
                 
             
                   echo "IDNO: " . $row[0]."<br>"."NAME:".$row[1]."<br>"."CONTACT:".$row[2]."<br>";
                    
              
           }
                 
        mysqli_free_result($result1);
      }    

    if ($result2){
while($roww = $result2->fetch_row())
                {
                     if($roww[0]==NULL)
                        {
                                echo "OVERALL ATTENDANCE: 0%";          
                        }
                       else
                     {
             
                   echo "OVERALL ATTENDANCE: " . $roww[0]."%<br>";
                     } 
              }

             

        mysqli_free_result($result2);
}
}

mysqli_close($con);
?>
				
			</div>
		</main>

		<nav id="nav">
			<div class="innertube">
				<h1>CATEGORY</h1>
				<ul>
					<li><a href="HOME.php">HOME</a></li>
					<li><a href="personaldetails.php">PERSONAL DETAILS</a></li>
					<li><a href="VIEWPERCENTAGE.php">VIEW PERCENTAGE</a></li>
					<li><a href="individual.html">INDIVIDUALATTENDANCE</a></li>
					<li><a href="cp.html">CHANGE PASSWORD</a></li>
                    <li><a href="logout.php">LOGOUT</a></li>
				</ul>
				
				
			</div>
		</nav>	
	</body>
</html>
