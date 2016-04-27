package jonathanfried.leagueapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText nameText;


    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText = (EditText) findViewById(R.id.nameText);

        Button viewButton = (Button) findViewById(R.id.searchButton);
        final Intent intent = new Intent(MainActivity.this,ViewSummonerActivity.class);

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameText.getText().toString().replaceAll("\\s+","").toLowerCase();
                intent.putExtra("name", name);
                startActivity(intent);
                //new GetSummonerInfo().execute();
            }
        });


    }


}
