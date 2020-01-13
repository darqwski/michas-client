package com.example.clientquiz;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TCPUtilities tcpConnection;
    public Context appContext;
    public final String addressIP = "192.168.0.32";
    public final Integer port = 6666;
    public String lastResponse = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tcpConnection = new TCPUtilities();
        tcpConnection.startConnection(addressIP,port);
        appContext=this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(tcpConnection.canRead()){
                        String text = tcpConnection.receiveMessage();
                        if(text!=lastResponse){
                            lastResponse=text;
                            proceedMessage(text);
                        }
                    }
                }
            }
        }).start();

        View.OnClickListener sendAnswerOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpConnection.sendMessage("ANSWER@@"+((TextView)findViewById(R.id.questionText)).getText()+"@@"+((Button)v).getText());
            }
        };

        (findViewById(R.id.sendRequest)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpConnection.sendMessage("GET@@");
            }
        });
        findViewById(R.id.answer1).setOnClickListener(sendAnswerOnClick);
        findViewById(R.id.answer2).setOnClickListener(sendAnswerOnClick);
        findViewById(R.id.answer3).setOnClickListener(sendAnswerOnClick);
        findViewById(R.id.answer4).setOnClickListener(sendAnswerOnClick);
    }

    public void proceedMessage(String text){
        final String [] responseParts = text.split("@@");
        if(responseParts[0].equals("QUESTION")){
            try {
                final JSONObject response = new JSONObject(responseParts[1]);
                final JSONArray answers = response.getJSONArray("answers");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((Button)findViewById(R.id.answer1)).setText(answers.getString(0));
                            ((Button)findViewById(R.id.answer2)).setText(answers.getString(1));
                            ((Button)findViewById(R.id.answer3)).setText(answers.getString(2));
                            ((Button)findViewById(R.id.answer4)).setText(answers.getString(3));
                            ((TextView)findViewById(R.id.questionText)).setText(response.getString("text"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(responseParts[0].equals("ANSWER")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(appContext,responseParts[1], Toast.LENGTH_SHORT).show();
                    ((TextView)findViewById(R.id.userPoints)).setText("Twój wynik to: "+responseParts[2]);
                }
            });
        } else if( responseParts[0].equals("NUMBER")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView)findViewById(R.id.userPoints2)).setText("Najlepszy wynik to: "+responseParts[2]);
                    ((TextView)findViewById(R.id.questionNumber)).setText("Pozostało pytań: "+responseParts[1]);
                }
            });
        }
        Log.wtf("RESPONSE", text);
    }
}
