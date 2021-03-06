package com.lateinapp.noraalex.lopade.Activities.Einheiten;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lateinapp.noraalex.lopade.Activities.LateinAppActivity;
import com.lateinapp.noraalex.lopade.Databases.DBHelper;
import com.lateinapp.noraalex.lopade.Databases.Tables.AdjektivDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.AdverbDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.PräpositionDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.SprichwortDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.SubjunktionDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.SubstantivDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.VerbDB;
import com.lateinapp.noraalex.lopade.Databases.Tables.Vokabel;
import com.lateinapp.noraalex.lopade.General;
import com.lateinapp.noraalex.lopade.R;
import com.lateinapp.noraalex.lopade.Score;

import static com.lateinapp.noraalex.lopade.Databases.SQL_DUMP.allVocabularyTables;
import static com.lateinapp.noraalex.lopade.Global.DEVELOPER;
import static com.lateinapp.noraalex.lopade.Global.DEV_CHEAT_MODE;

public class UserInputVokabeltrainer extends LateinAppActivity{

    private static final String TAG = "UserInputVokabeltrainer";

    private SharedPreferences sharedPref;
    private DBHelper dbHelper;

    private TextView request,
         solution,
         titel,
         mistakeAmount,
         score,
         highScore,
         combo;
    private EditText userInput;
    private ProgressBar progressBar;
    //FIXME: Remove button elevation to make it align with 'userInput'-EditText
    private Button bestaetigung,
        weiter;

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



    private Vokabel currentVokabel;

    private int lektion;
    private int backgroundColor,
                errorColor,
                correctColor,
                errorTextColor,
                correctTextColor;

    Animation animShake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_user_input);

        setup();

        newRequest();
    }

    private void setup(){

        Intent intent = getIntent();
        lektion = intent.getIntExtra("lektion",0);

        sharedPref = General.getSharedPrefrences(getApplicationContext());
        dbHelper = DBHelper.getInstance(getApplicationContext());

        backgroundColor = ResourcesCompat.getColor(getResources(), R.color.background, null);
        errorColor = ResourcesCompat.getColor(getResources(), R.color.error, null);
        errorTextColor = ResourcesCompat.getColor(getResources(), R.color.errorText, null);
        correctColor = ResourcesCompat.getColor(getResources(), R.color.correct, null);
        correctTextColor = ResourcesCompat.getColor(getResources(), R.color.correctText, null);

        mistakeAmount = findViewById(R.id.textUserInputMistakes);
        request = findViewById(R.id.textUserInputLatein);
        solution = findViewById(R.id.textUserInputDeutsch);
        highScore = findViewById(R.id.textUserInputHighScore);
        userInput = findViewById(R.id.textUserInputUserInput);
        progressBar = findViewById(R.id.progressBarUserInput);
        bestaetigung = findViewById(R.id.buttonUserInputEingabeBestätigt);
        weiter = findViewById(R.id.buttonUserInputNächsteVokabel);
        titel = findViewById(R.id.textUserInputÜberschrift);
        score = findViewById(R.id.textUserInputScore);
        combo = findViewById(R.id.textUserInputCombo);

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

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        //TODO: We dont have a score on other trainers yet.
        //This means that we have to hide the score/combo TextView originally
        //and only make it visible here in this trainer
        score.setVisibility(View.VISIBLE);
        combo.setVisibility(View.GONE);

        userInput.setHint("Übersetzung");
        //Makes it possible to move to the next vocabulary by pressing "enter"
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
        titel.setText("Vokabeltrainer " + lektion);

        solution.setVisibility(View.GONE);
        weiter.setVisibility(View.GONE);

        progressBar.setMax(100);
        progressBar.setProgress((int)(dbHelper.getGelerntProzent(lektion) * 100));

        mistakeAmount.setText("Fehler: " + dbHelper.getMistakeAmount(lektion));

        combo.setText(Score.getCombo(lektion, sharedPref) + "x");
        score.setText("Score: " + Score.getScoreVocabularyTrainer(lektion, sharedPref));
        highScore.setText("High-Score: " + Score.getHighScoreVocabularyTrainer(sharedPref, lektion));
    }

    private void newRequest(){

        progressBar.setProgress((int)(dbHelper.getGelerntProzent(lektion) * 100));

        //Checks if all vocabularies have been learned already
        if (dbHelper.getGelerntProzent(lektion) == 1) {

            hideKeyboard();

            allLearned();


        } else {
            userInput.setText("");
            userInput.setBackgroundColor(backgroundColor);
            userInput.setFocusableInTouchMode(true);

            userInput.setHintTextColor(ContextCompat.getColor(this, R.color.greyLight));
            userInput.setTextColor(ContextCompat.getColor(this, R.color.black));

            showKeyboard();

            //Getting a new vocabulary.
            currentVokabel = dbHelper.getRandomVocabulary(lektion);
            String lateinText = currentVokabel.getLatein();
            //#DEVELOPER
            if (DEVELOPER && DEV_CHEAT_MODE) lateinText += "\n" + currentVokabel.getDeutsch();
            request.setText(lateinText);

            //Adjusting the visibility of the buttons.
            bestaetigung.setVisibility(View.VISIBLE);
            weiter.setVisibility(View.GONE);
            solution.setVisibility(View.GONE);
        }
    }

    private void checkInput(){
        userInput.setFocusable(false);

        hideKeyboard();

        bestaetigung.setVisibility(View.GONE);
        weiter.setVisibility(View.VISIBLE);
        solution.setVisibility(View.VISIBLE);

        //Checking the userInput against the translation
        int color;
        int scoreDifference;
        if(compareTranslation(userInput.getText().toString(), currentVokabel.getDeutsch())){

            //Input was correct

            //@SCORE_CLEANUP
            scoreDifference = Score.modifyScore(true, lektion, sharedPref);

            //Checking the vocabulary as learned
            dbHelper.setGelernt(getVokabelTable(currentVokabel), currentVokabel.getId(), true);

            color = correctColor;

            userInput.setTextColor(correctTextColor);

            //@SCORE_CLEANUP
            if(Score.isNewHighscoreNow(lektion, sharedPref)){
                General.showMessage("New Highscore!!!", this);
            }

        }else {

            //Input was incorrect

            //@SCORE_CLEANUP
            scoreDifference = Score.modifyScore(false, lektion, sharedPref);

            dbHelper.incrementValue(getVokabelTable(currentVokabel), AdverbDB.FeedEntry.COLUMN_AMOUNT_INCORRECT, currentVokabel.getId());

            color = errorColor;

            userInput.setHintTextColor(ContextCompat.getColor(this, R.color.greyLight));
            userInput.setTextColor(errorTextColor);

            weiter.startAnimation(animShake);
            userInput.startAnimation(animShake);
        }
        userInput.setBackgroundColor(color);

        popupScore(scoreDifference);

        //@SCORE_CLEANUP
        // Currently non visible textView because we dont use combo right now
        // combo.setText(Score.getCombo(lektion, sharedPref) + "x");
        score.setText("Score: " + Score.getScoreVocabularyTrainer(lektion, sharedPref));
        highScore.setText("High-Score: " + Score.getHighScoreVocabularyTrainer(sharedPref, lektion));


        //Showing the correct translation

        int MAX_CHAR_AMOUNT = 50;
        String translation = currentVokabel.getDeutsch();
        while(translation.length() > MAX_CHAR_AMOUNT){
            String[] substrings = translation.split(",", -1);
            int lengthLastWord = substrings[substrings.length-1].length();

            translation = translation.substring(0, translation.length() - lengthLastWord - 1);
        }

        solution.setText(translation);
        mistakeAmount.setText("Fehler: " + dbHelper.getMistakeAmount(lektion));


    }

    /**
     * Compares the userInput with a wanted translation and returns if the comparison was successful.
     * @param userInput String to be compared with the translation
     * @param wantedTranslation the original translation to be compared with
     * @return Was the comparison successful
     */
    private boolean compareTranslation(String userInput, String wantedTranslation){

        String[] userTokens = userInput.split(",", -1);
        String[] translationTokens = wantedTranslation.split(",", -1);

        //Checking if every userToken[]-element matches with a translation
        for (String user : userTokens){

            // Returns false for empty tokens
            if (user.equals("")){
                return false;
            }

            //Deleting all whitespaces at the start of the token
            if (user.length() > 1) {
                while (user.charAt(0) == ' ') {
                    user = user.substring(1, user.length());
                    if (user.length() == 1) break;
                }
            }
            //Deleting all whitespaces at the end of the token
            if (user.length() > 1) {
                while (user.charAt(user.length() - 1) == ' ') {
                    user = user.substring(0, user.length() - 1);
                    if (user.length() == 1) break;
                }
            }

            boolean found = false;

            for (String translation : translationTokens){

                //Deleting all whitespaces at the start of the token
                while (translation.charAt(0) == ' '){
                    if (translation.length() == 1) break;
                    translation = translation.substring(1, translation.length());
                }

                //Deleting all whitespaces at the end of the token
                while (translation.charAt(translation.length()-1) == ' '){
                    if (translation.length() == 1) break;
                    translation = translation.substring(0, translation.length()-1);
                }
                //Checking with pronouns
                if (user.equalsIgnoreCase(translation)){
                    found = true;
                }
                //Checking without pronouns
                if (translation.contains("der") || translation.contains("die") || translation.contains("das") ||
                    translation.contains("Der") || translation.contains("Die") || translation.contains("Das")){
                    if (user.equalsIgnoreCase(translation.substring(4))){
                        found = true;
                    }
                }
                //Checking without 'Sich'/'sich'
                if (translation.contains("sich") || translation.contains("Sich")){
                    if (user.equalsIgnoreCase(translation.substring(5))){
                        found = true;
                    }
                }

            }

            if (!found){
                return false;
            }

        }

        //Everything was correct if we got here without returning false.
        return true;
    }

    /**
     * Determines what subclass of 'Vokabel' the given object is.
     * @param vokabel the 'Vokabel' where the type is to be determined
     * @return type of the 'Vokabel'-instance
     */
    private String getVokabelTable(Vokabel vokabel){

        if(vokabel instanceof SubstantivDB){

            return SubstantivDB.FeedEntry.TABLE_NAME;

        }else if(vokabel instanceof VerbDB){

            return VerbDB.FeedEntry.TABLE_NAME;

        }else if(vokabel instanceof SprichwortDB){

            return SprichwortDB.FeedEntry.TABLE_NAME;

        }else if(vokabel instanceof PräpositionDB){

            return PräpositionDB.FeedEntry.TABLE_NAME;

        }else if(vokabel instanceof AdverbDB){

            return AdverbDB.FeedEntry.TABLE_NAME;

        }else if(vokabel instanceof AdjektivDB){

            return AdjektivDB.FeedEntry.TABLE_NAME;

        }else if (vokabel instanceof SubjunktionDB) {

            return SubjunktionDB.FeedEntry.TABLE_NAME;

        }else {

            Log.e(TAG,"No VokabelTyp found");

            return "NO_MATCH_GET_VOKABEL_TABLE()";
        }
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
        progressBar.setVisibility(View.GONE);

        //@SCORE_CLEANUP
        score.setVisibility(View.GONE);
        //combo.setVisibility(View.GONE);
        highScore.setVisibility(View.GONE);
        mistakeAmount.setVisibility(View.GONE);


        titel.setVisibility(View.GONE);

        //Score screen
        //@SCORE_CLEANUP
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

        int mistakeAmount = dbHelper.getMistakeAmount(lektion);
        int vocabularyAmount = dbHelper.countTableEntries(allVocabularyTables, lektion);

        Score.updateHighscore(lektion, sharedPref);
        Score.updateLowestMistakesVoc(mistakeAmount, lektion, sharedPref);

        sCurrentTrainer.setText("Du hast gerade Lektion " + lektion + " abgeschlossen!");


        //@SCORE_CLEANUP
        int score = Score.getScoreVocabularyTrainer(lektion, sharedPref);
        int scoreMax = Score.getMaxPossiblePoints(vocabularyAmount);
        int highScore = Score.getHighScoreVocabularyTrainer(sharedPref, lektion);
        String grade = Score.getGradeFromMistakeAmount(vocabularyAmount, mistakeAmount);

        String endScoreText = Score.getLowestMistakesVoc(lektion, sharedPref) + "";
        SpannableStringBuilder highScoreText = General.makeSectionOfTextBold(highScore + "/" + scoreMax, ""+highScore);
        SpannableStringBuilder gradeText = General.makeSectionOfTextBold(grade, ""+grade);

        if(mistakeAmount != -1){
            sMistakeAmountValue.setText(Integer.toString(mistakeAmount) + "");
        }else{
            sMistakeAmountValue.setText("N/A");
        }
        sBestTryValue.setText(endScoreText);
        sHighScoreValue.setText(highScoreText);
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

                newRequest();
                break;

            //Checking if the user input was correct
            case (R.id.buttonUserInputEingabeBestätigt):

                checkInput();
                break;

            //Setting the 'learned' state of all vocabularies of the current lektion to false
            case (R.id.scoreButtonReset):

                resetCurrentLektion();
                break;

            //Returning to the previous activity
            case (R.id.scoreButtonBack):

                finish();
                break;
        }
    }

    //@SCORE_CLEANUP
    private void resetCurrentLektion(){


        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Lektion zurücksetzen?")
                .setMessage("Willst du den Vokabeltrainer für die Lektion " + lektion + " wirklich neu starten?\nDeine beste Note wird beibehalten!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        General.showMessage("Lektion " + lektion + " zurückgesetzt!", getApplicationContext());


                        dbHelper.resetLektion(lektion);
                        Score.resetScoreVocabulary(lektion, sharedPref);
                        finish();


                    }})
                .setNegativeButton(android.R.string.no, null).show();



    }

    //@SCORE_CLEANUP
    private void resetScore(){

        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Note zurücksetzen?")
                .setMessage("Willst du die Note des Vokabeltrainer für die Lektion " + lektion + " wirklich zurücksetzen?\nDeine beste Note wird hier gelöscht!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        General.showMessage("Score für lektion " + lektion + " zurückgesetzt!", getApplicationContext());


                        dbHelper.resetLektion(lektion);
                        Score.resetScoreVocabulary(lektion, sharedPref);
                        Score.resetHighscoreVocabulary(lektion, sharedPref);
                        Score.resetLowestMistakesVoc(lektion, sharedPref);
                        finish();


                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }


    @Override
    public void onPause() {
        super.onPause();

        hideKeyboard();
    }

    @Override
    public void openInfoPopup() {

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View popupView;

        popupView = layoutInflater.inflate(R.layout.popup_info_vokabeltrainer, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        Button btnDismiss = popupView.findViewById(R.id.popup_info_vokabeltrainer_dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void popupScore(int score){


        String message = "";
        int color;

        if(score > 0){
            message += "+";
            color = ResourcesCompat.getColor(getResources(), R.color.correct, null);

        }else if(score < 0){
            color = ResourcesCompat.getColor(getResources(), R.color.error, null);

        }else{
            //No score difference
            color = ResourcesCompat.getColor(getResources(), R.color.grey, null);

        }
        message += score;


        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(color);
        tv.setTextSize(30);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv.setId(View.generateViewId());

        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        ConstraintLayout layout = findViewById(R.id.constraint_Layout);
        layout.addView(tv, tv.getLayoutParams());

        ConstraintSet c = new ConstraintSet();
        c.clone(layout);
        c.connect(tv.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT,0);
        c.connect(tv.getId(), ConstraintSet.LEFT,   layout.getId(), ConstraintSet.LEFT,0);
        c.connect(tv.getId(), ConstraintSet.TOP,   layout.getId(), ConstraintSet.TOP,0);
        c.connect(tv.getId(), ConstraintSet.BOTTOM,   layout.getId(), ConstraintSet.BOTTOM,0);
        c.applyTo(layout);


        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.score_move_fade);
        anim.setFillAfter(true);
        tv.startAnimation(anim);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.reset_trainer_button, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //Opening a popup window
            case (R.id.action_reset_trainer):

                resetCurrentLektion();
                break;

            //Opening a popup window
            case (R.id.action_reset_score):

                resetScore();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}



