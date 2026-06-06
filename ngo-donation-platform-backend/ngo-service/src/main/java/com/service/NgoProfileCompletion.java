package com.service;

import com.entity.NGO;

import java.util.ArrayList;
import java.util.List;

public final class NgoProfileCompletion {

    private static final int TOTAL_FIELDS = 6;

    private NgoProfileCompletion() {
    }

    public static List<String> missingFields(NGO ngo, long documentCount) {
        List<String> missing = new ArrayList<>();
        if (isBlank(ngo.getName())) {
            missing.add("organization name");
        }
        if (isBlank(ngo.getAddress())) {
            missing.add("address");
        }
        if (isBlank(ngo.getPhoneNumber())) {
            missing.add("phone number");
        }
        if (isBlank(ngo.getContactEmail())) {
            missing.add("contact email");
        }
        if (isBlank(ngo.getDescription())) {
            missing.add("description");
        }
        if (documentCount < 1) {
            missing.add("at least one verification document");
        }
        return missing;
    }

    public static int percentComplete(NGO ngo, long documentCount) {
        int missing = missingFields(ngo, documentCount).size();
        return Math.round(((TOTAL_FIELDS - missing) * 100f) / TOTAL_FIELDS);
    }

    public static boolean isComplete(NGO ngo, long documentCount) {
        return missingFields(ngo, documentCount).isEmpty();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
