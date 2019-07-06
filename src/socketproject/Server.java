package socketproject;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    static File file = new File("questions.txt");
    static Map<String, String> data = DB_Connection.getData();
    static Map<String, String> questions = new HashMap<>();

    public static void main(String args[]) throws FileNotFoundException, IOException, SQLException {
        BufferedReader br = null;
        try {
            System.out.println("Server is running...");
            System.out.println("Waiting for someone to connect...");
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
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
                bw.flush();
            }
            br = new BufferedReader(new FileReader(file));
            String row = br.readLine();
            while (row != null && !row.isEmpty()) {
                String question = "";
                int correctAns = -1;
                for (int i = 0; i < 4; i++) {
                    if (row.contains("(correct)")) {
                        row = row.substring(0, row.length() - 10);
                        correctAns = i;
                    }
                    question += row + "\n";
                    row = br.readLine();
                }
                questions.put(question, String.valueOf(correctAns));
                if (row.equalsIgnoreCase("---")) {
                    row = br.readLine();
                    continue;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ServerSocket ss = new ServerSocket(2525);
            Socket s = ss.accept();

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            String hasAcc = dis.readUTF();
            while (true) {
                if (hasAcc.equalsIgnoreCase("Y")) {
                    String username = dis.readUTF();
                    if (data.containsKey(username)) {
                        dos.writeUTF("OK");
                        String password = dis.readUTF();
                        if (data.containsValue(password)) {
                            dos.writeUTF("---------Welcome " + username + " to online quiz");
                            while (true) {
                                String choice = dis.readUTF();
                                boolean exit = choice.equals("4");
                                switch (choice) {
                                    case "1":
                                        int points = 0;
                                        for (Map.Entry<String, String> entry : questions.entrySet()) {
                                            dos.writeUTF(entry.getKey());
                                            String answer = dis.readUTF();
                                            if (answer.equalsIgnoreCase(entry.getValue())) {
                                                dos.writeUTF("-------------------------------(Correct you got point)");
                                                points++;
                                            } else {
                                                dos.writeUTF("-------------------------------(Not correct)");
                                            }
                                        }
                                        DB_Connection.updateScore(username, points);
                                        dos.writeUTF("-------------------------------Quiz finished, points: " + points);
                                        break;
                                    case "2":
                                        int points2 = 0;
                                        for (Map.Entry<String, String> entry : questions.entrySet()) {
                                            dos.writeUTF(entry.getKey());
                                            String answer = dis.readUTF();
                                            if (answer.equalsIgnoreCase(entry.getValue())) {
                                                dos.writeUTF("-------------------------------(Correct you got point)");
                                                points2++;
                                            } else {
                                                dos.writeUTF("-------------------------------(Not correct)");
                                            }
                                        }
                                        DB_Connection.updateScore(username, points2);
                                        dos.writeUTF("-------------------------------Quiz finished, points: " + points2);
                                        break;
                                    case "3":
                                        dos.writeUTF(String.valueOf(DB_Connection.getScore(username)));
                                        break;
                                    case "4":
                                        try {
                                            s.close();
                                            dos.close();
                                            dis.close();
                                        } catch (Exception ex) {
                                            System.out.println("Server is not running / Couldn't connect");
                                        }
                                        break;
                                }
                                if (exit) {
                                    System.out.println("Connection closed!");
                                    break;
                                }

                            }
                        } else {
                            System.out.println("Failed to login (password: " + password + ")");
                            dos.writeUTF("NAN");
                        }
                    } else {
                        System.out.println("Failed to login (username: " + username + ")");
                        dos.writeUTF("NAN");
                    }
                } else {
                    while (true) {
                        String username = dis.readUTF();
                        if (data.containsKey(username)) {
                            dos.writeUTF("Username exists! choose another");
                        } else {
                            dos.writeUTF("OK");
                            String password = dis.readUTF();
                            DB_Connection.insertUser(username, password);
                            dos.writeUTF("---------Welcome " + username + " to online quiz");
                            while (true) {
                                String choice = dis.readUTF();
                                boolean exit = choice.equals("3");
                                switch (choice) {
                                    case "1":
                                        int points = 0;
                                        for (Map.Entry<String, String> entry : questions.entrySet()) {
                                            dos.writeUTF(entry.getKey());
                                            String answer = dis.readUTF();
                                            if (answer.equalsIgnoreCase(entry.getValue())) {
                                                dos.writeUTF("-------------------------------(Correct you got point)");
                                                points++;
                                            } else {
                                                dos.writeUTF("-------------------------------(Not correct)");
                                            }
                                        }
                                        dos.writeUTF("-------------------------------Quiz finished, points: " + points);
                                        break;
                                    case "2":
                                        int points2 = 0;
                                        for (Map.Entry<String, String> entry : questions.entrySet()) {
                                            dos.writeUTF(entry.getKey());
                                            String answer = dis.readUTF();
                                            if (answer.equalsIgnoreCase(entry.getValue())) {
                                                dos.writeUTF("-------------------------------(Correct you got point)");
                                                points2++;
                                            } else {
                                                dos.writeUTF("-------------------------------(Not correct)");
                                            }
                                        }
                                        dos.writeUTF("-------------------------------Quiz finished, points: " + points2);
                                        break;
                                    case "3":
                                        try {
                                            s.close();
                                            dos.close();
                                            dis.close();
                                        } catch (Exception ex) {
                                            System.out.println("Server is not running / Couldn't connect");
                                        }
                                        break;
                                }
                                if (exit) {
                                    System.out.println("Connection closed!");
                                    break;
                                }

                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("\nClient closed connection");
        }
    }
}



