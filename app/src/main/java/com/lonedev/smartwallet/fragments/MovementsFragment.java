package com.lonedev.smartwallet.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.UI_components.CursorAdapter;
import com.lonedev.smartwallet.activities.MainActivity;
import com.lonedev.smartwallet.activities.NewEntryActivity;
import com.lonedev.smartwallet.static_data.CursorType;
import com.lonedev.smartwallet.static_data.EntryCategoryAccess;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.static_data.IntentInfo;
import com.lonedev.smartwallet.static_data.MovementPeriodAccess;
import com.lonedev.smartwallet.data.DatabaseHandler;
import com.lonedev.smartwallet.support.MovementsFilter;

/**
 * Classe che rappresenta la schermata Movimenti dell'applicazione.
 * Trattandosi di un Fragment, la sua esistenza è strettamente legata a quella dell'Activity che la contiene, in questo caso MainActivity.
 *
 * @author Marco Michelini
 */
public class MovementsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    /**
     * Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    private Context context;

    /**
     * Cursore contenente esclusivamente movimenti. Di default, sono caricati tutti i movimenti relativi
     * alla settimana corrente.
     */
    private Cursor movementsCursor;

    /**
     * Attributo che permette di accedere al database dell'app (sia in scrittura che in lettura).
     */
    private DatabaseHandler databaseHandler;

    /**
     * Elemento dell'UI in cui vengono visualizzati i movimenti. Racchiude la RecyclerView corrispondente.
     */
    private LinearLayout movementsLayout;

    /**
     * Elemento dell'UI in cui vengono visualizzati i movimenti.
     */
    private RecyclerView movementsRecycler;

    /**
     * Attributo che permette di accedere all'array statico contenente i possibili tipi di Entry.
     * Questi sono mostrati all'interno di uno Spinner, permettendo all'utente di selezionare il tipo di movimenti da visualizzare.
     */
    private EntryTypeAccess entryType;

    /**
     * Attributo che permette di accedere all'array statico contenente le possibili categorie di Entry.
     * Questi sono mostrati all'interno di uno Spinner, permettendo all'utente di selezionare la categoria di movimenti da visualizzare.
     */
    private EntryCategoryAccess entryCategory;

    /**
     * Attributo che permette di accedere all'array statico contenente i possibili periodi temporali di un movimenti da visualizzare.
     * Questi sono mostrati all'interno di uno Spinner, permettendo all'utente di selezionare il periodo di movimenti da visualizzare.
     */
    private MovementPeriodAccess movementPeriod;

    /**
     * Riferimento all'activity che racchiude il Fragment.
     */
    private MainActivity parentActivity;

    /**
     * Costruttore di default del Fragment.
     * Deve obbligatoriamente essere esplicitato, anche se vuoto (come in questo caso).
     */
    public MovementsFragment() {}

    /**
     * Metodo chiamato quando il Fragment è associato alla sua Activity.
     *
     * @param context Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Crea e ritorna l'interfaccia del Fragment, aggiungendo le viste necessarie al suo layout (estensione .xml).
     * In questo caso, vengono aggiunti i movimenti relativi ai filtri selezionati.
     *
     * @param inflater Oggetto che converte un layout nell'oggetto Java corrispondente.
     * @param container La vista alla quale è associata l'UI del Fragment.
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato del Fragment.
     */
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View movementsView = inflater.inflate(R.layout.fragment_movements, container, false);

        initFragment(movementsView);
        loadMovements(inflater, container);

        return movementsView;
    }

    /**
     * Metodo chiamato quando la vista ritornata da onCreateView(LayoutInflater, ViewGroup, Bundle) è scollegata dal Fragment.
     * Generalmente usato per chiudere o liberare le risorse non più necessarie.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        movementsCursor.close();
        databaseHandler.close();
    }

    /**
     * Metodo che inizializza l'UI del Fragment, aggiungendo i necessari elementi alla vista fornita.
     * In particolare, viene inizializzato il Button che permette all'utente di aggiungere un nuovo movimento.
     * Se non è possibile recuperare un riferimento all'Activity che contiene il Fragment, questa viene distrutta e poi ricreata,
     * assieme al Fragment stesso.
     *
     * @param movementsView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initFragment(View movementsView) {
        parentActivity = (MainActivity) getActivity();

        if (parentActivity == null) {
            Intent reloadIntent = new Intent(context, MainActivity.class);
            reloadIntent.putExtra(IntentInfo.SCREEN, IntentInfo.MOVEMENTS);
            startActivity(reloadIntent);
        } else {
            Button newMovementButton = movementsView.findViewById(R.id.button_new_movement);
            databaseHandler = new DatabaseHandler(context);

            initTypeSpinner(movementsView);
            initCategorySpinner(movementsView);
            initPeriodSpinner(movementsView);

            movementsLayout = movementsView.findViewById(R.id.layout_movements);
            movementsRecycler = movementsView.findViewById(R.id.recycler_movements);

            newMovementButton.setOnClickListener(this);
        }

    }

    /**
     * Inizializza lo Spinner contenente i possibili tipi di movimento selezionabili dall'utente.
     * Permette di filtrare i movimenti visualizzati.
     *
     * @param movementsView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initTypeSpinner(View movementsView) {
        entryType = parentActivity.getEntryTypeAccess();
        Spinner filterType = movementsView.findViewById(R.id.spinner_type_filter);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, entryType.get(EntryTypeAccess.FILTER));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterType.setAdapter(typeAdapter);
        filterType.setSelection(MovementsFilter.getTypePosition());
        filterType.setOnItemSelectedListener(this);
    }

    /**
     * Inizializza lo Spinner contenente le possibili categorie di movimento selezionabili dall'utente.
     * Permette di filtrare i movimenti visualizzati.
     *
     * @param movementsView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initCategorySpinner(View movementsView) {
        entryCategory = parentActivity.getEntryCategoryAccess();
        Spinner filterCategory = movementsView.findViewById(R.id.spinner_category_filter);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, selectCategorySpinnerItems());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterCategory.setAdapter(categoryAdapter);
        filterCategory.setSelection(MovementsFilter.getCategoryPosition());
        filterCategory.setOnItemSelectedListener(this);
    }

    /**
     * Inizializza lo Spinner contenente i possibili periodi di movimento selezionabili dall'utente.
     * Permette di filtrare i movimenti visualizzati.
     *
     * @param movementsView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initPeriodSpinner(View movementsView) {
        movementPeriod = parentActivity.getMovementPeriodAccess();
        Spinner filterPeriod = movementsView.findViewById(R.id.spinner_period_filter);
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, movementPeriod.getAll());
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterPeriod.setAdapter(periodAdapter);
        filterPeriod.setSelection(MovementsFilter.getPeriodPosition());
        filterPeriod.setOnItemSelectedListener(this);
    }

    /**
     * Ritorna l'array delle possibili categorie di movimento selezionabili dall'utente, sulla base del tipo
     * attualmente selezionato.
     */
    private String[] selectCategorySpinnerItems() {
        if (MovementsFilter.getType().equals(entryType.get(EntryTypeAccess.INS)))
            return entryCategory.get(EntryCategoryAccess.FILTER_IN);
        else if (MovementsFilter.getType().equals(entryType.get(EntryTypeAccess.OUTS)))
            return entryCategory.get(EntryCategoryAccess.FILTER_OUT);
        else
            return entryCategory.get(EntryCategoryAccess.FILTER_ALL);
    }

    /**
     * Carica i movimenti (relativi ai filtri selezionati) dal database all'interno del relativo LinearLayout.
     *
     * @param inflater Oggetto che converte un layout nell'oggetto Java corrispondente.
     * @param container La vista alla quale è associata l'UI del Fragment.
     */
    private void loadMovements(LayoutInflater inflater, ViewGroup container) {
        movementsCursor = databaseHandler.getCursor(CursorType.MOVEMENTS);

        if (movementsCursor != null) {

            if (movementsCursor.getCount() == 0) {
                movementsLayout.addView(inflater.inflate(R.layout.no_movements, container, false));
            } else {
                CursorAdapter adapter = new CursorAdapter(databaseHandler, movementsCursor, parentActivity);
                movementsRecycler.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                movementsRecycler.setLayoutManager(layoutManager);
            }

        }
    }

    /**
     * Metodo chiamato ogni qual volta l'utente seleziona un elemento di uno degli Spinner del Fragment.
     * Ad ogni selezione, viene aggiornato il relativo attributo di MovementsFilter, classe static utilizzata
     * per filtrare i movimenti visualizzati.
     *
     * @param parent Lo Spinner corrispondente all'elemento selezionato.
     * @param view La vista all'interno dello Spinner che è stata cliccata.
     * @param position La posizione della vista selezionata all'interno dello Spinner.
     * @param id L'indice di riga associato all'elemento selezionato.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (movementsCursor != null) {

            switch (parent.getId()) {
                case R.id.spinner_type_filter:
                    selectFilterType((int) id);
                    break;
                case R.id.spinner_category_filter:
                    selectFilterCategory(parent, (int) id);
                    break;
                case R.id.spinner_period_filter:
                    selectFilterPeriod((int) id);
                    break;
            }

        }
    }

    /**
     * Aggiorna la classe static MovementsFilter (in particolare, ne aggiorna il tipo) sulla base del tipo di
     * movimento selezionato.
     * Inoltre, se tale tipo è diverso da quello selezionato in precedenza, crea e inizializza una nuova istanza del Fragment.
     *
     * @param itemID L'indice di riga associato all'elemento selezionato.
     */
    private void selectFilterType(int itemID) {
        String previousType = MovementsFilter.getType();

        switch (itemID) {
            case 1:
                MovementsFilter.setType(entryType.get(EntryTypeAccess.INS));
                break;
            case 2:
                MovementsFilter.setType(entryType.get(EntryTypeAccess.OUTS));
                break;
            default:
                MovementsFilter.setType(entryType.get(EntryTypeAccess.ALL));
        }

        if (!MovementsFilter.getType().equals(previousType))
            parentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                    new MovementsFragment()).commit();
    }

    /**
     * Aggiorna la classe static MovementsFilter (in particolare, ne aggiorna la categoria) sulla base della categoria di
     * movimento selezionata.
     * Inoltre, se tale categoria è diversa da quella selezionata in precedenza, crea e inizializza una nuova istanza del Fragment.
     *
     * @param parent Lo Spinner corrispondente all'elemento selezionato.
     * @param itemID L'indice di riga associato all'elemento selezionato.
     */
    private void selectFilterCategory(AdapterView<?> parent, int itemID) {
        String previousCategory = MovementsFilter.getCategory();

        if (itemID == 0)
            MovementsFilter.setCategory(entryCategory.get(EntryCategoryAccess.ALL));
        else
            MovementsFilter.setCategory((String) parent.getSelectedItem());

        if (!MovementsFilter.getCategory().equals(previousCategory))
            parentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                    new MovementsFragment()).commit();
    }

    /**
     * Aggiorna la classe static MovementsFilter (in particolare, ne aggiorna il periodo) sulla base del periodo di
     * movimento selezionato.
     * Inoltre, se tale periodo è diverso da quello selezionato in precedenza, crea e inizializza una nuova istanza del Fragment.
     *
     * @param itemID L'indice di riga associato all'elemento selezionato.
     */
    private void selectFilterPeriod(int itemID) {
        String previousPeriod = MovementsFilter.getPeriod();

        switch (itemID) {
            case 1:
                MovementsFilter.setPeriod(movementPeriod.get(MovementPeriodAccess.LAST_MONTH));
                break;
            case 2:
                MovementsFilter.setPeriod(movementPeriod.get(MovementPeriodAccess.LAST_THREE_MONTHS));
                break;
            case 3:
                MovementsFilter.setPeriod(movementPeriod.get(MovementPeriodAccess.LAST_YEAR));
                break;
            default:
                MovementsFilter.setPeriod(movementPeriod.get(MovementPeriodAccess.LAST_WEEK));
        }

        if (!MovementsFilter.getPeriod().equals(previousPeriod))
            parentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                    new MovementsFragment()).commit();
    }

    /**
     * Metodo chiamato quando un elemento di uno Spinner, precedentemente selezionato, viene deselezionato.
     *
     * @param parent Lo Spinner corrispondente all'elemento selezionato.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Metodo chiamato alla pressione del pulsante di aggiunta di un nuovo movimento.
     * Crea e visualizza una nuova istanza di NewEntryActivity.
     *
     * @param v Il pulsante di aggiunta di un nuovo movimento.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, NewEntryActivity.class);
        intent.putExtra(IntentInfo.SCREEN, IntentInfo.MOVEMENTS);
        startActivity(intent);
    }

}
