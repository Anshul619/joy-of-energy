package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public class PricePlan {

    /**
     * Every plan would contain rate per kWh, plan name, peakTimeMultipliers etc.
     */
    private final String energySupplier;
    private final String planName;
    private final BigDecimal unitRate; // unit price per kWh
    private final List<PeakTimeMultiplier> peakTimeMultipliers;

    public PricePlan(String planName, String energySupplier, BigDecimal unitRate, List<PeakTimeMultiplier> peakTimeMultipliers) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = peakTimeMultipliers;
    }

    public String getEnergySupplier() {
        return energySupplier;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {

        // More readable code
        for (PeakTimeMultiplier currentMultiplier: peakTimeMultipliers) {

            if (currentMultiplier.dayOfWeek.equals(dateTime.getDayOfWeek())) {
                return unitRate.multiply(currentMultiplier.multiplier);
            }
        }

        return unitRate;


        /*return peakTimeMultipliers.stream()
                .filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek()))
                .findFirst()
                .map(multiplier -> unitRate.multiply(multiplier.multiplier))
                .orElse(unitRate);*/
    }


    static class PeakTimeMultiplier {

        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        // Multiplier and day of the week
        public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }
}
