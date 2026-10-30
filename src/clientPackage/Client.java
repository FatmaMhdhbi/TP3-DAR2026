package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    private static final int PORT = 1234;

    public static void main(String[] args) {

        // Adresse du serveur : argument 1 (sinon localhost)
        String host = (args.length > 0) ? args[0] : "localhost";

        try (Socket socket = new Socket()) {

            // Adresse distante = InetAddress + port
            InetSocketAddress adresse =
                    new InetSocketAddress(InetAddress.getByName(host), PORT);

            // Connexion avec un délai maximum de 5 secondes
            socket.connect(adresse, 5000);

            System.out.println("Connexion au serveur " + adresse + " réussie !");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            System.out.println("Message du serveur : " + br.readLine());

        } catch (IOException e) {
            System.out.println("Erreur du client : " + e.getMessage());
        }
    }
}