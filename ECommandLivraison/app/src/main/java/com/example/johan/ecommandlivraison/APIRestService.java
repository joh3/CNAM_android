package com.example.johan.ecommandlivraison;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIRestService {

    @GET("client/commande/{idCommande}")
    Call<List<Client>> infoClient(@Path("idCommande") String idCommande);

    @GET("tournee/{id}")
    Call<List<Commande>> listeCommandes(@Path("id") int id);

    @GET("livreur/{id}")
    Call<List<Livreur>> infoLivreur(@Path("id") String id);

    @GET("article/commande/{idCommande}")
    Call<List<Article>> listeArticle(@Path("idCommande") String idCommande);

    @FormUrlEncoded
    @POST("commande/livraison/")
    Call<Object> postEtatCmd(@Field("idCommande") String idCommande, @Field("etatCommande") String etatCommande);

    @FormUrlEncoded
    @POST("commande/livraison/debut")
    Call<Object> postDebutTrn(@Field("idTournee") String idTournee);

    @FormUrlEncoded
    @POST("commande/livraison/fin")
    Call<Object> postFinTrn(@Field("idTournee") String idTournee);

    @FormUrlEncoded
    @POST("/livreur/etat")
    Call<Object> postEtatLiv(@Field("statut") String statut, @Field("idLivreur") String idLivreur);
}
