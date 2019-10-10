package com.lonedev.smartwallet.data;

/**
 * Classe che modellizza i tre possibili tipi di record dell'applicazione (movimenti, limiti di spesa, obiettivi).
 * Il saldo disponibile è considerato un'entità separata, gestita direttamente da DatabaseHandler.
 *
 * @author Marco Michelini
 */
public class Entry {

    /**
     * Stringa che identifica il record all'interno dell'applicazione.
     * Questa coincide con:
     *      - Per la classe Target, il nome dell'obiettivo.
     *      - Per le classi Movement e Limit, la categoria di spesa.
     */
    protected String description;

    /**
     * Il valore del record.
     * Questo coincide con:
     *      - Per la classe Movement, la quantità di denaro associata al movimento.
     *      - Per la classe Limit, la quantità di denaro spesa nel mese corrente in una determinata categoria.
     *      - Per la classe Target, la quantità di denaro complessivamente destinata all'acquisto di un dato bene.
     */
    protected float value;

    /**
     * Inizializza un nuovo record con la descrizione fornita.
     *
     * @param description La descrizione del nuovo record.
     */
    public Entry(String description) {
        this.description = description;
        value = 0;
    }

    /**
     * Inizializza un nuovo record con il valore e la descrizione forniti.
     *
     * @param description La descrizione del nuovo record.
     * @param value Il valore del nuovo record.
     */
    public Entry(String description, float value) {
        this.description = description;
        this.value = value;
    }

    /**
     * Ritorna la descrizione del record.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ritorna il valore del record.
     */
    public float getValue() {
        return value;
    }

}
