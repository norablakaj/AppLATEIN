package com.lateinapp.noraalex.lopade.Activities.Einheiten;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lateinapp.noraalex.lopade.Activities.LateinAppActivity;
import com.lateinapp.noraalex.lopade.Databases.DBHelper;
import com.lateinapp.noraalex.lopade.Databases.Tables.Personalendung_PräsensDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.VerbDB;
import com.lateinapp.noraalex.lopade.General;
import com.lateinapp.noraalex.lopade.R;
import com.lateinapp.noraalex.lopade.Score;

import java.util.Arrays;
import java.util.Random;

import static com.lateinapp.noraalex.lopade.Global.DEVELOPER;
import static com.lateinapp.noraalex.lopade.Global.DEV_CHEAT_MODE;
import static com.lateinapp.noraalex.lopade.Global.KEY_CURRENT_MISTAKE_AMOUNT_PERSONALENDUNG_CLICK;
import static com.lateinapp.noraalex.lopade.Global.KEY_PROGRESS_CLICK_DEKLINATIONSENDUNG;
import static com.lateinapp.noraalex.lopade.Global.KEY_PROGRESS_CLICK_PERSONALENDUNG;
import static com.lateinapp.noraalex.lopade.Global.KEY_PROGRESS_USERINPUT_ESSEVELLENOLLE;

public class ClickPersonalendung extends LateinAppActivity {

    private static final String TAG = "ClickPersonalendung";

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

    private DBHelper dbHelper;
    private SharedPreferences sharedPref;

    private Button weiter,
            zurück,
            reset;
    private ProgressBar progressBar;
    private TextView latein, amountWrong;

    private final String[] faelle = {
            Personalendung_PräsensDB.FeedEntry.COLUMN_1_SG,
            Personalendung_PräsensDB.FeedEntry.COLUMN_2_SG,
            Personalendung_PräsensDB.FeedEntry.COLUMN_3_SG,
            Personalendung_PräsensDB.FeedEntry.COLUMN_1_PL,
            Personalendung_PräsensDB.FeedEntry.COLUMN_2_PL,
            Personalendung_PräsensDB.FeedEntry.COLUMN_3_PL};
    private ToggleButton[] buttons;

    private String konjugation;
    private int backgroundColor;
    private static final int maxProgress = 20;

    Animation animShake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_click_personalendung);

        setup();

        newVocabulary();

    }

    private void setup(){

        dbHelper = DBHelper.getInstance(getApplicationContext());

        sharedPref = General.getSharedPrefrences(this);

        backgroundColor = ResourcesCompat.getColor(getResources(), R.color.background, null);

        latein = findViewById(R.id.textGrammatikPersonalendungLatein);
        progressBar = findViewById(R.id.progressBarPersonalendung);

        ToggleButton ersteSg = findViewById(R.id.buttonGrammatikPersonalendung1PersSg);
        ToggleButton zweiteSg = findViewById(R.id.buttonGrammatikPersonalendung2PersSg);
        ToggleButton dritteSg = findViewById(R.id.buttonGrammatikPersonalendung3PersSg);
        ToggleButton erstePl = findViewById(R.id.buttonGrammatikPersonalendung1PersPl);
        ToggleButton zweitePl = findViewById(R.id.buttonGrammatikPersonalendung2PersPl);
        ToggleButton drittePl = findViewById(R.id.buttonGrammatikPersonalendung3PersPl);

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

        reset = findViewById(R.id.buttonGrammatikPersonalendungReset);
        weiter = findViewById(R.id.buttonGrammatikPersonalendungWeiter);
        zurück = findViewById(R.id.buttonGrammatikPersonalendungZurück);

        amountWrong = findViewById(R.id.textUserInputMistakes2);

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        buttons = new ToggleButton[]{
                ersteSg, zweiteSg,
                dritteSg, erstePl,
                zweitePl, drittePl
        };

        progressBar.setMax(maxProgress);
        int progress = sharedPref.getInt(KEY_PROGRESS_CLICK_PERSONALENDUNG, 0);
        if (progress > maxProgress) progress = maxProgress;
        progressBar.setProgress(progress);

        int wrong = Score.getCurrentMistakesPersClick(sharedPref);
        if (wrong == -1){
            wrong = 0;
        }
        amountWrong.setText("Fehler: " + wrong);

    }

    /**
     * Checks if the user already completed the 'grammatikKonjugation'.
     * Retrieves a new vocabulary and sets it to be the current one.
     */
    private void newVocabulary(){
        
        int progress = sharedPref.getInt(KEY_PROGRESS_CLICK_PERSONALENDUNG, 0);

        latein.setBackgroundColor(backgroundColor);

        if (progress < maxProgress) {
            progressBar.setProgress(progress);

            konjugation = faelle[new Random().nextInt(faelle.length)];

            VerbDB currentVokabel = dbHelper.getRandomVerb();
            String lateinText = dbHelper.getKonjugiertesVerb(currentVokabel.getId(), konjugation);

            //#DEVELOPER
            if (DEVELOPER && DEV_CHEAT_MODE) lateinText += "\n" + konjugation;
          
            latein.setText(lateinText);
            
        }else {
            allLearned();

        }

    }

    /**
     * Handles button-clicks
     * @param view the clicked element
     */
    public void personalendungButtonClicked(View view){

        //This is easier than placing all elements of the array into the switch statement
        for(ToggleButton tb: buttons){
            if(tb.getId() == view.getId()){
                checkInput(tb);
                weiter.setVisibility(View.VISIBLE);

                for(ToggleButton toggleButton: buttons){
                    toggleButton.setClickable(false);
                }
                return;
            }
        }

        switch (view.getId()){

            //Gets the next vocabulary
            case (R.id.buttonGrammatikPersonalendungWeiter):

                weiter.setVisibility(View.GONE);
                newVocabulary();

                for (ToggleButton tb: buttons){
                    tb.setChecked(false);
                    tb.setClickable(true);
                    tb.setBackground(ContextCompat.getDrawable(this, R.drawable.toggle_button_selector));
                }
                break;

            //Resets all progress up to this point
            case (R.id.buttonGrammatikPersonalendungReset):

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(KEY_PROGRESS_CLICK_PERSONALENDUNG, 0);
                editor.apply();

                finish();
                break;

            case (R.id.buttonGrammatikPersonalendungZurück):

                finish();
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

    private void checkInput(ToggleButton tb){

        int index = Arrays.asList(buttons).indexOf(tb);
        int wantedIndex = Arrays.asList(faelle).indexOf(konjugation);

        boolean correct = (index == wantedIndex);

        int color;

        int currentScore = sharedPref.getInt(KEY_PROGRESS_CLICK_PERSONALENDUNG, 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        if (correct) {
            tb.setBackground(ContextCompat.getDrawable(this, R.drawable.toggle_button_correct_selector));

            color = ResourcesCompat.getColor(getResources(), R.color.correct, null);

            editor.putInt(KEY_PROGRESS_CLICK_PERSONALENDUNG, currentScore + 1);
        }else {
            buttons[wantedIndex].setBackground(ContextCompat.getDrawable(this, R.drawable.toggle_button_correct_selector));
            tb.setBackground(ContextCompat.getDrawable(this, R.drawable.toggle_button_wrong_selector));

            color = ResourcesCompat.getColor(getResources(), R.color.error, null);

            if (currentScore > 0) {
                editor.putInt(KEY_PROGRESS_CLICK_PERSONALENDUNG, currentScore - 1);
            }

            Score.incrementCurrentMistakesPersClick(sharedPref);

            int wrong = Score.getCurrentMistakesPersClick(sharedPref);
            if (wrong == -1){
                wrong = 0;
            }
            amountWrong.setText("Fehler: " + wrong);

        }
        editor.apply();

        latein.setBackgroundColor(color);

    }

    private void resetCurrentLektion(){


        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Trainer zurücksetzen?")
                .setMessage("Willst du den Personalendung-Trainer wirklich neu starten?\nDeine beste Note wird beibehalten!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        General.showMessage("Personalendung-Trainer zurückgesetzt!", getApplicationContext());

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(KEY_PROGRESS_CLICK_PERSONALENDUNG, 0);
                        editor.apply();

                        Score.resetCurrentMistakesPersClick(sharedPref);
                        finish();

                    }})
                .setNegativeButton(android.R.string.no, null).show();



    }

    private void allLearned(){

        for (ToggleButton b : buttons){
            b.setVisibility(View.GONE);
        }

        weiter.setVisibility(View.GONE);
        latein.setVisibility(View.GONE);
        reset.setVisibility(View.GONE);
        zurück.setVisibility(View.GONE);

        ((TextView)findViewById(R.id.textGrammatikPersonalendungAufgabe)).setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);

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

        int mistakeAmount = Score.getCurrentMistakesPersClick(sharedPref);

        Score.updateLowestMistakesPersClick(mistakeAmount, sharedPref);

        sCurrentTrainer.setText("Du hast gerade den Personalendung-Trainer abgeschlossen!");

        String grade = Score.getGradeFromMistakeAmount(maxProgress + 2*mistakeAmount, mistakeAmount);

        String lowestEverText = Score.getLowestMistakesPersClick(sharedPref) + "";
        SpannableStringBuilder gradeText = General.makeSectionOfTextBold(grade, ""+grade);

        if(mistakeAmount != -1){
            sMistakeAmountValue.setText(Integer.toString(mistakeAmount) + "");
        }else{
            sMistakeAmountValue.setText("N/A");
        }
        sBestTryValue.setText(lowestEverText);
        sGradeValue.setText(gradeText);
    }

}
