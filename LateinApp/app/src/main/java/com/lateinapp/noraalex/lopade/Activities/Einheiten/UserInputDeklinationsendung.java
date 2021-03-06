package com.lateinapp.noraalex.lopade.Activities.Einheiten;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lateinapp.noraalex.lopade.Activities.LateinAppActivity;
import com.lateinapp.noraalex.lopade.Databases.DBHelper;
import com.lateinapp.noraalex.lopade.Databases.Tables.DeklinationsendungDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.Vokabel;
import com.lateinapp.noraalex.lopade.General;
import com.lateinapp.noraalex.lopade.R;
import com.lateinapp.noraalex.lopade.Score;

import java.util.Random;

import static com.lateinapp.noraalex.lopade.Global.DEVELOPER;
import static com.lateinapp.noraalex.lopade.Global.DEV_CHEAT_MODE;
import static com.lateinapp.noraalex.lopade.Global.KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG;
import static com.lateinapp.noraalex.lopade.Global.KEY_PROGRESS_USERINPUT_ESSEVELLENOLLE;

/**
 * Created by Alexander on 07.03.2018.
 */

//TODO: Make compatible with multiple tenses
public class UserInputDeklinationsendung extends LateinAppActivity {

    //FIXME: Class name is too long for logging -> max 23 chars
    private static final String TAG = "UserInputDekl";

    private SharedPreferences sharedPref;
    private DBHelper dbHelper;

    //Score stuff
    private TextView sCongratulations,
            sCurrentTrainer,
            sMistakeAmount,
            sMistakeAmountValue,
            sBestTry,
            sBestTryValue,
            sHighScore,
            sHighScoreValue,
            sGrade,
            sGradeValue;
    private Button sBack,
            sReset;

    private TextView request,
            solution,
            titel,
            amountWrong;
    private EditText userInput;
    private ProgressBar progressBar;
    //FIXME: Remove button elevation to make it align with 'userInput'-EditText
    private Button bestaetigung,
            weiter,
            reset,
            zurück;

    private Vokabel currentVokabel;
    private String currentDeclination;

    Animation animShake;

    private int[] weights;
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

    private String extraFromEinheitenUebersicht;
    private int backgroundColor;
    private final int maxProgress = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_user_input);

        setup();

        newVocabulary();
    }

    private void setup(){

        Intent intent = getIntent();
        extraFromEinheitenUebersicht = intent.getStringExtra("ExtraInputDeklinationsendung");

        sharedPref = General.getSharedPrefrences(getApplicationContext());
        dbHelper = DBHelper.getInstance(getApplicationContext());

        backgroundColor = ResourcesCompat.getColor(getResources(), R.color.background, null);
        request = findViewById(R.id.textUserInputLatein);
        solution = findViewById(R.id.textUserInputDeutsch);
        userInput = findViewById(R.id.textUserInputUserInput);
        progressBar = findViewById(R.id.progressBarUserInput);
        bestaetigung = findViewById(R.id.buttonUserInputEingabeBestätigt);
        weiter = findViewById(R.id.buttonUserInputNächsteVokabel);
        reset = findViewById(R.id.scoreButtonReset);
        zurück = findViewById(R.id.scoreButtonBack);
        titel = findViewById(R.id.textUserInputÜberschrift);

        //Score stuff
        sCongratulations = findViewById(R.id.scoreCongratulations);
        sCurrentTrainer = findViewById(R.id.scoreCurrentTrainer);
        sMistakeAmount = findViewById(R.id.scoreMistakes);
        sMistakeAmountValue = findViewById(R.id.scoreMistakeValue);
        sBestTry = findViewById(R.id.scoreBestRunMistakeAmount);
        sBestTryValue = findViewById(R.id.scoreEndScoreValue);
        sHighScore = findViewById(R.id.scoreHighScore);
        sHighScoreValue = findViewById(R.id.scoreHighScoreValue);
        sGrade = findViewById(R.id.scoreGrade);
        sGradeValue = findViewById(R.id.scoreGradeValue);
        sBack = findViewById(R.id.scoreButtonBack);
        sReset = findViewById(R.id.scoreButtonReset);

        amountWrong = findViewById(R.id.textUserInputMistakes);

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        TextView score = findViewById(R.id.textUserInputScore);
        score.setVisibility(View.GONE);

        userInput.setHint("Deklinierter Substantiv");        //Makes it possible to move to the next vocabulary by pressing "enter"
        userInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {


                    userInputButtonClicked(findViewById(R.id.buttonUserInputEingabeBestätigt));
                    return true;
                }
                return false;
            }
        });
        titel.setText("Deklinationstrainer");

        solution.setVisibility(View.GONE);
        weiter.setVisibility(View.GONE);

        weightSubjects(extraFromEinheitenUebersicht);

        progressBar.setMax(maxProgress);


        int wrong = Score.getCurrentMistakesDeklInput(sharedPref);
        if (wrong == -1){
            wrong = 0;
        }
        amountWrong.setText("Fehler: " + wrong);

    }

    private void newVocabulary(){

        int progress = sharedPref.getInt(KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG + extraFromEinheitenUebersicht, 0);

        if (progress < maxProgress) {

            progressBar.setProgress(progress);

            showKeyboard();

            //Resetting the userInput.
            userInput.setText("");
            userInput.setBackgroundColor(backgroundColor);
            userInput.setFocusableInTouchMode(true);

            //Getting a new vocabulary.
            //FIXME: Don't return a random number but one according to the progress (nom->1 /...) [also fix the TODO below with this]
            //random number from 1 to 5 to choose, where the vocabulary comes from
            //Blueprint for randNum: int randomNum = rand.nextInt((max - min) + 1) + min;
            int rand = new Random().nextInt((5 - 1) + 1) + 1;
            currentVokabel = dbHelper.getRandomSubstantiv();
            currentDeclination = getRandomDeklination();

            //FIXME Remove nom_sg
            while (currentDeclination.equals(faelle[0])){
                currentDeclination = getRandomDeklination();
            }
            String lateinText = dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), DeklinationsendungDB.FeedEntry.COLUMN_NOM_SG);

            String personalendungUser = currentDeclination.replace("_", " ");
            personalendungUser = personalendungUser.replace("Sg", "Sg.");
            personalendungUser = personalendungUser.replace("Pl", "Pl.");
            personalendungUser = personalendungUser.replace("Nom", "Nom.");
            personalendungUser = personalendungUser.replace("Gen", "Gen.");
            personalendungUser = personalendungUser.replace("Dat", "Dat.");
            personalendungUser = personalendungUser.replace("Akk", "Akk.");
            personalendungUser = personalendungUser.replace("Abl", "Abl.");
            lateinText += "\n" + personalendungUser;

            //#DEVELOPER
            if (DEVELOPER && DEV_CHEAT_MODE){
                lateinText += "\n" + dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), currentDeclination);
            }
            request.setText(lateinText);

            bestaetigung.setVisibility(View.VISIBLE);
            weiter.setVisibility(View.GONE);
            solution.setVisibility(View.GONE);

        } else {

            progressBar.setProgress(maxProgress);

            hideKeyboard();

            allLearned();
        }


    }

    /**
     * Sets weights for all entries of 'faelle' depending on the current value of lektion
     * Copied from GrammatikUserInputDeklinationsendung.class
     */
    private void weightSubjects(String extra){

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

        switch (extra){

            case "NOMINATIV":
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
                break;

            case "AKKUSATIV":
                weightNomSg = 2;
                weightNomPl = 2;
                weightGenSg = 0;
                weightGenPl = 0;
                weightDatSg = 0;
                weightDatPl = 0;
                weightAkkSg = 3;
                weightAkkPl = 3;
                weightAblSg = 0;
                weightAblPl = 0;
                break;

            case "DATIV":

                weightNomSg = 1;
                weightNomPl = 1;
                weightGenSg = 0;
                weightGenPl = 0;
                weightDatSg = 2;
                weightDatPl = 2;
                weightAkkSg = 1;
                weightAkkPl = 1;
                weightAblSg = 0;
                weightAblPl = 0;
                break;

            case "ABLATIV":
                weightNomSg = 1;
                weightNomPl = 1;
                weightGenSg = 0;
                weightGenPl = 0;
                weightDatSg = 1;
                weightDatPl = 1;
                weightAkkSg = 1;
                weightAkkPl = 1;
                weightAblSg = 3;
                weightAblPl = 3;
                break;

            case "GENITIV":

                weightNomSg = 1;
                weightNomPl = 1;
                weightGenSg = 4;
                weightGenPl = 4;
                weightDatSg = 1;
                weightDatPl = 1;
                weightAkkSg = 1;
                weightAkkPl = 1;
                weightAblSg = 1;
                weightAblPl = 1;
                break;

            // Alle gleichmäßig
            default:
                weightNomSg = 1;
                weightNomPl = 1;
                weightGenSg = 1;
                weightGenPl = 1;
                weightDatSg = 1;
                weightDatPl = 1;
                weightAkkSg = 1;
                weightAkkPl = 1;
                weightAblSg = 1;
                weightAblPl = 1;
        }

        weights = new int[]{
                weightNomSg,
                weightNomPl,
                weightGenSg,
                weightGenPl,
                weightDatSg,
                weightDatPl,
                weightAkkSg,
                weightAkkPl,
                weightAblSg,
                weightAblPl};
    }

    /**
     * Copied from GrammatikUserInputDeklinationsendung.class
     * @return the String of the chosen declination as defined in the array "faelle[]"
     */
    private String getRandomDeklination(){

        //Getting a upper bound for the random number being retrieved afterwards
        int max =  (weights[0]+
                    weights[1]+
                    weights[2]+
                    weights[3]+
                    weights[4]+
                    weights[5]+
                    weights[6]+
                    weights[7]+
                    weights[8]+
                    weights[9]);

        Random randomNumber = new Random();
        int intRandom = randomNumber.nextInt(max) + 1;
        int sum = 1;
        int sumNew;

        /*
        Each case gets a width corresponding to the 'weights'-arr.
        Goes through every case and checks if the 'randomInt' is in the area of the current case
         */
        int randomVocabulary = -1;
        for(int i = 0; i < weights.length; i++){

            sumNew = sum + weights[i];

            //checks if 'intRandom' is between the 'sum' and 'sumNew' and thus in the area of the current case
            if (intRandom >= sum && intRandom < sumNew){
                randomVocabulary = i;
                break;
            }
            else {
                sum = sumNew;
            }
        }

        if(randomVocabulary == -1){
            //Something went wrong. Log error-message
            Log.e(TAG, "Getting a randomDeclination failed! Returned -1 for " +
                    "\nrandomNumber: " + randomNumber +
                    "\nlektion: " + extraFromEinheitenUebersicht);
        }

        return faelle[randomVocabulary];
    }

    private void checkInput(){
        userInput.setFocusable(false);

        hideKeyboard();


        //Checking the userInput against the translation
        int color;
        if(compareString(userInput.getText().toString(), dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), currentDeclination))){
            color = ResourcesCompat.getColor(getResources(), R.color.correct, null);

            SharedPreferences.Editor editor = sharedPref.edit();

            //Increasing the counter by 1
            editor.putInt(KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG + extraFromEinheitenUebersicht,
                    sharedPref.getInt(KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG+extraFromEinheitenUebersicht, 0) + 1);
            editor.apply();
        }else {
            color = ResourcesCompat.getColor(getResources(), R.color.error, null);

            if (sharedPref.getInt(KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG+extraFromEinheitenUebersicht, 0) > 0) {
                SharedPreferences.Editor editor = sharedPref.edit();
                //Decreasing the counter by 1
                editor.putInt(KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG + extraFromEinheitenUebersicht,
                        sharedPref.getInt(KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG + extraFromEinheitenUebersicht, 0) - 1);
                editor.apply();
            }

            weiter.startAnimation(animShake);
            userInput.startAnimation(animShake);

            Score.incrementCurrentMistakesDeklInput(sharedPref);

            int wrong = Score.getCurrentMistakesDeklInput(sharedPref);
            if (wrong == -1){
                wrong = 0;
            }
            amountWrong.setText("Fehler: " + wrong);

        }
        userInput.setBackgroundColor(color);

        //Showing the correct translation
        solution.setText(dbHelper.getDekliniertenSubstantiv(currentVokabel.getId(), currentDeclination));

        bestaetigung.setVisibility(View.GONE);
        weiter.setVisibility(View.VISIBLE);
        solution.setVisibility(View.VISIBLE);

    }

    private void resetCurrentLektion(){


        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Trainer zurücksetzen?")
                .setMessage("Willst du den Deklinations-Trainer wirklich neu starten?\nDeine beste Note wird beibehalten!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        General.showMessage("Deklinations-Trainer zurückgesetzt!", getApplicationContext());

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(KEY_PROGRESS_USERINPUT_DEKLINATIONSENDUNG, 0);
                        editor.apply();

                        Score.resetCurrentMistakesDeklInput(sharedPref);
                        finish();

                    }})
                .setNegativeButton(android.R.string.no, null).show();



    }

    /**
     * Compares the userInput with a wanted input and returns if the comparison was successful.
     * @param userInput String to be compared with the wanted input
     * @param wantedString the original string to be compared with
     * @return Was the comparison successful?
     */
    private boolean compareString(String userInput, String wantedString){

        // Returns false for empty input
        if (userInput.equals("")){
            return false;
        }

        //Deleting all whitespaces at the start of the input
        if (userInput.length() > 1) {
            while (userInput.charAt(0) == ' ') {
                userInput = userInput.substring(1, userInput.length());
                if (userInput.length() == 1) break;
            }
        }
        //Deleting all whitespaces at the end of the input
        if (userInput.length() > 1) {
            while (userInput.charAt(userInput.length() - 1) == ' ') {
                userInput = userInput.substring(0, userInput.length() - 1);
                if (userInput.length() == 1) break;
            }
        }

        if (userInput.equalsIgnoreCase(wantedString)) return true;
        else return false;

    }

    /**
     * Executed when all vocabularies are learned.
     */
    private void allLearned(){

        request.setVisibility(View.GONE);
        solution.setVisibility(View.GONE);
        userInput.setVisibility(View.GONE);
        bestaetigung.setVisibility(View.GONE);
        weiter.setVisibility(View.GONE);
        reset.setVisibility(View.VISIBLE);
        zurück.setVisibility(View.VISIBLE);



        sCongratulations.setVisibility(View.VISIBLE);
        sCurrentTrainer.setVisibility(View.VISIBLE);
        sMistakeAmount.setVisibility(View.VISIBLE);
        sMistakeAmountValue.setVisibility(View.VISIBLE);
        sBestTry.setVisibility(View.VISIBLE);
        sBestTryValue.setVisibility(View.VISIBLE);
        sHighScore.setVisibility(View.GONE);
        sHighScoreValue.setVisibility(View.GONE);
        sGrade.setVisibility(View.VISIBLE);
        sGradeValue.setVisibility(View.VISIBLE);
        sBack.setVisibility(View.VISIBLE);
        sReset.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);

        amountWrong.setVisibility(View.GONE);

        int mistakeAmount = Score.getCurrentMistakesDeklInput(sharedPref);

        Score.updateLowestMistakesDeklInput(mistakeAmount, sharedPref);

        sCurrentTrainer.setText("Du hast gerade den Deklinationsendungs-Trainer abgeschlossen!");

        String grade = Score.getGradeFromMistakeAmount(maxProgress + 2*mistakeAmount, mistakeAmount);

        String lowestEverText = Score.getLowestMistakesDeklInput(sharedPref) + "";
        SpannableStringBuilder gradeText = General.makeSectionOfTextBold(grade, ""+grade);

        if(mistakeAmount != -1){
            sMistakeAmountValue.setText(Integer.toString(mistakeAmount) + "");
        }else{
            sMistakeAmountValue.setText("N/A");
        }
        sBestTryValue.setText(lowestEverText);
        sGradeValue.setText(gradeText);
    }

    /**
     * Handling the button-presses
     * @param view the view of the pressed button
     */
    public void userInputButtonClicked(View view){

        switch (view.getId()){

            //Checking if all vocabularies have been learned already and getting a new one
            case (R.id.buttonUserInputNächsteVokabel):

                newVocabulary();
                break;

            //Checking if the user input was correct
            case (R.id.buttonUserInputEingabeBestätigt):

                checkInput();
                break;

            case (R.id.scoreButtonReset):

                resetCurrentLektion();
                break;

            //Returning to the previous activity
            case (R.id.scoreButtonBack):
                finish();
                break;


        }
    }

    @Override
    public void onPause() {
        super.onPause();

        hideKeyboard();
    }
}
