package com.lonedev.smartwallet.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.activities.NewEntryActivity;
import com.lonedev.smartwallet.static_data.EntryCategoryAccess;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.data.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta la porzione di interfaccia caratteristica dell'aggiunta di un nuovo movimento.
 * Trattandosi di un Fragment, la sua esistenza è strettamente legata a quella dell'Activity che la contiene, in questo caso NewEntryActivity.
 *
 * @author Marco Michelini
 */
public class NewMovementFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    /**
     * Interfaccia che permette lo scambio di informazioni tra il Fragment e l'Activity che la contiene.
     * Utilizzata per passare a NewEntryActivity i tre riferimenti a newMovementType, newMovementCategory e targetName.
     */
    public interface DataBridge {
        void newMovementFragmentCreated(Spinner[] newMovementSpinners);
    }

    /**
     * Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    private Context context;

    /**
     * Istanza di DataBridge, usata per chiamarne l'unico metodo e quindi passare informazioni a NewEntryActivity.
     */
    private DataBridge dataBridge;

    /**
     * Attributo che permette di accedere al database dell'app (sia in scrittura che in lettura).
     */
    private DatabaseHandler databaseHandler;

    /**
     * Spinner contenente i possibili tipi di movimento selezionabili dall'utente.
     */
    private Spinner newMovementType;

    /**
     * Spinner contenente le possibili categorie di movimento selezionabili dall'utente.
     */
    private Spinner newMovementCategory;


    /**
     * Spinner contenente i nomi degli obiettivi attualmente impostati dall'utente, permettendone l'incremento.
     * Viene mostrato all'utente solo se questo ha impostato degli obiettivi.
     */
    private Spinner targetName;

    /**
     * Attributo che permette di accedere all'array statico contenente i possibili tipi di Entry.
     * Questi sono mostrati all'interno di uno Spinner, permettendo all'utente di selezionare il tipo di movimenti da visualizzare.
     */
    private EntryTypeAccess entryType;

    /**
     * Adapter facente da collegamento tra l'array contenente le possibili categorie (in entrata) selezionabili
     * dall'utente e newMovementCategory.
     */
    private ArrayAdapter<String> categoryInAdapter;

    /**
     * Adapter facente da collegamento tra l'array contenente le possibili categorie (in uscita) selezionabili
     * dall'utente e newMovementCategory.
     */
    private ArrayAdapter<String> categoryOutAdapter;

    /**
     * Lista dei nomi di tutti gli obiettivi attualmente impostati dall'utente, sarà poi caricata in targetName.
     */
    private List<String> targets;

    /**
     * Riferimento all'activity che racchiude il Fragment.
     */
    private NewEntryActivity parentActivity;

    /**
     * Costruttore di default del Fragment.
     * Deve obbligatoriamente essere esplicitato, anche se vuoto (come in questo caso).
     */
    public NewMovementFragment() {}

    /**
     * Metodo chiamato quando il Fragment è associato alla sua Activity.
     *
     * @param context Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataBridge = (DataBridge) context;
        this.context = context;
    }

    /**
     * Crea e ritorna l'interfaccia del Fragment, aggiungendo le viste necessarie al suo layout (estensione .xml).
     * In questo caso, vengono aggiunti gli Spinner newMovementType, newMovementCategory e targetName.
     * Passa i relativi riferimenti a NewEntryActivity.
     *
     * @param inflater Oggetto che converte un layout nell'oggetto Java corrispondente.
     * @param container La vista alla quale è associata l'UI del Fragment.
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato del Fragment.
     */
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View newMovementView = inflater.inflate(R.layout.fragment_new_movement, container, false);

        initFragment(newMovementView);
        dataBridge.newMovementFragmentCreated(new Spinner[] {newMovementType, newMovementCategory, targetName});

        return newMovementView;
    }

    /**
     * Metodo chiamato quando la vista ritornata da onCreateView(LayoutInflater, ViewGroup, Bundle) è scollegata dal Fragment.
     * Generalmente usato per chiudere o liberare le risorse non più necessarie.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseHandler.close();
    }

    /**
     * Metodo che inizializza l'UI del Fragment, aggiungendo i necessari elementi alla vista fornita.
     * Se non è possibile recuperare un riferimento all'Activity che contiene il Fragment, questa viene distrutta e poi ricreata,
     * assieme al Fragment stesso.
     *
     * @param newMovementView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initFragment(View newMovementView) {
        parentActivity = (NewEntryActivity) getActivity();

        if (parentActivity == null) {
            Intent reloadIntent = new Intent(context, NewEntryActivity.class);
            startActivity(reloadIntent);
        } else {
            databaseHandler = new DatabaseHandler(context);

            initTypeSpinner(newMovementView);
            initCategorySpinner(newMovementView);
            initTargetSpinner(newMovementView);
        }

    }

    /**
     * Inizializza newMovementType.
     *
     * @param newMovementView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initTypeSpinner(View newMovementView) {
        entryType = parentActivity.getEntryTypeAccess();
        newMovementType = newMovementView.findViewById(R.id.spinner_new_movement_type);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, selectTypeSpinnerItems());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newMovementType.setAdapter(typeAdapter);
        newMovementType.setOnItemSelectedListener(this);
    }

    /**
     * Ritorna l'array da visualizzare all'interno di newMovementType.
     * Se l'utente ha impostato degli obiettivi, viene aggiunta la relativa voce all'interno dello Spinner.
     */
    private String[] selectTypeSpinnerItems() {
        targets = new ArrayList<>();
        targets.add(entryType.get(EntryTypeAccess.SELECT_TARGET));
        targets.addAll(databaseHandler.getTargetNames());

        if (targets.size() == 1) return entryType.get(EntryTypeAccess.NEW_MOVEMENT_NO_TARGETS);
        else return entryType.get(EntryTypeAccess.NEW_MOVEMENT);
    }

    /**
     * Inizializza newMovementCategory.
     *
     * @param newMovementView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initCategorySpinner(View newMovementView) {
        EntryCategoryAccess entryCategory = parentActivity.getEntryCategoryAccess();
        newMovementCategory = newMovementView.findViewById(R.id.spinner_new_movement_category);
        newMovementCategory.setVisibility(View.INVISIBLE);
        categoryInAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, entryCategory.get(EntryCategoryAccess.NEW_MOVEMENT_IN));
        categoryOutAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, entryCategory.get(EntryCategoryAccess.NEW_MOVEMENT_OUT));
        categoryInAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryOutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newMovementCategory.setOnItemSelectedListener(this);
    }

    /**
     * Inizializza targetName.
     *
     * @param newMovementView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initTargetSpinner(View newMovementView) {
        targetName = newMovementView.findViewById(R.id.spinner_target_name);
        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, targets.toArray(new String[0]));
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetName.setAdapter(targetAdapter);
        targetName.setVisibility(View.INVISIBLE);
        targetName.setOnItemSelectedListener(this);
    }

    /**
     * Metodo chiamato ogni qual volta l'utente seleziona un elemento di uno degli Spinner del Fragment.
     *
     * @param parent Lo Spinner corrispondente all'elemento selezionato.
     * @param view La vista all'interno dello Spinner che è stata cliccata.
     * @param position La posizione della vista selezionata all'interno dello Spinner.
     * @param id L'indice di riga associato all'elemento selezionato.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_new_movement_type) selectNewMovementType((int) id);
    }

    /**
     * Mostra/nasconde gli Spinner del Fragment a seconda della selezione dell'utente.
     * Questo permette di mostrare solo le informazioni pertinenti a tale selezione.
     *
     * @param itemID L'indice di riga associato all'elemento selezionato.
     */
    private void selectNewMovementType(int itemID) {
        switch (itemID) {
            case 0:
                newMovementCategory.setVisibility(View.INVISIBLE);
                targetName.setVisibility(View.INVISIBLE);
                break;
            case 1:
                targetName.setVisibility(View.INVISIBLE);
                newMovementCategory.setAdapter(categoryInAdapter);
                newMovementCategory.setSelection(0);
                newMovementCategory.setVisibility(View.VISIBLE);
                break;
            case 2:
                targetName.setVisibility(View.INVISIBLE);
                newMovementCategory.setAdapter(categoryOutAdapter);
                newMovementCategory.setSelection(0);
                newMovementCategory.setVisibility(View.VISIBLE);
                break;
            case 3:
                newMovementCategory.setVisibility(View.INVISIBLE);
                targetName.setSelection(0);
                targetName.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Metodo chiamato quando un elemento di uno Spinner, precedentemente selezionato, viene deselezionato.
     *
     * @param parent Lo Spinner corrispondente all'elemento selezionato.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}
