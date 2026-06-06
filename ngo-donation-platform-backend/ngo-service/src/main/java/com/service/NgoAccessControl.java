package com.service;

import com.entity.NGO;
import com.entity.NGOStatus;
import com.exceptions.EntityNotFoundException;

public final class NgoAccessControl {

    private NgoAccessControl() {
    }

    public static void assertOwner(NGO ngo, Long userId) {
        if (userId == null || !userId.equals(ngo.getUserId())) {
            throw new IllegalStateException("You can only manage your own organization profile");
        }
    }

    /** Profile and documents may be edited only while status is PENDING (before admin review). */
    public static void assertProfileEditable(NGO ngo) {
        if (ngo.getStatus() != NGOStatus.PENDING) {
            throw new IllegalStateException(
                    "Your profile is locked while it is under review or after a decision. Contact support if you need changes.");
        }
    }

    public static void assertApproved(NGO ngo) {
        if (ngo.getStatus() != NGOStatus.APPROVED) {
            throw new IllegalStateException(
                    "Your organization must be approved by an administrator before you can use this feature.");
        }
    }

    public static NGO requireNgo(NGO ngo, Long ngoId) {
        if (ngo == null) {
            throw new EntityNotFoundException("NGO not found with id " + ngoId);
        }
        return ngo;
    }
}
