package com.lonedev.smartwallet.static_data;

import android.content.Context;

import com.lonedev.smartwallet.R;

/**
 * Classe che racchiude le chiavi legate ai possibili tipi di Entry, così come l'istanza di SparseArray<String> che li contiene.
 * L'ordine delle chiavi rispecchia quello delle corrispondenti stringhe all'interno del file strings.xml (e di stringArray).
 * Fornisce inoltre degli array di chiavi per accedere alle diverse liste di tipi mostrate nell'applicazione.
 *
 * @author Marco Michelini
 */
public final class EntryTypeAccess extends StringArrayAccess {
    public static final int NONE = 0;
    public static final int ALL = 1;
    public static final int IN = 2;
    public static final int INS = 3;
    public static final int OUT = 4;
    public static final int OUTS = 5;
    public static final int LIMIT = 6;
    public static final int TARGET = 7;
    public static final int SELECT_TARGET = 8;

    /**
     * Array delle chiavi associate alle stringhe visualizzate nello Spinner filterType.
     * (si veda MovementsFragment)
     */
    public static final int[] FILTER = {ALL, INS, OUTS};

    /**
     * Array delle chiavi associate alle stringhe visualizzate nello Spinner newMovementType, in assenza di obiettivi
     * nel database.
     * (si veda NewMovementFragment)
     */
    public static final int[] NEW_MOVEMENT_NO_TARGETS = {NONE, IN, OUT};

    /**
     * Array delle chiavi associate alle stringhe visualizzate nello Spinner newMovementType, in presenza di obiettivi
     * nel database.
     * (si veda NewMovementFragment)
     */
    public static final int[] NEW_MOVEMENT = {NONE, IN, OUT, TARGET};

    /**
     * Array delle chiavi associate alle stringhe visualizzate nello Spinner newProgressType.
     * (si veda NewProgressFragment)
     */
    public static final int[] NEW_PROGRESS = {NONE, LIMIT, TARGET};

    /**
     * Inizializza un'istanza di EntryTypeAccess, fornendole l'ID corrispondente al corretto array
     * del file strings.xml.
     *
     * @param context Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    public EntryTypeAccess(Context context) {
        super(context, R.array.entry_types);
    }

}
