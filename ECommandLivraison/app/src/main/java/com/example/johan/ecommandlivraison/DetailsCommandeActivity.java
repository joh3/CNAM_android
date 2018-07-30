package com.example.johan.ecommandlivraison;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsCommandeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Commande cmd;
    Client cli;
    APIRestService instanceAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_commande);


        Bundle bundle = getIntent().getExtras();
        Intent i = getIntent();
        cmd = (Commande) i.getSerializableExtra("Commande");

        setTitle("Commande #"+cmd.getIdCommande());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        instanceAPI = initAPIService();

        remplirInfoClient(instanceAPI, cmd.getIdCommande()+"");
        remplirInfoArticles(instanceAPI,cmd.getIdCommande()+"");

        Button btnValid= (Button) findViewById(R.id.btn_validation_commande);
        btnValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailsCommandeActivity.this, "Validation de la bonne livraison...",Toast.LENGTH_SHORT).show();
                setEtatCommande(cmd.getIdCommande()+"","Livrée");
            }
        });

        Button btnAnnul= (Button) findViewById(R.id.btn_annulation_commande);
        btnAnnul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailsCommandeActivity.this, "Validation d'un problème lors de la livraison...",Toast.LENGTH_SHORT).show();
                setEtatCommande(cmd.getIdCommande()+"","Non livrée");
            }
        });
    }

    private void setEtatCommande(String idCommande, String etatCommande) {
        //changer l'état de la commande avec un post

        Call<Object> request = instanceAPI.postEtatCmd(idCommande,etatCommande);
        request.enqueue(new Callback<Object>(){
            @Override
            public void onResponse(Call<Object> request, Response<Object> response) {
                Log.d("DEBUG", response.toString());
                finish();
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("Message2", t.toString());
                Log.d("Message2", "onResponse: " + call.request().toString());
            }
        });
    }

    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ClientFragment(),"Client");
        adapter.addFragment(new CommandeFragment(), "Commande");
        viewPager.setAdapter(adapter);
    }

    public APIRestService initAPIService(){
        SharedPreferences prfs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String IP = prfs.getString("IP", "192.168.1.20");
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://"+IP+":3000/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(APIRestService.class);
    }

    public void remplirInfoClient(APIRestService api, String idCommande){
        Call<List<Client>> call = api.infoClient(idCommande);
        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                List<Client> clients = response.body();
                if (clients.get(0) != null) {
                    cli = clients.get(0);
                }
                //nom+prenom cli dans tab commande
                TextView nomCli = (TextView) findViewById(R.id.valNomCli);
                if ((cli.getNom() != null) && (cli.getPrenom() != null)) {
                    nomCli.setText(cli.getNom() + " " + cli.getPrenom());
                }
                else{
                    nomCli.setText("Aucun nom");
                }
                //email cli dans tab commande
                TextView emaCli = (TextView) findViewById(R.id.valEmaCli);
                if (cli.getEmail() != null) {
                    emaCli.setText(cli.getEmail());
                } else {
                    emaCli.setText("aucun email");
                }

                //tel liv dans nav
                TextView telCli = (TextView) findViewById(R.id.valTelCli);
                if (cli.getNumTel() != null) {
                    telCli.setText(cli.getNumTel());
                } else {
                    cli.setNumTel("");
                    telCli.setText(cli.getNumTel());
                }

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPhone);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cli.getNumTel()));
                        startActivity(phoneIntent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Log.d("Message2", t.toString());
                Log.d("Message2", "onResponse: " + call.request().toString());
            }
        });
    }

    public void remplirInfoArticles(APIRestService api, String idCommande){
        Call<List<Article>> call = api.listeArticle(idCommande);
        call.enqueue(new Callback<List<Article>>(){
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                List<Article> articles = response.body();
                if(articles.size()!=0) {
                    cmd.setArticles(articles);
                    ListView listeArticles = (ListView) findViewById(R.id.listeArticles);
                    listeArticles.setAdapter(new ArticleAdapter(DetailsCommandeActivity.this, articles));

                    TextView somme = (TextView) findViewById(R.id.sommeArticle);
                    somme.setText(cmd.sommePrix() + "€");
                    Log.d("SOMMEEEEEE", cmd.sommePrix() + "");
                }
            }
            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                Log.d("Message2", t.toString());
                Log.d("Message2", "onResponse: " + call.request().toString());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_navigation, menu);
        MenuItem item = menu.findItem(R.menu.action_bar_navigation);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //  item selection
        switch (item.getItemId()) {
            case R.id.action_naviguation:
                String adrCommande = cmd.getAdresse()+" "+cmd.getCodePostal()+" "+cmd.getVille();
                Intent mapsIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + adrCommande));
                startActivity(mapsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
