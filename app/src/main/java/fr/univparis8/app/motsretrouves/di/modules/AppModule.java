package  fr.univparis8.app.motsretrouves.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fr.univparis8.app.motsretrouves.features.ViewModelFactory;
import fr.univparis8.app.motsretrouves.data.GameDataSource;
import fr.univparis8.app.motsretrouves.data.GameThemeRepository;
import fr.univparis8.app.motsretrouves.data.WordDataSource;
import fr.univparis8.app.motsretrouves.features.gamehistory.GameHistoryViewModel;
import fr.univparis8.app.motsretrouves.features.gameover.GameOverViewModel;
import fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel;
import fr.univparis8.app.motsretrouves.features.mainmenu.MainMenuViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdularis on 18/07/17.
 */

@Module
public class AppModule {

    private Application mApp;

    public AppModule(Application application) {
        mApp = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(GameDataSource gameDataSource,
                                             WordDataSource wordDataSource) {
        return new ViewModelFactory(
                new GameOverViewModel(gameDataSource),
                new GamePlayViewModel(gameDataSource, wordDataSource),
                new MainMenuViewModel(new GameThemeRepository()),
                new GameHistoryViewModel(gameDataSource)
        );
    }
}
