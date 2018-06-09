package com.example.johan.ecommandlivraison;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Commande implements Serializable {
    private int idCommande;
    private float prixTotal;
    private String adresse;
    private String codePostal;
    private String ville;
    private int ordre;
    private List<Article> articles = new ArrayList<Article>();

    public Commande(){}

    public Commande(int idCommande, float prixTotal, String adresse, String codePostal, String ville, int ordre) {
        this.idCommande = idCommande;
        this.prixTotal = prixTotal;
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
        this.ordre = ordre;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public float getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(float prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getOrdre() { return ordre;}

    public void setOrdre(int ordre) { this.ordre = ordre; }

    public void setArticles(List<Article> liste){
        this.articles.addAll(liste);
    }

    public Article getArticle(int index){
        return this.articles.get(index);
    }

    public float sommePrix(){
        float total = 0;
        for (Article a: articles) {
            total += (a.getPrixHT() * a.getQuantite());
        }
        return total;
    }
}
