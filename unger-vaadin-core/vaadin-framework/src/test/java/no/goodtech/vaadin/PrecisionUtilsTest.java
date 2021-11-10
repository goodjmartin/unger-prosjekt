package no.goodtech.vaadin;

import org.junit.Assert;
import org.junit.Test;

public class PrecisionUtilsTest {

    @Test
    public void roundUpToNoDecimals() {
        PrecisionUtils precisionUtils = new PrecisionUtils(0);

        // Positive numbers
        Assert.assertEquals("2001", precisionUtils.formatNumberRoundUp(2000.001));
        Assert.assertEquals("2001", precisionUtils.formatNumberRoundUp(2000.499));
        Assert.assertEquals("2001", precisionUtils.formatNumberRoundUp(2000.500));
        Assert.assertEquals("2001", precisionUtils.formatNumberRoundUp(2000.999));

        // Negative numbers
        Assert.assertEquals("-2001", precisionUtils.formatNumberRoundUp(-2000.001));
        Assert.assertEquals("-2001", precisionUtils.formatNumberRoundUp(-2000.499));
        Assert.assertEquals("-2001", precisionUtils.formatNumberRoundUp(-2000.500));
        Assert.assertEquals("-2001", precisionUtils.formatNumberRoundUp(-2000.999));
    }

    @Test
    public void roundHalfUpNoDecimals() {
        PrecisionUtils precisionUtils = new PrecisionUtils(0);

        // Positive numbers
        Assert.assertEquals("2000", precisionUtils.formatNumber(2000.001));
        Assert.assertEquals("2000", precisionUtils.formatNumber(2000.499));
        Assert.assertEquals("2001", precisionUtils.formatNumber(2000.500));
        Assert.assertEquals("2001", precisionUtils.formatNumber(2000.999));

        // Negative numbers
        Assert.assertEquals("-2000", precisionUtils.formatNumber(-2000.001));
        Assert.assertEquals("-2000", precisionUtils.formatNumber(-2000.499));
        Assert.assertEquals("-2001", precisionUtils.formatNumber(-2000.500));
        Assert.assertEquals("-2001", precisionUtils.formatNumber(-2000.999));
    }

    @Test
    public void roundUpToTwoDecimals() {
        PrecisionUtils precisionUtils = new PrecisionUtils(2);

        // Positive numbers
        Assert.assertEquals("2000.01", precisionUtils.formatNumberRoundUp(2000.0001));
        Assert.assertEquals("2000.01", precisionUtils.formatNumberRoundUp(2000.0049));
        Assert.assertEquals("2000.51", precisionUtils.formatNumberRoundUp(2000.5001));
        Assert.assertEquals("2001.00", precisionUtils.formatNumberRoundUp(2000.9999));

        // Negative numbers
        Assert.assertEquals("-2000.01", precisionUtils.formatNumberRoundUp(-2000.0001));
        Assert.assertEquals("-2000.01", precisionUtils.formatNumberRoundUp(-2000.0049));
        Assert.assertEquals("-2000.51", precisionUtils.formatNumberRoundUp(-2000.5001));
        Assert.assertEquals("-2001.00", precisionUtils.formatNumberRoundUp(-2000.9999));
    }

    @Test
    public void roundHalfUpTwoDecimals() {
        PrecisionUtils precisionUtils = new PrecisionUtils(2);

        // Positive numbers
        Assert.assertEquals("2000.00", precisionUtils.formatNumber(2000.0001));
        Assert.assertEquals("2000.00", precisionUtils.formatNumber(2000.0049));
        Assert.assertEquals("2000.50", precisionUtils.formatNumber(2000.5001));
        Assert.assertEquals("2001.00", precisionUtils.formatNumber(2000.9999));

        // Negative numbers
        Assert.assertEquals("-2000.00", precisionUtils.formatNumber(-2000.0001));
        Assert.assertEquals("-2000.00", precisionUtils.formatNumber(-2000.0049));
        Assert.assertEquals("-2000.50", precisionUtils.formatNumber(-2000.5001));
        Assert.assertEquals("-2001.00", precisionUtils.formatNumber(-2000.9999));
    }

    @Test
    public void roundHalfUpTwoDecimalsOverrideDefault() {
        PrecisionUtils precisionUtils = new PrecisionUtils(3);

        // Positive numbers
        Assert.assertEquals("2000.00", precisionUtils.formatNumber(2000.0001, 2));
        Assert.assertEquals("2000.00", precisionUtils.formatNumber(2000.0049, 2));
        Assert.assertEquals("2000.50", precisionUtils.formatNumber(2000.5001, 2));
        Assert.assertEquals("2001.00", precisionUtils.formatNumber(2000.9999, 2));

        // Negative numbers
        Assert.assertEquals("-2000.00", precisionUtils.formatNumber(-2000.0001, 2));
        Assert.assertEquals("-2000.00", precisionUtils.formatNumber(-2000.0049, 2));
        Assert.assertEquals("-2000.50", precisionUtils.formatNumber(-2000.5001, 2));
        Assert.assertEquals("-2001.00", precisionUtils.formatNumber(-2000.9999, 2));
    }

    @Test
    public void roundHalfUpTwoDecimalsUseDefaultWhenDecimalIsNull() {
        PrecisionUtils precisionUtils = new PrecisionUtils(2);

        // Positive numbers
        Assert.assertEquals("2000.00", precisionUtils.formatNumber(2000.0001, null));
        Assert.assertEquals("2000.00", precisionUtils.formatNumber(2000.0049, null));
        Assert.assertEquals("2000.50", precisionUtils.formatNumber(2000.5001, null));
        Assert.assertEquals("2001.00", precisionUtils.formatNumber(2000.9999, null));

        // Negative numbers
        Assert.assertEquals("-2000.00", precisionUtils.formatNumber(-2000.0001, null));
        Assert.assertEquals("-2000.00", precisionUtils.formatNumber(-2000.0049, null));
        Assert.assertEquals("-2000.50", precisionUtils.formatNumber(-2000.5001, null));
        Assert.assertEquals("-2001.00", precisionUtils.formatNumber(-2000.9999, null));
    }

}
