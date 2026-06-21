package com.example.canteen.data.setting;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class SettingsRepository {

    private static SettingsRepository instance;
    public static SettingsRepository getInstance() {
        if (instance == null) {
            instance = new SettingsRepository();
        }
        return instance;
    }

    public Single<SettingsSnapshot> getSettingsSnapshot() {
        return Single.fromCallable(() -> new SettingsSnapshot(
                "小刻",
                307193467L,
                "2026-06-20 12:00",
                "https:",
                "/v1",
                "是大聪明喵",
                "307193467",
                true,
                false
                //true,
                //false
        ));
    }

    
    public Completable updateUserName(String newName) {
        return Completable.complete();
    }

    
    public Completable updateAvatar(String avatarUri) {
        return Completable.complete();
    }

    
    public Completable deleteAllAccountData() {
        return Completable.complete();
    }

    
    public Completable resyncData() {
        return Completable.complete();
    }

    
    public Completable clearCache() {
        return Completable.complete();
    }

    
    public Completable clearHistory() {
        return Completable.complete();
    }

    
    public Completable saveServerSettings(String serverAddress, String defaultApi) {
        return Completable.complete();
    }

    
    public Completable setThemeMode(String mode) {
        return Completable.complete();
    }

    
    public Completable setAutoSync(boolean enabled) {
        return Completable.complete();
    }

    
    public Completable setAllowMobileData(boolean enabled) {
        return Completable.complete();
    }

    
    //public Completable setReduceMotion(boolean enabled) {
    //    return Completable.complete();
    //}

    
    //public Completable setCompactMode(boolean enabled) {
    //    return Completable.complete();
    //}

    
    public Completable runLocalSettingAction(Runnable action) {
        return Completable.fromAction(action::run);
    }
}
