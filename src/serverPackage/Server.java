package serverPackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

    public ClientProcess(Socket socket, int numeroClient) {
        this.socket = socket;
        this.numeroClient = numeroClient;
    }

    @Override
    public void run() {

        try (
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            // Envoyer au client son numéro d'ordre
            pw.println("Vous êtes le client n°" + numeroClient);
            System.out.println("Message envoyé au client n°" + numeroClient);

            // Rester connecté jusqu'à la déconnexion du client (ou "quit")
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.equalsIgnoreCase("quit")) {
                    break;
                }
                System.out.println("Client n°" + numeroClient + " dit : " + ligne);
                pw.println("Echo : " + ligne);
            }

        } catch (IOException e) {
            System.out.println("Erreur avec le client n°" + numeroClient
                    + " : " + e.getMessage());
        } finally {
            System.out.println("Client n°" + numeroClient + " déconnecté");
            try { socket.close(); } catch (IOException ignored) { }
        }
    }
}

