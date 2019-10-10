package com.lonedev.smartwallet.data;

/**
 * Classe che rappresenta un obiettivo, inteso come la quantità di denaro da mettere da parte per l'acquisto di un determinato bene.
 * Dal punto di vista strutturale Progress e Target sono sostanzialmente la stessa classe, ma nell'applicazione sono considerate due
 * entità distinte.
 *
 * @author Marco Michelini
 */
public class Target extends Progress {

    /**
     * Inizializza un nuovo obiettivo con il valore obiettivo e il nome forniti.
     *
     * @param description Il nome del nuovo obiettivo.
     * @param max Il valore obiettivo del nuovo obiettivo.
     */
    public Target(String description, float max) {
        super(description, max);
    }

    /**
     * Inizializza un nuovo obiettivo con valore, valore obiettivo, percentuale di completamento e nome forniti.
     *
     * @param description Il nome del nuovo obiettivo.
     * @param value Il valore del nuovo obiettivo.
     * @param max Il valore obiettivo del nuovo obiettivo.
     * @param percent La percentuale di completamento del nuovo obiettivo.
     */
    public Target(String description, float value, float max, int percent) {
        super(description, value, max, percent);
    }

}
