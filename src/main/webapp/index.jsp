<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Payment API</title>
		<link rel="stylesheet" href="style.css" />
	</head>
	<body>
		<h1>Withdraw money by M-Birr</h1>
		<form action="payment" method="POST">
			<label><b>Your Name: </b></label>
			<input type="text" name="name" id="box" required/><br /><br />
			<label><b>Mbirr Registered Phone Number: </b></label>
			<input type="tel" name="tel" id="box" required/><br /><br />
			<label><b>Amount to withdraw: </b></label>
			<input type="number" name="amount" id="box" required/><br /><br />
			<input type="submit" name="submit" value="Withdraw" id="submit" /><br/><br />
		</form>
	</body>
</html>