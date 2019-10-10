package com.lonedev.smartwallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.lonedev.smartwallet.static_data.CursorType;
import com.lonedev.smartwallet.static_data.EntryCategoryAccess;
import com.lonedev.smartwallet.static_data.EntryTypeAccess;
import com.lonedev.smartwallet.static_data.MovementPeriodAccess;
import com.lonedev.smartwallet.static_data.WarningAccess;
import com.lonedev.smartwallet.support.MovementsFilter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Classe che permette di gestire il database dell'applicazione, fornendovi un accesso in scrittura e in lettura.
 *
 * @author Marco Michelini
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    /**
     * Il nome del database.
     * Assegnare un nome al database assicura che questo venga salvato su disco.
     */
    private static final String DB_NAME = "SWDatabase";

    /**
     * L'attuale versione del database.
     * Permette di rilasciare aggiornamenti alla struttura del database in maniera sicura ed efficace.
     */
    private static final int DB_VERSION = 1;

    /**
     * Il numero di colonne della tabella MOVEMENTS all'interno del database.
     */
    private static final int MOVEMENTS_TABLE_COLUMNS = 6;

    /**
     * Il numero di colonne della tabella LIMITS all'interno del database.
     */
    private static final int LIMITS_TABLE_COLUMNS = 5;

    /**
     * Il numero di colonne della tabella TARGETS all'interno del database.
     */
    private static final int TARGETS_TABLE_COLUMNS = 4;

    /**
     * Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    private Context context;

    /**
     * Inizializza un nuovo DatabaseHandler con il contesto fornito.
     *
     * @param context Il contesto dell'app. Conserva informazioni di carattere generale sull'applicazione.
     */
    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Crea le quattro tabelle del database (BALANCE, MOVEMENTS, LIMITS, TARGETS) e inizializza il saldo disponibile.
     * Eseguito soltanto se il database non è ancora stato creato.
     *
     * @param database Il database dell'applicazione.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        createBalanceTable(database);
        createMovementsTable(database);
        createLimitsTable(database);
        createTargetsTable(database);
        initBalance(database);
    }

    /**
     * Aggiorna la struttura del database, modificando le tabelle esistenti o aggiungendone di nuove.
     * Eseguito soltanto se la versione attuale del database precede DB_VERSION.
     *
     * @param database Il database dell'app.
     * @param oldVersion L'attuale versione del database.
     * @param newVersion La nuova versione del database, memorizzata in DB_VERSION.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    /**
     * Crea la tabella BALANCE, in cui viene memorizzato il saldo attuale. La tabella contiene una sola riga (è necessario un
     * unico valore, che verrà di volta in volta aggiornato).
     * Si riporta lo stato della tabella alla creazione del database:
     *
     *      _id     VALUE
     *       1        0
     *
     *      _id    -> ID intero, usato all'interno del database per identificare una specifica riga.
     *      VALUE  -> Il saldo disponibile.
     *
     * @param database Il database dell'app.
     */
    private void createBalanceTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE BALANCE ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "VALUE REAL);");
    }

    /**
     * Crea la tabella MOVEMENTS, in cui vengono memorizzati i movimenti, uno per riga.
     * All'interno dell'app, i movimenti sono sempre ordinati dal più al meno recente.
     * Si riporta lo stato della tabella alla creazione del database:
     *
     *      _id   DESCRIPTION   VALUE   DAY   MONTH   YEAR   ID
     *
     *      DESCRIPTION   -> La categoria del movimento.
     *      VALUE         -> Il valore del movimento.
     *      DAY           -> Il giorno del mese del movimento.
     *      MONTH         -> Il mese del movimento.
     *      YEAR          -> L'anno del movimento.
     *      ID            -> L'ID del movimento.
     *
     * @param database Il database dell'app.
     */
    private void createMovementsTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE MOVEMENTS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "DESCRIPTION TEXT, "
                + "VALUE REAL, "
                + "DAY INTEGER, "
                + "MONTH INTEGER, "
                + "YEAR INTEGER, "
                + "ID TEXT);");
    }

    /**
     * Crea la tabella LIMITS, in cui vengono memorizzati i limiti di spesa, uno per riga.
     * All'interno dell'app, i limiti di spesa sono sempre disposti in ordine di percentuale di completamento descrescente.
     * Si riporta lo stato della tabella alla creazione del database:
     *
     *      _id   DESCRIPTION   VALUE   MAX   PERCENT   MONTH
     *
     *      DESCRIPTION   -> La categoria del limite di spesa.
     *      VALUE         -> Il valore del limite di spesa.
     *      MAX           -> Il valore massimo mensile del limite di spesa.
     *      PERCENT       -> La percentuale di completamento del limite di spesa.
     *      MONTH         -> Il mese del limite di spesa.
     *
     * @param database Il database dell'app.
     */
    private void createLimitsTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE LIMITS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "DESCRIPTION TEXT, "
                + "VALUE REAL, "
                + "MAX REAL, "
                + "PERCENT INTEGER, "
                + "MONTH INTEGER);");
    }

    /**
     * Crea la tabella TARGETS, in cui vengono memorizzati gli obiettivi, uno per riga.
     * All'interno dell'app, gli obiettivi sono sempre disposti in ordine di percentuale di completamento descrescente.
     * Si riporta lo stato della tabella alla creazione del database:
     *
     *      _id   DESCRIPTION   VALUE   MAX   PERCENT
     *
     *      DESCRIPTION   -> Il nome dell'obiettivo.
     *      VALUE         -> Il valore dell'obiettivo.
     *      MAX           -> Il valore obiettivo dell'obiettivo.
     *      PERCENT       -> La percentuale di completamento dell'obiettivo.
     *
     * @param database Il database dell'app.
     */
    private void createTargetsTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE TARGETS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "DESCRIPTION TEXT, "
                + "VALUE REAL, "
                + "MAX REAL, "
                + "PERCENT INTEGER);");
    }

    /**
     * Rimuove dal database movimenti e limiti di spesa non più validi.
     */
    public void clearOld() {
        clearOldMovements();
        clearOldLimits();
    }

    /**
     * Elimina dal database i movimenti degli anni precedenti.
     */
    private void clearOldMovements() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        SQLiteDatabase database = getDatabase();

        if (database != null) {
            database.delete("MOVEMENTS","YEAR < ?", new String[] {String.valueOf(currentYear)});
            database.close();
        }
    }

    /**
     * Elimina dal database i limiti di spesa dei mesi precedenti.
     */
    private void clearOldLimits() {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        SQLiteDatabase database = getDatabase();

        if (database != null) {
            database.delete("LIMITS","MONTH < ?", new String[] {String.valueOf(currentMonth)});
            database.close();
        }
    }

    /**
     * Inizializza il saldo disponibile. Chiamato solo alla creazione del database.
     */
    private void initBalance(SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("VALUE", 0);
        database.insert("BALANCE", null, contentValues);
    }

    /**
     * Ritorna il saldo attualmente disponibile.
     * Se non è possibile accedere al database, ritorna -1.
     */
    public float getBalance() {
        float balance = -1;
        SQLiteDatabase database = getDatabase();

        if (database != null) {
            Cursor cursor = database.query ("BALANCE", new String[] {"VALUE"}, "_id = ?",
                    new String[] {Integer.toString(1)}, null, null, null);
            cursor.moveToFirst();
            balance = cursor.getFloat(0);
            cursor.close();
            database.close();
        }

        return balance;
    }

    /**
     * Incrementa il saldo disponibile dell'ammontare fornito.
     * Se non è possibile accedere al database, il saldo disponibile non viene modificato.
     *
     * @param amount La quantità di denaro da aggiungere al saldo disponibile.
     */
    public void increaseBalance(float amount) {
        float balance = getBalance();
        SQLiteDatabase database = getDatabase();

        if (balance != -1 && database != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("VALUE", balance + amount);
            database.update("BALANCE", contentValues, "_id = ?", new String[] {String.valueOf(1)});
            database.close();
        }

    }

    /**
     * Aggiunge un nuovo record al database, scegliendo autonomamente la tabella corretta.
     * Se non è possibile accedere al database, il record non viene aggiunto.
     *
     * @param entry Il record da aggiungere al database.
     */
    public void insert(Entry entry) {
        SQLiteDatabase database = getDatabase();

        if (database != null) {
            ContentValues contentValues = new ContentValues();
            String table = null;
            contentValues.put("DESCRIPTION", entry.getDescription());
            contentValues.put("VALUE", entry.getValue());

            if (entry instanceof Movement) {
                contentValues.put("DAY", ((Movement) entry).getDay());
                contentValues.put("MONTH", ((Movement) entry).getMonth());
                contentValues.put("YEAR", ((Movement) entry).getYear());
                contentValues.put("ID", ((Movement) entry).getID());
                table = "MOVEMENTS";
            } else if (entry instanceof Progress) {
                contentValues.put("MAX", ((Progress) entry).getMax());
                contentValues.put("PERCENT", ((Progress) entry).getPercent());

                if (entry instanceof Limit) {
                    contentValues.put("MONTH", ((Limit) entry).getMonth());
                    table = "LIMITS";
                } else if (entry instanceof Target) {
                    table = "TARGETS";
                }

            }

            if (table != null)
                database.insert(table, null, contentValues);

            database.close();
        }

    }

    /**
     * Ritorna il record corrispondente al cursore e alla posizione forniti.
     * Se non è possibile recuperarlo, o questo non esiste, ritorna null.
     *
     * @param cursor Il cursore da cui prelevare il record.
     * @param position La posizione del record all'interno di cursor.
     */
    public Entry get(Cursor cursor, int position) {
        if (cursor.getCount() == 0 || position < 0 || position > cursor.getCount()) {
            return null;
        } else {
            cursor.moveToPosition(position);
            String description = cursor.getString(0), cursorType = getCursorType(cursor);
            float value = cursor.getFloat(1);

            if (cursorType.equals(CursorType.MOVEMENTS)) {
                int day = cursor.getInt(2);
                int month = cursor.getInt(3);
                int year = cursor.getInt(4);
                String ID = cursor.getString(5);
                return new Movement(description, value, day, month, year, ID);
            } else if (cursorType.equals(CursorType.LIMITS) || cursorType.equals(CursorType.TARGETS)) {
                float max = cursor.getFloat(2);
                int percent = cursor.getInt(3);

                if (cursorType.equals(CursorType.LIMITS)) {
                    AvailableLimitCategories.remove(description);
                    return new Limit(description, value, max, percent);
                }

                return new Target(description, value, max, percent);
            } else {
                return null;
            }
        }
    }

    /**
     * Rimuove dal database il movimento individuato da identifier.
     * Se questo non esiste, o se non è possibile accedervi, non viene cancellato alcun movimento.
     *
     * @param identifier Stringa che identifica il movimento. Può essere un ID oppure il nome dell'obiettivo associato al movimento.
     */
    public void deleteMovement(String identifier) {
        List<String> targetNames = getTargetNames();
        SQLiteDatabase database = getDatabase();

        if (targetNames != null && database != null) {
            database.delete("MOVEMENTS", "ID = ? OR DESCRIPTION = ?", new String[] {identifier, identifier});
            database.close();
        }

    }

    /**
     * Rimuove dal database il progresso corrispodente alla descrizione fornita.
     * Se non vi sono corrispondenze, o se non è possibile accedere al database, non viene rimosso alcun progresso.
     *
     * @param description La descrizione del progresso da rimuovere dal database.
     */
    public void deleteProgress(String description) {
        SQLiteDatabase database = getDatabase();

        if (database != null) {
            database.delete("LIMITS", "DESCRIPTION = ?", new String[] {description});
            database.delete("TARGETS", "DESCRIPTION = ?", new String[] {description});
            database.close();
        }

    }

    /**
     * Incrementa il valore del progresso corrispondente alla descrizione fornita dell'ammontare fornito.
     * Se non vi sono corrispondenze, o se non è possibile accedere al database, non viene incrementato alcun progresso.
     *
     * @param description La descrizione del progresso da incrementare.
     * @param amount La quantità di denaro da aggiungere al valore del progresso identificato da description.
     * @return true se il progresso ha raggiunto/superato il suo limite massimo.
     */
    public boolean increaseProgress(String description, float amount) {
        Progress progress = getProgress(description);
        boolean maxReached = false;

        if (progress != null) {
            float value = progress.getValue(), max = progress.getMax();
            SQLiteDatabase database = getDatabase();

            if (database != null) {
                float newValue = value + amount;
                int newPercent = Math.round((newValue / max) * 100);

                if (newValue >= max)
                    maxReached = true;

                ContentValues contentValues = new ContentValues();
                contentValues.put("VALUE", newValue);
                contentValues.put("PERCENT", newPercent);
                database.update("LIMITS", contentValues, "DESCRIPTION = ?", new String[] {description});
                database.update("TARGETS", contentValues, "DESCRIPTION = ?", new String[] {description});
                database.close();
            }

        }

        return maxReached;
    }

    /**
     * Imposta il limite massimo del progresso corrispondente alla descrizione fornita al limite massimo fornito, e ne aggiorna
     * la percentuale di completamento di conseguenza.
     * Se non vi sono corrispondenze, o se non è possibile accedere al database, non viene aggiornato alcun progresso.
     *
     * @param description La descrizione del progresso da aggiornare.
     * @param newMax Il nuovo limite massimo del progresso corrispondente a description.
     */
    public void updateProgress(String description, float newMax) {
        Progress progress = getProgress(description);

        if (progress != null) {
            float value = progress.getValue();
            SQLiteDatabase database = getDatabase();

            if (database != null) {
                int newPercent = Math.round((value / newMax) * 100);
                ContentValues contentValues = new ContentValues();
                contentValues.put("PERCENT", newPercent);
                contentValues.put("MAX", newMax);
                database.update("LIMITS", contentValues, "DESCRIPTION = ?", new String[] {description});
                database.update("TARGETS", contentValues, "DESCRIPTION = ?", new String[] {description});
                database.close();
            }

        }
    }

    /**
     * Ritorna il progresso corrispondente alla descrizione fornita.
     * Se non vi sono corrispondenze, o se non è possibile accedere al database, ritorna null.
     *
     * @param description La descrizione del progresso da recuperare.
     */
    public Progress getProgress(String description) {
        List<String> limitCategories = getLimitCategories(), targetNames = getTargetNames();
        boolean descriptionIsCategory = false;
        Cursor cursor = null;

        if (limitCategories != null && limitCategories.contains(description)) {
            cursor = getCursor(CursorType.LIMITS, description);
            descriptionIsCategory = true;
        } else if (targetNames != null && targetNames.contains(description)) {
            cursor = getCursor(CursorType.TARGETS, description);
        }

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        } else {
            cursor.moveToFirst();
            float value = cursor.getFloat(1);
            float max = cursor.getFloat(2);
            int percent = cursor.getInt(3);

            if (descriptionIsCategory)
                return new Limit(description, value, max, percent);

            return new Target(description, value, max, percent);
        }

    }

    /**
     * Ritorna la lista aggiornata delle categorie associate ai limiti di spesa attualmente monitorati dall'utente.
     */
    public List<String> getLimitCategories() {
        List<String> limitCategories = new ArrayList<>();
        SQLiteDatabase database = getDatabase();

        if (database != null) {
            Cursor cursor = database.query("LIMITS", new String[]{"DESCRIPTION"}, null,
                    null, null, null, null);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                for (int i : new int[cursor.getCount()]) {
                    limitCategories.add(cursor.getString(0));
                    cursor.moveToNext();
                }

            }

            cursor.close();
            database.close();
        }

        return limitCategories;
    }

    /**
     * Ritorna la lista aggiornata dei nomi degli obiettivi attualmente impostati dall'utente.
     */
    public List<String> getTargetNames() {
        List<String> targetNames = new ArrayList<>();
        SQLiteDatabase database = getDatabase();

        if (database != null) {
            Cursor cursor = database.query ("TARGETS", new String[] {"DESCRIPTION"}, null,
                    null, null, null, null);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                for (int i : new int[cursor.getCount()]) {
                    targetNames.add(cursor.getString(0));
                    cursor.moveToNext();
                }

            }

            cursor.close();
            database.close();
        }

        return targetNames;
    }

    /**
     * Ritorna un SQLiteDatabase con accesso in scrittura e in lettura al database dell'app.
     * Se non è possibile accedere al database, visualizza un errore in un Toast e ritorna null.
     */
    private SQLiteDatabase getDatabase() {
        try {
            return getWritableDatabase();
        } catch (SQLiteException e) {
            new WarningAccess(context).show(WarningAccess.DATABASE_ERROR);
        }

        return null;
    }

    /**
     * Ritorna il tipo del cursore fornito.
     * Se il tipo del cursore non è fra quelli previsti dall'app, ritorna null.
     *
     * @param cursor Il cursore da identificare.
     */
    public String getCursorType(Cursor cursor) {
        if (cursor.getColumnCount() == MOVEMENTS_TABLE_COLUMNS)
            return CursorType.MOVEMENTS;
        else if (cursor.getColumnCount() == LIMITS_TABLE_COLUMNS)
            return CursorType.LIMITS;
        else if (cursor.getColumnCount() == TARGETS_TABLE_COLUMNS)
            return CursorType.TARGETS;

        return null;
    }

    /**
     * Ritorna il cursore corrispondente al tipo fornito.
     * Se il tipo del cursore non è fra quelli previsti dall'app, o se non è possibile accedere al database, ritorna null.
     *
     * @param cursorType Il tipo del cursore da ritornare.
     */
    public Cursor getCursor(String cursorType) {
        Cursor cursor = null;
        SQLiteDatabase database = getDatabase();

        if (database != null) {

            switch (cursorType) {
                case CursorType.MOVEMENTS:
                    String[] selectionArgs;
                    String selection = generateSelection();

                    if (selection.isEmpty()) {
                        cursor = database.query("MOVEMENTS", new String[]{"DESCRIPTION", "VALUE", "DAY", "MONTH",
                                "YEAR", "ID"}, null, null, null, null, "_id DESC");
                    }
                    else {
                        selectionArgs = generateSelectionArgs();
                        cursor = database.query("MOVEMENTS", new String[] {"DESCRIPTION", "VALUE", "DAY", "MONTH",
                                "YEAR", "ID"}, selection, selectionArgs, null, null, "_id DESC");
                    }

                    break;
                case CursorType.LIMITS:
                    cursor = database.query("LIMITS", new String[] {"DESCRIPTION", "VALUE", "MAX", "PERCENT", "MONTH"},
                            null, null, null, null, "PERCENT DESC");
                    break;
                case CursorType.TARGETS:
                    cursor = database.query("TARGETS", new String[] {"DESCRIPTION", "VALUE", "MAX", "PERCENT"},
                            null, null, null, null, "PERCENT DESC");
                    break;
            }

        }

        return cursor;
    }

    /**
     * Ritorna il cursore corrispondente al tipo fornito e costituito dei soli progressi corrispondenti alla descrizione fornita.
     * Se il tipo del cursore non è relativo a un progresso, o se non è possibile accedere al database, ritorna null.
     *
     * @param cursorType Il tipo del cursore da ritornare.
     * @param entryDescription La descrizione che identifica i progressi del cursore da ritornare.
     */
    public Cursor getCursor(String cursorType, String entryDescription) {
        Cursor cursor = null;
        SQLiteDatabase database = getDatabase();

        if (database != null) {

            switch (cursorType) {
                case CursorType.LIMITS:
                    cursor = database.query("LIMITS", new String[] {"DESCRIPTION", "VALUE", "MAX", "PERCENT", "MONTH"},
                            "DESCRIPTION = ?", new String[] {entryDescription}, null, null,
                            "PERCENT DESC");
                    break;
                case CursorType.TARGETS:
                    cursor = database.query("TARGETS", new String[] {"DESCRIPTION", "VALUE", "MAX", "PERCENT"},
                            "DESCRIPTION = ?", new String[] {entryDescription}, null, null,
                            "PERCENT DESC");
                    break;
            }

        }

        return cursor;
    }

    /**
     * Genera l'argomento selection del metodo query (si veda la classe SQLiteDatabase sul sito Android Developers) utilizzato per
     * filtrare gli elementi di un determinato cursore. In questo caso, permette di considerare solo i movimenti che rispettano i
     * filtri selezionati dall'utente nella schermata Movimenti dell'app.
     */
    private String generateSelection() {
        String partialCondition = null;
        StringBuilder selection = new StringBuilder();
        EntryTypeAccess entryTypeAccess = new EntryTypeAccess(context);
        EntryCategoryAccess entryCategoryAccess = new EntryCategoryAccess(context);
        MovementPeriodAccess movementPeriodAccess = new MovementPeriodAccess(context);

        if (!MovementsFilter.getType().equals(entryTypeAccess.get(EntryTypeAccess.ALL))) {
            if (MovementsFilter.getType().equals(entryTypeAccess.get(EntryTypeAccess.INS)))
                partialCondition = "VALUE > ?";
            else
                partialCondition = "VALUE < ?";

            selection.append(partialCondition);
        }

        if (!MovementsFilter.getCategory().equals(entryCategoryAccess.get(EntryCategoryAccess.ALL))) {
            if (partialCondition != null)
                selection.append(" AND ");

            partialCondition = "DESCRIPTION = ?";
            selection.append(partialCondition);
        }

        if (!MovementsFilter.getPeriod().equals(movementPeriodAccess.get(MovementPeriodAccess.LAST_YEAR))) {
            if (partialCondition != null)
                selection.append(" AND ");

            if (MovementsFilter.getPeriod().equals(movementPeriodAccess.get(MovementPeriodAccess.LAST_WEEK)))
                partialCondition = "MONTH = ? AND DAY >= ?";
            else if (MovementsFilter.getPeriod().equals(movementPeriodAccess.get(MovementPeriodAccess.LAST_MONTH)))
                partialCondition = "MONTH = ?";
            else
                partialCondition = "MONTH <= ?";

            selection.append(partialCondition);
        }

        return selection.toString();
    }

    /**
     * Genera l'argomento selectionArgs del metodo query (si veda la classe SQLiteDatabase sul sito Android Developers) utilizzato per
     * filtrare gli elementi di un determinato cursore. In questo caso, permette di considerare solo i movimenti che rispettano i
     * filtri selezionati dall'utente nella schermata Movimenti dell'app.
     */
    private String[] generateSelectionArgs() {
        EntryTypeAccess entryTypeAccess = new EntryTypeAccess(context);
        EntryCategoryAccess entryCategoryAccess = new EntryCategoryAccess(context);
        MovementPeriodAccess movementPeriodAccess = new MovementPeriodAccess(context);
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        List<String> partialSelectionArgs = new ArrayList<>();
        String[] selectionArgs;
        int dayCondition;

        if (!MovementsFilter.getType().equals(entryTypeAccess.get(EntryTypeAccess.ALL)))
            partialSelectionArgs.add(String.valueOf(0));

        if (!MovementsFilter.getCategory().equals(entryCategoryAccess.get(EntryCategoryAccess.ALL)))
            partialSelectionArgs.add(MovementsFilter.getCategory());

        if (!MovementsFilter.getPeriod().equals(movementPeriodAccess.get(MovementPeriodAccess.LAST_YEAR))) {
            if (MovementsFilter.getPeriod().equals(movementPeriodAccess.get(MovementPeriodAccess.LAST_WEEK))) {
                if (currentDayOfWeek == Calendar.SUNDAY)
                    dayCondition = currentDayOfMonth - 6;
                else
                    dayCondition = currentDayOfMonth - currentDayOfWeek + 2;

                partialSelectionArgs.add(String.valueOf(currentMonth));
                partialSelectionArgs.add(String.valueOf(dayCondition));
            }

            else if (MovementsFilter.getPeriod().equals(movementPeriodAccess.get(MovementPeriodAccess.LAST_MONTH)))
                partialSelectionArgs.add(String.valueOf(currentMonth));
            else
                partialSelectionArgs.add(String.valueOf(currentMonth - 2));
        }

        selectionArgs = new String[partialSelectionArgs.size()];
        selectionArgs = partialSelectionArgs.toArray(selectionArgs);
        return selectionArgs;
    }

}

