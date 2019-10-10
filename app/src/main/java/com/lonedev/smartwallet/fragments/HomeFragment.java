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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.UI_components.CursorAdapter;
import com.lonedev.smartwallet.activities.MainActivity;
import com.lonedev.smartwallet.static_data.CursorType;
import com.lonedev.smartwallet.static_data.IntentInfo;
import com.lonedev.smartwallet.support.DataFormat;
import com.lonedev.smartwallet.data.DatabaseHandler;

/**
 * Classe che rappresenta la schermata Home dell'applicazione.
 * Trattandosi di un Fragment, la sua esistenza è strettamente legata a quella dell'Activity che la contiene, in questo caso MainActivity.
 *
 * @author Marco Michelini
 */
public class HomeFragment extends Fragment {

    /**
     * Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    private Context context;

    /**
     * Attributo che permette di accedere al database dell'app (sia in scrittura che in lettura).
     */
    private DatabaseHandler databaseHandler;

    /**
     * Cursore contenente esclusivamente i limiti di spesa rilevanti, vale a dire quelli più
     * vicini ad essere completati.
     */
    private Cursor limitsCursor;

    /**
     * Cursore contenente esclusivamente gli obiettivi rilevanti, vale a dire quelli più
     * vicini ad essere completati.
     */
    private Cursor targetsCursor;

    /**
     * Vista usata per visualizzare il saldo attualmente disponibile.
     */
    private TextView balanceView;

    /**
     * Elemento dell'UI in cui vengono visualizzati i limiti di spesa rilevanti. Racchiude la RecyclerView corrispondente.
     */
    private LinearLayout relevantLimitsLayout;

    /**
     * Elemento dell'UI in cui vengono visualizzati gli obiettivi rilevanti. Racchiude la RecyclerView corrispondente.
     */
    private LinearLayout relevantTargetsLayout;

    /**
     * Elemento dell'UI in cui vengono visualizzati i limiti di spesa rilevanti.
     */
    private RecyclerView relevantLimitsRecycler;

    /**
     * Elemento dell'UI in cui vengono visualizzati gli obiettivi rilevanti.
     */
    private RecyclerView relevantTargetsRecycler;

    /**
     * Riferimento all'activity che racchiude il Fragment.
     */
    private MainActivity parentActivity;

    /**
     * Costruttore di default del Fragment.
     * Deve obbligatoriamente essere esplicitato, anche se vuoto (come in questo caso).
     */
    public HomeFragment() {}

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
     * In questo caso, vengono aggiunti il saldo disponibile e i progressi rilevanti.
     *
     * @param inflater Oggetto che converte un layout nell'oggetto Java corrispondente.
     * @param container La vista alla quale è associata l'UI del Fragment.
     * @param savedInstanceState Variabile utilizzata per salvare/caricare lo stato del Fragment.
     */
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);

        initFragment(homeView);
        loadBalance();
        loadRelevantLimits(inflater, container);
        loadRelevantTargets(inflater, container);

        return homeView;
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
     * @param homeView La vista creata da onCreateView(LayoutInflater, ViewGroup, Bundle).
     */
    private void initFragment(View homeView) {
        parentActivity = (MainActivity) getActivity();

        if (parentActivity == null) {
            Intent reloadIntent = new Intent(context, MainActivity.class);
            reloadIntent.putExtra(IntentInfo.SCREEN, IntentInfo.HOME);
            startActivity(reloadIntent);
        } else {
            databaseHandler = new DatabaseHandler(getActivity());
            balanceView = homeView.findViewById(R.id.balance);
            relevantLimitsLayout = homeView.findViewById(R.id.layout_relevant_limits);
            relevantTargetsLayout = homeView.findViewById(R.id.layout_relevant_targets);
            relevantLimitsRecycler = homeView.findViewById(R.id.recycler_relevant_limits);
            relevantTargetsRecycler = homeView.findViewById(R.id.recycler_relevant_targets);
        }

    }

    /**
     * Carica il saldo attualmente disponibile dal database e imposta il relativo TextView.
     */
    private void loadBalance() {
        String balance = DataFormat.format(databaseHandler.getBalance()) + " " + getResources().getString(R.string.currency);
        balanceView.setText(balance);
    }

    /**
     * Carica i limiti di spesa rilevanti dal database all'interno del relativo LinearLayout.
     */
    private void loadRelevantLimits(@NonNull LayoutInflater inflater, ViewGroup container) {
        limitsCursor = databaseHandler.getCursor(CursorType.LIMITS);

        if (limitsCursor != null) {

            if (limitsCursor.getCount() == 0) {
                relevantLimitsLayout.addView(inflater.inflate(R.layout.no_limits, container, false));
            } else {
                CursorAdapter limitAdapter = new CursorAdapter(databaseHandler, limitsCursor, parentActivity, true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                relevantLimitsRecycler.setAdapter(limitAdapter);
                relevantLimitsRecycler.setLayoutManager(layoutManager);
            }

        }
    }

    /**
     * Carica gli obiettivi rilevanti dal database all'interno del relativo LinearLayout.
     */
    private void loadRelevantTargets(@NonNull LayoutInflater inflater, ViewGroup container) {
        targetsCursor = databaseHandler.getCursor(CursorType.TARGETS);

        if (targetsCursor != null) {

            if (targetsCursor.getCount() == 0) {
                relevantTargetsLayout.addView(inflater.inflate(R.layout.no_targets, container, false));
            } else {
                CursorAdapter targetAdapter = new CursorAdapter(databaseHandler, targetsCursor, parentActivity, true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                relevantTargetsRecycler.setAdapter(targetAdapter);
                relevantTargetsRecycler.setLayoutManager(layoutManager);
            }

        }
    }

}