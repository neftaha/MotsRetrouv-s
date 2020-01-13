package fr.univparis8.app.motsretrouves.di.component;

import fr.univparis8.app.motsretrouves.di.modules.AppModule;
import fr.univparis8.app.motsretrouves.di.modules.DataSourceModule;
import fr.univparis8.app.motsretrouves.features.FullscreenActivity;
import fr.univparis8.app.motsretrouves.features.gamehistory.GameHistoryActivity;
import fr.univparis8.app.motsretrouves.features.gameover.GameOverActivity;
import fr.univparis8.app.motsretrouves.features.gameplay.GamePlayActivity;
import fr.univparis8.app.motsretrouves.features.mainmenu.MainMenuActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdularis on 18/07/17.
 */

@Singleton
@Component(modules = {AppModule.class, DataSourceModule.class})
public interface AppComponent {

    void inject(GamePlayActivity activity);

    void inject(MainMenuActivity activity);

    void inject(GameOverActivity activity);

    void inject(FullscreenActivity activity);

    void inject(GameHistoryActivity activity);

}
