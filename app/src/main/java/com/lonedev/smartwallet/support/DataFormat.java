package com.lonedev.smartwallet.support;

/**
 * Classe che fornisce metodi utili alla formattazione di varie tipologie di informazione.
 * Utilizzata per formattare date e valori visualizzati all'interno dell'app.
 *
 * @author Marco Michelini
 */
public abstract class DataFormat {

    /**
     * Il massimo numero di cifre decimali visualizzate all'interno dell'app.
     */
    private static final int MAX_DECIMAL_DIGITS = 2;

    /**
     * Formatta il valore numerico fornito in modo che risulti essere un intero oppure un numero decimale avente MAX_DECIMAL_DIGITS
     * cifre decimali, e ne ritorna la stringa.
     *
     * @param value Il valore numerico da formattare.
     */
    public static String format(float value) {
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(value));
        int commaIndex = stringBuilder.lastIndexOf("."), stringLength = stringBuilder.length();

        if (commaIndex == -1 || stringLength - commaIndex == MAX_DECIMAL_DIGITS + 1) {
            return stringBuilder.toString();
        } else if (stringLength - commaIndex == MAX_DECIMAL_DIGITS) {

            if (stringBuilder.charAt(stringLength - 1) == '0')
                stringBuilder.delete(commaIndex, stringLength);
            else
                stringBuilder.append("0");

        } else if (stringLength - commaIndex > MAX_DECIMAL_DIGITS + 1) {
            stringBuilder.delete(commaIndex + MAX_DECIMAL_DIGITS + 1, stringLength);
        }

        return stringBuilder.toString();
    }

    /**
     * Formatta la data fornita nel formato dd/mm/yy, e ne ritorna la stringa.
     *
     * @param day Giorno del mese da formattare.
     * @param month Mese da formattare.
     * @param year Anno da formattare.
     */
    public static String format(int day, int month, int year) {
        StringBuilder stringBuilder = new StringBuilder();

        if (day < 10)
            stringBuilder.append("0");

        stringBuilder.append(day).append("/");

        if (month < 10)
            stringBuilder.append("0");

        stringBuilder.append(month).append("/").append(String.valueOf(year).substring(2));
        return stringBuilder.toString();
    }

}
