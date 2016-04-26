package jonathanfried.leagueapp;

import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {

    EditText nameText;
    TextView nameView,levelView;
    ImageView iconView;

    String summonerName, summonerLevel, iconID, name;
    Image icon;
    static final String API_URL = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/";
    static final String API_KEY = "?api_key=63a72fed-f624-48d5-8c1a-0e6ad21667a5";
    static final String ICON_URL = "http://ddragon.leagueoflegends.com/cdn/6.8.1/img/profileicon/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText = (EditText) findViewById(R.id.nameText);
        nameView = (TextView) findViewById(R.id.nameView);
        levelView = (TextView) findViewById(R.id.levelView);
        iconView = (ImageView) findViewById(R.id.iconView);
        Button viewButton = (Button) findViewById(R.id.searchButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameText.getText().toString().replaceAll("\\s+","").toLowerCase();
                new GetSummonerInfo().execute();
            }
        });


    }

    class GetSummonerInfo extends AsyncTask<Void,Void,String>{
        protected void onPreExecute{
            nameView.setText("");
            levelView.setText("");
            iconView.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls){
            try{
                URL url = new URL(API_URL + name + API_KEY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                int responseCode = httpURLConnection.getResponseCode();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();

                }
                finally {
                    httpURLConnection.disconnect();
                }


            }

            catch(Exception e){
                Log.e("ERROR", e.getMessage(),e);
                return null;
            }
        }

        protected void onPostExecute(String response){
            if(response == null){
                nameView.setText("Summoner Not Found");
                iconView.setVisibility(View.INVISIBLE);
            }


        }
    }
}
