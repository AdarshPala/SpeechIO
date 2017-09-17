package com.example.android.speechio;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.System.currentTimeMillis;


public class PractiseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static double foo = 0;
    public static long beginningTime = 0;
    public static long endTime = 0;

    public static ArrayList<String> result;
    private TextView txvResult;
    private DatabaseReference mRef;
    private StringBuilder val2 = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://speechio-7621c.firebaseio.com/");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txvResult = (TextView) findViewById(R.id.txvResult);
        collectStrings();
    }

    static final int CONTACT_REQUEST = 10;

    public void getSpeechInput(View view) {

        beginningTime = System.currentTimeMillis();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "10000");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CONTACT_REQUEST);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        endTime = System.currentTimeMillis();
        switch (requestCode) {
            case CONTACT_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                }
                break;
        }
        compareAlgorithm(val2.toString(), result.get(0));
    }

    public void collectStrings () {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("speechWritten")){
                    String val = dataSnapshot.getValue().toString();

                    for(int i = 15; i< val.length()-1; i++){
                        val2.append(val.charAt(i)); //the speech typed by the user
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void compareAlgorithm (String textInput, String speechInput)
    {
        String textInputSen = textInput.replaceAll ("\\.", "");
        textInputSen = textInput.replaceAll (",", "");
        textInputSen = textInput.replaceAll ("\\?", "");
        String[] textInputWords = textInputSen.split (" ");
        String speechInputt = speechInput.replaceAll(",", "");
        String[] speechInputWords = speechInputt.split (" ");
        int shorter;
        int score = 0;
        if (textInputWords.length > speechInputWords.length)
            shorter = speechInputWords.length;
        else
            shorter = textInputWords.length;
        int count = 0;
        while (count <= shorter && (count + 4) <= shorter)
        {
            for (int j = 0 ; j < 4 ; j++)
            {
                for (int k = 0 ; k < 4 ; k++)
                {
                    if (textInputWords [count + k].equalsIgnoreCase (speechInputWords [count + j]))
                        score++;
                }
            }
            count += 4;
        }
        int shortest = shorter / 4;
        double ans = (double) score / (double) (shortest * 4);
        if (ans > 1)
            ans = 1;
        ans = ans*100;
        foo = ans;
        //myName.setText(foo);
    }

    ////////////////////////////////hello my name is Joe from America and I like beer
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practise, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upload) {
            Intent intent = new Intent(this, UploadActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_practise) {
            Intent intent = new Intent(this, PractiseActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_results) {
            Intent intent = new Intent(this, ResultsActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_share) {

        }

        else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
