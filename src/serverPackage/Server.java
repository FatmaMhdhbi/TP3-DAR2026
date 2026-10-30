package serverPackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// Classe principale du serveur
public class Server {

    public static void main(String[] args) {

        // Compteur du nombre de clients connectés
        int numeroClient = 0;

        // Création du serveur TCP sur le port 1234
        try (ServerSocket serverSocket = new ServerSocket(1234)) {

            System.out.println("Serveur en attente des clients...");

            // Le serveur reste actif pour accepter plusieurs clients
            while (true) {

                // Attendre la connexion d'un nouveau client
                Socket socket = serverSocket.accept();

                // Attribuer un numéro au client connecté
                numeroClient++;

                // Afficher les informations du client
                System.out.println("Client n°" + numeroClient
                        + " connecté depuis : "
                        + socket.getRemoteSocketAddress());

                // Créer un thread indépendant pour ce client
                ClientProcess clientProcess =
                        new ClientProcess(socket, numeroClient);

                // Démarrer le thread
                clientProcess.start();
            }

        } catch (IOException e) {
            System.out.println("Erreur du serveur : " + e.getMessage());
        }
    }
}

// Classe qui gère la communication avec un client
class ClientProcess extends Thread {

    private Socket socket;
    private int numeroClient;

    // Constructeur : recevoir la socket et le numéro du client
    public ClientProcess(Socket socket, int numeroClient) {
        this.socket = socket;
        this.numeroClient = numeroClient;
    }

    @Override
    public void run() {

        try {
            // Créer un flux pour envoyer du texte au client
            PrintWriter pw = new PrintWriter(
                    socket.getOutputStream(), true);

            // Envoyer au client son numéro de connexion
            pw.println("Vous êtes le client n°" + numeroClient);

            System.out.println("Message envoyé au client n°"
                    + numeroClient);

            // Fermer la connexion avec ce client
            pw.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Erreur avec le client n°"
                    + numeroClient + " : " + e.getMessage());
        }
    }
}
