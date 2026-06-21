package com.example.canteen.data.setting;

public class SettingsSnapshot {

    private final String userName;
    private final long uid;
    private final String lastSyncTime;
    private final String serverAddress;
    private final String defaultApi;
    private final String aboutMe;
    private final String qq;
    private final boolean autoSync;
    private final boolean allowMobileData;
    //private final boolean reduceMotion;
    //private final boolean compactMode;

    public SettingsSnapshot(String userName, long uid, String lastSyncTime,
                            String serverAddress, String defaultApi,
                            String aboutMe, String qq,
                            boolean autoSync, boolean allowMobileData
                            //boolean reduceMotion, boolean compactMode
                            ) {
        this.userName = userName;
        this.uid = uid;
        this.lastSyncTime = lastSyncTime;
        this.serverAddress = serverAddress;
        this.defaultApi = defaultApi;
        this.aboutMe = aboutMe;
        this.qq = qq;
        this.autoSync = autoSync;
        this.allowMobileData = allowMobileData;
        //this.reduceMotion = reduceMotion;
        //this.compactMode = compactMode;
    }

    public String getUserName() { return userName; }
    public long getUid() { return uid; }
    public String getLastSyncTime() { return lastSyncTime; }
    public String getServerAddress() { return serverAddress; }
    public String getDefaultApi() { return defaultApi; }
    public String getAboutMe() { return aboutMe; }
    public String getQq() { return qq; }
    public boolean isAutoSync() { return autoSync; }
    public boolean isAllowMobileData() { return allowMobileData; }
    //public boolean isReduceMotion() { return reduceMotion; }
    //public boolean isCompactMode() { return compactMode; }
}