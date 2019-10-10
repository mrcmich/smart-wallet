package com.lonedev.smartwallet.UI_components;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lonedev.smartwallet.R;
import com.lonedev.smartwallet.activities.NewEntryActivity;
import com.lonedev.smartwallet.data.AvailableLimitCategories;
import com.lonedev.smartwallet.data.Entry;
import com.lonedev.smartwallet.data.Limit;
import com.lonedev.smartwallet.data.Movement;
import com.lonedev.smartwallet.data.Progress;
import com.lonedev.smartwallet.static_data.CursorType;
import com.lonedev.smartwallet.static_data.IntentInfo;
import com.lonedev.smartwallet.fragments.MovementsFragment;
import com.lonedev.smartwallet.fragments.ProgressesFragment;
import com.lonedev.smartwallet.support.DataFormat;
import com.lonedev.smartwallet.data.DatabaseHandler;

import java.util.Calendar;

/**
 * Adapter facente da collegamento tra i record inseriti dall'utente (memorizzati all'interno del database dell'app) e i relativi
 * RecyclerView nelle schermate Home, Movimenti e Progressi.
 *
 * @author Marco Michelini
 */
public class CursorAdapter extends RecyclerView.Adapter<CursorAdapter.ViewHolder> {

    /**
     * Il massimo numero di limiti da visualizzare nella schermata Home.
     */
    private final int MAX_RELEVANT_LIMITS = 5;

    /**
     * Il massimo numero di obiettivi da visualizzare nella schermata Home.
     */
    private final int MAX_RELEVANT_TARGETS = 3;

    /**
     * Attributo che permette di accedere al database dell'app (sia in scrittura che in lettura).
     */
    private DatabaseHandler databaseHandler;

    /**
     * Cursore contenente una lista di record salvati nel database.
     * Può essere di qualsiasi tipo, purché previsto dall'applicazione.
     */
    private Cursor cursor;

    /**
     * Il tipo del cursore passato all'interno del costruttore, influenza il comportamento di CursorAdapter (diversi tipi di record
     * hanno una diversa struttura, e sono memorizzati in modo diverso nel database dell'app).
     */
    private String cursorType;

    /**
     * Riferimento all'activity oontenente CursorAdapter, in modo diretto o indiretto (attraverso un Fragment).
     */
    private AppCompatActivity parentActivity;

    /**
     * Se il cursore passato corrisponde ad un progresso, ne determina il layout.
     * Progressi rilevanti e non rilevanti sono visualizzati diversamente e in schermate differenti (Home nel primo
     * caso, Progressi nel secondo).
     */
    private boolean showOnlyRelevant;

    /**
     * Se il cursore passato corrisponde ad un movimento, contiene la data del movimento precedentemente visualizzato.
     * Se tale stringa corrisponde alla data del movimento corrente, viene adottato un layout differente, tale da aggregare i due.
     */
    private String movementPreviousDate;

    /**
     * Inizializza un nuovo CursorAdapter con i parametri passati.
     * Se il cursore contiene dei progressi, questi sono implicitamente considerati come non rilevanti.
     *
     * @param databaseHandler Oggetto che fornisce accesso in scrittura e in lettura al database.
     * @param cursor Il cursore contenente i record da visualizzare.
     * @param parentActivity Riferimento all'activity oontenente CursorAdapter.
     */
    public CursorAdapter(DatabaseHandler databaseHandler, Cursor cursor, AppCompatActivity parentActivity) {
        this.databaseHandler = databaseHandler;
        this.cursor = cursor;
        cursorType = databaseHandler.getCursorType(cursor);
        this.parentActivity = parentActivity;
        showOnlyRelevant = false;
        movementPreviousDate = " ";
    }

    /**
     * Inizializza un nuovo CursorAdapter con i parametri passati.
     * Non essendoci distinzione fra movimenti rilevanti e non, assume che il cursore passato contenga dei progressi.
     *
     * @param databaseHandler Oggetto che fornisce accesso in scrittura e in lettura al database.
     * @param cursor Il cursore contenente i record da visualizzare.
     * @param parentActivity Riferimento all'activity oontenente CursorAdapter.
     * @param showOnlyRelevant Parametro che specifica se i progressi da visualizzare sono rilevanti o no.
     */
    public CursorAdapter(DatabaseHandler databaseHandler, Cursor cursor, AppCompatActivity parentActivity, boolean showOnlyRelevant) {
        this.databaseHandler = databaseHandler;
        this.cursor = cursor;
        cursorType = databaseHandler.getCursorType(cursor);
        this.parentActivity = parentActivity;
        movementPreviousDate = " ";

        if (cursorType.equals(CursorType.MOVEMENTS))
            this.showOnlyRelevant = false;
        else
            this.showOnlyRelevant = showOnlyRelevant;
    }

    /**
     * Metodo chiamato quando il RecyclerView associato a CursorAdapter richiede un nuovo ViewHolder, in cui visualizzare il
     * successivo record del cursore.
     * Il layout di tale ViewHolder varia a seconda del tipo di record (movimento o progresso) e, nel caso in cui il record in
     * questione sia un progresso, della sua rilevanza.
     *
     * @param viewGroup La vista in cui verrà inserito il nuovo ViewHolder dopo la sua creazione.
     * @param viewType Il tipo della nuova vista.
     * @return Il ViewHolder in cui inserire il record successivo.
     */
    @NonNull
    @Override
    public CursorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        int holderLayout;

        if (cursorType.equals(CursorType.MOVEMENTS)) {
            holderLayout = R.layout.movement;
        } else {

            if (showOnlyRelevant) holderLayout = R.layout.relevant_progress;
            else holderLayout = R.layout.progress;

        }

        return new CursorAdapter.ViewHolder(inflater.inflate(holderLayout, viewGroup, false));
    }

    /**
     * Inserisce all'interno del ViewHolder fornito le informazioni relative al record individuato dalla posizione fornita.
     *
     * @param viewHolder Il ViewHolder ritornato da onCreateViewHolder(ViewGroup, int).
     * @param position La posizione del nuovo record all'interno del RecyclerView associato a CursorAdapter e del cursore.
     */
    @Override
    public void onBindViewHolder(@NonNull CursorAdapter.ViewHolder viewHolder, int position) {
        View holder = viewHolder.holder;
        final Entry entry = databaseHandler.get(cursor, position);

        if (showOnlyRelevant) bindRelevantProgressData((Progress) entry, holder);
        else bindData(entry, holder);

    }

    /**
     * Inserisce le informazioni del progresso fornito all'interno della vista fornita.
     * In questo caso, il progresso è trattato come rilevante.
     *
     * @param progress Il progresso di cui si vogliono visualizzare le informazioni.
     * @param holder Il ViewHolder ritornato da onCreateViewHolder(ViewGroup, int), in cui inserire le informazioni del progresso.
     */
    private void bindRelevantProgressData(final Progress progress, View holder) {
        TextView textView;
        ProgressBar progressBar;
        float max = progress.getMax(), value = progress.getValue();
        int percent = progress.getPercent(), progressColor = selectProgressColor(percent);
        String valueText = DataFormat.format(value), maxText = "/ " + DataFormat.format(max), description = progress.getDescription();

        if (percent > 100) percent = 100;

        textView = holder.findViewById(R.id.description_relevant_progress);
        textView.setText(description);
        textView = holder.findViewById(R.id.value_relevant_progress);
        textView.setTextColor(progressColor);
        textView.setText(valueText);
        textView = holder.findViewById(R.id.max_relevant_progress);
        textView.setText(maxText);
        progressBar = holder.findViewById(R.id.bar_relevant_progress);
        progressBar.setProgressTintList(ColorStateList.valueOf(progressColor));
        progressBar.setProgress(percent);
    }

    /**
     * Ritorna il colore utilizzato per il valore del progresso, e se questo è rilevante anche per la sua barra di completamento.
     * Il colore scelto varia a seconda che il progresso sia un limite di spesa o un obiettivo, e della percentuale di completamento
     * fornita.
     *
     * @param percent La percentuale di completamento del progresso.
     */
    private int selectProgressColor(int percent) {
        int progressColor;

        if (percent < 100) {
            progressColor = parentActivity.getResources().getColor(R.color.colorPrimary, null);

            if (cursorType.equals(CursorType.TARGETS))
                progressColor = parentActivity.getResources().getColor(R.color.deleteButtonColor, null);

        } else {
            progressColor = parentActivity.getResources().getColor(R.color.deleteButtonColor, null);

            if (cursorType.equals(CursorType.TARGETS))
                progressColor = parentActivity.getResources().getColor(R.color.colorPrimary, null);

        }

        return progressColor;
    }

    /**
     * Inserisce all'interno del ViewHolder fornito le informazioni relative al record fornito.
     * In particolare, questo viene fatto chiamando il metodo più idoneo al tipo di record.
     * Se il record è un progresso, questo viene trattato come non rilevante.
     *
     * @param entry Il record di cui si vogliono visualizzare le informazioni.
     * @param holder Il ViewHolder ritornato da onCreateViewHolder(ViewGroup, int), in cui inserire le informazioni del record.
     */
    private void bindData(final Entry entry, View holder) {
        if (entry instanceof Movement) {
            bindMovementData((Movement) entry, holder);
        } else if (entry instanceof Progress){
            bindProgressData((Progress) entry, holder);
        }
    }

    /**
     * Inserisce all'interno del ViewHolder fornito le informazioni relative al movimento fornito.
     * Inizializza inoltre il pulsante di eliminazione di un movimento, che consente di rimuovere dal database ogni movimento
     * la cui data di creazione sia quella corrente (ciò consente all'utente di correggere eventuali errori o distrazioni).
     *
     * @param movement Il movimento di cui si vogliono visualizzare le informazioni.
     * @param holder Il ViewHolder ritornato da onCreateViewHolder(ViewGroup, int), in cui inserire le informazioni del movimento.
     */
    private void bindMovementData(final Movement movement, View holder) {
        TextView textView;
        Calendar currentDate = Calendar.getInstance();
        String valueText, description = movement.getDescription(), date = movement.getDate(),
                currentDateString = DataFormat.format(currentDate.get(Calendar.DAY_OF_MONTH),
                        currentDate.get(Calendar.MONTH) + 1, currentDate.get(Calendar.YEAR));
        ImageButton deleteButton = holder.findViewById(R.id.button_delete_movement);
        float value = movement.getValue();

        if (value < 0)
            valueText = "- " + DataFormat.format(Math.abs(value)) + " " + parentActivity.getResources().getString(R.string.currency);
        else
            valueText = "+ " + DataFormat.format(Math.abs(value)) + " " + parentActivity.getResources().getString(R.string.currency);

        textView = holder.findViewById(R.id.category_movement);
        textView.setText(description);
        textView = holder.findViewById(R.id.value_movement);
        textView.setText(valueText);
        textView = holder.findViewById(R.id.date_movement);

        if (!date.equals(movementPreviousDate)) {
            textView.setText(date);
            movementPreviousDate = date;
        } else {
            textView.setHeight(0);
        }

        deleteButton.setVisibility(View.INVISIBLE);

        if (currentDateString.equals(date)) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (movement.getValue() < 0)
                        databaseHandler.increaseProgress(movement.getDescription(), -1 * Math.abs(movement.getValue()));

                    databaseHandler.increaseBalance(-1 * movement.getValue());
                    databaseHandler.deleteMovement(movement.getID());
                    parentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                            new MovementsFragment()).commit();
                }
            });
        }
    }

    /**
     * Inserisce le informazioni del progresso fornito all'interno della vista fornita.
     * In questo caso, il progresso è trattato come non rilevante.
     * Inizializza inoltre i pulsanti di modifica ed eliminazione di un progresso, consentendo di rimuovere un progresso dal
     * database o di modificarne il limite massimo.
     *
     * @param progress Il progresso di cui si vogliono visualizzare le informazioni.
     * @param holder Il ViewHolder ritornato da onCreateViewHolder(ViewGroup, int), in cui inserire le informazioni del progresso.
     */
    private void bindProgressData(final Progress progress, View holder) {
        TextView textView;
        ImageButton editButton = holder.findViewById(R.id.button_edit_progress);
        ImageButton deleteButton = holder.findViewById(R.id.button_delete_progress);
        String description = progress.getDescription();
        float value = progress.getValue(), max = progress.getMax();
        int percent = progress.getPercent(), progressColor = selectProgressColor(percent);
        String valueText = DataFormat.format(value), maxText = "/ " + DataFormat.format(max);

        textView = holder.findViewById(R.id.description_progress);
        textView.setText(description);
        textView = holder.findViewById(R.id.value_progress);
        textView.setTextColor(progressColor);
        textView.setText(valueText);
        textView = holder.findViewById(R.id.max_progress);
        textView.setText(maxText);

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentActivity, NewEntryActivity.class);
                intent.putExtra(IntentInfo.SCREEN, IntentInfo.EDIT_PROGRESS);
                intent.putExtra(IntentInfo.TEXT, progress.getDescription());
                parentActivity.startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (progress instanceof Limit) {
                    AvailableLimitCategories.add(progress.getDescription());
                } else {

                    if (progress.getPercent() < 100) {
                        databaseHandler.deleteMovement(progress.getDescription());
                        databaseHandler.increaseBalance(progress.getValue());
                    }

                }

                databaseHandler.deleteProgress(progress.getDescription());
                parentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        new ProgressesFragment()).commit();

            }
        });
    }

    /**
     * Ritorna il numero di record contenuti nel cursore passato al costruttore.
     */
    @Override
    public int getItemCount() {
        if (showOnlyRelevant) {
            int refItems = MAX_RELEVANT_LIMITS;

            if (cursorType.equals(CursorType.TARGETS))
                refItems = MAX_RELEVANT_TARGETS;

            return Math.min(refItems, cursor.getCount());
        }

        return cursor.getCount();
    }

    /**
     * Classe interna che descrive la vista utilizzata dal RecyclerView associato a CursorAdapter per visualizzare
     * i record contenuti in cursor. Diversi tipi di record sono rappresentati all'interno di ViewHolder con un differente layout.
     *
     * @author Marco Michelini
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * La vista in cui saranno poi inserite le informazioni dei record contenuti in cursor.
         */
        private View holder;

        /**
         * Inizializza un nuovo ViewHolder con la vista fornita.
         *
         * @param holder La vista in cui saranno poi inserite le informazioni dei record contenuti in cursor.
         */
        public ViewHolder(View holder) {
            super(holder);
            this.holder = holder;
        }
    }

}
