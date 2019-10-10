package com.lonedev.smartwallet.static_data;

import android.content.Context;
import android.widget.Toast;

import com.lonedev.smartwallet.R;

/**
 * Classe che permette, attraverso le sue chiavi intere, di accedere alla lista di possibili messaggi di errore
 * previsti dall'app, e salvati nel file strings.xml.
 *
 * @author Marco Michelini
 */
public class WarningAccess extends StringArrayAccess {
    public static final int DATABASE_ERROR = 0;
    public static final int LIMIT_REACHED = 1;
    public static final int TARGET_REACHED = 2;
    public static final int INVALID_VALUE = 3;
    public static final int MOVEMENT_TYPE_NOT_SELECTED = 4;
    public static final int MOVEMENT_CATEGORY_NOT_SELECTED = 5;
    public static final int PROGRESS_TYPE_NOT_SELECTED = 6;
    public static final int LIMIT_CATEGORY_NOT_SELECTED = 7;
    public static final int TARGET_NOT_SELECTED = 8;
    public static final int NOT_ENOUGH_MONEY = 9;
    public static final int EMPTY_NAME_TARGET = 10;
    public static final int DUPLICATE_TARGET = 11;

    /**
     * Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    private Context context;

    /**
     * Inizializza WarningAccess tramite l'ID dell'array statico, contenente la lista di tutti i possibili messaggi di errore,
     * all'interno del file strings.xml.
     *
     * @param context Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    public WarningAccess(Context context) {
        super(context, R.array.warnings);
        this.context = context;
    }

    /**
     * Mostra in un Toast il messaggio di errore corrispondente alla chiave fornita.
     * Se non vi è alcuna corrispondenza, non viene mostrato alcun Toast.
     *
     * @param key La chiave associata al messaggio di errore da mostrare.
     */
    public void show(int key) {
        String message = get(key);

        if (message != null) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        }

    }

}
