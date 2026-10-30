package operationPackage;

import java.io.Serializable;

// Classe représentant une opération arithmétique
public class Operation implements Serializable {

    // Identifiant de la classe sérialisable
    private static final long serialVersionUID = 1L;

    // Premier nombre
    private double a;

    // Opérateur : +, -, * ou /
    private String operateur;

    // Deuxième nombre
    private double b;

    // Constructeur permettant de créer une opération
    public Operation(double a, String operateur, double b) {
        this.a = a;
        this.operateur = operateur;
        this.b = b;
    }

    // Retourner le premier nombre
    public double getA() {
        return a;
    }

    // Retourner l'opérateur
    public String getOperateur() {
        return operateur;
    }

    // Retourner le deuxième nombre
    public double getB() {
        return b;
    }
}
