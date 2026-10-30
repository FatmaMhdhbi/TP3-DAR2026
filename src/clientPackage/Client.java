package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// Classe du client
public class Client {

    public static void main(String[] args) {

        // Connexion au serveur sur le port 1234
        try (Socket socket = new Socket("localhost", 1234)) {

            System.out.println("Connexion au serveur réussie !");

            // Créer un flux pour recevoir le message du serveur
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Lire le numéro envoyé par le serveur
            String message = br.readLine();

            // Afficher le message reçu
            System.out.println("Message du serveur : " + message);

        } catch (IOException e) {
            System.out.println("Erreur du client : " + e.getMessage());
        }
    }
}
