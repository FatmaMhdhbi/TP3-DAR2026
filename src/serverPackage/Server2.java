package serverPackage;

import operationPackage.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.EOFException;

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
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos =
                        new ObjectOutputStream(socket.getOutputStream())
        ) {
            // Traiter les opérations de ce client jusqu'à "FIN" ou déconnexion
            while (true) {

                Object recu = ois.readObject();

                // Tout objet autre qu'une Operation termine la session
                if (!(recu instanceof Operation)) {
                    break;
                }

                Operation op = (Operation) recu;
                double a = op.getA();
                String operateur = op.getOperateur();
                double b = op.getB();
                double resultat;

                switch (operateur) {
                    case "+": resultat = a + b; break;
                    case "-": resultat = a - b; break;
                    case "*": resultat = a * b; break;
                    case "/":
                        if (b == 0) {
                            oos.writeObject("Erreur : division par zero");
                            oos.flush();
                            continue;
                        }
                        resultat = a / b;
                        break;
                    default:
                        oos.writeObject("Erreur : operateur invalide");
                        oos.flush();
                        continue;
                }

                // Compteur global : accès synchronisé
                int numeroOperation = Server2.incrementerCompteur();

                System.out.println("Opération n°" + numeroOperation
                        + " [" + socket.getRemoteSocketAddress() + " / "
                        + getName() + "] : "
                        + a + " " + operateur + " " + b + " = " + resultat);

                oos.writeObject("Résultat = " + resultat);
                oos.flush();
            }

        } catch (EOFException e) {
            // Le client s'est déconnecté : situation normale
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur pendant le traitement du client : "
                    + e.getMessage());
        } finally {
            System.out.println("Client déconnecté : "
                    + socket.getRemoteSocketAddress());
            try { socket.close(); } catch (IOException ignored) { }
        }
    }
}