package com.lateinapp.noraalex.lopade.Activities.Einheiten;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lateinapp.noraalex.lopade.Activities.LateinAppActivity;
import com.lateinapp.noraalex.lopade.Databases.DBHelper;
import com.lateinapp.noraalex.lopade.Databases.Tables.DeklinationsendungDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.Vokabel;
import com.lateinapp.noraalex.lopade.General;
import com.lateinapp.noraalex.lopade.R;

import java.util.ArrayList;
import java.util.Random;

import static com.lateinapp.noraalex.lopade.Global.DEVELOPER;
import static com.lateinapp.noraalex.lopade.Global.DEV_CHEAT_MODE;
import static com.lateinapp.noraalex.lopade.Global.KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG;

public class ClickDeklinationsendung extends LateinAppActivity {

    private static final String TAG = "ClickDeklinationsendung";

    private DBHelper dbHelper;
    private SharedPreferences sharedPref;

    private TextView lateinVokabel;
    private ToggleButton nom_sg, nom_pl,
            gen_sg, gen_pl,
            dat_sg, dat_pl,
            akk_sg, akk_pl,
            abl_sg, abl_pl;

    private Button weiter,
            zurück,
            reset,
            checkInput;

    private ProgressBar progressBar;

    private final String[] faelle = {
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

    private ToggleButton[] buttons;

    private int backgroundColor;

    private static final int maxProgress = 20;

    private ArrayList<String> allCorrectCases;

    private Vokabel currentVokabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_click_deklination);

        setup();

        newVocabulary();
    }

    private void setup(){

        dbHelper = new DBHelper(getApplicationContext());

        sharedPref = getSharedPreferences("SharedPreferences", 0);

        allCorrectCases = new ArrayList<>(10);

        backgroundColor = ResourcesCompat.getColor(getResources(), R.color.background, null);

        lateinVokabel = findViewById(R.id.textGrammatikDeklinationLatein);
        progressBar = findViewById(R.id.progressBarGrammatikDeklination);

        nom_sg = findViewById(R.id.buttonGrammatikDeklinationNomSg);
        nom_pl = findViewById(R.id.buttonGrammatikDeklinationNomPl);
        gen_sg = findViewById(R.id.buttonGrammatikDeklinationGenSg);
        gen_pl = findViewById(R.id.buttonGrammatikDeklinationGenPl);
        dat_sg = findViewById(R.id.buttonGrammatikDeklinationDatSg);
        dat_pl = findViewById(R.id.buttonGrammatikDeklinationDatPl);
        akk_sg = findViewById(R.id.buttonGrammatikDeklinationAkkSg);
        akk_pl = findViewById(R.id.buttonGrammatikDeklinationAkkPl);
        abl_sg = findViewById(R.id.buttonGrammatikDeklinationAblSg);
        abl_pl = findViewById(R.id.buttonGrammatikDeklinationAblPl);

        reset = findViewById(R.id.buttonGrammatikDeklinationReset);
        checkInput = findViewById(R.id.buttonGrammatikDeklinationCheckInput);
        weiter = findViewById(R.id.buttonGrammatikDeklinationWeiter);
        zurück = findViewById(R.id.buttonGrammatikDeklinationZurück);

        buttons = new ToggleButton[]{
                nom_sg, nom_pl,
                gen_sg, gen_pl,
                dat_sg, dat_pl,
                akk_sg, akk_pl,
                abl_sg, abl_pl
        };

        progressBar.setMax(maxProgress);
        int progress = sharedPref.getInt(KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG, 0);
        if (progress > maxProgress){
            progress = maxProgress;
        }

        progressBar.setProgress(progress);

    }

    /**
     * Checks if the user already completed the 'grammatikDeklination'.
     * Retrieves a new vocabulary and sets it to be the current one.
     */
    private void newVocabulary(){

        int progress = sharedPref.getInt(KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG, 0);

        lateinVokabel.setBackgroundColor(backgroundColor);

        if (progress < maxProgress) {
            progressBar.setProgress(progress);

            String declination = faelle[new Random().nextInt(faelle.length)];

            //TODO: Failproof this -> extended tests?
            currentVokabel = dbHelper.getRandomSubstantiv();

            allCorrectCases.clear();
            allCorrectCases.add(declination);

            //Adding all declinations that have the same form of the substantive:
            //Example: templum is  nom & akk Sg. -> both should be correct
            for (String fall : faelle){

                if (!declination.equals(fall)){

                    //Comparing if the declinated vocabulary in both cases are the same
                    if (dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), fall).equals(currentVokabel.getLatein())){

                        allCorrectCases.add(fall);
                    }

                }
            }

            String lateinText = dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), allCorrectCases.get(0));

            //#DEVELOPER
            if (DEVELOPER && DEV_CHEAT_MODE) {
                //Lowering the text size if more than 2 correct cases exist so it fits the screen.
                if (allCorrectCases.size() > 2) {
                    lateinVokabel.setTextSize(24);
                }else{
                    lateinVokabel.setTextSize(30);
                }

                //Setting the text containing the right declinations.
                lateinText += "\n";
                for (String correctCase : allCorrectCases) {
                    if (allCorrectCases.indexOf(correctCase) != 0) lateinText += " & ";
                    lateinText += correctCase;
                }
            }

            lateinVokabel.setText(lateinText);

        }else {
            progressBar.setProgress(maxProgress);


            backgroundColor = ResourcesCompat.getColor(getResources(), R.color.background, null);


            for (ToggleButton b : buttons){
                b.setVisibility(View.GONE);
            }
            weiter.setVisibility(View.GONE);
            checkInput.setVisibility(View.GONE);
            lateinVokabel.setVisibility(View.GONE);
            reset.setVisibility(View.VISIBLE);
            zurück.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Handles button-clicks
     * @param view the clicked element
     */
    public void deklinationstrainerButtonClicked(View view){

        switch (view.getId()){

            //Checks the user input
            case (R.id.buttonGrammatikDeklinationCheckInput):

                weiter.setVisibility(View.VISIBLE);
                checkInput.setVisibility(View.GONE);
                checkInput();

                for (ToggleButton tb: buttons){
                    tb.setEnabled(false);
                }

                break;

            //Gets the next vocabulary
            case (R.id.buttonGrammatikDeklinationWeiter):

                weiter.setVisibility(View.GONE);
                checkInput.setVisibility(View.VISIBLE);
                newVocabulary();

                for (ToggleButton tb: buttons){
                    tb.setChecked(false);
                    tb.setEnabled(true);
                }

                break;

            //Resets all progress up to this point
            case (R.id.buttonGrammatikDeklinationReset):

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG, 0);
                editor.apply();

                finish();
                break;

            //Closes the activity and returns to the last one
            case (R.id.buttonGrammatikDeklinationZurück):

                finish();
                break;
        }
    }

    private void checkInput(){

        ArrayList<String> checkedButtons = new ArrayList<>(allCorrectCases.size());

        for(int i = 0; i < buttons.length; i++){
            if(buttons[i].isChecked()){
                checkedButtons.add(faelle[i]);
            }
        }

        boolean correct = General.areUnorderedListsEqual(checkedButtons, allCorrectCases);

        SharedPreferences.Editor editor = sharedPref.edit();
        int color;

        int currentScore = sharedPref.getInt(KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG, 0);

        if (correct) {
            color = ResourcesCompat.getColor(getResources(), R.color.correct, null);

            editor.putInt(KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG, currentScore + 1);
        }else {
            color = ResourcesCompat.getColor(getResources(), R.color.error, null);

            if (currentScore > 0) {
                editor.putInt(KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG, currentScore - 1);
            }
        }
        editor.apply();

        lateinVokabel.setBackgroundColor(color);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
