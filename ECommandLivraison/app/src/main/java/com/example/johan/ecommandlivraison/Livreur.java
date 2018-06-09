package com.example.johan.ecommandlivraison;

public class Livreur {
    private String idLivreur;
    private String nom;
    private String prenom;
    private String email;

    public Livreur(String nom, String prenom, String email, String idLivreur) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.idLivreur = idLivreur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdLivreur() {
        return idLivreur;
    }

    public void setIdLivreur(String idLivreur) {
        this.idLivreur = idLivreur;
    }
}
