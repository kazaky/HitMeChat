package io.lattis.hitme;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by kazaky on 2/25/2017.
 */

public class HitMeApp extends Application {
    private static HitMeApp INSTANCE;

    public static HitMeApp get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        if(INSTANCE == null) {
            INSTANCE = this;
        }


        Realm.init(this);
        // Configure Realm for the application
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("hit_me.realm")
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        super.onCreate();

    }
}
