package fr.univparis8.app.motsretrouves;

import android.app.Application;

import fr.univparis8.app.motsretrouves.di.component.AppComponent;
import fr.univparis8.app.motsretrouves.di.component.DaggerAppComponent;
import fr.univparis8.app.motsretrouves.di.modules.AppModule;

/**
 * Created by abdularis on 18/07/17.
 */

public class MotsRetrouves extends Application {

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
