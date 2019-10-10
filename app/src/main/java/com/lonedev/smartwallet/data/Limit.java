package com.lonedev.smartwallet.data;

import java.util.Calendar;

/**
 * Classe che rappresenta un limite di spesa, ovvero la spesa massima mensile (definibile dall'utente) per una certa categoria.
 * I limiti di spesa hanno valenza mensile, e sono automaticamente rimossi dal database dell'app alla loro scadenza.
 *
 * @author Marco Michelini
 */
public class Limit extends Progress {

    /**
     * Il mese in cui il limite di spesa è stato creato.
     */
    private int month;

    /**
     * Inizializza un nuovo limite di spesa con il valore massimo mensile e la categoria forniti.
     * In particolare, il mese è quello corrente.
     *
     * @param description La categoria del nuovo limite di spesa.
     * @param max Il valore massimo mensile del nuovo limite di spesa.
     */
    public Limit(String description, float max) {
        super(description, max);
        month = (Calendar.getInstance().get(Calendar.MONTH)) + 1;
    }

    /**
     * Inizializza un nuovo limite di spesa con valore, valore massimo mensile, percentuale di completamento e categoria forniti.
     *
     * @param description La categoria del nuovo limite di spesa.
     * @param value Il valore del nuovo limite di spesa.
     * @param max Il valore massimo mensile del nuovo limite di spesa.
     * @param percent La percentuale di completamento del nuovo limite di spesa.
     */
    public Limit(String description, float value, float max, int percent) {
        super(description, value, max, percent);
        month = (Calendar.getInstance().get(Calendar.MONTH)) + 1;
    }

    /**
     * Ritorna il mese del limite di spesa.
     */
    public int getMonth() {
        return month;
    }

}
