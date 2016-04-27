package jonathanfried.leagueapp;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewSummonerActivity extends AppCompatActivity {

    TextView nameView,levelView,gameInfoView;
    ImageView iconView;
    String name;
    ProgressBar progressBar;

    String summonerName, summonerLevel, iconID,summonerID;
    Image icon;
    static final String API_URL_SUMMONER = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/";
    static final String API_URL_GAMES = "https://na.api.pvp.net/api/lol/na/v1.3/game/by-summoner/";
    static final String API_KEY = "?api_key=63a72fed-f624-48d5-8c1a-0e6ad21667a5";
    static final String ICON_URL = "http://ddragon.leagueoflegends.com/cdn/6.8.1/img/profileicon/";
    static final String ICON_EXT = ".png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summoner);
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");

        nameView = (TextView) findViewById(R.id.nameView);
        levelView = (TextView) findViewById(R.id.levelView);
        iconView = (ImageView) findViewById(R.id.iconView);
        gameInfoView = (TextView) findViewById(R.id.gameInfoView);
        progressBar = (ProgressBar) findViewById(R.id.retrievalProgress);
        progressBar.setVisibility(View.INVISIBLE);

        new GetSummonerInfo().execute();

    }

    class GetSummonerInfo extends AsyncTask<Void,Void,String> {
        protected void onPreExecute(){
            nameView.setText("");
            levelView.setText("");
            iconView.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls){
            try{
                URL url = new URL(API_URL_SUMMONER + name + API_KEY);
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

                } finally {
                    httpURLConnection.disconnect();
                }
            } catch(Exception e){
                Log.e("ERROR", e.getMessage(),e);
                return null;
            }
        }

        protected void onPostExecute(String response){
            if(response == null){
                nameView.setText(R.string.summoner_not_found);
                iconView.setVisibility(View.INVISIBLE);
            }

            try {
                JSONObject jsonRawObject = new JSONObject(response);

                JSONObject info = jsonRawObject.getJSONObject(name);
                summonerName = info.getString("name");
                summonerLevel = Long.toString(info.getLong("summonerLevel"));
                iconID = Integer.toString(info.getInt("profileIconId"));
                summonerID = Long.toString(info.getLong("id"));

                nameView.setText(summonerName);
                levelView.setText(summonerLevel);
                new GetIcon().execute();

            }
            catch(Exception e){
                Log.e("ERROR", e.getMessage(),e);
            }
        }
    }

    class GetIcon extends AsyncTask<Void,Void,Drawable>{
        protected void onPreExecute(){

        }

        protected Drawable doInBackground(Void... urls){


            //HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //int responseCode = urlConnection.getResponseCode();
            try{
                URL url = new URL(ICON_URL + iconID + ICON_EXT);
                InputStream is = (InputStream) url.getContent();
                return Drawable.createFromStream(is,"srcName");

            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(Drawable rawImage){
            iconView.setImageDrawable(rawImage);
            new GetRecentGames().execute();
        }

    }

    class GetRecentGames extends AsyncTask<Void,Void,String>{
        protected void onPreExecute(){
            gameInfoView.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls){
            try{
                URL url = new URL(API_URL_GAMES + summonerID + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line=br.readLine()) != null){
                        sb.append(line).append("\n");
                    }
                    br.close();
                    return sb.toString();
                }finally {
                    urlConnection.disconnect();
                }
            }catch(Exception e){
                Log.e("ERROR",e.getMessage(),e);
                return null;
            }
        }

        protected void onPostExecute(String response){
            if(response == null){
                gameInfoView.setText("Error Retrieving Recent Games");
                progressBar.setVisibility(View.INVISIBLE);
            }
            try {
                JSONObject jsonRawObject = new JSONObject(response);
                JSONArray gameArray = jsonRawObject.getJSONArray("games");

            }catch(Exception e){
                Log.e("ERROR PROCESSING GAMES",e.getMessage(),e);
            }
        }
    }
}
