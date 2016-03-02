<?php
$servername = "localhost";
$username = "root";
$password = "RSVRINKKHESH";
$dbname = "project";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "UPDATE namelist SET name='Doe' WHERE id=1";
$conn->query($sql);

$conn->close();
?> 
