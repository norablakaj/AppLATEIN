package com.example.norablakaj.lateinapp.Databases.Tables;

/**
 * Created by Alexander on 10.02.2018.
 */

//TODO Where should this class be in the project structure
public class Vokabel {

    private final int id;
    private final String latein,
            deutsch;

    public Vokabel(int id, String lateinInf, String deutsch){
        this.id = id;
        this.latein = lateinInf;
        this.deutsch = deutsch;
    }

    public int getId() {
        return id;
    }
    public String getLatein() {
        return latein;
    }
    public String getDeutsch() {
        return deutsch;
    }
}