package uk.tw.energy.domain;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.tw.energy.controller.PricePlanComparatorController;

import java.math.BigDecimal;
import java.time.*;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PricePlanTest {

    private final String ENERGY_SUPPLIER_NAME = "Energy Supplier Name";

    Logger logger = LoggerFactory.getLogger(PricePlanTest.class.getName());

    @Test
    public void shouldReturnTheEnergySupplierGivenInTheConstructor() {
        PricePlan pricePlan = new PricePlan(null, ENERGY_SUPPLIER_NAME, null, null);

        assertThat(pricePlan.getEnergySupplier()).isEqualTo(ENERGY_SUPPLIER_NAME);
    }

    @Test
    public void shouldReturnTheBasePriceGivenAnOrdinaryDateTime() throws Exception {
        LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
        PricePlan.PeakTimeMultiplier peakTimeMultiplier = new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, singletonList(peakTimeMultiplier));

        BigDecimal price = pricePlan.getPrice(normalDateTime);

        assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReturnAnExceptionPriceGivenExceptionalDateTime() throws Exception {

        // This datetime should meet the exception
        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);

        // On wednesday, the cost would be 10 times as normal.
        PricePlan.PeakTimeMultiplier peakTimeMultiplier = new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);

        // Unit rate is 1
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, singletonList(peakTimeMultiplier));

        // Get price, corresponding to date-time
        BigDecimal price = pricePlan.getPrice(exceptionalDateTime);

        logger.info("1 week before time ->" + Instant.now().minus(Duration.ofDays(20)).getEpochSecond());
        //logger.info(price.toString());

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReceiveMultipleExceptionalDateTimes() throws Exception {

        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
        PricePlan.PeakTimeMultiplier peakTimeMultiplier = new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PricePlan.PeakTimeMultiplier otherPeakTimeMultiplier = new PricePlan.PeakTimeMultiplier(DayOfWeek.TUESDAY, BigDecimal.TEN);
        List<PricePlan.PeakTimeMultiplier> peakTimeMultipliers = Arrays.asList(peakTimeMultiplier, otherPeakTimeMultiplier);
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, peakTimeMultipliers);

        BigDecimal price = pricePlan.getPrice(exceptionalDateTime);

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }
}
