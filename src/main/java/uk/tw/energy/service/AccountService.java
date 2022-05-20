package uk.tw.energy.service;

import org.springframework.stereotype.Service;

import java.util.Map;

// To store which meter belongs to which plan?

@Service
public class AccountService {

    // This is set as configuration in smartMeterToPricePlanAccounts
    private final Map<String, String> smartMeterToPricePlanAccounts;

    public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
}
