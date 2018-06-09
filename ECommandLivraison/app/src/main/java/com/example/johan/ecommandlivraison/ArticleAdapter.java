package com.example.johan.ecommandlivraison;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {
    private List<Article> values;
    private Context context;

    public ArticleAdapter(Context context, List<Article> values) {
        super(context, R.layout.row_article, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_article, parent, false);
        }
        TextView libelle = (TextView) row.findViewById(R.id.libArticle);
        TextView prix = (TextView) row.findViewById(R.id.prixArticle);

        Article item = values.get(position);
        libelle.setText(item.getLibelle());
        prix.setText(""+item.getPrixHT()+" €");
        return row;
    }
}
