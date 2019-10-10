package com.lonedev.smartwallet.static_data;

import android.content.Context;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe astratta che permette di accedere agli array statici di stringhe memorizzati nel file strings.xml.
 * Questo approccio facilita notevolmente l'eventuale traduzione dell'applicazione, dato che sarà sufficiente tradurre le stringhe
 * di testo contenute in tale file.
 *
 * @author Marco Michelini
 */
public abstract class StringArrayAccess {
    /**
     * Oggetto che racchiude coppie di indici interi e stringhe, permette di accedere con facilità agli array statici
     * che si vogliano mostrare in uno Spinner. Simile dal punto di vista logico a una Map, ma più efficiente in questo contesto.
     */
    protected SparseArray<String> stringArray;

    /**
     * Carica in stringArray tutte le stringhe contenute nell'array statico corrispondente all'ID passato, associando
     * ad ognuna una chiave intera (a partire da 0).
     *
     * @param context Il contesto dell'applicazione. Fornisce informazioni di carattere generale sull'app.
     * @param arrayResourceID L'ID dell'array all'interno del file strings.xml.
     */
    public StringArrayAccess(Context context, int arrayResourceID) {
        stringArray = new SparseArray<>();
        String[] arrayResource = context.getResources().getStringArray(arrayResourceID);

        for (int i = 0; i < arrayResource.length; i++) {
            stringArray.append(i, arrayResource[i]);
        }
    }

    /**
     * Ritorna la stringa corrispondente alla chiave passata come parametro. Se la chiave non appartiene a stringArray,
     * ritorna invece null.
     *
     * @param key Chiave.
     */
    public String get(int key) {
        if (key >= 0 && key < stringArray.size()) return stringArray.get(key);
        return null;
    }

    /**
     * Ritorna un array contenente le stringhe corrispondenti alle chiavi passate come parametro. Eventuali chiavi
     * non appartenenti a stringArray sono ignorate.
     *
     * @param keys Array di chiavi.
     */
    public String[] get(int[] keys) {
        List<String> values = new ArrayList<>();

        for (int k : keys) {
            if (k >= 0 && k < stringArray.size()) values.add(stringArray.get(k));
        }

        return values.toArray(new String[0]);
    }

    /**
     * Ritorna tutte le stringhe contenute in stringArray.
     */
    public String[] getAll() {
        List<String> values = new ArrayList<>();

        for (int i = 0; i < stringArray.size(); i++) {
            values.add(stringArray.get(i));
        }

        return values.toArray(new String[0]);
    }

}
