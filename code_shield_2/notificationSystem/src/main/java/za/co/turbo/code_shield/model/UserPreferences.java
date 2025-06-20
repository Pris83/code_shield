package za.co.turbo.code_shield.model;

public class UserPreferences {
    private boolean emailEnabled;
    private boolean smsEnabled;

    public UserPreferences(boolean emailEnabled, boolean smsEnabled) {
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }
}