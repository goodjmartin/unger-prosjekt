package no.goodtech.vaadin;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility class for precision number formatting.
 */
public class PrecisionUtils {

    private final Integer decimals;

    public PrecisionUtils(final Integer decimals) {
        this.decimals = decimals;
    }

    /**
     * Formats number according to standard (Half Up) formatting rules
     *
     * @param value The value to be formatted
     * @return The formatted value
     */
    public String formatNumber(final Double value) {
        return formatNumber(value, decimals);
    }

    /**
     * Formats number according to standard (Half Up) formatting rules
     *
     * @param value The value to be formatted
     * @param decimals Number of decimals in the result
     * @return The formatted value
     */
    public String formatNumber(final Double value, final Integer decimals) {
        DecimalFormat decimalFormat;

        // Rounding numbers half up
        decimalFormat = new DecimalFormat(getDecimalFormatPattern(decimals));
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(new Locale("en", "US")));

        return decimalFormat.format(value);
    }

    /**
     * Formats number according to standard (Up) formatting rules
     *
     * @param value The value to be formatted
     * @return The formatted value
     */
    public String formatNumberRoundUp(final Double value) {
        return formatNumberRoundUp(value, decimals);
    }

    /**
     * Formats number according to standard (Up) formatting rules
     *
     * @param value The value to be formatted
     * @param decimals Number of decimals in the result
     * @return The formatted value
     */
    public String formatNumberRoundUp(final Double value, final Integer decimals) {
        DecimalFormat decimalFormatRoundUp;

        // Rounding numbers up
        decimalFormatRoundUp = new DecimalFormat(getDecimalFormatPattern(decimals));
        decimalFormatRoundUp.setRoundingMode(RoundingMode.UP);
        decimalFormatRoundUp.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(new Locale("en", "US")));

        return decimalFormatRoundUp.format(value);
    }

    /**
     * Construct the decimal format pattern to be used
     *
     * @param decimals Number of decimals in the result
     * @return The decimal format pattern
     */
    private String getDecimalFormatPattern(final Integer decimals) {
        int numberOfDecimals = (decimals != null) ? decimals : this.decimals;

        String decimalFormatPattern = "0";

        if (numberOfDecimals > 0) {
            decimalFormatPattern += ".";

            for (int count = 0; count < numberOfDecimals; count++) {
                decimalFormatPattern += "0";
            }
        }

        return decimalFormatPattern;
    }

}
