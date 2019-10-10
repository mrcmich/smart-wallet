package com.lonedev.smartwallet.support;

import com.lonedev.smartwallet.static_data.EntryCategoryAccess;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.static_data.MovementPeriodAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe astratta che modellizza un filtro per movimenti.
 * Contiene i filtri impostati dall'utente nella schermata Movimenti, utilizzati per selezionare
 * quali movimenti visualizzare nel relativo RecyclerView.
 *
 * @author Marco Michelini
 */
public abstract class MovementsFilter {

    /**
     * Il tipo di filtro, per filtrare i movimenti in base al loro tipo.
     */
    private static String type;

    /**
     * L'indice di riga associato al tipo di filtro all'interno del relativo Spinner di MovementsFragment.
     * Permette di mantenere la selezione del tipo anche quando l'utente abbandona la schermata Movimenti.
     */
    private static int typePosition;

    /**
     * La categoria del filtro, per filtrare i movimenti in base alla loro categoria.
     */
    private static String category;

    /**
     * L'indice di riga associato alla categoria del filtro all'interno del relativo Spinner di MovementsFragment.
     * Permette di mantenere la selezione della categoria anche quando l'utente abbandona la schermata Movimenti.
     */
    private static int categoryPosition;

    /**
     * Il periodo del filtro, per filtrare i movimenti in base alla loro data di creazione.
     */
    private static String period;

    /**
     * L'indice di riga associato al periodo del filtro all'interno del relativo Spinner di MovementsFragment.
     * Permette di mantenere la selezione del periodo anche quando l'utente abbandona la schermata Movimenti.
     */
    private static int periodPosition;

    /**
     * Attributo che permette di accedere all'array statico contenente i possibili tipi di Entry.
     * Questi sono mostrati all'interno di uno Spinner, permettendo all'utente di selezionare il tipo di movimenti da visualizzare.
     */
    private static EntryTypeAccess entryType;

    /**
     * Attributo che permette di accedere all'array statico contenente le possibili categorie di Entry.
     * Questi sono mostrati all'interno di uno Spinner, permettendo all'utente di selezionare la categoria di movimenti da visualizzare.
     */
    private static EntryCategoryAccess entryCategory;

    /**
     * Attributo che permette di accedere all'array statico contenente i possibili periodi temporali di un movimenti da visualizzare.
     * Questi sono mostrati all'interno di uno Spinner, permettendo all'utente di selezionare il periodo di movimenti da visualizzare.
     */
    private static MovementPeriodAccess movementPeriod;

    /**
     * Inizializza MovementsFilter con le sottoclassi di StringArrayAccess fornite.
     *
     * @param eta Riferimento all'istanza di EntryTypeAccess.
     * @param eca Riferimento all'istanza di EntryCategoryAccess.
     * @param mpa Riferimento all'istanza di MovementPeriodAccess.
     */
    public static void init(EntryTypeAccess eta, EntryCategoryAccess eca, MovementPeriodAccess mpa) {
        entryType = eta;
        entryCategory = eca;
        movementPeriod = mpa;
        type = entryType.get(EntryTypeAccess.ALL);
        category = entryCategory.get(EntryCategoryAccess.ALL);
        period = movementPeriod.get(MovementPeriodAccess.LAST_WEEK);
        typePosition = -1;
        categoryPosition = -1;
        periodPosition = -1;
    }

    /**
     * Ritorna il tipo di filtro.
     */
    public static String getType() {
        return type;
    }

    /**
     * Ritorna la categoria del filtro.
     */
    public static String getCategory() {
        return category;
    }

    /**
     * Ritorna il periodo del filtro.
     */
    public static String getPeriod() {
        return period;
    }

    /**
     * Ritorna l'indice di riga associato al tipo di filtro all'interno del relativo Spinner di MovementsFragment.
     */
    public static int getTypePosition() {
        return typePosition;
    }

    /**
     * Ritorna l'indice di riga associato alla categoria del filtro all'interno del relativo Spinner di MovementsFragment.
     */
    public static int getCategoryPosition() {
        return categoryPosition;
    }

    /**
     * Ritorna l'indice di riga associato al periodo del filtro all'interno del relativo Spinner di MovementsFragment.
     */
    public static int getPeriodPosition() {
        return periodPosition;
    }

    /**
     * Imposta il tipo di filtro al tipo fornito, e ne aggiorna la posizione.
     *
     * @param t Il nuovo tipo di filtro.
     */
    public static void setType(String t) {
        type = t;
        List<String> types = new ArrayList<>(Arrays.asList(entryType.get(EntryTypeAccess.FILTER)));
        typePosition = types.indexOf(t);
    }

    /**
     * Imposta la categoria del filtro alla categoria fornita, e ne aggiorna la posizione.
     *
     * @param c La nuova categoria del filtro.
     */
    public static void setCategory(String c) {
        category = c;
        List<String> categories;

        if (type.equals(entryType.get(EntryTypeAccess.INS)))
            categories = new ArrayList<>(Arrays.asList(entryCategory.get(EntryCategoryAccess.FILTER_IN)));
        else if (type.equals(entryType.get(EntryTypeAccess.OUTS)))
            categories = new ArrayList<>(Arrays.asList(entryCategory.get(EntryCategoryAccess.FILTER_OUT)));
        else
            categories = new ArrayList<>(Arrays.asList(entryCategory.get(EntryCategoryAccess.FILTER_ALL)));

        categoryPosition = categories.indexOf(c);
    }

    /**
     * Imposta il periodo del filtro al periodo fornito, e ne aggiorna la posizione.
     *
     * @param p Il nuovo periodo del filtro.
     */
    public static void setPeriod(String p) {
        period = p;
        List<String> periods = new ArrayList<>(Arrays.asList(movementPeriod.getAll()));
        periodPosition = periods.indexOf(p);
    }

}
