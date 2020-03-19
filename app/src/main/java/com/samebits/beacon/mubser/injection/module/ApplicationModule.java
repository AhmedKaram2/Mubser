
package com.samebits.beacon.mubser.injection.module;

import android.app.Application;
import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {
    protected final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return this.application;
    }


    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    public BeaconManager provideBeaconManager() {
        BeaconManager manager = BeaconManager.getInstanceForApplication(application);
        //manager.setDebug(true);
        return manager;
    }

}