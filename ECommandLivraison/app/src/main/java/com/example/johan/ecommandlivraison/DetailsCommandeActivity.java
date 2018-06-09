package com.example.johan.ecommandlivraison;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    }
    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ClientFragment(),"Client");
        adapter.addFragment(new CommandeFragment(), "Commande");
        viewPager.setAdapter(adapter);
    }

    public APIRestService initAPIService(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://"+getString(R.string.ip)+":3000/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(APIRestService.class);
    }

    public void remplirInfoClient(APIRestService api, String idCommande){
        Call<List<Client>> call = api.infoClient(idCommande);
        call.enqueue(new Callback<List<Client>>(){
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                List<Client> clients = response.body();
                if (clients.get(0)!=null) {
                    cli = clients.get(0);
                    //nom+prenom cli dans tab commande
                    TextView nomCli = (TextView) findViewById(R.id.valNomCli);
                    nomCli.setText(cli.getNom() + " " + cli.getPrenom());

                    //email cli dans tab commande
                    TextView emaCli = (TextView) findViewById(R.id.valEmaCli);
                    emaCli.setText(cli.getEmail());

                    //tel liv dans nav
                    TextView telCli = (TextView) findViewById(R.id.valTelCli);
                    telCli.setText(cli.getNumTel());
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPhone);

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cli.getNumTel()));
                            startActivity(phoneIntent);
                        }
                    });
                }
                else {
                    Toast.makeText(DetailsCommandeActivity.this, "Error: Aucun client", Toast.LENGTH_SHORT).show();
                }
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
                else {
                    Toast.makeText(DetailsCommandeActivity.this, "Error: Aucun article", Toast.LENGTH_SHORT).show();
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
