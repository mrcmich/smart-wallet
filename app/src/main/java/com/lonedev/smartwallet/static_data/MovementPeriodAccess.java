package com.lonedev.smartwallet.static_data;

import android.content.Context;

import com.lonedev.smartwallet.R;

/**
 * Classe che permette, attraverso le sue chiavi intere, di accedere alla lista di possibili periodi di movimento
 * previsti dall'app, e salvati nel file strings.xml.
 * Questi sono usati all'interno del relativo Spinner di MovementsFragment, per filtrare i movimenti visualizzati in base
 * alla loro data di creazione.
 *
 * @author Marco Michelini
 */
public class MovementPeriodAccess extends StringArrayAccess {
    public static final int LAST_WEEK = 0;
    public static final int LAST_MONTH = 1;
    public static final int LAST_THREE_MONTHS = 2;
    public static final int LAST_YEAR = 3;

    /**
     * Inizializza MovementPeriodAccess tramite l'ID dell'array statico, contenente la lista di tutti i possibili periodi di movimento,
     * all'interno del file strings.xml.
     *
     * @param context Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    public MovementPeriodAccess(Context context) {
        super(context, R.array.movement_periods);
    }

}
