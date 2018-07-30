package com.example.johan.ecommandlivraison;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ParamsActivity extends AppCompatActivity {
    public SharedPreferences prfs;
    EditText edIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
         prfs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String IP = prfs.getString("IP","192.168.1.20");

        //initialisation IP valeur
        edIP = findViewById(R.id.editIP);
        edIP.setText(IP);

        Button button= (Button) findViewById(R.id.btnValiderParams);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addSharedPreferences("IP",edIP.getText()+"");
                finish();
            }
        });
    }

    public void addSharedPreferences(String name, String value){
        prfs = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        prfs
                .edit()
                .putString(name, value)
                .apply();
    }
}
