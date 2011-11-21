package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.lsd.LSDEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author neilellis@cazcade.com
 */
public class FountainUserUpdateService {

    public static final long ABOUT_ONE_DAY_IN_MILLIS = (3600 * 1000 * 24);

    @Autowired
    private FountainEmailService emailService;

    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private FountainSocialDAO socialDAO;


    /**
     * This is an initial trivial solution to user updates.
     */
    @Scheduled(cron = "0 0 8 1/1 * ? *")
    public void trivialUpdateLoop() {

        socialDAO.forEachUser(new FountainSocialDAO.UserCallback() {
            @Override
            public void process(LSDEntity userEntity, LSDEntity aliasEntity) {
                emailService.send(userEntity, "daily-updates", socialDAO.getUpdateSummaryForAlias(aliasEntity.getURI(), System.currentTimeMillis() - ABOUT_ONE_DAY_IN_MILLIS));
            }
        });

    }


}
