package com.example.johan.ecommandlivraison;

public class Article {
    private String libelle;
    private int quantite;
    private float prixHT;

    public Article(String libelle, int quantite, float prixHT) {
        this.libelle = libelle;
        this.quantite = quantite;
        this.prixHT = prixHT;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public float getPrixHT() {
        return prixHT;
    }

    public void setPrixHT(float prixHT) {
        this.prixHT = prixHT;
    }
}
