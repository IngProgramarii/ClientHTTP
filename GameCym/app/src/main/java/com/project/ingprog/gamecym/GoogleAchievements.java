package com.project.ingprog.gamecym;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by danyh on 3/30/2016.
 */
public class GoogleAchievements {
    public enum Achievements
    {
        LOGIN,
        BIOSTATS,
        PLAN_WEEK,
        FIRST_DAY,
        FIRST_EXERCISE,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY,
        TEST1,
        TEST2
    }

    static private Map<Achievements, String> achievementsStringMap;
    static private boolean wasInitialized = false;

    private static GoogleApiClient googleApiClient = null;

    private static void init()
    {
        achievementsStringMap = new HashMap<Achievements, String>();

        achievementsStringMap.put(Achievements.LOGIN,           "CgkIm-uxrpIMEAIQAA");
        achievementsStringMap.put(Achievements.BIOSTATS,        "CgkIm-uxrpIMEAIQCw");
        achievementsStringMap.put(Achievements.PLAN_WEEK,       "CgkIm-uxrpIMEAIQAQ");
        achievementsStringMap.put(Achievements.FIRST_DAY,       "CgkIm-uxrpIMEAIQAg");
        achievementsStringMap.put(Achievements.FIRST_EXERCISE,  "CgkIm-uxrpIMEAIQAw");
        achievementsStringMap.put(Achievements.MONDAY,          "CgkIm-uxrpIMEAIQBA");
        achievementsStringMap.put(Achievements.TUESDAY,         "CgkIm-uxrpIMEAIQBQ");
        achievementsStringMap.put(Achievements.WEDNESDAY,       "CgkIm-uxrpIMEAIQBg");
        achievementsStringMap.put(Achievements.THURSDAY,        "CgkIm-uxrpIMEAIQBw");
        achievementsStringMap.put(Achievements.FRIDAY,          "CgkIm-uxrpIMEAIQCA");
        achievementsStringMap.put(Achievements.SATURDAY,        "CgkIm-uxrpIMEAIQCQ");
        achievementsStringMap.put(Achievements.SUNDAY,          "CgkIm-uxrpIMEAIQCg");

    }

    public static <T extends Context &
            GoogleApiClient.ConnectionCallbacks &
            GoogleApiClient.OnConnectionFailedListener> GoogleApiClient getGoogleApiClient(T context)
    {
        if(!wasInitialized) {
            init();
            wasInitialized = true;
        }

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(context)
                .addOnConnectionFailedListener(context)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        return googleApiClient;
    }

    public static String getUniqueId()
    {
       return Games.Players.getCurrentPlayerId(googleApiClient);
    }

    public static void unlockAchievement(Achievements achievement)
    {
        Games.Achievements.unlock(googleApiClient, achievementsStringMap.get(achievement));
    }
}