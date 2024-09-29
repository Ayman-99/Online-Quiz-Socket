package socketproject;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static Socket socket = null;
    private static DataInputStream dataInputStream = null;
    private static DataOutputStream dataOutputStream = null;
    private static Scanner scanner = new Scanner(System.in);

    static {
        System.out.println("Client is running...");
        System.out.println("Waiting to connect...");
    }

    public static void main(String[] args) {
        try {
            initializeConnection();
            boolean isAuthenticated = false;

            System.out.println("Do you have an account? Y/N");
            String hasAccount = scanner.next();
            dataOutputStream.writeUTF(hasAccount);

            while (true) {
                if (isAuthenticated) {
                    showQuizMenu();
                    int choice = scanner.nextInt();
                    dataOutputStream.writeUTF(String.valueOf(choice));
                    if (choice == 4) {
                        closeConnection();
                        break;
                    }
                    handleQuizChoice(choice);
                } else {
                    isAuthenticated = handleAuthentication(hasAccount);
                }
            }
        } catch (IOException ioException) {
            System.out.println("Server ended the connection!");
        } finally {
            closeConnection();
        }
    }

    private static void initializeConnection() throws IOException {
        InetAddress ip = InetAddress.getLocalHost();
        int port = 2525;
        socket = new Socket(ip, port);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("Connected to " + socket.getInetAddress() + " and streams have been initialized (Enter 'bye' to close connection)");
    }

    private static boolean handleAuthentication(String hasAccount) throws IOException {
        if (hasAccount.equalsIgnoreCase("Y")) {
            return authenticateExistingUser();
        } else {
            return registerNewUser();
        }
    }

    private static boolean authenticateExistingUser() throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.next();
        if (username.equals("bye")) return false;

        dataOutputStream.writeUTF(username);
        String response = dataInputStream.readUTF();

        if (response.equalsIgnoreCase("NAN")) {
            System.out.println("Username does not exist. Try again.");
            return false;
        } else {
            return verifyPassword();
        }
    }

    private static boolean verifyPassword() throws IOException {
        System.out.print("Enter password: ");
        String password = scanner.next();
        if (password.equals("bye")) return false;

        dataOutputStream.writeUTF(password);
        String response = dataInputStream.readUTF();

        if (response.equalsIgnoreCase("NAN")) {
            System.out.println("Password is incorrect.");
            return false;
        } else {
            System.out.println(response);
            return startQuiz();
        }
    }

    private static boolean registerNewUser() throws IOException {
        System.out.print("Enter username to register: ");
        String username = scanner.next();
        if (username.equals("bye")) return false;

        dataOutputStream.writeUTF(username);
        String response = dataInputStream.readUTF();

        if (response.equalsIgnoreCase("Username exists! choose another")) {
            System.out.println(response);
            return false;
        } else {
            System.out.print("Enter password to register: ");
            String password = scanner.next();
            dataOutputStream.writeUTF(password);
            System.out.println(dataInputStream.readUTF());
            return startQuiz();
        }
    }

    private static boolean startQuiz() throws IOException {
        System.out.print("Do you want to start the math exam? Y/N: ");
        String flag = scanner.next();
        dataOutputStream.writeUTF(flag);
        if(flag.equalsIgnoreCase("N")) {
        	closeConnection();
        }
        return flag.equalsIgnoreCase("Y");
    }

    private static void showQuizMenu() {
        System.out.println("Quiz features initializing...");
        System.out.println("1- Start quiz");
        System.out.println("2- Redo quiz");
        System.out.println("3- Show latest quiz score");
        System.out.println("4- Close connection");
        System.out.print("Enter 1, 2, 3, or 4: ");
    }

    private static void handleQuizChoice(int choice) throws IOException {
        switch (choice) {
            case 1:
            case 2:
                handleQuiz();
                break;
            case 3:
                showLatestScore();
                break;
            default:
                System.out.println("Invalid choice, please try again.");
                break;
        }
    }

    private static void handleQuiz() throws IOException {
        boolean moreQuestions = true;
        while (moreQuestions) {
            String question = dataInputStream.readUTF();
            if (question.startsWith("-------------------------------")) {
                moreQuestions = false;
            } else {
                System.out.print(question);
                String answer = scanner.next();
                dataOutputStream.writeUTF(answer);
                System.out.println(dataInputStream.readUTF());
            }
        }
    }

    private static void showLatestScore() throws IOException {
        System.out.println("---------------------------Your latest score in the last quiz is: " + dataInputStream.readUTF());
    }

    private static void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (dataInputStream != null) dataInputStream.close();
            System.out.println("Connection closed!");
        } catch (IOException e) {
            System.out.println("Failed to close connection.");
        }
    }
}
