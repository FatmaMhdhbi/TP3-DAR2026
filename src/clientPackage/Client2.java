package clientPackage;

import operationPackage.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

// Client de l'application calculatrice
public class Client2 {

    public static void main(String[] args) {

        // Lire les données saisies par l'utilisateur
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Donner le premier nombre : ");
            double a = scanner.nextDouble();

            System.out.print("Donner l'operateur (+, -, *, /) : ");
            String operateur = scanner.next();

            System.out.print("Donner le deuxieme nombre : ");
            double b = scanner.nextDouble();

            // Créer l'objet contenant les données de l'opération
            Operation op = new Operation(a, operateur, b);

            // Connexion au serveur sur le port 1235
            try (Socket socket = new Socket("localhost", 1235)) {

                System.out.println("Connexion au serveur réussie !");

                // Envoyer l'objet Operation au serveur
                ObjectOutputStream oos =
                        new ObjectOutputStream(socket.getOutputStream());

                oos.writeObject(op);
                oos.flush();

                // Recevoir la réponse du serveur
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());

                // La réponse est un objet String
                String resultat = (String) ois.readObject();

                // Afficher le résultat
                System.out.println("Réponse du serveur : " + resultat);

                // Fermer les flux
                ois.close();
                oos.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur du client : " + e.getMessage());
        }
    }
}
