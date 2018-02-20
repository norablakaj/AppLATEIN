package com.example.norablakaj.lateinapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.norablakaj.lateinapp.R;

//TODO: Find a better name for this
public abstract class DevActivity extends AppCompatActivity{

    Menu menu;
    boolean onPause = false;

    /**
     * Auto-generated by Android Studio
     * @param menu the menu object from "/menu/action_button.xmlrButton.xml"
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_button, menu);

        this.menu = menu;

        adjustSettings();

        String temp;
        if (Vokabeltrainer.isDevCheatMode()){
            temp = "ON";
        }else{
            temp = "OFF";
        }
        MenuItem devVokCheat = this.menu.findItem(R.id.action_dev_Vokabeltrainer_Cheat);
        devVokCheat.setTitle("DEV: Cheat-Mode: " + temp);

        return true;
    }

    /**
     * Auto-generated by Android Studio
     * @param item The item selected by the user
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
        }

        if (id == R.id.action_dev_DB_Helper){
            Intent dbManager = new Intent(this, AndroidDatabaseManager.class);
            startActivity(dbManager);
        }

        if (id == R.id.action_dev_Vokabeltrainer_Cheat){
            //toggles the DevCheatMode
            Vokabeltrainer.setDevCheatMode(!Vokabeltrainer.isDevCheatMode());

            String temp;
            if (Vokabeltrainer.isDevCheatMode()){
                temp = "ON";
            }else{
                temp = "OFF";
            }
            MenuItem devVokCheat = this.menu.findItem(R.id.action_dev_Vokabeltrainer_Cheat);
            devVokCheat.setTitle("DEV: Vokabeltrainer-Cheat: " + temp);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        onPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (onPause){
            onPause = false;

            adjustSettings();
        }
    }

    private void adjustSettings(){

        MenuItem devDBHelper = this.menu.findItem(R.id.action_dev_DB_Helper);
        MenuItem devVokCheat = this.menu.findItem(R.id.action_dev_Vokabeltrainer_Cheat);
        if (Home.DEVELOPER) {
            devDBHelper.setVisible(true);
            devVokCheat.setVisible(true);
        }else {
            devDBHelper.setVisible(false);
            devVokCheat.setVisible(false);
        }

    }

}
