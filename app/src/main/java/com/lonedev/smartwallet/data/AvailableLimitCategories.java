package com.lonedev.smartwallet.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe astratta contenente la lista di categorie non ancora monitorate dall'utente.
 * Al primo avvio dell'applicazione, sono monitorabili tutte le categorie relative a movimenti in uscita.
 * Tale lista viene aggiornata ogni qual volta l'utente crea/elimina un limite di spesa.
 *
 * @author Marco Michelini
 */
public abstract class AvailableLimitCategories {

    /**
     * Lista delle categorie monitorabili dall'utente.
     */
    private static List<String> availableCategories;

    /**
     * Inizializza availableCategories con le categorie fornite.
     *
     * @param allOutCategories Array contenente tutte le categorie monitorabili dall'utente.
     */
    public static void init(String[] allOutCategories) {
        if (allOutCategories == null)
            availableCategories = null;
        else
            availableCategories = new ArrayList<>(Arrays.asList(allOutCategories));
    }

    /**
     * Aggiunge una nuova categoria monitorabile ad availableCategories.
     *
     * @param category La categoria da aggiungere.
     */
    public static void add(String category) {
        availableCategories.add(category);
    }


    /**
     * Rimuove una categoria monitorabile da availableCategories.
     *
     * @param category La categoria da rimuovere.
     */
    public static void remove(String category) {
        availableCategories.remove(category);
    }

    /**
     * Ritorna un array contenente tutte le categorie monitorabili dall'utente.
     */
    public static String[] get() {
        if (availableCategories == null || availableCategories.isEmpty())
            return new String[0];

        return availableCategories.toArray(new String[0]);
    }

}
