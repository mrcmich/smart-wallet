package com.lonedev.smartwallet.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.static_data.EntryCategoryAccess;
import com.lonedev.smartwallet.data.AvailableLimitCategories;
import com.lonedev.smartwallet.data.DatabaseHandler;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.static_data.MovementPeriodAccess;
import com.lonedev.smartwallet.support.MovementsFilter;

/**
 * Classe creata all'avvio dell'applicazione, si occupa di caricare in memoria e inizializzare le risorse necessarie.
 *
 * @author Marco Michelini
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     * Attributo che permette di accedere al database dell'app (sia in scrittura che in lettura).
     */
    private DatabaseHandler databaseHandler;

    /**
     * Metodo chiamato alla creazione dell'Activity.
     *
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato dell'Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        initActivity();
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Metodo chiamato alla distruzione dell'Activity, permette di chiudere o liberare le risorse non più necessarie.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHandler.close();
    }

    /**
     * Metodo chiamato durante la creazione dell'Activity, per inizializzarne lo stato.
     * Carica e inizializza le risorse necessarie al funzionamento dell'applicazione ed elimina movimenti e
     * limiti di spesa non più pertinenti (precedenti l'anno e il mese corrente, rispettivamente).
     */
    private void initActivity() {
        databaseHandler = new DatabaseHandler(this);
        databaseHandler.clearOld();
        EntryTypeAccess entryType = new EntryTypeAccess(this);
        EntryCategoryAccess entryCategory = new EntryCategoryAccess(this);
        MovementPeriodAccess movementPeriod = new MovementPeriodAccess(this);
        AvailableLimitCategories.init(entryCategory.get(EntryCategoryAccess.NEW_LIMIT));
        MovementsFilter.init(entryType, entryCategory, movementPeriod);
    }

}
