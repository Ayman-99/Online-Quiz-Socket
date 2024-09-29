package socketproject;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static Map<String, User> usersData = new HashMap<>(); // For user credentials and scores
    private static Map<String, String> questions = new HashMap<>(); // For storing quiz questions and correct answers
    private static String currentUser = ""; // The current logged-in user

    public static void main(String[] args) {
        try {
            loadUsers();
            System.out.println("Server is running...");
            System.out.println("Waiting for someone to connect...");
            checkAndLoadQuestions();

            ServerSocket serverSocket = new ServerSocket(2525);
            Socket socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            handleClientConnection(dataInputStream, dataOutputStream, socket);

        } catch (IOException e) {
            System.out.println("Error in server connection: " + e.getMessage());
        }
    }

    private static void handleClientConnection(DataInputStream dis, DataOutputStream dos, Socket socket) throws IOException {
        try {
            String hasAccount = dis.readUTF();

            if (hasAccount.equalsIgnoreCase("bye")) {
                closeConnection(socket, dos, dis);
                return;
            }

            if (hasAccount.equalsIgnoreCase("Y")) {
                authenticateUser(dis, dos);
            } else {
                registerUser(dis, dos);
            }

            while (true) {
                String choice = dis.readUTF();
                boolean exit = choice.equals("4");

                switch (choice) {
                    case "1":
                    	handleQuiz(dis, dos, choice);
                    case "2":
                        handleQuiz(dis, dos, choice);
                        break;
                    case "3":
                        showLatestScore(dos);
                        break;
                    case "4":
                        closeConnection(socket, dos, dis);
                        break;
                    default:
                        dos.writeUTF("Invalid choice. Please select again.");
                        break;
                }

                if (exit) {
                    System.out.println("Connection closed!");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client closed the connection.");
        } finally {
            saveUsers();
        }
    }

    private static void authenticateUser(DataInputStream dis, DataOutputStream dos) throws IOException {
        while (true) {
            String username = dis.readUTF();

            if (usersData.containsKey(username)) {
                dos.writeUTF("OK");
                String password = dis.readUTF();

                if (usersData.get(username).getPassword().equals(password)) {
                    currentUser = username;
                    dos.writeUTF("Authentication successful!");
                    break;
                } else {
                    dos.writeUTF("NAN"); // Wrong password
                }
            } else {
                dos.writeUTF("NAN"); // User not found
            }
        }
    }

    private static void registerUser(DataInputStream dis, DataOutputStream dos) throws IOException {
        while (true) {
            String username = dis.readUTF();

            if (usersData.containsKey(username)) {
                dos.writeUTF("Username exists! choose another");
            } else {
                dos.writeUTF("OK");
                String password = dis.readUTF();
                usersData.put(username, new User(username, password, "0"));
                saveUsers();
                currentUser = username;
                dos.writeUTF("Welcome " + username + " to the online quiz");
                break;
            }
        }
    }

    private static void handleQuiz(DataInputStream dis, DataOutputStream dos, String choice) throws IOException {
        int points = 0;

        for (Map.Entry<String, String> entry : questions.entrySet()) {
            dos.writeUTF(entry.getKey());
            String answer = dis.readUTF();

            if (answer.equals(entry.getValue())) {
                dos.writeUTF("-------------------------------(Correct you got a point)");
                points++;
            } else {
                dos.writeUTF("-------------------------------(Not correct)");
            }
        }

        usersData.get(currentUser).addPoint();
        dos.writeUTF("-------------------------------Quiz finished, points: " + points);
    }

    private static void showLatestScore(DataOutputStream dos) throws IOException {
        dos.writeUTF("Your score: " + usersData.get(currentUser).getScore());
    }

    private static void closeConnection(Socket socket, DataOutputStream dos, DataInputStream dis) throws IOException {
        saveUsers();
        if (socket != null) socket.close();
        if (dos != null) dos.close();
        if (dis != null) dis.close();
    }

    private static void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("users.txt")))) {
            for (Map.Entry<String, User> entry : usersData.entrySet()) {
                bw.write(entry.getValue().getUsername() + " " + entry.getValue().getPassword() + " " + entry.getValue().getScore() + "\n");
                bw.flush();
            }
        } catch (IOException e) {
            System.out.println("Error saving users data: " + e.getMessage());
        }
    }

    private static void loadUsers() {
        File file = new File("users.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\\s"); // Username Password Score
                    usersData.put(tokens[0], new User(tokens[0], tokens[1], tokens[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users data: " + e.getMessage());
        }
    }

    private static void checkAndLoadQuestions() {
        File file = new File("questions.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write("Q1: (A + B)*(A+B) \n"
                            + "1. A*A + B*B \n"
                            + "2. A*A +A*B + B*B \n"
                            + "3. A*A +2*A*B + B*B (correct) \n"
                            + "---\n"
                            + "Q2: (A + B)*(A - B) \n"
                            + "1. A*A + 2*B*B \n"
                            + "2. A*A - B*B (correct) \n"
                            + "3. A*A -2*A*B + B*B \n"
                            + "---\n"
                            + "Q3: sin(x)*sin(x) + cos(x)*cos(x) \n"
                            + "1. 1 (correct) \n"
                            + "2. 2 \n"
                            + "3. 3\n"
                            + "---");
                }
            } catch (IOException e) {
                System.out.println("Error creating questions file: " + e.getMessage());
            }
        }

        loadQuestions();
    }

    private static void loadQuestions() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File("questions.txt")))) {
            String row = br.readLine();

            while (row != null && !row.isEmpty()) {
                StringBuilder question = new StringBuilder();
                int correctAns = -1;

                for (int i = 0; i < 4; i++) {
                    if (row.contains("(correct)")) {
                        row = row.substring(0, row.length() - 10);
                        correctAns = i;
                    }
                    question.append(row).append("\n");
                    row = br.readLine();
                }

                questions.put(question.toString(), String.valueOf(correctAns));

                if (row != null && row.equalsIgnoreCase("---")) {
                    row = br.readLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading questions: " + e.getMessage());
        }
    }
}
