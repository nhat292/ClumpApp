package com.ck.clump;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.karumi.dexter.Dexter;
import com.pubnub.api.Pubnub;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private static App app;
    private static RealmConfiguration config;
    private Pubnub pubNub;
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = this;
        /*Fabric analytics*/
        Fabric.with(this, new Crashlytics());
        /*Init Dexter for check runtime permissions*/
        Dexter.initialize(this);
        /*Init real database*/
        Realm.init(this);
        config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
        /*Init fireBase push notification*/
        FirebaseApp.initializeApp(this);
        /*Init PubNub*/
        pubNub = new Pubnub(getString(R.string.pubnub_public_key), getString(R.string.pubnub_subscribe_key));
        /*Init Test Memory Leak*/
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);*/
    }

    public static App getInstance() {
        return app;
    }

    public static RealmConfiguration getRealmConfig() {
        return config;
    }

    public Realm getRealm() {
        return realm;
    }

    public Pubnub getPubNub() {
        return pubNub;
    }

}
