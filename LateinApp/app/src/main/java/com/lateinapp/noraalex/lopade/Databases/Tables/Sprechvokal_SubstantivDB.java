package com.lateinapp.noraalex.lopade.Databases.Tables;

import android.provider.BaseColumns;

public class Sprechvokal_SubstantivDB {

    private Sprechvokal_SubstantivDB(){}

    public static class FeedEntry implements BaseColumns {

        //Table name
        public static final String TABLE_NAME = "Sprechvokal_Substantiv";

        //TODO: Maybe add a 'name' Column like in the 'Sprechvokal_Präsens' table.
        //Table columns
        public static final String
                COLUMN_NOM_SG = "Nom_Sg",
                COLUMN_NOM_PL = "Nom_Pl",
                COLUMN_GEN_SG = "Gen_Sg",
                COLUMN_GEN_PL = "Gen_Pl",
                COLUMN_DAT_SG = "Dat_Sg",
                COLUMN_DAT_PL = "Dat_Pl",
                COLUMN_AKK_SG = "Akk_Sg",
                COLUMN_AKK_PL = "Akk_PL",
                COLUMN_ABL_SG = "Abl_Sg",
                COLUMN_ABL_PL = "Abl_Pl";
    }
}
