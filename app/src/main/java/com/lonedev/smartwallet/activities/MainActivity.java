package com.lonedev.smartwallet.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lonedev.smartwallet.static_data.EntryCategoryAccess;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.static_data.IntentInfo;
import com.lonedev.smartwallet.fragments.HomeFragment;
import com.lonedev.smartwallet.fragments.MovementsFragment;
import com.lonedev.smartwallet.fragments.ProgressesFragment;
import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.static_data.MovementPeriodAccess;
import com.lonedev.smartwallet.static_data.WarningAccess;

/**
 * Classe creata dopo il caricamento e l'inizializzazione delle risorse (a opera di LoadingActivity).
 * Principalmente, gestisce il passaggio fra le tre schermate principali dell'applicazione (Home, Movimenti, Progressi),
 * rappresentate da altrettanti Fragment da essa racchiusi (HomeFragment, MovementsFragment, ProgressesFragment).
 *
 * @author Marco Michelini
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Attributo rappresentante la barra di navigazione dell'applicazione, che permette il passaggio fra le sue schermate
     * principali.
     */
    private BottomNavigationView bottomNavigation;

    /**
     * Attributo che permette di accedere all'array statico contenente i possibili tipi di Entry.
     */
    private EntryTypeAccess entryType;

    /**
     * Attributo che permette di accedere all'array statico contenente le possibili categorie di Entry.
     */
    private EntryCategoryAccess entryCategory;

    /**
     * Attributo che permette di accedere all'array statico contenente i possibili periodi temporali di un movimento.
     */
    private MovementPeriodAccess movementPeriod;

    /**
     * Metodo chiamato alla creazione dell'Activity.
     *
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato dell'Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActivity();
        selectStartingFragment();
        checkForReachedProgress();
    }

    /**
     * Metodo chiamato durante la creazione dell'Activity, per inizializzarne lo stato.
     * Inizializza toolbar, barra di navigazione e attributi necessari ad accedere agli array statici usati dall'app.
     */
    private void initActivity() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        bottomNavigation = findViewById(R.id.navigation_bottom);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);
        entryType = new EntryTypeAccess(this);
        entryCategory = new EntryCategoryAccess(this);
        movementPeriod = new MovementPeriodAccess(this);
    }

    /**
     * Metodo che seleziona la schermata da visualizzare (tra Home, Movimenti e Progressi) sulla base dell'informazione passata
     * da un'istanza di Intent. In assenza di tale informazione, viene selezionata la schermata Home.
     */
    private void selectStartingFragment() {
        int intentType = getIntent().getIntExtra(IntentInfo.SCREEN, 0);

        if (intentType != 0) {

            if (intentType == IntentInfo.HOME) bottomNavigation.setSelectedItemId(R.id.nav_home);
            else if (intentType == IntentInfo.MOVEMENTS) bottomNavigation.setSelectedItemId(R.id.nav_movements);
            else if (intentType == IntentInfo.PROGRESSES) bottomNavigation.setSelectedItemId(R.id.nav_progresses);

        } else {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * Metodo che verifica il raggiungimento di un limite di spesa o di un obiettivo, basandosi sulle informazioni passate
     * all'Activity da un'istanza di Intent.
     */
    private void checkForReachedProgress() {
        WarningAccess warningAccess = new WarningAccess(this);
        int intentData = getIntent().getIntExtra(IntentInfo.WARNING, 0);

        if (intentData != 0) {

            if (intentData == IntentInfo.LIMIT_REACHED) warningAccess.show(WarningAccess.LIMIT_REACHED);
            else if (intentData == IntentInfo.TARGET_REACHED) warningAccess.show(WarningAccess.TARGET_REACHED);

        }
    }

    /**
     * Listener utilizzato per rilevare l'item della barra di navigazione (Home, Movimenti, Progressi) selezionato
     * dall'utente, e passare dunque alla relativa schermata.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_movements:
                            selectedFragment = new MovementsFragment();
                            break;
                        case R.id.nav_progresses:
                            selectedFragment = new ProgressesFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment).commit();
                    return true;
                }
            };

    /**
     * Ritorna un riferimento a entryType, evitando ai Fragment racchiusi nell'Activity di caricare nuovamente
     * in memoria l'array statico contenente i possibili tipi di Entry.
     */
    public EntryTypeAccess getEntryTypeAccess() {
        return entryType;
    }

    /**
     * Ritorna un riferimento a entryCategory, evitando ai Fragment racchiusi nell'Activity di caricare nuovamente
     * in memoria l'array statico contenente le possibili categorie di Entry.
     */
    public EntryCategoryAccess getEntryCategoryAccess() {
        return entryCategory;
    }

    /**
     * Ritorna un riferimento a movementPeriod, evitando ai Fragment racchiusi nell'Activity di caricare nuovamente
     * in memoria l'array statico contenente i possibili periodi temporali di un movimento.
     */
    public MovementPeriodAccess getMovementPeriodAccess() {
        return movementPeriod;
    }

}
