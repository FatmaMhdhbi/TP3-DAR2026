package clientPackage;

import operationPackage.Operation;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Lance plusieurs clients en parallèle pour tester le compteur global
public class ClientTest {

    public static void main(String[] args) throws InterruptedException {

        String host = (args.length > 0) ? args[0] : "localhost";
        int nbClients = 5;
        int nbOperations = 10;

        Thread[] clients = new Thread[nbClients];

        for (int i = 0; i < nbClients; i++) {
            final int id = i + 1;

            clients[i] = new Thread(() -> {
                try (Socket socket = new Socket(host, 1235)) {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.flush();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                    for (int j = 1; j <= nbOperations; j++) {
                        oos.writeObject(new Operation(id, "+", j));
                        oos.flush();
                        ois.readObject();
                    }
                    oos.writeObject("FIN");
                    oos.flush();
                    System.out.println("Client test " + id + " terminé");
                } catch (Exception e) {
                    System.out.println("Erreur client test " + id + " : " + e.getMessage());
                }
            });
            clients[i].start();   // tous démarrent presque en même temps
        }

        for (Thread t : clients) {
            t.join();
        }
        System.out.println("Test terminé : " + (nbClients * nbOperations)
                + " opérations attendues");
    }
}