package com.example.recharge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChooseOperatorActivity extends AppCompatActivity {

    SearchView searchOperator;
    ListView listOperator;

    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> listDTH = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private String typeIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_operator);

        searchOperator = (SearchView) findViewById(R.id.searchOperator);
        listOperator = (ListView) findViewById(R.id.listOperator);

        Intent intent = getIntent();
        typeIntent = intent.getStringExtra("type");

        list.add("airtel");
        list.add("jio");
        list.add("idea");
        list.add("vodafone");
        list.add("bsnl");

        listDTH.add("airtel");
        listDTH.add("tata sky");
        listDTH.add("bigtv");
        listDTH.add("suntv");
        listDTH.add("videocon");
        listDTH.add("dishtv");

        if(typeIntent.equalsIgnoreCase("DTH")){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDTH);
        }
        else{
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        }

        listOperator.setAdapter(adapter);

        listOperator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TextView textView = (TextView) view.findViewById(R.id.);
                String pass = listOperator.getItemAtPosition(position).toString();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putExtra("operator", pass);
                startActivity(intent);
            }
        });

        searchOperator.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return true;
            }
        });



    }
}