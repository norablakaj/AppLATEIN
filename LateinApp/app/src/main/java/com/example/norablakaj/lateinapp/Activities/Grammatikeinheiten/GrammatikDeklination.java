package com.example.norablakaj.lateinapp.Activities.Grammatikeinheiten;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.norablakaj.lateinapp.Activities.DevActivity;
import com.example.norablakaj.lateinapp.Activities.Home;
import com.example.norablakaj.lateinapp.Activities.LektionUebersicht;
import com.example.norablakaj.lateinapp.Activities.Vokabeltrainer;
import com.example.norablakaj.lateinapp.Databases.DBHelper;
import com.example.norablakaj.lateinapp.Databases.Tables.DeklinationsendungDB;
import com.example.norablakaj.lateinapp.Databases.Tables.Vokabel;
import com.example.norablakaj.lateinapp.R;

import java.util.Random;

public class GrammatikDeklination extends DevActivity {

    TextView grammatikUeberschrift;
    TextView grammatikAufgabenstellung;
    TextView latein;

    Button nom_sg, nom_pl,
            gen_sg, gen_pl,
            dat_sg, dat_pl,
            akk_sg, akk_pl,
            abl_sg, abl_pl;

    Button weiter;
    
    ProgressBar progressBar;

    String[] faelle = {
            DeklinationsendungDB.FeedEntry.COLUMN_NOM_SG,
            DeklinationsendungDB.FeedEntry.COLUMN_NOM_PL,
            DeklinationsendungDB.FeedEntry.COLUMN_GEN_SG,
            DeklinationsendungDB.FeedEntry.COLUMN_GEN_PL,
            DeklinationsendungDB.FeedEntry.COLUMN_DAT_SG,
            DeklinationsendungDB.FeedEntry.COLUMN_DAT_PL,
            DeklinationsendungDB.FeedEntry.COLUMN_AKK_SG,
            DeklinationsendungDB.FeedEntry.COLUMN_AKK_PL,
            DeklinationsendungDB.FeedEntry.COLUMN_ABL_SG,
            DeklinationsendungDB.FeedEntry.COLUMN_ABL_PL
    };

    int[] weights;

    int weightNomSg;
    int weightNomPl;
    int weightGenSg;
    int weightGenPl;
    int weightDatSg;
    int weightDatPl;
    int weightAkkSg;
    int weightAkkPl;
    int weightAblSg;
    int weightAblPl;

    DBHelper dbHelper;

    int lektion;

    Vokabel currentVokabel;
    String declination;

    SharedPreferences sharedPref;

    //TODO: make all DBHelper into a private variable that calls .close() on onDestroy()/onFinish()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammatik_deklination);

        Intent intent = getIntent();
        lektion = intent.getIntExtra("lektion", 0);

        dbHelper = new DBHelper(getApplicationContext());

        grammatikUeberschrift = findViewById(R.id.GrammatikDeklinationÜberschrift);
        grammatikAufgabenstellung = findViewById(R.id.GrammatikDeklinationAufgabe);
        latein = findViewById(R.id.GrammatikDeklinationLatein);

        nom_sg = findViewById(R.id.GrammatikDeklinationNomSg);
        nom_pl = findViewById(R.id.GrammatikDeklinationNomPl);
        gen_sg = findViewById(R.id.GrammatikDeklinationGenSg);
        gen_pl = findViewById(R.id.GrammatikDeklinationGenPl);
        dat_sg = findViewById(R.id.GrammatikDeklinationDatSg);
        dat_pl = findViewById(R.id.GrammatikDeklinationDatPl);
        akk_sg = findViewById(R.id.GrammatikDeklinationAkkSg);
        akk_pl = findViewById(R.id.GrammatikDeklinationAkkPl);
        abl_sg = findViewById(R.id.GrammatikDeklinationAblSg);
        abl_pl = findViewById(R.id.GrammatikDeklinationAblPl);
        weiter = findViewById(R.id.GrammatikDeklinationWeiter);

        weiter.setVisibility(View.GONE);

        sharedPref = getSharedPreferences("SharedPreferences", 0);

        progressBar = findViewById(R.id.GrammatikDeklinationProgressBar);
        progressBar.setMax(20);

        if (lektion == 1) {

            weightNomSg = 1;
            weightNomPl = 1;
            weightGenSg = 0;
            weightGenPl = 0;
            weightDatSg = 0;
            weightDatPl = 0;
            weightAkkSg = 0;
            weightAkkPl = 0;
            weightAblSg = 0;
            weightAblPl = 0;

        } else if (lektion == 2) {

            weightNomSg = 1;
            weightNomPl = 1;
            weightGenSg = 0;
            weightGenPl = 0;
            weightDatSg = 0;
            weightDatPl = 0;
            weightAkkSg = 2;
            weightAkkPl = 2;
            weightAblSg = 0;
            weightAblPl = 0;

        } else if (lektion == 3) {

            weightNomSg = 1;
            weightNomPl = 1;
            weightGenSg = 0;
            weightGenPl = 0;
            weightDatSg = 4;
            weightDatPl = 4;
            weightAkkSg = 1;
            weightAkkPl = 1;
            weightAblSg = 0;
            weightAblPl = 0;

        } else if (lektion == 4) {

            weightNomSg = 1;
            weightNomPl = 1;
            weightGenSg = 0;
            weightGenPl = 0;
            weightDatSg = 1;
            weightDatPl = 1;
            weightAkkSg = 1;
            weightAkkPl = 1;
            weightAblSg = 6;
            weightAblPl = 6;

        } else if (lektion >= 5) {

            weightNomSg = 1;
            weightNomPl = 1;
            weightGenSg = 8;
            weightGenPl = 8;
            weightDatSg = 1;
            weightDatPl = 1;
            weightAkkSg = 1;
            weightAkkPl = 1;
            weightAblSg = 1;
            weightAblPl = 1;

        } else {
            Log.e("LektionNotFound", "Lektion " + lektion + " in GrammatikDeklination.java not found");
            weightNomSg = 0;
            weightNomPl = 0;
            weightGenSg = 0;
            weightGenPl = 0;
            weightDatSg = 0;
            weightDatPl = 0;
            weightAkkSg = 0;
            weightAkkPl = 0;
            weightAblSg = 0;
            weightAblPl = 0;
        }

        weights = new int[]
                {weightNomSg,
                weightNomPl,
                weightAkkSg,
                weightAkkPl,
                weightDatSg,
                weightDatPl,
                weightAblSg,
                weightAblPl,
                weightGenSg,
                weightGenPl};

        declination = faelle[getRandomVocabularyNumber()];

        currentVokabel = dbHelper.getRandomSubstantiv(lektion);

        if (Home.DEVELOPER && Vokabeltrainer.isDevCheatMode()){
            latein.setText(dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), declination)
                            + "\n" +declination);
        }else {
            latein.setText(dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), declination));
        }

        setButtonsVisible(lektion);

    }

    public int getRandomVocabularyNumber(){
        
        int max =  (weightNomSg +
                    weightNomPl +
                    weightGenSg +
                    weightGenPl +
                    weightDatSg +
                    weightDatPl +
                    weightAkkSg +
                    weightAkkPl +
                    weightAblSg +
                    weightAblPl);

        Random randomNumber = new Random();
        int intRandom = randomNumber.nextInt(max);
        intRandom++;
        int sum = 1;
        int sumNew;

        //TODO: Can we make this initialisation better? Can we prevent the error without?
        int randomVocabulary = -1;
        for(int i = 0; i < weights.length; i++){

            sumNew = sum + weights[i];

            if (intRandom >= sum && intRandom < sumNew){

                randomVocabulary = i;
                break;
            }
            else {
                sum = sumNew;
            }
        }

        if(randomVocabulary == -1){
            Log.e("randomVocabulary", "Getting a randomDeclination failed! Returned -1 for " +
                    "\nrandomNumber: " + randomNumber +
                    "\nlektion: " + lektion);
        }

        return randomVocabulary;
    }

    public void deklinationstrainerButtonClicked(View view){

        if (view.getId() == R.id.GrammatikDeklinationNomSg){
            if(faelle[0].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationNomPl){
            if(faelle[1].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationGenSg){
            if(faelle[2].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationGenPl){
            if(faelle[3].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationDatSg){
            if(faelle[4].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationDatPl){
            if(faelle[5].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationAkkSg){
            if(faelle[6].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationAkkPl){
            if(faelle[7].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationAblSg){
            if(faelle[8].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationAblPl){
            if(faelle[9].equals(declination)){
                deklinationChosen(true);
            }else{
                deklinationChosen(false);
            }
        }else if (view.getId() == R.id.GrammatikDeklinationWeiter){
            
            weiter.setVisibility(View.GONE);
            nom_sg.setVisibility(View.VISIBLE);
            nom_pl.setVisibility(View.VISIBLE);
            gen_sg.setVisibility(View.VISIBLE);
            gen_pl.setVisibility(View.VISIBLE);
            dat_sg.setVisibility(View.VISIBLE);
            dat_pl.setVisibility(View.VISIBLE);
            akk_sg.setVisibility(View.VISIBLE);
            akk_pl.setVisibility(View.VISIBLE);
            abl_sg.setVisibility(View.VISIBLE);
            abl_pl.setVisibility(View.VISIBLE);

            declination = faelle[getRandomVocabularyNumber()];

            currentVokabel = dbHelper.getRandomSubstantiv(lektion);

            if (Home.DEVELOPER && Vokabeltrainer.isDevCheatMode()){
                latein.setText(dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), declination)
                        + "\n" +declination);
            }else {
                latein.setText(dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), declination));
            }

            int color = ResourcesCompat.getColor(getResources(), R.color.GhostWhite, null);
            latein.setBackgroundColor(color);

            setButtonsVisible(lektion);
        }

    }

    private void deklinationChosen(boolean correct){
        
        int color;
        if (correct) {
            color = ResourcesCompat.getColor(getResources(), R.color.InputRightGreen, null);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("Deklination"+lektion, sharedPref.getInt("Deklination"+lektion, 0) + 1);
            editor.apply();

            progressBar.setProgress(sharedPref.getInt("Deklination" +lektion, 0));
        }else{
            color = ResourcesCompat.getColor(getResources(), R.color.InputWrongRed, null);
        }
        latein.setBackgroundColor(color);
    
        weiter.setVisibility(View.VISIBLE);
        nom_sg.setVisibility(View.GONE);
        nom_pl.setVisibility(View.GONE);
        gen_sg.setVisibility(View.GONE);
        gen_pl.setVisibility(View.GONE);
        dat_sg.setVisibility(View.GONE);
        dat_pl.setVisibility(View.GONE);
        akk_sg.setVisibility(View.GONE);
        akk_pl.setVisibility(View.GONE);
        abl_sg.setVisibility(View.GONE);
        abl_pl.setVisibility(View.GONE);
    }

    private void setButtonsVisible(int lektion){
        if (lektion == 1){
            nom_sg.setVisibility(View.VISIBLE);
            nom_pl.setVisibility(View.VISIBLE);
            gen_sg.setVisibility(View.GONE);
            gen_pl.setVisibility(View.GONE);
            dat_sg.setVisibility(View.GONE);
            dat_pl.setVisibility(View.GONE);
            akk_sg.setVisibility(View.GONE);
            akk_pl.setVisibility(View.GONE);
            abl_sg.setVisibility(View.GONE);
            abl_pl.setVisibility(View.GONE);

        }else if (lektion == 2){
            nom_sg.setVisibility(View.VISIBLE);
            nom_pl.setVisibility(View.VISIBLE);
            gen_sg.setVisibility(View.GONE);
            gen_pl.setVisibility(View.GONE);
            dat_sg.setVisibility(View.GONE);
            dat_pl.setVisibility(View.GONE);
            akk_sg.setVisibility(View.VISIBLE);
            akk_pl.setVisibility(View.VISIBLE);
            abl_sg.setVisibility(View.GONE);
            abl_pl.setVisibility(View.GONE);

        }else if (lektion == 3){
            nom_sg.setVisibility(View.VISIBLE);
            nom_pl.setVisibility(View.VISIBLE);
            gen_sg.setVisibility(View.GONE);
            gen_pl.setVisibility(View.GONE);
            dat_sg.setVisibility(View.VISIBLE);
            dat_pl.setVisibility(View.VISIBLE);
            akk_sg.setVisibility(View.VISIBLE);
            akk_pl.setVisibility(View.VISIBLE);
            abl_sg.setVisibility(View.GONE);
            abl_pl.setVisibility(View.GONE);

        }else if (lektion == 4){
            nom_sg.setVisibility(View.VISIBLE);
            nom_pl.setVisibility(View.VISIBLE);
            gen_sg.setVisibility(View.GONE);
            gen_pl.setVisibility(View.GONE);
            dat_sg.setVisibility(View.VISIBLE);
            dat_pl.setVisibility(View.VISIBLE);
            akk_sg.setVisibility(View.VISIBLE);
            akk_pl.setVisibility(View.VISIBLE);
            abl_sg.setVisibility(View.VISIBLE);
            abl_pl.setVisibility(View.VISIBLE);
        }else if (lektion >= 5){
            nom_sg.setVisibility(View.VISIBLE);
            nom_pl.setVisibility(View.VISIBLE);
            gen_sg.setVisibility(View.VISIBLE);
            gen_pl.setVisibility(View.VISIBLE);
            dat_sg.setVisibility(View.VISIBLE);
            dat_pl.setVisibility(View.VISIBLE);
            akk_sg.setVisibility(View.VISIBLE);
            akk_pl.setVisibility(View.VISIBLE);
            abl_sg.setVisibility(View.VISIBLE);
            abl_pl.setVisibility(View.VISIBLE);
        }
    }
}
