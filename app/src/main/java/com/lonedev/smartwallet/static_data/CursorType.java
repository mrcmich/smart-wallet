package com.lonedev.smartwallet.static_data;

/**
 * Classe che racchiude i tre possibili tipi di Cursor, permettendo di accedere alle relative tabelle
 * all'interno del database.
 *
 * @author Marco Michelini
 */
public final class CursorType {

    /**
     * Indica un tipo di cursore che contiene solo movimenti.
     */
    public static final String MOVEMENTS = "MOVEMENTS";

    /**
     * Indica un tipo di cursore che contiene solo limiti di spesa.
     */
    public static final String LIMITS = "LIMITS";

    /**
     * Indica un tipo di cursore che contiene solo obiettivi.
     */
    public static final String TARGETS = "TARGETS";

}
