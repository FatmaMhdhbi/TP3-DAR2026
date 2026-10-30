package clientPackage;

import operationPackage.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {

    private static final int PORT = 1235;

    public static void main(String[] args) {

        String host = (args.length > 0) ? args[0] : "localhost";

        try (Socket socket = new Socket();
             Scanner scanner = new Scanner(System.in)) {

            InetSocketAddress adresse =
                    new InetSocketAddress(InetAddress.getByName(host), PORT);
            socket.connect(adresse, 5000);
            System.out.println("Connexion au serveur " + adresse + " réussie !");

            // Ordre important : client crée d'abord le flux de sortie
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            while (true) {
                System.out.print("Premier nombre (ou q pour quitter) : ");
                String saisie = scanner.next();
                if (saisie.equalsIgnoreCase("q")) {
                    oos.writeObject("FIN");   // signaler la fin au serveur
                    oos.flush();
                    break;
                }
                double a = Double.parseDouble(saisie);

                System.out.print("Opérateur (+, -, *, /) : ");
                String operateur = scanner.next();

                System.out.print("Deuxième nombre : ");
                double b = scanner.nextDouble();

                oos.writeObject(new Operation(a, operateur, b));
                oos.flush();

                String reponse = (String) ois.readObject();
                System.out.println("Réponse du serveur : " + reponse);
            }

        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            System.out.println("Erreur du client : " + e.getMessage());
        }
    }
}