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
import android.widget.EditText;
import android.widget.Spinner;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.activities.NewEntryActivity;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.data.AvailableLimitCategories;

/**
 * Classe che rappresenta la porzione di interfaccia caratteristica dell'aggiunta di un nuovo progresso.
 * Trattandosi di un Fragment, la sua esistenza è strettamente legata a quella dell'Activity che la contiene, in questo caso NewEntryActivity.
 *
 * @author Marco Michelini
 */
public class NewProgressFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    /**
     * Interfaccia che permette lo scambio di informazioni tra il Fragment e l'Activity che la contiene.
     * Utilizzata per passare a NewEntryActivity i tre riferimenti a newProgressType, newLimitCategory e newTargetName.
     */
    public interface DataBridge {
        void newProgressFragmentCreated(Spinner[] newProgressSpinners, EditText newTargetName);
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
     * Spinner contenente i possibili tipi di progresso selezionabili dall'utente.
     */
    private Spinner newProgressType;

    /**
     * Spinner contenente le possibili categorie di progresso selezionabili dall'utente (e dunque monitorabili), prese
     * dalla classe static AvailableLimitCategories.
     * Viene mostrato solamente se l'utente intende creare un nuovo limite di spesa.
     */
    private Spinner newLimitCategory;

    /**
     * Campo di testo editabile, per l'inserimento (da parte dell'utente) del nome associato al nuovo obiettivo.
     * Viene mostrato solamente se l'utente intende creare un nuovo obiettivo.
     */
    private EditText newTargetName;

    /**
     * Riferimento all'activity che racchiude il Fragment.
     */
    private NewEntryActivity parentActivity;

    /**
     * Costruttore di default del Fragment.
     * Deve obbligatoriamente essere esplicitato, anche se vuoto (come in questo caso).
     */
    public NewProgressFragment() {}

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
     * In questo caso, vengono aggiunti gli Spinner newProgressType, newLimitCategory e l'EditText newTargetName.
     * Passa i relativi riferimenti a NewEntryActivity.
     *
     * @param inflater Oggetto che converte un layout nell'oggetto Java corrispondente.
     * @param container La vista alla quale è associata l'UI del Fragment.
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato del Fragment.
     */
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View newProgressView = inflater.inflate(R.layout.fragment_new_progress, container, false);

        initFragment(newProgressView);
        dataBridge.newProgressFragmentCreated(new Spinner[] {newProgressType, newLimitCategory}, newTargetName);

        return newProgressView;
    }

    /**
     * Metodo che inizializza l'UI del Fragment, aggiungendo i necessari elementi alla vista fornita.
     * Se non è possibile recuperare un riferimento all'Activity che contiene il Fragment, questa viene distrutta e poi ricreata,
     * assieme al Fragment stesso.
     *
     * @param newProgressView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initFragment(View newProgressView) {
        parentActivity = (NewEntryActivity) getActivity();

        if (parentActivity == null) {
            Intent reloadIntent = new Intent(context, NewEntryActivity.class);
            startActivity(reloadIntent);
        } else {
            initTypeSpinner(newProgressView);
            initCategorySpinner(newProgressView);
            initTargetNameField(newProgressView);
        }
    }

    /**
     * Inizializza newProgressType.
     *
     * @param newProgressView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initTypeSpinner(View newProgressView) {
        EntryTypeAccess entryType = parentActivity.getEntryTypeAccess();
        newProgressType = newProgressView.findViewById(R.id.spinner_new_progress_type);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, entryType.get(EntryTypeAccess.NEW_PROGRESS));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newProgressType.setAdapter(typeAdapter);
        newProgressType.setOnItemSelectedListener(this);
    }

    /**
     * Inizializza newLimitCategory.
     *
     * @param newProgressView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initCategorySpinner(View newProgressView) {
        newLimitCategory = newProgressView.findViewById(R.id.spinner_new_limit_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, AvailableLimitCategories.get());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newLimitCategory.setAdapter(categoryAdapter);
        newLimitCategory.setVisibility(View.INVISIBLE);
        newLimitCategory.setOnItemSelectedListener(this);
    }

    /**
     * Inizializza newTargetName.
     *
     * @param newProgressView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initTargetNameField(View newProgressView) {
        newTargetName = newProgressView.findViewById(R.id.new_target_name);
        newTargetName.setVisibility(View.INVISIBLE);
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
        if (parent.getId() == R.id.spinner_new_progress_type) selectNewProgressType((int) id);
    }

    /**
     * Mostra/nasconde gli elementi del Fragment a seconda della selezione dell'utente.
     * Questo permette di mostrare solo le informazioni pertinenti a tale selezione.
     *
     * @param itemID L'indice di riga associato all'elemento selezionato.
     */
    private void selectNewProgressType(int itemID) {
        switch (itemID) {
            case 0:
                newLimitCategory.setVisibility(View.INVISIBLE);
                newTargetName.setVisibility(View.INVISIBLE);
                break;
            case 1:
                newLimitCategory.setVisibility(View.VISIBLE);
                newLimitCategory.setSelection(0);
                newTargetName.setVisibility(View.INVISIBLE);
                break;
            case 2:
                newLimitCategory.setVisibility(View.INVISIBLE);
                newTargetName.setVisibility(View.VISIBLE);
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
