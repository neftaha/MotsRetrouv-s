package fr.univparis8.app.motsretrouves.features.mainmenu;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import fr.univparis8.app.motsretrouves.data.GameThemeRepository;
import fr.univparis8.app.motsretrouves.model.GameTheme;

import java.util.List;

public class MainMenuViewModel extends ViewModel {

    private GameThemeRepository mGameThemeRepository;

    private MutableLiveData<List<GameTheme>> mOnGameThemeLoaded;

    public MainMenuViewModel(GameThemeRepository gameThemeRepository) {
        mGameThemeRepository = gameThemeRepository;
        mOnGameThemeLoaded = new MutableLiveData<>();
    }

    public void loadData() {
        mOnGameThemeLoaded.setValue(mGameThemeRepository.getGameThemes());
    }

    public LiveData<List<GameTheme>> getOnGameThemeLoaded() {
        return mOnGameThemeLoaded;
    }
}
