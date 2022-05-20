package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;

import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cost")
public class MeterCostController {

    private final PricePlanService pricePlanService;

    public MeterCostController(PricePlanService pricePlanService) {
        this.pricePlanService = pricePlanService;
    }

    @GetMapping("/{smartMeterId}")
    public ResponseEntity getCost(@PathVariable String smartMeterId) {

        BigDecimal cost = pricePlanService.getLastWeekConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (cost.equals(BigDecimal.ZERO)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cost);
    }
}


