package com.lonedev.smartwallet.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.static_data.EntryCategoryAccess;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.static_data.IntentInfo;
import com.lonedev.smartwallet.data.Limit;
import com.lonedev.smartwallet.data.Movement;
import com.lonedev.smartwallet.data.AvailableLimitCategories;
import com.lonedev.smartwallet.data.Target;
import com.lonedev.smartwallet.fragments.NewMovementFragment;
import com.lonedev.smartwallet.fragments.NewProgressFragment;
import com.lonedev.smartwallet.UI_components.Numpad;
import com.lonedev.smartwallet.data.DatabaseHandler;
import com.lonedev.smartwallet.static_data.WarningAccess;

/**
 * Classe creata all'aggiunta di un movimento e all'aggiunta/modifica di un progresso, gestisce la raccolta delle informazioni necessarie e la
 * creazione di una nuova entry. Questo viene fatto mediante due diversi Fragment (racchiusi dall'Activity), a seconda che
 * l'entry in questione sia un movimento (NewMovementFragment) o un progresso (NewProgressFragment). Utilizza le interfacce
 * NewMovementFragment.DataBridge e NewProgressFragment.DataBridge per comunicare con i due Fragment. Si occupa infine essa stessa della
 * modifica di un progresso.
 *
 * @author Marco Michelini
 */
public class NewEntryActivity extends AppCompatActivity implements NewMovementFragment.DataBridge, NewProgressFragment.DataBridge {

    /**
     * Il tastierino numerico per l'inserimento del valore del movimento o del massimo del progresso.
     */
    private Numpad numpad;

    /**
     * Memorizza il tipo di informazione trasportata dall'intent.
     */
    private int intentType;

    /**
     * L'intent che sarà poi utilizzato per la creazione di una nuova Activity.
     */
    private Intent outputIntent;

    /**
     * Permette di accedere al database dell'app (sia in scrittura che in lettura).
     */
    private DatabaseHandler databaseHandler;

    /**
     * Prende in input il nome del nuovo obiettivo.
     */
    private EditText newTargetName;

    /**
     * Mostra il valore/massimo inserito dall'utente in tempo reale.
     */
    private TextView newEntryValue;

    /**
     * Permette di accedere all'array statico contenente i possibili messaggi di errore dell'app.
     */
    private WarningAccess warning;

    /**
     * Permette di accedere all'array statico contenente i possibili tipi di Entry.
     */
    private EntryTypeAccess entryType;

    /**
     * Permette di accedere all'array statico contenente le possibili categorie di Entry.
     */
    private EntryCategoryAccess entryCategory;

    /**
     * Visualizza la lista di possibili tipologie di movimento (entrata, uscita ed eventualmente Obiettivo).
     */
    private Spinner newMovementType;

    /**
     * Visualizza la lista di possibili categorie di movimento (diverse a seconda del tipo).
     */
    private Spinner newMovementCategory;

    /**
     * Visualizza la lista di possibili obiettivi, se presenti.
     */
    private Spinner targetName;

    /**
     * Visualizza la lista di possibili tipologie di progresso (limite di spesa, obiettivo).
     */
    private Spinner newProgressType;

    /**
     * Visualizza la lista delle categorie monitorabili dal limite di spesa.
     */
    private Spinner newLimitCategory;

    /**
     * Metodo chiamato internamente a NewMovementFragment dopo la sua creazione, permette la comunicazione delle informazioni
     * raccolte da quest'ultimo (tipo, categoria e valore del movimento) all'Activity.
     *
     * @param newMovementSpinners Array contenente i riferimenti ai tre Spinner di NewMovementFragment.
     */
    @Override
    public void newMovementFragmentCreated(Spinner[] newMovementSpinners) {
        newMovementType = newMovementSpinners[0];
        newMovementCategory = newMovementSpinners[1];
        targetName = newMovementSpinners[2];
    }

    /**
     * Metodo chiamato internamente a NewProgressFragment dopo la sua creazione, permette la comunicazione delle informazioni
     * raccolte da quest'ultimo (tipo, categoria - se il progresso è un limite di spesa - e nome - se il progresso è un
     * obiettivo - del progresso) all'Activity.
     *
     * @param newProgressSpinners Array contenente i riferimenti ai due Spinner di NewProgressFragment.
     * @param newTargetName Riferimento all'omonima istanza di EditText usata internamente da NewProgressFragment.
     */
    @Override
    public void newProgressFragmentCreated(Spinner[] newProgressSpinners, EditText newTargetName) {
        newProgressType = newProgressSpinners[0];
        newLimitCategory = newProgressSpinners[1];
        this.newTargetName = newTargetName;
    }

    /**
     * Metodo chiamato alla creazione dell'Activity.
     *
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato dell'Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        initActivity();
        loadFragment();
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
     */
    private void initActivity() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_new_entry));
        newEntryValue = findViewById(R.id.value_new_entry);
        numpad = new Numpad((GridLayout) findViewById(R.id.layout_numpad), newEntryValue);
        intentType = getIntent().getIntExtra(IntentInfo.SCREEN, 0);
        databaseHandler = new DatabaseHandler(this);
        outputIntent = new Intent(this, MainActivity.class);
        warning = new WarningAccess(this);
        entryType = new EntryTypeAccess(this);
        entryCategory = new EntryCategoryAccess(this);
    }

    /**
     * Visualizza il fragment corretto dell'Activity, a seconda che l'utente voglia aggiungere un movimento (NewMovementFragment)
     * o un progresso (NewProgressFragment). Nel caso si voglia invece modificare un progresso, non viene visualizzato alcun Fragment, dato che se
     * ne occupa direttamente l'Activity.
     */
    private void loadFragment() {
        String toolbarTitle = null;
        CardView newEntryCard = findViewById(R.id.card_new_entry);
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            if (intentType == IntentInfo.MOVEMENTS) {
                toolbarTitle = getResources().getString(R.string.new_movement_toolbar_text);
                newEntryCard.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_new_entry,
                        new NewMovementFragment()).commit();
            } else if (intentType == IntentInfo.PROGRESSES){
                toolbarTitle = getResources().getString(R.string.new_progress_toolbar_text);
                newEntryCard.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_new_entry,
                        new NewProgressFragment()).commit();
            } else if (intentType == IntentInfo.EDIT_PROGRESS){
                toolbarTitle = getResources().getString(R.string.edit_progress_toolbar_text);
                newEntryCard.setVisibility(View.INVISIBLE);
            }

            if (toolbarTitle == null)
                toolbarTitle = getResources().getString(R.string.app_name);

            supportActionBar.setTitle(toolbarTitle);
        }

    }

    /**
     * Metodo chiamato alla creazione di un menù delle opzioni, permette di aggiungere alla toolbar
     * una o più azioni contestuali. In questo caso, permette di confermare l'inserimento delle
     * informazioni necessarie all'aggiunta di un movimento o all'aggiunta/modifica di un progresso.
     *
     * @param menu Il menù da visualizzare.
     * @return true per visualizzare il menù.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Metodo chiamato ogni qual volta viene selezionata un elemento dal menù delle opzioni.
     * Permette di gestirne le azioni.
     *
     * @param item L'elemento del menù che è stato selezionato.
     * @return true per indicare che l'evento è stato processato.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_confirm) {

            switch (intentType) {
                case IntentInfo.MOVEMENTS:
                    createNewMovement();
                    break;
                case IntentInfo.PROGRESSES:
                    createNewProgress();
                    break;
                case IntentInfo.EDIT_PROGRESS:
                    editProgress();
                    break;
            }

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Metodo che si occupa della creazione di un nuovo movimento.
     * In particolare:
     *      1. Verifica che la selezione dell'utente sia valida.
     *      2. Crea un nuovo movimento, inserendolo nel database dell'applicazione.
     *      3. Aggiorna il saldo disponibile all'interno del database.
     *      4. Incrementa il limite di spesa o l'obiettivo eventualmente associato al movimento e ne verifica il raggiungimento.
     */
    private void createNewMovement() {
        String type = (String) newMovementType.getSelectedItem(), category = (String) newMovementCategory.getSelectedItem(),
                targetName = (String) this.targetName.getSelectedItem(), stringValue = newEntryValue.getText().toString();
        int typeID = (int) newMovementType.getSelectedItemId(), categoryID = (int) newMovementCategory.getSelectedItemId(),
                targetNameID = (int) this.targetName.getSelectedItemId();
        float value = -1;

        if (!stringValue.isEmpty()) value = Float.valueOf(stringValue);

        if (value == -1 || numpad.getState() != Numpad.VALID_VALUE) {
            warning.show(WarningAccess.INVALID_VALUE);
        } else if (typeID == 0) {
            warning.show(WarningAccess.MOVEMENT_TYPE_NOT_SELECTED);
        } else if ((typeID == 1 || typeID == 2) && categoryID == 0) {
            warning.show(WarningAccess.MOVEMENT_CATEGORY_NOT_SELECTED);
        } else if (typeID == 3 && targetNameID == 0) {
            warning.show( WarningAccess.TARGET_NOT_SELECTED);
        } else {
            if (type.equals(entryType.get(EntryTypeAccess.IN))) {
                databaseHandler.increaseBalance(value);
                databaseHandler.insert(new Movement(category, value));
                outputIntent.putExtra(IntentInfo.SCREEN, IntentInfo.MOVEMENTS);
                startActivity(outputIntent);
            } else if (type.equals(entryType.get(EntryTypeAccess.OUT))) {

                if (databaseHandler.getBalance() - value >= 0) {
                    databaseHandler.increaseBalance(-1 * value);
                    databaseHandler.insert(new Movement(category, -1 * value));
                    outputIntent.putExtra(IntentInfo.SCREEN, IntentInfo.MOVEMENTS);

                    if (databaseHandler.increaseProgress(category, value))
                        outputIntent.putExtra(IntentInfo.WARNING, IntentInfo.LIMIT_REACHED);

                    startActivity(outputIntent);
                } else {
                    warning.show( WarningAccess.NOT_ENOUGH_MONEY);
                }

            } else if (type.equals(entryType.get(EntryTypeAccess.TARGET))) {

                if (databaseHandler.getBalance() - value >= 0) {
                    databaseHandler.increaseBalance(-1 * value);
                    databaseHandler.insert(new Movement(targetName, -1 * value));
                    outputIntent.putExtra(IntentInfo.SCREEN, IntentInfo.MOVEMENTS);

                    if (databaseHandler.increaseProgress(targetName, value))
                        outputIntent.putExtra(IntentInfo.WARNING, IntentInfo.TARGET_REACHED);

                    startActivity(outputIntent);
                } else {
                    warning.show( WarningAccess.NOT_ENOUGH_MONEY);
                }

            }
        }
    }

    /**
     * Metodo che si occupa della creazione di un nuovo progresso.
     * In particolare:
     *      1. Verifica che la selezione dell'utente sia valida.
     *      2. Crea un nuovo progresso, inserendolo nel database dell'applicazione. Se questo è un limite, rimuove la sua categoria dalla lista delle categorie monitorabili
     *         (si veda la classe AvailableLimitCategories).
     */
    private void createNewProgress() {
        String type = (String) newProgressType.getSelectedItem(), category = (String) newLimitCategory.getSelectedItem(),
                targetName = newTargetName.getText().toString(), stringMax = newEntryValue.getText().toString();
        int typeID = (int) newProgressType.getSelectedItemId(), categoryID = (int) newLimitCategory.getSelectedItemId();
        float max = -1;

        if (!stringMax.isEmpty()) max = Float.valueOf(stringMax);

        if (max == -1 || numpad.getState() != Numpad.VALID_VALUE) {
            warning.show(WarningAccess.INVALID_VALUE);
        } else if (typeID == 0) {
            warning.show(WarningAccess.PROGRESS_TYPE_NOT_SELECTED);
        } else if (typeID == 1 && categoryID == 0) {
            warning.show(WarningAccess.LIMIT_CATEGORY_NOT_SELECTED);
        } else if (typeID == 2 && targetName.isEmpty()) {
            warning.show(WarningAccess.EMPTY_NAME_TARGET);
        } else if (typeID == 2 && databaseHandler.getTargetNames().contains(targetName)) {
            warning.show(WarningAccess.DUPLICATE_TARGET);
        } else {

            if (type.equals(entryType.get(EntryTypeAccess.LIMIT))) {
                AvailableLimitCategories.remove(category);
                databaseHandler.insert(new Limit(category, max));

            } else if (type.equals(entryType.get(EntryTypeAccess.TARGET))) {
                databaseHandler.insert(new Target(targetName, max));
            }

            outputIntent.putExtra(IntentInfo.SCREEN, IntentInfo.PROGRESSES);
            startActivity(outputIntent);
        }

    }

    /**
     * Metodo che si occupa della modifica di un nuovo progresso. Ne viene preservato il valore attuale,
     * mentre il nuovo massimo è quello impostato dall'utente.
     * In particolare:
     *      1. Verifica che la selezione dell'utente sia valida.
     *      2. Aggiorna il progresso all'interno del database.
     */
    private void editProgress() {
        String description = getIntent().getStringExtra(IntentInfo.TEXT);
        float max = Float.valueOf(newEntryValue.getText().toString());

        if (numpad.getState() == Numpad.VALID_VALUE) {
            databaseHandler.updateProgress(description, max);
            outputIntent.putExtra(IntentInfo.SCREEN, IntentInfo.PROGRESSES);
            startActivity(outputIntent);
        } else {
            warning.show(WarningAccess.INVALID_VALUE);
        }

    }

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

}

