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
import android.widget.Button;
import android.widget.LinearLayout;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.UI_components.CursorAdapter;
import com.lonedev.smartwallet.activities.MainActivity;
import com.lonedev.smartwallet.activities.NewEntryActivity;
import com.lonedev.smartwallet.static_data.CursorType;
import com.lonedev.smartwallet.static_data.IntentInfo;
import com.lonedev.smartwallet.data.DatabaseHandler;

/**
 * Classe che rappresenta la schermata Progressi dell'applicazione.
 * Trattandosi di un Fragment, la sua esistenza è strettamente legata a quella dell'Activity che la contiene, in questo caso MainActivity.
 *
 * @author Marco Michelini
 */
public class ProgressesFragment extends Fragment implements View.OnClickListener {

    /**
     * Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    private Context context;

    /**
     * Attributo che permette di accedere al database dell'app (sia in scrittura che in lettura).
     */
    private DatabaseHandler databaseHandler;

    /**
     * Cursore contenente esclusivamente limiti di spesa.
     */
    private Cursor limitsCursor;

    /**
     * Cursore contenente esclusivamente obiettivi.
     */
    private Cursor targetsCursor;

    /**
     * Elemento dell'UI in cui vengono visualizzati i limiti di spesa. Racchiude la RecyclerView corrispondente.
     */
    private LinearLayout limitsLayout;

    /**
     * Elemento dell'UI in cui vengono visualizzati gli obiettivi. Racchiude la RecyclerView corrispondente.
     */
    private LinearLayout targetsLayout;

    /**
     * Elemento dell'UI in cui vengono visualizzati i limiti di spesa.
     */
    private RecyclerView limitsRecycler;

    /**
     * Elemento dell'UI in cui vengono visualizzati gli obiettivi.
     */
    private RecyclerView targetsRecycler;

    /**
     * Riferimento all'activity che racchiude il Fragment.
     */
    private MainActivity parentActivity;

    /**
     * Costruttore di default del Fragment.
     * Deve obbligatoriamente essere esplicitato, anche se vuoto (come in questo caso).
     */
    public ProgressesFragment() {}

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
     * In questo caso, vengono aggiunti limiti di spesa e obiettivi.
     *
     * @param inflater Oggetto che converte un layout nell'oggetto Java corrispondente.
     * @param container La vista alla quale è associata l'UI del Fragment.
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato del Fragment.
     */
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View progressesView = inflater.inflate(R.layout.fragment_progresses, container, false);

        initFragment(progressesView);
        loadLimits(inflater, container);
        loadTargets(inflater, container);

        return progressesView;
    }

    /**
     * Metodo chiamato quando la vista ritornata da onCreateView(LayoutInflater, ViewGroup, Bundle) è scollegata dal Fragment.
     * Generalmente usato per chiudere o liberare le risorse non più necessarie.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        limitsCursor.close();
        targetsCursor.close();
        databaseHandler.close();
    }

    /**
     * Metodo che inizializza l'UI del Fragment, aggiungendo i necessari elementi alla vista fornita.
     * Se non è possibile recuperare un riferimento all'Activity che contiene il Fragment, questa viene distrutta e poi ricreata,
     * assieme al Fragment stesso.
     *
     * @param progressesView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initFragment(View progressesView) {
        parentActivity = (MainActivity) getActivity();

        if (parentActivity == null) {
            Intent reloadIntent = new Intent(context, MainActivity.class);
            reloadIntent.putExtra(IntentInfo.SCREEN, IntentInfo.PROGRESSES);
            startActivity(reloadIntent);
        } else {
            Button newProgressButton = progressesView.findViewById(R.id.button_new_progress);
            databaseHandler = new DatabaseHandler(context);
            limitsLayout = progressesView.findViewById(R.id.layout_limits);
            targetsLayout = progressesView.findViewById(R.id.layout_targets);
            limitsRecycler = progressesView.findViewById(R.id.recycler_limits);
            targetsRecycler = progressesView.findViewById(R.id.recycler_targets);
            newProgressButton.setOnClickListener(this);
        }

    }

    /**
     * Carica i limiti di spesa dal database all'interno del relativo LinearLayout.
     */
    private void loadLimits(@NonNull LayoutInflater inflater, ViewGroup container) {
        limitsCursor = databaseHandler.getCursor(CursorType.LIMITS);

        if (limitsCursor != null) {

            if (limitsCursor.getCount() == 0) {
                limitsLayout.addView(inflater.inflate(R.layout.no_limits, container, false));
            } else {
                CursorAdapter limitAdapter = new CursorAdapter(databaseHandler, limitsCursor, parentActivity);
                limitsRecycler.setAdapter(limitAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                limitsRecycler.setLayoutManager(layoutManager);
            }

        }
    }

    /**
     * Carica gli obiettivi dal database all'interno del relativo LinearLayout.
     */
    private void loadTargets(@NonNull LayoutInflater inflater, ViewGroup container) {
        targetsCursor = databaseHandler.getCursor(CursorType.TARGETS);

        if (targetsCursor != null) {

            if (targetsCursor.getCount() == 0) {
                targetsLayout.addView(inflater.inflate(R.layout.no_targets, container, false));
            } else {
                CursorAdapter targetAdapter = new CursorAdapter(databaseHandler, targetsCursor, parentActivity);
                targetsRecycler.setAdapter(targetAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                targetsRecycler.setLayoutManager(layoutManager);
            }

        }
    }

    /**
     * Metodo chiamato alla pressione del pulsante di aggiunta di un nuovo progresso.
     * Crea e visualizza una nuova istanza di NewEntryActivity.
     *
     * @param v Il pulsante di aggiunta di un nuovo progresso.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, NewEntryActivity.class);
        intent.putExtra(IntentInfo.SCREEN, IntentInfo.PROGRESSES);
        startActivity(intent);
    }

}
