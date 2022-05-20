package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricePlanService {

    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;

    private final AccountService accountService;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService, AccountService accountService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
        this.accountService = accountService;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
    }

    public BigDecimal getLastWeekConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> lastWeekElectricityReadings = meterReadingService.getLastWeekReadings(smartMeterId);

        // When there are no readings
        if (!lastWeekElectricityReadings.isPresent()) {
            return BigDecimal.ZERO;
        }

        String planName = this.accountService.getPricePlanIdForSmartMeterId(smartMeterId);

        for (PricePlan pricePlanIterator: pricePlans) {

            if (pricePlanIterator.getPlanName() == planName) {

                return calculateCost(lastWeekElectricityReadings.get(), pricePlanIterator);

            }

        }

        // When smart meter doesn't have any plans
        return BigDecimal.valueOf(-1);
    }

    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

        BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
        return averagedCost.multiply(pricePlan.getUnitRate());
    }


    private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::getReading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
        ElectricityReading first = electricityReadings.stream()
                .min(Comparator.comparing(ElectricityReading::getTime))
                .get();
        ElectricityReading last = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::getTime))
                .get();

        return BigDecimal.valueOf(Duration.between(first.getTime(), last.getTime()).getSeconds() / 3600.0);
    }

}
