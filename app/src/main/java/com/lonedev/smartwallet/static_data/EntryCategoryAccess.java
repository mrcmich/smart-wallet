package com.lonedev.smartwallet.static_data;

import android.content.Context;

import com.lonedev.smartwallet.R;

/**
 * Classe che permette, attraverso le sue chiavi intere, di accedere alla lista di possibili categorie di spesa (sia in entrata
 * che in uscita) previste dall'app, e salvate nel file strings.xml.
 *
 * @author Marco Michelini
 */
public final class EntryCategoryAccess extends StringArrayAccess {
    public static final int NONE = 0;
    public static final int ALL = 1;
    public static final int CLOTHING = 2;
    public static final int TRANSPORTATION = 3;
    public static final int HEALTH = 4;
    public static final int SPORT = 5;
    public static final int FOOD = 6;
    public static final int OUTINGS = 7;
    public static final int RENT = 8;
    public static final int HOBBIES = 9;
    public static final int TRIPS = 10;
    public static final int TECH = 11;
    public static final int GARDENING = 12;
    public static final int BILLS = 13;
    public static final int VEHICLE_MAINTENANCE = 14;
    public static final int SALARY = 15;
    public static final int SALES = 16;
    public static final int GIFTS = 17;
    public static final int OTHER = 18;

    /**
     * Ritorna un array contenente le chiavi associate alle categorie da mostrare nel relativo Spinner di MovementsFragment.
     * In questo caso, tutte le possibili categorie selezionabili, sia in entrata che in uscita.
     */
    public static final int[] FILTER_ALL = {ALL, CLOTHING, TRANSPORTATION, HEALTH, SPORT, FOOD, OUTINGS, RENT, HOBBIES, TRIPS,
            TECH, GARDENING, BILLS, VEHICLE_MAINTENANCE, SALARY, SALES, GIFTS, OTHER};

    /**
     * Ritorna un array contenente le chiavi associate alle categorie da mostrare nel relativo Spinner di MovementsFragment.
     * In questo caso, solo le possibili categorie in entrata selezionabili.
     */
    public static final int[] FILTER_IN = {ALL, SALARY, SALES, GIFTS, OTHER};

    /**
     * Ritorna un array contenente le chiavi associate alle categorie da mostrare nel relativo Spinner di MovementsFragment.
     * In questo caso, solo le possibili categorie in uscita selezionabili.
     */
    public static final int[] FILTER_OUT = {ALL, CLOTHING, TRANSPORTATION, HEALTH, SPORT, FOOD, OUTINGS, RENT, HOBBIES, TRIPS,
            TECH, GARDENING, BILLS, VEHICLE_MAINTENANCE, OTHER};

    /**
     * Ritorna un array contenente le chiavi associate alle categorie da mostrare nel relativo Spinner di NewMovementFragment.
     * In questo caso, solo le possibili categorie in entrata selezionabili.
     */
    public static final int[] NEW_MOVEMENT_IN = {NONE, SALARY, SALES, GIFTS, OTHER};

    /**
     * Ritorna un array contenente le chiavi associate alle categorie da mostrare nel relativo Spinner di NewMovementFragment.
     * In questo caso, solo le possibili categorie in uscita selezionabili.
     */
    public static final int[] NEW_MOVEMENT_OUT = {NONE, CLOTHING, TRANSPORTATION, HEALTH, SPORT, FOOD, OUTINGS, RENT, HOBBIES,
            TRIPS, TECH, GARDENING, BILLS, VEHICLE_MAINTENANCE, OTHER};

    /**
     * Ritorna un array contenente le chiavi associate alle categorie da mostrare nel relativo Spinner di NewProgressFragment.
     * In questo caso, solo le categorie che è possibile e sensato monitorare.
     */
    public static final int[] NEW_LIMIT = {NONE, CLOTHING, TRANSPORTATION, HEALTH, SPORT, FOOD, OUTINGS, HOBBIES, TRIPS,
            TECH, GARDENING, BILLS, VEHICLE_MAINTENANCE};

    /**
     * Inizializza EntryCategoryAccess tramite l'ID dell'array statico, contenente la lista di tutte le possibili categorie di spesa,
     * all'interno del file strings.xml.
     *
     * @param context Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    public EntryCategoryAccess(Context context) {
        super(context, R.array.entry_categories);
    }

}
