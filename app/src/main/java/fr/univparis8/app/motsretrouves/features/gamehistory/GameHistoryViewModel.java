package fr.univparis8.app.motsretrouves.features.gamehistory;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import fr.univparis8.app.motsretrouves.data.GameDataSource;
import fr.univparis8.app.motsretrouves.model.GameDataInfo;

import java.util.ArrayList;
import java.util.List;

public class GameHistoryViewModel extends ViewModel {

    private GameDataSource mGameDataSource;
    private MutableLiveData<List<fr.univparis8.app.motsretrouves.model.GameDataInfo>> mOnGameDataInfoLoaded;

    public GameHistoryViewModel(GameDataSource gameDataSource) {
        mGameDataSource = gameDataSource;
        mOnGameDataInfoLoaded = new MutableLiveData<>();
    }

    public void loadGameHistory() {
        mGameDataSource.getGameDataInfos(infoList -> mOnGameDataInfoLoaded.setValue(infoList));
    }

    public void deleteGameData(fr.univparis8.app.motsretrouves.model.GameDataInfo gameDataInfo) {
        mGameDataSource.deleteGameData(gameDataInfo.getId());
        loadGameHistory();
    }

    public void clear() {
        mGameDataSource.deleteGameDatas();
        mOnGameDataInfoLoaded.setValue(new ArrayList<>());
    }

    public LiveData<List<GameDataInfo>> getOnGameDataInfoLoaded() {
        return mOnGameDataInfoLoaded;
    }
}
