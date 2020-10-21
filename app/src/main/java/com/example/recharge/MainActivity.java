package com.example.recharge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.msebera.android.httpclient.Header;
public class MainActivity extends AppCompatActivity {

    Button rechargeBtn, chooseOperator;

      private String num, am, op, ser, operatorIntent;

    public EditText number, amount;
    TextView operator;

   Switch switchDTH;

    HashMap<String, String> operatorDTH = new HashMap<>();
    HashMap<String, String> operatorMob = new HashMap<>();

    List<Long> randomList = new ArrayList<Long>(Arrays.asList(1L, 2L));

    private Long merRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rechargeBtn = (Button) findViewById(R.id.rechargeBtn);


        number = (EditText) findViewById(R.id.number);
        amount = (EditText) findViewById(R.id.amount);
        operator = (TextView) findViewById(R.id.operator);
        switchDTH = (Switch) findViewById(R.id.switchDTH);
        chooseOperator = (Button) findViewById(R.id.chooseOperator);

        Intent intent = getIntent();
        operatorIntent = intent.getStringExtra("operator");
        operator.setText(operatorIntent);

        String numPref = number.getText().toString();
        String amountPref = amount.getText().toString();
        Boolean switchPref = true;

        operatorMob.put("airtel", "ATL");
        operatorMob.put("jio", "JRE");
        operatorMob.put("bsnl", "BNT");
        operatorMob.put("idea","IDA");
        operatorMob.put("vodafone", "VDF");


        operatorDTH.put("tata sky", "TSY");
        operatorDTH.put("airtel", "ATD");
        operatorDTH.put("bigtv", "BTV");
        operatorDTH.put("suntv", "STV");
        operatorDTH.put("videocon", "VCD");
        operatorDTH.put("dishtv", "DTV");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String savedNumber = preferences.getString("number", numPref);
        number.setText(savedNumber);

        String savedAmount = preferences.getString("amount", amountPref);
        amount.setText(savedAmount);

        Boolean savedSwitch = preferences.getBoolean("switch", switchPref);
        if (savedSwitch){
            switchDTH.setChecked(true);
        }

        chooseOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();


                 String numPref = number.getText().toString();
                 String amountPref = amount.getText().toString();
                Boolean switchPref = false;
                 if(switchDTH.isChecked()){
                     switchPref = true;
                 }
                editor.putBoolean("switch", switchPref);


                editor.putString("number", numPref);
                editor.putString("amount", amountPref);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), ChooseOperatorActivity.class);
                if(switchDTH.isChecked()){
                    intent.putExtra("type", "DTH");
                }
                else{
                    intent.putExtra("type", "Mobile");
                }

                startActivity(intent);


            }
        });


        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingText();


                setOp();
                checkCr();

                try {
                    DownloadTask task = new DownloadTask();
                    task.execute("http://venusrecharge.co.in/Transaction.aspx?authkey=10034&authpass=RANGANATHA@990&mobile=" + num + "&amount=" + am + "&opcode=" + op + "&Merchantrefno=" + merRef+ "&ServiceType=" + ser);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Racharge failed!", Toast.LENGTH_SHORT).show();
                }

                // getResponse();

             /*   AsyncHttpClient client = new AsyncHttpClient();
                client.get("http://venusrecharge.co.in/Transaction.aspx?authkey=10034&authpass=RANGANATHA@990&mobile=" + num + "&amount=" + am + "&opcode=" + op + "&Merchantrefno=" + merRef+ "&ServiceType=" + ser, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(), "failure" + error, Toast.LENGTH_SHORT).show();
                        Log.i("error", error.toString());
                    }
                });

              */




            }
        });


    }


    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data=reader.read();
                while(data!=-1){
                    char current =(char) data;
                    result+=current;
                    data=reader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Racharge failed!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Racharge failed!", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";

                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new InputSource(new StringReader(result)));
                NodeList errNodes = doc.getElementsByTagName("Response");
                if (errNodes.getLength() > 0) {
                    Element err = (Element)errNodes.item(0);
                    System.out.println("ResponseStatus -"+err.getElementsByTagName("ResponseStatus").item(0).getTextContent());
                    String status = err.getElementsByTagName("ResponseStatus").item(0).getTextContent();
                    String description = err.getElementsByTagName("Description").item(0).getTextContent();
                    message = status+" - "+description;

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
              //  System.out.println("dst_offset -"+err.getElementsByTagName("dst_offset").item(0).getTextContent());
                //System.out.println("time_zone_id -"+err.getElementsByTagName("time_zone_id").item(0).getTextContent());
                //System.out.println("time_zone_name -"+err.getElementsByTagName("time_zone_name").item(0).getTextContent());




            } else {
                    Toast.makeText(getApplicationContext(), "Failed to make a recharge request! Try again later.", Toast.LENGTH_LONG).show();
                // success
            }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void getEditText(){
        num = number.getText().toString();
        am = amount.getText().toString();
        op = operator.getText().toString();

    }

    private final void gettingText(){

        getEditText();

        merRef = generateRandom();
        int i=0;
        while(i<randomList.size()){
       if(merRef == randomList.get(i)){
          merRef = generateRandom();
          i=0;
       }
       else {
           i++;
       }
        }

        randomList.add(merRef);



    }

    private void setOp(){

        if(switchDTH.isChecked()){

            ser = "DH";

            if(op.equalsIgnoreCase("airtel")){
                op="ATD";

            }

            else if(op.equalsIgnoreCase("tata sky")){
                op="TSY";

            }
            else if(op.equalsIgnoreCase("bigtv")){
                op="BTV";

            }
            else if(op.equalsIgnoreCase("suntv")){
                op="STV";

            }
            else if(op.equalsIgnoreCase("videocon")){
                op="VCD";

            }
            else if(op.equalsIgnoreCase("dishtv")){
                op="DTV";

            }

        } else{

            ser = "MR";

            if(op.equalsIgnoreCase("airtel")){
                op="ATL";

            }

            else if(op.equalsIgnoreCase("jio")){
                op="JRE";

            }
            else if(op.equalsIgnoreCase("bsnl")){
                op="BNT";

            }
            else if(op.equalsIgnoreCase("idea")){
                op="IDA";

            }
            else if(op.equalsIgnoreCase("vodafone")){
                op="VDF";

            }

        }

    }

    private static long generateRandom(){

        Random rand = new Random();

        long x = (long)(rand.nextDouble()*100000000000000L);

        return Long.valueOf(x);


    }

    private void checkCr(){

        System.out.println(am+num+op+merRef+ser);

    }


   /* private void getResponse(){
        try {
            String url = "http://venusrecharge.co.in/Transaction.aspx?authkey=10034&authpass=RANGANATHA@990&mobile=" + num + "&amount=" + am + "&opcode=" + op + "&Merchantrefno=" + merRef+ "&ServiceType=" + ser;
            System.out.println(url);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print in String
            // System.out.println(response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("Response");
            if (errNodes.getLength() > 0) {
                Element err = (Element)errNodes.item(0);
                System.out.println("ResponseStatus -"+err.getElementsByTagName("ResponseStatus").item(0).getTextContent());
               /* System.out.println("dst_offset -"+err.getElementsByTagName("dst_offset").item(0).getTextContent());
                System.out.println("time_zone_id -"+err.getElementsByTagName("time_zone_id").item(0).getTextContent());
                System.out.println("time_zone_name -"+err.getElementsByTagName("time_zone_name").item(0).getTextContent());




            } else {
                // success
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
   public void okok(View view)
   {
       Intent in = new Intent(MainActivity.this, PaytmActivity.class);
   }

}
