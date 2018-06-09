package com.example.johan.ecommandlivraison;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListeCommandeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        APIRestService instanceApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_liste_commande);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        instanceApi = initAPIService();
        chargerDonnees(instanceApi);

    }


    public void chargerDonnees(APIRestService api){
        remplirListeCommande(api);
        remplirInfosLivreur(api,"1");
    }


    public void remplirListeCommande(APIRestService api){
        final ListView mListview = (ListView)findViewById(R.id.listviewCommandes);
        Call<List<Commande>> call = api.listeCommandes(1);
        call.enqueue(new Callback<List<Commande>>() {
            @Override
            public void onResponse(Call<List<Commande>> call, Response<List<Commande>> response) {
                List<Commande> commandes = response.body();
                mListview.setAdapter(new CommandeAdapter(ListeCommandeActivity.this, commandes));
                mListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        Commande item = (Commande) adapter.getItemAtPosition(position);
                        Intent intent = new Intent(ListeCommandeActivity.this,DetailsCommandeActivity.class);
//                      intent.putExtra("idCmd",item.getIdCommande());
//                      intent.putExtra("adrCmd",item.getAdresse()+" "+item.getCodePostal()+" "+item.getVille());
                        intent.putExtra("Commande", item);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onFailure(Call<List<Commande>> call, Throwable t) {
                Log.d("message1", t.toString());
                Log.d("message2", "onResponse: " + call.request().toString());
                Toast.makeText(ListeCommandeActivity.this, "Aucune connexion avec le server, veillez recommencer",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void remplirInfosLivreur(APIRestService api, String idLivreur){
        Call<List<Livreur>> call = api.infoLivreur(idLivreur);
        call.enqueue(new Callback<List<Livreur>>(){
            @Override
            public void onResponse(Call<List<Livreur>> call, Response<List<Livreur>> response) {
                List<Livreur> livreurs = response.body();
                Livreur liv = livreurs.get(0);

                //email liv dans nav
                TextView emaLiv = (TextView) findViewById(R.id.ema_liv);
                emaLiv.setText(liv.getEmail());

                //nom liv dans nav
                TextView nomLiv = (TextView) findViewById(R.id.nom_liv);
                nomLiv.setText(liv.getNom()+" "+liv.getPrenom());
            }

            @Override
            public void onFailure(Call<List<Livreur>> call, Throwable t) {
                Log.d("Message2", t.toString());
                Log.d("Message2", "onResponse: " + call.request().toString());
                Toast.makeText(ListeCommandeActivity.this, "error :(",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public APIRestService initAPIService(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://"+getString(R.string.ip)+":3000/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(APIRestService.class);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.liste_commande, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_finish) {
//            confirmerMessage("Confirmation", "Etes-vous sûr de vouloir valider les commandes ?");
//            return true;
//        }
        if (id == R.id.action_reload) {
            chargerDonnees(instanceApi);
            Toast.makeText(ListeCommandeActivity.this,"Actualisation...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void confirmerMessage(String titre, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setCancelable(true);
        builder.setTitle(titre);
        builder.setMessage(message);
        builder.setPositiveButton("Confirmer",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
