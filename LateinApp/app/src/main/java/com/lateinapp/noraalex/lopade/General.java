package com.lateinapp.noraalex.lopade;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


public class General {

    private static SharedPreferences sharedPreferences;

    public static void showMessage(String msg, Context context){

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(String msg, Context context, int duration){

        Toast.makeText(context, msg, duration).show();
    }

    /**
     * Replaces a set of characterCombinations with a corresponding umlaut each
     * TODO: This would be redundant if we could just import a .csv file with a better charset -> Maybe google sheets??
     * @param s the String to be updated
     * @return the updated String
     */
    public static String replaceWithUmlaut(String s){

        s = s.replace("aeae", "ä");
        s = s.replace("AeAe", "Ä");
        s = s.replace("ueue", "ü");
        s = s.replace("UeUe", "Ü");
        s = s.replace("oeoe", "ö");
        s = s.replace("OeOe", "Ö");
        s = s.replace("sz", "ß");

        /*
       TODO: Implement this
        s = s.replace("-A", "Ā");
        s = s.replace("-a", "ā");
        s = s.replace("-E", "Ē");
        s = s.replace("-e", "ē");
        s = s.replace("-I", "Ī");
        s = s.replace("-i", "ī");
        s = s.replace("-O", "Ō");
        s = s.replace("-o", "ō");
        s = s.replace("-U", "Ū");
        s = s.replace("-u", "ū");

        s = s.replace("#A", "Ă");
        s = s.replace("#a", "ă");
        s = s.replace("#E", "Ĕ");
        s = s.replace("#e", "ĕ");
        s = s.replace("#I", "Ĭ");
        s = s.replace("#i", "ĭ");
        s = s.replace("#O", "Ŏ");
        s = s.replace("#o", "ŏ");
        s = s.replace("#U", "Ŭ");
        s = s.replace("#u", "ŭ");
        */

        return s;
    }

    public static SharedPreferences getSharedPrefrences(Context context){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        }

        return sharedPreferences;
    }

    //
    //Scoring System
    //

    private static final int MAX_COMBO = 3;
    private static final int MIN_COMBO = 0;

    public static int calculateMaxPossiblePoints(int pointBaseline, int inputAmount){

        int score = 0;

        int combo = MIN_COMBO;

        for(int i = 0; i < inputAmount; i++){

            score += getScoreDifference(getComboMultiplier(combo), pointBaseline, true);;

            if(combo >= MIN_COMBO && combo < MAX_COMBO){
                combo++;
            }
        }

        return score;
    }

    public static int modifyScore(int pointBaseline, boolean inputCorrect, int lektion, SharedPreferences sharedPreferences){

        int oldScore = getPoints(lektion, sharedPreferences);
        int combo = getCombo(lektion, sharedPreferences);

        int difference = getScoreDifference(combo, pointBaseline, inputCorrect);

        int delta = difference;
        if(oldScore + difference < 0){
            delta = 0;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Score_" + lektion, oldScore + delta);
        editor.putInt("Score_All", getPoints(lektion, sharedPreferences) + delta);
        editor.apply();

        //Does not return the change in score.
        //if the score is 0 we dont subtract points but still show a "-100" for example
        return difference;
    }

    public static int getPoints(int lektion, SharedPreferences sharedPreferences){
        return sharedPreferences.getInt("Score_" + lektion, 0);
    }

    public static int getTotalPoints(SharedPreferences sharedPreferences){
        return sharedPreferences.getInt("Score_All", 0);
    }

    public static void resetPoints(int lektion, SharedPreferences sharedPreferences){

        int oldPoints = getPoints(lektion, sharedPreferences);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Score_" + lektion, 0);
        editor.putInt("Score_All", getPoints(lektion, sharedPreferences) - oldPoints);
        editor.apply();
    }

    public static void increaseCombo(int lektion, SharedPreferences sharedPreferences){

        int currentCombo = sharedPreferences.getInt("Combo_" + lektion, MIN_COMBO);

        if(currentCombo >= MIN_COMBO && currentCombo < MAX_COMBO){
            currentCombo++;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Combo_" + lektion, currentCombo);
        editor.apply();
    }

    public static void decreaseCombo(int lektion, SharedPreferences sharedPreferences){

        int currentCombo = sharedPreferences.getInt("Combo_" + lektion, MIN_COMBO);

        if(currentCombo > MIN_COMBO && currentCombo <= MAX_COMBO){
            currentCombo--;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Combo_" + lektion, currentCombo);
        editor.apply();
    }


    private static int getScoreDifference(int combo, int pointBaseline, boolean inputCorrect){

        int amount;

        //Calculating final amount to increase the score by
        if(inputCorrect){
            amount = combo * pointBaseline;
        }else{
            amount = -pointBaseline;
        }
        return (amount);
    }

    private static int getComboMultiplier(int combo){
        return (int)pow(2, combo);
    }

    public static int getCombo(int lektion, SharedPreferences sharedPreferences){
        int combo = sharedPreferences.getInt("Combo_" + lektion, MIN_COMBO);
        return getComboMultiplier(combo);
    }

    public static void resetCombo(int lektion, SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("Combo_" + lektion, MIN_COMBO);

        editor.apply();
    }



    //https://en.wikipedia.org/wiki/Exponentiation_by_squaring
    //More efficient pow algorithm with O(log n) instead of O(n)
    private static float pow(float base, int exp){

        if (exp < 0) {
            base = 1 / base;
            exp *= -1;
        }else if(exp == 0){
            return 1;
        }

        int y = 1;

        while (exp > 1) {
            if(exp % 2 == 0){
                base *= base;
                exp /= 2;
            }else{
                y *= base;
                base *= base;
                exp = (exp - 1)/2;
            }
        }
        return base * y;

    }
}