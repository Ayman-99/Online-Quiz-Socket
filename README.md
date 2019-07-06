Initial version of upcoming GUI application. This version implements client/server model. Done by socket programming [java].

## Quick start

- Download the files from github
- Import the project using NetBeans
- Import the database in your mysql server
- Update src > socketproject > DB_Connection.java with the database details[Line 31].
- Open src > socketproject > Server.java > Run 
- Open src > socketproject > Client.java > Run 


## Notes:
- Server must be run first then client.
- Once server.java execute it will automatically creates txt file with inital questions. (Add more with same format if you want).
- Users are stored in the database (can be changed).
- Application supports 1 to 1 connection (No threads)

## Features:
Client:
-	Connect to server
-	Create new contact by providing <new user> command
-	And password by providing <psw= password>
-	Login to the server if he already registered by providing the user name and password 
-	Asking for the math exam
-	Can redo the exam again
-	Can show the scores of his tests
-	Close the connection.
  
Server:
-	Wait for a client connection.
-	Store the user name and password of the users
-	Verify if the user in registered 
-	When a client connection is accepted (if user is registered) send an acknowledgement (a welcome message).
-	From Questions database, server sends the first test questions plus three possible answers to the client.
-	Receives the answer from the client. Stores and evaluates the answer.
-	After that the server sends score to the client and if he like to repeat the test
-	Store the score of the test for that user

```
Need support ? 

Email: aymanhun@gmail.com

