package com.example.johan.ecommandlivraison;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
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
    SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID_LIV = "PREFS_ID_LIV";
    private static final String PREFS_ID_TOUR = "PREFS_ID_TOUR";

    @Override
    protected void onResume() {
        super.onResume();
        chargerDonnees(initAPIService());
        Log.d("RESUME", "onResume: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

        if(!isSharedPreferences("IP")){
            addSharedPreferences("IP", "192.168.1.20");
        }

        instanceApi = initAPIService();
        chargerDonnees(instanceApi);

        Button buttonDebut= (Button) findViewById(R.id.buttonDebut);
        buttonDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnDeb = (Button) findViewById(R.id.buttonDebut);
                Button btnFin = (Button) findViewById(R.id.buttonFin);
                setDateTournee(true);
                btnDeb.setEnabled(false);
                btnFin.setEnabled(true);
            }
        });

        Button buttonFin= (Button) findViewById(R.id.buttonFin);
        buttonFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnDeb = (Button) findViewById(R.id.buttonDebut);
                Button btnFin = (Button) findViewById(R.id.buttonFin);
                setDateTournee(false);
                btnDeb.setEnabled(false);
                btnFin.setEnabled(false);
                btnFin.setEnabled(false);
                finish();
                startActivity(getIntent());
            }
        });
    }


    public void chargerDonnees(APIRestService api){
        remplirListeCommande(api);
        remplirInfosLivreur(api,"1");
    }

    public void setDateTournee(boolean dateDebut){
        String idTournee = sharedPreferences.getString("idTournee","0");
        //envoi de la date de début
        if(dateDebut){
            Call<Object> requestDeb = instanceApi.postDebutTrn(idTournee);
            requestDeb.enqueue(new Callback<Object>(){
                @Override
                public void onResponse(Call<Object> request, Response<Object> response) {
                    Log.d("Message"," ENVOI DATE DEBUT OK");
                    addSharedPreferences("dateDebut", "true");
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d("Message2", t.toString());
                    Log.d("Message2", "onResponse: " + call.request().toString());
                }
            });
        }
        //envoi de la date de fin
        else {
            Call<Object> requestEnd = instanceApi.postFinTrn(idTournee);
            requestEnd.enqueue(new Callback<Object>(){
                @Override
                public void onResponse(Call<Object> request, Response<Object> response) {
                    Log.d("Message"," ENVOI DATE FIN OK");
                    //changement de l'état du livreur
                    String idLiv = sharedPreferences.getString("idLivreur", "0");
                    Call<Object> requestStateLiv = instanceApi.postEtatLiv("Disponible",""+idLiv);
                    requestStateLiv.enqueue(new Callback<Object>(){
                        @Override
                        public void onResponse(Call<Object> request, Response<Object> response) {
                            Log.d("Message"," ENVOI ETAT LIV OK");
                        }
                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Log.d("Message2", t.toString());
                            Log.d("Message2", "onResponse: " + call.request().toString());
                        }
                    });
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d("Message2", t.toString());
                    Log.d("Message2", "onResponse: " + call.request().toString());
                }
            });
        }
    }


    public void remplirListeCommande(APIRestService api){
        final ListView mListview = (ListView)findViewById(R.id.listviewCommandes);
        Call<List<Commande>> call = api.listeCommandes(1);
        call.enqueue(new Callback<List<Commande>>() {
            @Override
            public void onResponse(Call<List<Commande>> call, Response<List<Commande>> response) {
                Button btnDeb = (Button) findViewById(R.id.buttonDebut);
                Button btnFin = (Button) findViewById(R.id.buttonFin);
                List<Commande> commandes = response.body();
                if(commandes.size() != 0) {
                    addSharedPreferences("idTournee", commandes.get(0).getIdTournee() + "");
                    //traitement des boutons
                    Log.d("MESSAGE MESSAGE MESSAGE", "" + commandes.get(0).getDateDeb());
                    Log.d("MESSAGE MESSAGE MESSAGE", "" + commandes.get(0).getDateFin());
                    addSharedPreferences("dateDebut", commandes.get(0).getDateDeb());

                    if (commandes.get(0).getDateDeb() == null) {
                        btnDeb.setEnabled(true);
                        btnFin.setEnabled(false);

                    } else {
                        if (commandes.get(0).getDateFin() == null) {
                            btnDeb.setEnabled(false);
                            btnFin.setEnabled(true);
                        }
                    }

                    mListview.setAdapter(new CommandeAdapter(ListeCommandeActivity.this, commandes));
                    mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                            String datDeb = sharedPreferences.getString("dateDebut", "null");
                            if (datDeb.equalsIgnoreCase("null")) {
                                Toast.makeText(ListeCommandeActivity.this, "Vous devez lancer la tournée, avant de commencer. ", Toast.LENGTH_LONG).show();
                            } else {
                                Commande item = (Commande) adapter.getItemAtPosition(position);
                                Intent intent = new Intent(ListeCommandeActivity.this, DetailsCommandeActivity.class);
                                //                      intent.putExtra("idCmd",item.getIdCommande());
                                //                      intent.putExtra("adrCmd",item.getAdresse()+" "+item.getCodePostal()+" "+item.getVille());
                                intent.putExtra("Commande", item);
                                startActivity(intent);
                            }


                        }
                    });
                }
                else {
                    btnDeb.setEnabled(false);
                    btnFin.setEnabled(false);
                }
            }
            @Override
            public void onFailure(Call<List<Commande>> call, Throwable t) {
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
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                final View headerLayout = navigationView.getHeaderView(0);
                TextView emaLiv = (TextView)headerLayout.findViewById(R.id.emaLivreur);
                TextView nomLiv = (TextView)headerLayout.findViewById(R.id.nomLivreur);
                TextView idLiv = (TextView)headerLayout.findViewById(R.id.idLivreur);
                emaLiv.setText(liv.getEmail());
                nomLiv.setText(liv.getNom() + " " + liv.getPrenom());
                idLiv.setText(liv.getIdLivreur());
                Log.d("debbugggg", isSharedPreferences("idLivreur")+"");
                addSharedPreferences("idLivreur",liv.getIdLivreur()+"");

            }

            @Override
            public void onFailure(Call<List<Livreur>> call, Throwable t) {
                Log.d("Message2", t.toString());
                Log.d("Message2", "onResponse: " + call.request().toString());
            }
        });
    }

    public APIRestService initAPIService(){
        String IP = sharedPreferences.getString("IP","192.168.1.20");
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://"+IP+":3000/")
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

    public void addSharedPreferences(String name, String value){
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString(name, value)
                .apply();
    }

    public boolean isSharedPreferences(String name){
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(name)){
            return true;
        }
        return false;
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
            chargerDonnees(initAPIService());
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
        if (id == R.id.nav_params) {
            Intent intent = new Intent(ListeCommandeActivity.this,ParamsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {

        }
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

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, Geolocalisation.class));
                    checkLocationPermission();
                }
                return;
            }
        }
    }
    public boolean checkLocationPermission(){
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this,Geolocalisation.class));
        super.onDestroy();
    }
}
