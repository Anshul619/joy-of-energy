package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.time.Instant;

@Service
public class MeterReadingService {

    // HashMap which would have readings - meter => list of readings
    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    // Constructor would set the HashMap
    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    // Get readings
    // What is Optional - https://www.geeksforgeeks.org/optional-ofnullable-method-in-java-with-examples/
    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public Optional<List<ElectricityReading>> getLastWeekReadings(String smartMeterId) {

        List<ElectricityReading> allElectricityReadings = meterAssociatedReadings.get(smartMeterId);

        List<ElectricityReading> output = new ArrayList<>();

        Instant lastWeekTimeInstant = Instant.now().minus(Duration.ofDays(7));

        for (ElectricityReading er: allElectricityReadings) {
            if (er.getTime().isAfter(lastWeekTimeInstant)) {
                output.add(er);
            }
        }

        return Optional.ofNullable(output);
    }

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
        }

        // Append all readings at once, in the HashMap
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
    }
}
