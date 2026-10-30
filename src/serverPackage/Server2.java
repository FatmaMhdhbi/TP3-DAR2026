package serverPackage;

import operationPackage.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Serveur multithread pour la calculatrice
public class Server2 {

    // Port utilisé par le serveur
    private static final int PORT = 1235;

    // Compteur global partagé entre tous les threads
    private static int compteur = 0;

    public static void main(String[] args) {

        // Création du serveur
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Serveur calculatrice en attente des clients...");

            // Accepter plusieurs connexions
            while (true) {

                // Attendre la connexion d'un client
                Socket socket = serverSocket.accept();

                System.out.println("Client connecté : "
                        + socket.getRemoteSocketAddress());

                // Créer un thread pour traiter ce client
                ClientProcess2 process = new ClientProcess2(socket);

                // Démarrer le thread
                process.start();
            }

        } catch (IOException e) {
            System.out.println("Erreur serveur : " + e.getMessage());
        }
    }

    // Méthode synchronisée pour incrémenter le compteur
    public static synchronized int incrementerCompteur() {
        compteur++;
        return compteur;
    }
}

// Thread chargé de traiter les opérations d'un client
class ClientProcess2 extends Thread {

    private Socket socket;

    public ClientProcess2(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (
                // Recevoir l'objet Operation envoyé par le client
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());

                // Envoyer l'objet résultat au client
                ObjectOutputStream oos =
                        new ObjectOutputStream(socket.getOutputStream())
        ) {

            // Lire l'objet reçu
            Operation op = (Operation) ois.readObject();

            // Récupérer les données de l'opération
            double a = op.getA();
            String operateur = op.getOperateur();
            double b = op.getB();

            // Calculer le résultat
            double resultat;

            switch (operateur) {
                case "+":
                    resultat = a + b;
                    break;

                case "-":
                    resultat = a - b;
                    break;

                case "*":
                    resultat = a * b;
                    break;

                case "/":
                    if (b == 0) {
                        oos.writeObject("Erreur : division par zero");
                        oos.flush();
                        return;
                    }
                    resultat = a / b;
                    break;

                default:
                    oos.writeObject("Erreur : operateur invalide");
                    oos.flush();
                    return;
            }

            // Incrémenter le compteur global de manière synchronisée
            int numeroOperation = Server2.incrementerCompteur();

            // Afficher les informations dans la console du serveur
            System.out.println("Opération n°" + numeroOperation
                    + " : " + a + " " + operateur + " " + b
                    + " = " + resultat);

            // Envoyer le résultat au client
            oos.writeObject("Résultat = " + resultat);
            oos.flush();

        } catch (IOException | ClassNotFoundException e) {

            System.out.println("Erreur pendant le traitement du client : "
                    + e.getMessage());

        } finally {

            // Fermer la connexion avec le client
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Erreur de fermeture : "
                        + e.getMessage());
            }
        }
    }
}
