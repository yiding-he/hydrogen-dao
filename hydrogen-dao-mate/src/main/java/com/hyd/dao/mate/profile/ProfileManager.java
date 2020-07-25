package com.hyd.dao.mate.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public final class ProfileManager {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Preferences PROFILES_ROOT = Preferences.userRoot().node("/com/hyd/dao/mate/profiles");

    private static final ProfileManager instance = new ProfileManager();

    private final List<DatabaseProfile> profileList;  // should be mutable list

    public static ProfileManager getInstance() {
        return instance;
    }

    //////////////////////////////////////////////////////////////

    private ProfileManager() {
        this.profileList = readProfileList();
    }

    public List<DatabaseProfile> getProfileList() {
        return profileList;
    }

    public ArrayList<DatabaseProfile> readProfileList() {
        try {
            final int profileCount = PROFILES_ROOT.getInt("count", 0);
            if (profileCount == 0) {
                return new ArrayList<>();
            }

            ArrayList<DatabaseProfile> profileList = new ArrayList<>();
            for (int i = 0; i < profileCount; i++) {
                String json = PROFILES_ROOT.get("profile." + i, null);
                if (json != null) {
                    profileList.add(OBJECT_MAPPER.readValue(json, DatabaseProfile.class));
                }
            }

            return profileList;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProfile(DatabaseProfile databaseProfile) {
        try {
            final int index = this.profileList.indexOf(databaseProfile);

            if (index == -1) {
                PROFILES_ROOT.putInt("count", this.profileList.size() + 1);
                PROFILES_ROOT.put("profile." + this.profileList.size(), OBJECT_MAPPER.writeValueAsString(databaseProfile));
                this.profileList.add(databaseProfile);
            } else {
                PROFILES_ROOT.put("profile." + index, OBJECT_MAPPER.writeValueAsString(databaseProfile));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
