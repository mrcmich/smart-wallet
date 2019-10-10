package com.lonedev.smartwallet.UI_components;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lonedev.smartwallet.R;

/**
 * Classe che modellizza un tastierino numerico, usato per inserire il valore di un movimento o il limite massimo di un progresso.
 * Il valore in questione è visualizzato all'interno del TextView passato al costruttore.
 *
 * @author Marco Michelini
 */
public class Numpad implements OnClickListener {

    /**
     * Possibile stato di Numpad. Indica che la stringa associata al valore inserito è nulla, e dunque non valida.
     */
    private static final int NULL = 0;

    /**
     * Possibile stato di Numpad. Indica che la stringa associata al valore inserito è vuota, e dunque non valida.
     */
    private static final int EMPTY = 1;

    /**
     * Possibile stato di Numpad. Indica che la stringa associata al valore inserito è valida.
     */
    public static final int VALID_VALUE = 2;

    /**
     * La massima lunghezza della stringa associata al valore numerico inserito.
     */
    private static final float MAX_LENGTH = 10;

    /**
     * Il massimo numero di cifre decimali del valore numerico inserito.
     */
    private static final float MAX_DECIMAL_DIGITS = 2;

    /**
     * Il layout che rappresenta l'interfaccia di Numpad.
     */
    private GridLayout layout;

    /**
     * Attributo utilizzato per convertire il valore numerico inserito in una stringa, permettendone una più facile
     * visualizzazione nel formato voluto.
     */
    private StringBuilder value;

    /**
     * Il campo di testo in cui viene visualizzato, in tempo reale, il valore numerico inserito.
     */
    private TextView field;

    /**
     * Lo stato attuale di Numpad.
     */
    private int state;

    /**
     * La posizione della virgola all'interno della stringa associata al valore numerico inserito.
     * Se non presente, il suo indice è pari a -1.
     */
    private int commaIndex;

    /**
     * Inizializza un Numpad con il layout e il campo di testo forniti.
     * In particolare, ne inizializza i pulsanti.
     *
     * @param layout Il layout in cui visualizzare l'interfaccia del Numpad.
     * @param field Il campo di testo in cui visualizzare il valore numerico inserito tramite il Numpad.
     */
    public Numpad(GridLayout layout, TextView field) {
        this.layout = layout;
        this.field = field;
        state = EMPTY;
        value = new StringBuilder("0");
        field.setText(value);
        commaIndex = -1;
        initButtons();
    }

    /**
     * Inizializza i pulsanti del Numpad.
     */
    private void initButtons() {
        Button numKey0 = layout.findViewById(R.id.numpad_0);
        numKey0.setOnClickListener(this);
        Button numKey1 = layout.findViewById(R.id.numpad_1);
        numKey1.setOnClickListener(this);
        Button numKey2 = layout.findViewById(R.id.numpad_2);
        numKey2.setOnClickListener(this);
        Button numKey3 = layout.findViewById(R.id.numpad_3);
        numKey3.setOnClickListener(this);
        Button numKey4 = layout.findViewById(R.id.numpad_4);
        numKey4.setOnClickListener(this);
        Button numKey5 = layout.findViewById(R.id.numpad_5);
        numKey5.setOnClickListener(this);
        Button numKey6 = layout.findViewById(R.id.numpad_6);
        numKey6.setOnClickListener(this);
        Button numKey7 = layout.findViewById(R.id.numpad_7);
        numKey7.setOnClickListener(this);
        Button numKey8 = layout.findViewById(R.id.numpad_8);
        numKey8.setOnClickListener(this);
        Button numKey9 = layout.findViewById(R.id.numpad_9);
        numKey9.setOnClickListener(this);
        Button commaKey = layout.findViewById(R.id.numpad_comma);
        commaKey.setOnClickListener(this);
        ImageButton deleteLastKey = layout.findViewById(R.id.numpad_delete);
        deleteLastKey.setOnClickListener(this);
    }

    /**
     * Metodo chiamato alla pressione di uno dei pulsanti del Numpad.
     * Identifica il pulsante premuto, modifica conseguentemente value e aggiorna lo stato di Numpad.
     *
     * @param v Il pulsante premuto dall'utente.
     */
    @Override
    public void onClick(View v) {
        if ((commaIndex == -1 && value.toString().length() < MAX_LENGTH) || (commaIndex != -1 &&
                value.toString().length() - commaIndex < MAX_DECIMAL_DIGITS + 1 && value.toString().length() < MAX_LENGTH)) {
            findButtonClicked(v);
        }

        updateState();

        if (v.getId() == R.id.numpad_delete && state != NULL)
            value.deleteCharAt(value.length() - 1);

        field.setText(value);
    }

    /**
     * Identifica il pulsante premuto ed esegue la relativa funzione.
     *
     * @param view Il pulsante premuto.
     */
    private void findButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.numpad_0:
                if (state != EMPTY) value.append(0);
                break;
            case R.id.numpad_1:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(1);
                break;
            case R.id.numpad_2:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(2);
                break;
            case R.id.numpad_3:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(3);
                break;
            case R.id.numpad_4:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(4);
                break;
            case R.id.numpad_5:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(5);
                break;
            case R.id.numpad_6:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(6);
                break;
            case R.id.numpad_7:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(7);
                break;
            case R.id.numpad_8:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(8);
                break;
            case R.id.numpad_9:
                if (state == EMPTY) value.deleteCharAt(0);
                value.append(9);
                break;
            case R.id.numpad_comma:
                if (!(value.toString().contains(".")) && state != NULL) {
                    commaIndex = value.toString().length();
                    value.append(".");
                }
                break;
        }
    }

    /**
     * Aggiorna lo stato di Numpad in base al valore numerico attualmente inserito.
     */
    private void updateState() {
        if (value.toString().equals("0")) state = EMPTY;
        else if (value.length() == 0)
            state = NULL;
        else
            state = VALID_VALUE;
    }

    /**
     * Ritorna lo stato del Numpad.
     */
    public int getState() {
        return state;
    }

}


