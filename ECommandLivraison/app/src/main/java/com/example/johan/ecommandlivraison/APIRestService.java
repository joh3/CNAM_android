package com.example.johan.ecommandlivraison;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIRestService {

    public static final String ENDPOINT = "http://192.168.1.53:3000/";

    @GET("client/commande/{idCommande}")
    Call<List<Client>> infoClient(@Path("idCommande") String idCommande);

    @GET("tournee/{id}")
    Call<List<Commande>> listeCommandes(@Path("id") int id);

    @GET("livreur/{id}")
    Call<List<Livreur>> infoLivreur(@Path("id") String id);

    @GET("article/commande/{idCommande}")
    Call<List<Article>> listeArticle(@Path("idCommande") String idCommande);
}
