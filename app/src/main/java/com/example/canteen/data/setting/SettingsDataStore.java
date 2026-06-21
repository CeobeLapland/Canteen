/*package com.example.canteen.data.setting;
import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


import androidx.datastore.preferences.core.BooleanPreferencesKey;

import androidx.datastore.preferences.core.StringPreferencesKey;

public class SettingsDataStore {
    // 存储文件名
    private static final String DATA_STORE_FILE_NAME = "app_server_settings";
    private static volatile SettingsDataStore INSTANCE;
    private final RxDataStore<Preferences> dataStore;

    // 定义所有Key
    public static final StringPreferencesKey KEY_SERVER_ADDRESS = StringPreferencesKey.create("serverAddress");
    public static final StringPreferencesKey KEY_DEFAULT_API = StringPreferencesKey.create("defaultApi");
    public static final BooleanPreferencesKey KEY_AUTO_SYNC = BooleanPreferencesKey.create("autoSync");
    public static final BooleanPreferencesKey KEY_ALLOW_MOBILE_DATA = BooleanPreferencesKey.create("allowMobileData");
    public static final StringPreferencesKey KEY_LAST_SYNC_TIME = StringPreferencesKey.create("lastSyncTime");

    private SettingsDataStore(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context.getApplicationContext(), DATA_STORE_FILE_NAME).build();
    }

    // 单例
    public static SettingsDataStore getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SettingsDataStore.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SettingsDataStore(context);
                }
            }
        }
        return INSTANCE;
    }

    //==================== 保存所有设置 ====================
    public Single<Preferences> saveSettings(String serverAddress,
                                            String defaultApi,
                                            boolean autoSync,
                                            boolean allowMobileData,
                                            String lastSyncTime) {
        return dataStore.updateDataAsync(prefs -> {
            Preferences.MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_SERVER_ADDRESS, serverAddress);
            mutable.set(KEY_DEFAULT_API, defaultApi);
            mutable.set(KEY_AUTO_SYNC, autoSync);
            mutable.set(KEY_ALLOW_MOBILE_DATA, allowMobileData);
            mutable.set(KEY_LAST_SYNC_TIME, lastSyncTime);
            return Single.just(mutable);
        });
    }

    // 单个字段保存
    public Single<Preferences> saveServerAddress(String address) {
        return dataStore.updateDataAsync(prefs -> {
            Preferences.MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_SERVER_ADDRESS, address);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> saveDefaultApi(String api) {
        return dataStore.updateDataAsync(prefs -> {
            Preferences.MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_DEFAULT_API, api);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> saveAutoSync(boolean autoSync) {
        return dataStore.updateDataAsync(prefs -> {
            Preferences.MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_AUTO_SYNC, autoSync);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> saveAllowMobileData(boolean allow) {
        return dataStore.updateDataAsync(prefs -> {
            Preferences.MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_ALLOW_MOBILE_DATA, allow);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> saveLastSyncTime(String time) {
        return dataStore.updateDataAsync(prefs -> {
            Preferences.MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_LAST_SYNC_TIME, time);
            return Single.just(mutable);
        });
    }

    //==================== 一次性读取 ====================
    public Single<String> getServerAddress() {
        return dataStore.data().firstOrError()
                .map(prefs -> prefs.get(KEY_SERVER_ADDRESS) == null ? "" : prefs.get(KEY_SERVER_ADDRESS));
    }

    public Single<String> getDefaultApi() {
        return dataStore.data().firstOrError()
                .map(prefs -> prefs.get(KEY_DEFAULT_API) == null ? "" : prefs.get(KEY_DEFAULT_API));
    }

    public Single<Boolean> getAutoSync() {
        return dataStore.data().firstOrError()
                .map(prefs -> prefs.get(KEY_AUTO_SYNC) != null && prefs.get(KEY_AUTO_SYNC));
    }

    public Single<Boolean> getAllowMobileData() {
        return dataStore.data().firstOrError()
                .map(prefs -> prefs.get(KEY_ALLOW_MOBILE_DATA) != null && prefs.get(KEY_ALLOW_MOBILE_DATA));
    }

    public Single<String> getLastSyncTime() {
        return dataStore.data().firstOrError()
                .map(prefs -> prefs.get(KEY_LAST_SYNC_TIME) == null ? "" : prefs.get(KEY_LAST_SYNC_TIME));
    }

    //==================== 实时监听 ====================
    public Flowable<String> observeServerAddress() {
        return dataStore.data().map(prefs -> prefs.get(KEY_SERVER_ADDRESS) == null ? "" : prefs.get(KEY_SERVER_ADDRESS));
    }

    public Flowable<String> observeDefaultApi() {
        return dataStore.data().map(prefs -> prefs.get(KEY_DEFAULT_API) == null ? "" : prefs.get(KEY_DEFAULT_API));
    }

    public Flowable<Boolean> observeAutoSync() {
        return dataStore.data().map(prefs -> prefs.get(KEY_AUTO_SYNC) != null && prefs.get(KEY_AUTO_SYNC));
    }

    public Flowable<Boolean> observeAllowMobileData() {
        return dataStore.data().map(prefs -> prefs.get(KEY_ALLOW_MOBILE_DATA) != null && prefs.get(KEY_ALLOW_MOBILE_DATA));
    }

    public Flowable<String> observeLastSyncTime() {
        return dataStore.data().map(prefs -> prefs.get(KEY_LAST_SYNC_TIME) == null ? "" : prefs.get(KEY_LAST_SYNC_TIME));
    }

    // 清空所有配置
    public Single<Preferences> clearAllSettings() {
        return dataStore.updateDataAsync(prefs -> {
            Preferences.MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.clear();
            return Single.just(mutable);
        });
    }
}
*/