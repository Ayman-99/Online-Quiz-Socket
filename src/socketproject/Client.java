package socketproject;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    static {
        System.out.println("Client is running...");
        System.out.println("Waiting to connect...");
    }

    private static Socket s = null;
    private static DataInputStream dis = null;
    private static DataOutputStream dos = null;

    public static void main(String[] args) {

        try {
            InetAddress ip = InetAddress.getLocalHost();
            int port = 2525;
            Scanner scanner = new Scanner(System.in);

            s = new Socket(ip, port);

            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Connected to " + s.getInetAddress() + " and streams have been initilzied (Enter bye to close connection)");
            boolean authpass = false;
            int choice = -1;
            System.out.println("Do you have account? Y/N");
            String hasAcc = scanner.next();
            dos.writeUTF(hasAcc);
            while (true) {
                if (authpass) {
                    System.out.println("Quiz features initializing...");
                    System.out.println("1- Start quiz\n2- Redo quiz\n3- Show latest quiz score\n4- close connection");
                    System.out.print("Enter 1 or 2 or 3 or 4: ");
                    choice = scanner.nextInt();
                    dos.writeUTF(String.valueOf(choice));
                    boolean exit = choice == 4;
                    switch (choice) {
                        case 1:
                            boolean more1 = true;
                            String response1 = "";
                            while (more1) {
                                response1 = dis.readUTF();
                                if (response1.startsWith("-------------------------------")) {
                                    more1 = false;
                                } else {
                                    System.out.print(response1);
                                    String answer1 = scanner.next();
                                    dos.writeUTF(answer1);
                                    System.out.println(dis.readUTF());
                                }
                            }
                            System.out.println(response1);
                            break;
                        case 2:
                            boolean more2 = true;
                            String response2 = "";
                            while (more2) {
                                response2 = dis.readUTF();
                                if (response2.startsWith("-------------------------------")) {
                                    more2 = false;
                                } else {
                                    System.out.print(response2);
                                    String answer1 = scanner.next();
                                    dos.writeUTF(answer1);
                                    System.out.println(dis.readUTF());
                                }
                            }
                            System.out.println(response2);
                            break;
                        case 3:
                            System.out.println("---------------------------Your latest score in last quiz is " + dis.readUTF());
                            break;
                        case 4:
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
                } else {
                    if (hasAcc.equalsIgnoreCase("Y")) {
                        System.out.print("Enter username: ");
                        String username = scanner.next(); //Getting username from user
                        if (username.equals("bye")) {
                            break;
                        }
                        dos.writeUTF(username); //Sending it to the server to verfiy it
                        String response1 = dis.readUTF(); //Getting response
                        if (response1.equalsIgnoreCase("NAN")) { //if its NAN means the user didn't register
                            System.out.println("Username does not exist (Try again)");
                        } else { //Username exists, we check for password
                            System.out.print("Enter password: ");
                            String password = scanner.next();
                            if (password.equals("bye")) {
                                break;
                            }
                            dos.writeUTF(password);
                            String response2 = dis.readUTF();
                            if (response2.equalsIgnoreCase("NAN")) { //Password is wrong he has to register
                                System.out.println("Password is wrong");
                            } else { //All good start quiz
                                System.out.println(response2);
                                System.out.print("Do you want to start math exam? Y / N ?");
                                String flag = scanner.next();
                                dos.writeUTF(flag);
                                if (flag.equalsIgnoreCase("Y")) {
                                    authpass = true;
                                } else {
                                    System.exit(0);
                                }
                            }
                        }
                    } else {
                        System.out.print("Enter username to register: ");
                        String username = scanner.next(); //Getting username from user
                        if (username.equals("bye")) {
                            break;
                        }
                        dos.writeUTF(username); //Sending it to the server to verfiy it
                        String response1 = dis.readUTF(); //Getting response
                        if (response1.equalsIgnoreCase("Username exists! choose another")) { //if its NAN means the user didn't register
                            System.out.println("Username exists! choose another");
                        } else { //Username exists, we check for password
                            System.out.print("Enter password to register: ");
                            String password = scanner.next();
                            dos.writeUTF(password);
                            System.out.println(dis.readUTF());
                            System.out.print("Do you want to start math exam? Y / N ?");
                            String flag = scanner.next();
                            dos.writeUTF(flag);
                            if (flag.equalsIgnoreCase("Y")) {
                                authpass = true;
                            } else {
                                System.exit(0);
                            }
                        }
                    }
                }
            }
        } catch (IOException ioException) {
            System.out.println("Server ended the connection!");
        } finally {
            try {
                s.close();
                dos.close();
                dis.close();
            } catch (Exception ex) {
                System.out.println("Server is not running / Couldn't connect");
            }
        }
    }
}





