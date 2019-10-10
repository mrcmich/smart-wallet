package com.lonedev.smartwallet.data;

import com.lonedev.smartwallet.support.DataFormat;

import java.util.Calendar;
import java.util.UUID;

/**
 * Classe che rappresenta un movimento (in contanti).
 * I movimenti sono distinti in entrate e uscite, a seconda del loro valore (positivo nel primo caso, negativo nel secondo),
 * hanno valenza annuale e sono automaticamente rimossi dal database dell'app alla loro scadenza.
 *
 * @author Marco Michelini
 */
public class Movement extends Entry {

    /**
     * Il giorno del mese in cui il movimento è stato creato.
     */
    private int day;

    /**
     * Il mese in cui il movimento è stato creato.
     */
    private int month;

    /**
     * L'anno in cui il movimento è stato creato.
     */
    private int year;

    /**
     * Identifica univocamente un movimento, permettendo di eliminarlo dal database.
     */
    private String ID;

    /**
     * Inizializza un nuovo movimento con il valore e la categoria forniti.
     * In particolare, la data è quella corrente.
     *
     * @param description La categoria del nuovo movimento.
     * @param value Il valore del nuovo movimento.
     */
    public Movement(String description, float value) {
        super(description, value);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        year = Calendar.getInstance().get(Calendar.YEAR);
        ID = UUID.randomUUID().toString();
    }

    /**
     * Inizializza un nuovo movimento con valore, data, ID e categoria forniti.
     *
     * @param description La categoria del nuovo movimento.
     * @param value Il valore del nuovo movimento.
     * @param day Il giorno del mese del nuovo movimento.
     * @param month Il mese del nuovo movimento.
     * @param year L'anno del nuovo movimento.
     * @param ID L'ID del nuovo movimento.
     */
    public Movement(String description, float value, int day, int month, int year, String ID) {
        super(description, value);
        this.day = day;
        this.month = month;
        this.year = year;
        this.ID = ID;
    }

    /**
     * Ritorna il giorno del mese del movimento.
     */
    public int getDay() {
        return day;
    }

    /**
     * Ritorna il mese del movimento.
     */
    public int getMonth() {
        return month;
    }

    /**
     * Ritorna l'anno del movimento.
     */
    public int getYear() {
        return year;
    }

    /**
     * Ritorna l'ID del movimento.
     */
    public String getID() {
        return ID;
    }

    /**
     * Ritorna la data in cui il movimento è stato aggiunto nel formato dd/mm/yy.
     */
    public String getDate() {
        return DataFormat.format(day, month, year);
    }

}
