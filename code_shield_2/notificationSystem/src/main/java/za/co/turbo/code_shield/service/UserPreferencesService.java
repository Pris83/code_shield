package za.co.turbo.code_shield.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.model.UserPreferences;

@Service
public class UserPreferencesService {

    private static final Logger logger = LoggerFactory.getLogger(UserPreferencesService.class);

    public UserPreferences getPreferences(User user) {
//        UserPreferences preferences = user.getUserPreferences();
        UserPreferences preferences = new UserPreferences(true, true);

        if (preferences == null) {
            logger.warn("No preferences found for user '{}'. Falling back to default preferences.", user.getUsername());
            return new UserPreferences(false, false);
        }

        return preferences;
    }
}
