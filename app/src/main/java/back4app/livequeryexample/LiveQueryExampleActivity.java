package back4app.livequeryexample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ParseACL;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryClient;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

public class LiveQueryExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_query_example);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        EditText pokeText = (EditText) findViewById(R.id.pokeText);
        setSupportActionBar(toolbar);

        // Back4App's Parse setup
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("lOnJ1j6c5OHzz7RFmlXATbEqGvKA3BuT9KXGyWdO")
                .clientKey("fYMc6jNRWTjqBDUJKMn0Tex5x4OBwLT0F9sgObbj")
                .server("https://parseapi.back4app.com/").build()
        );

        LiveQueryClient.init("wss://newapptest.back4app.io", "lOnJ1j6c5OHzz7RFmlXATbEqGvKA3BuT9KXGyWdO", true);
        LiveQueryClient.connect();

        // Subscription
        final Subscription sub = new BaseQuery.Builder("Message")
                .where("destination", "pokelist")
                .addField("content")
                .build()
                .subscribe();

        sub.on(LiveQueryEvent.CREATE, new OnListener() {
            @Override
            public void on(JSONObject object) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EditText pokeText = (EditText) findViewById(R.id.pokeText);
                        numPokes++;
                        if(numPokes == 1) {
                            pokeText.setText("Poked " + numPokes + " time.");
                        }
                        else {
                            pokeText.setText("Poked " + numPokes + " times.");
                        }
                    }
                });
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ParseObject poke = new ParseObject("Message");
                poke.put("content", "poke");
                poke.put("destination", "pokelist");
                poke.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Snackbar.make(view, "Poke has been sent!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_query_example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int numPokes = 0;
}
