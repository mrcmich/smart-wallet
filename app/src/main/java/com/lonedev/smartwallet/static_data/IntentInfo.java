package com.lonedev.smartwallet.static_data;

/**
 * Classe che definisce il tipo di informazione trasportata da un'istanza di Intent.
 * Viene usata nell'applicazione per trasmettere informazioni tra due Activity.
 *
 * @author Marco Michelini
 */
public final class IntentInfo {

    /**
     * Chiave associata al valore trasportato dall'intent. In questo caso, indica
     * che il valore in questione è una stringa.
     */
    public static final String TEXT = "IntentExtraString";

    /**
     * Chiave associata al valore trasportato dall'intent. In questo caso, indica
     * che il valore in questione rappresenta un warning, da visualizzare in un Toast.
     */
    public static final String WARNING = "IntentExtraInteger";

    /**
     * Chiave associata al valore trasportato dall'intent. In questo caso, indica
     * che il valore in questione rappresenta una schermata, ovvero un'Activity o un Fragment da visualizzare.
     */
    public static final String SCREEN = "IntentType";

    /**
     * Valore possibilmente associato a WARNING. Indica il raggiungimento di un limite di spesa.
     */
    public static final int LIMIT_REACHED = -1;

    /**
     * Valore possibilmente associato a WARNING. Indica il raggiungimento di un obiettivo.
     */
    public static final int TARGET_REACHED = -2;

    /**
     * Valore possibilmente associato a SCREEN. Indica come HomeFragment la schermata da visualizzare.
     */
    public static final int HOME = 1;

    /**
     * Valore possibilmente associato a SCREEN. Indica come MovementsFragment la schermata da visualizzare.
     */
    public static final int MOVEMENTS = 2;

    /**
     * Valore possibilmente associato a SCREEN. Indica come ProgressesFragment la schermata da visualizzare.
     */
    public static final int PROGRESSES = 3;

    /**
     * Valore possibilmente associato a SCREEN. Indica come NewEntryActivity la schermata da visualizzare (e specifica
     * che si vuole modificare, e non aggiungere, un progresso).
     */
    public static final int EDIT_PROGRESS = 4;

}
