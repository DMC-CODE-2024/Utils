package com.eirs.pairs.service;

import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.duplicateMsisdnUpdate.DuplicateUpdateMsisdnUtility;
import com.eirs.pairs.duplicateToBlack.DuplicateToBlackListUtility;
import com.eirs.pairs.mgmtClean.PairMgmtInitStartCleanUtility;
import com.eirs.pairs.pairClean.PairCleanUpUtility;
import com.eirs.pairs.pairMsisdnUpdate.PairingUpdateMsisdnUtility;
import com.eirs.pairs.pairingExpiry.PairingToBlackListUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UtilityFinderService {

    @Autowired
    ApplicationContext applicationContext;

    public UtilityService getUtility(UtilityType utilityType) {
        switch (utilityType) {
            case PAIRING_EXPIRY_PROCESS -> { // ok
                return applicationContext.getBean(PairingToBlackListUtility.class);
            }
            case GREY_TO_BLACKLIST -> {
                return applicationContext.getBean(StolenGreyToBlackUtility.class);
            }
//            case REMINDER_UTILITY -> {
//                return applicationContext.getBean(NotificationReminderUtility.class);
//            }
            case REMINDER_UTILITY -> {
                return applicationContext.getBean(GenericReminderUtility.class);
            }
            case DUPLICATE_EXPIRY_PROCESS -> { //Ok
                return applicationContext.getBean(DuplicateToBlackListUtility.class);
            }
            case PAIRING_CLEAN -> { //ok
                return applicationContext.getBean(PairCleanUpUtility.class);
            }
            case PAIR_MGMT_INIT_START_CLEAN -> { //ok
                return applicationContext.getBean(PairMgmtInitStartCleanUtility.class);
            }
            case PAIR_UPDATE_MSISDN -> { //Ok
                return applicationContext.getBean(PairingUpdateMsisdnUtility.class);
            }
            case DUPLICATE_UPDATE_MSISDN -> { //Ok
                return applicationContext.getBean(DuplicateUpdateMsisdnUtility.class);
            }
            default -> {
                log.error("No Utility service found for {}", utilityType);
                return null;
            }
        }

    }

}
