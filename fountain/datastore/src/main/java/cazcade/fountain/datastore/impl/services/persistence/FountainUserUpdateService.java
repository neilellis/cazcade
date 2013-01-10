/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.ChangeReport;
import cazcade.fountain.datastore.impl.FountainSocialDAO;
import cazcade.fountain.datastore.impl.FountainUserDAO;
import cazcade.fountain.index.persistence.dao.AliasDAO;
import cazcade.fountain.index.persistence.entities.AliasEntity;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class FountainUserUpdateService {
    public static final long HOUR_IN_MILLIS = 3600L * 1000L;
    public static final long DAY_IN_MILLIS  = 24L * HOUR_IN_MILLIS;

    @Nonnull
    private final Logger log = Logger.getLogger(FountainUserDAOImpl.class);

    @Autowired
    private FountainEmailService emailService;

    @Autowired
    private FountainSocialDAO socialDAO;

    @Autowired
    private FountainUserDAO userDAO;

    @Autowired
    private AliasDAO aliasDAO;

    private boolean test;


    /**
     * This is an initial trivial solution to user updates.
     */
    //todo: change to a less frequent cron schedule as shown in the commented out annotation
    //just started it as hourly to get things going (makes testing easier).
    //    @Scheduled(cron = "0 0 19 1/1 * ? *")
    @Scheduled(cron = "0 0 0/1 1/1 * ?")
    //    @Scheduled(cron = "0 0/5 * 1/1 * ?")
    public void trivialUpdateLoop() {
        if (!CommonConstants.IS_PRODUCTION && !test) {
            return;
        }
        final long nowMillis = System.currentTimeMillis();
        final Date now = new Date(nowMillis);
        //yeah yeah I know - not accurate - will do for now.
        final long lastHour = System.currentTimeMillis() - HOUR_IN_MILLIS;
        final long yesterday = System.currentTimeMillis() - DAY_IN_MILLIS;
        final long lastWeek = System.currentTimeMillis() - 7L * DAY_IN_MILLIS;
        final long lastMonth = System.currentTimeMillis() - 28L * DAY_IN_MILLIS;

        userDAO.forEachUser(new FountainUserDAO.UserCallback() {
            @Override
            public void process(@Nonnull final LSDTransferEntity userEntity, @Nonnull final LSDTransferEntity aliasEntity) throws InterruptedException, UnsupportedEncodingException {
                log.info("Sending update to " + aliasEntity.getURI());
                final AliasEntity alias = aliasDAO.getOrCreateAlias(aliasEntity.getURI().asString());
                long lastEmailUpdateDate = alias.getLastEmailUpdateDate() != null
                                           ? alias.getLastEmailUpdateDate().getTime()
                                           : yesterday;
                boolean send = yesterday >= lastEmailUpdateDate;
                if (userEntity.hasAttribute(LSDAttribute.EMAIL_UPDATE_FREQUENCY)) {
                    final String frequency = userEntity.getAttribute(LSDAttribute.EMAIL_UPDATE_FREQUENCY);
                    if ("H".equals(frequency)) {
                        send = lastHour > lastEmailUpdateDate - HOUR_IN_MILLIS / 4;
                    }
                    else if ("D".equals(frequency)) {
                        send = yesterday > lastEmailUpdateDate - DAY_IN_MILLIS / 4;
                    }
                    else if ("W".equals(frequency)) {
                        send = lastWeek > lastEmailUpdateDate - DAY_IN_MILLIS / 2;
                    }
                    else if ("M".equals(frequency)) {
                        send = lastMonth > lastEmailUpdateDate - DAY_IN_MILLIS / 2;
                    }
                    else if ("U".equals(frequency)) {
                        send = false;
                    }
                    else {
                        throw new IllegalArgumentException("Unrecognized period " + frequency);
                    }
                }
                if (test) {
                    send = true;
                    lastEmailUpdateDate = 0;
                }
                if (send) {
                    final ChangeReport report = socialDAO.getUpdateSummaryForAlias(aliasEntity.getURI(), lastEmailUpdateDate);
                    if (report.hasChangedFollowedBoards() || report.hasChangedOwnedBoards() || report.hasLatestChanges()) {
                        emailService.send(userEntity, aliasEntity, "latest-updates.vm", "Latest updates from Boardcast", report, test);
                        alias.setLastEmailUpdateDate(now);
                        //                    if (!test) {
                        aliasDAO.saveUser(alias);
                        //                    }
                    }
                }
            }
        });
    }

    public void setTest(final boolean test) {
        this.test = test;
    }
}
