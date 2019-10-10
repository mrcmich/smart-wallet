package com.lonedev.smartwallet.data;

/**
 * Classe che modellizza i due possibili tipi di progressi dell'applicazione (limiti di spesa e obiettivi).
 *
 * @author Marco Michelini
 */
public class Progress extends Entry {

    /**
     * Il limite massimo del progresso.
     * Corrisponde:
     *      - Per un limite di spesa, al suo valore massimo mensile.
     *      - Per un obiettivo, al suo valore obiettivo.
     */
    protected float max;

    /**
     * La percentuale di completamento del progresso.
     */
    protected int percent;

    /**
     * Inizializza un nuovo progresso con il limite massimo e la descrizione forniti.
     *
     * @param description La descrizione del nuovo progresso.
     * @param max Il limite massimo del nuovo progresso.
     */
    public Progress(String description, float max) {
        super(description);
        this.max = max;
        percent = 0;
    }

    /**
     * Inizializza un nuovo progresso con valore, limite massimo, percentuale di completamento e descrizione forniti.
     *
     * @param description La descrizione del nuovo progresso.
     * @param value Il valore del nuovo progresso.
     * @param max Il limite massimo del nuovo progresso.
     * @param percent La percentuale di completamento del nuovo progresso.
     */
    public Progress(String description, float value, float max, int percent) {
        super(description, value);
        this.max = max;
        this.percent = percent;
    }

    /**
     * Ritorna il limite massimo del progresso.
     */
    public float getMax() {
        return max;
    }

    /**
     * Ritorna la percentuale di completamento del progresso.
     */
    public int getPercent() {
        return percent;
    }

}
