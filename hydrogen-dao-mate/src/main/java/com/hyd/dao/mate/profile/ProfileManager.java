package com.hyd.dao.mate.profile;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class ProfileManager {

    private static final Preferences PROFILES_ROOT = Preferences.userRoot().node("/com/hyd/dao/mate/profiles");

    private static final ProfileManager instance = new ProfileManager();

    public static ProfileManager getInstance() {
        return instance;
    }

    private final List<DatabaseProfile> profileList;  // should be mutable list

    //////////////////////////////////////////////////////////////

    private ProfileManager() {
        this.profileList = readProfileList();
    }

    public List<DatabaseProfile> getProfileList() {
        return profileList;
    }

    public ArrayList<DatabaseProfile> readProfileList() {
        final int profileCount = PROFILES_ROOT.getInt("count", 0);
        if (profileCount == 0) {
            return new ArrayList<>();
        }

        ArrayList<DatabaseProfile> profileList = new ArrayList<>();
        for (int i = 0; i < profileCount; i++) {
            String json = PROFILES_ROOT.get("profile." + i, null);
            if (json != null) {
                profileList.add(JSON.parseObject(json, DatabaseProfile.class));
            }
        }

        return profileList;
    }

    public void saveProfile(DatabaseProfile databaseProfile) {
        final int index = this.profileList.indexOf(databaseProfile);

        if (index == -1) {
            PROFILES_ROOT.putInt("count", this.profileList.size() + 1);
            PROFILES_ROOT.put("profile." + this.profileList.size(), JSON.toJSONString(databaseProfile));
            this.profileList.add(databaseProfile);
        } else {
            PROFILES_ROOT.put("profile." + index, JSON.toJSONString(databaseProfile));
        }
    }
}
