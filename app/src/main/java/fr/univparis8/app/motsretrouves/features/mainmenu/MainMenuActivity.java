package fr.univparis8.app.motsretrouves.features.mainmenu;

 import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

 import androidx.lifecycle.ViewModelProviders;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;

 import fr.univparis8.app.motsretrouves.R;
 import fr.univparis8.app.motsretrouves.features.ViewModelFactory;
import fr.univparis8.app.motsretrouves.MotsRetrouves;
import fr.univparis8.app.motsretrouves.features.FullscreenActivity;
import fr.univparis8.app.motsretrouves.features.gamehistory.GameHistoryActivity;
import fr.univparis8.app.motsretrouves.features.gameplay.GamePlayActivity;
import fr.univparis8.app.motsretrouves.model.GameTheme;
import fr.univparis8.app.motsretrouves.easyadapter.MultiTypeAdapter;
import fr.univparis8.app.motsretrouves.features.settings.SettingsActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends FullscreenActivity {

    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.game_template_spinner) Spinner mGridSizeSpinner;

    @BindArray(R.array.game_round_dimension_values)
    int[] mGameRoundDimVals;

    @Inject
    ViewModelFactory mViewModelFactory;
    fr.univparis8.app.motsretrouves.features.mainmenu.MainMenuViewModel mViewModel;
    MultiTypeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.bind(this);
        ((MotsRetrouves) getApplication()).getAppComponent().inject(this);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(fr.univparis8.app.motsretrouves.features.mainmenu.MainMenuViewModel.class);
        mViewModel.getOnGameThemeLoaded().observe(this, this::showGameThemeList);


        mAdapter = new MultiTypeAdapter();
        mAdapter.addDelegate(
                GameTheme.class,
                R.layout.item_game_theme,
                (model, holder) -> holder.<TextView>find(R.id.textThemeName).setText(model.getName()),
                (model, view) -> Toast.makeText(MainMenuActivity.this, model.getName(), Toast.LENGTH_SHORT).show()
        );
        mAdapter.addDelegate(
                HistoryItem.class,
                R.layout.item_histories,
                (model, holder) -> {},
                (model, view) -> {
                    Intent i = new Intent(MainMenuActivity.this, GameHistoryActivity.class);
                    startActivity(i);
                }
        );

        mRv.setAdapter(mAdapter);
        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mViewModel.loadData();
    }

    public void showGameThemeList(List<GameTheme> gameThemes) {
        mAdapter.setItems(gameThemes);
        mAdapter.insertAt(0, new HistoryItem());
    }

    @OnClick(R.id.settings_button)
    public void onSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.new_game_btn)
    public void onNewGameClick() {
        int dim = mGameRoundDimVals[ mGridSizeSpinner.getSelectedItemPosition() ];
        Intent intent = new Intent(MainMenuActivity.this, GamePlayActivity.class);
        intent.putExtra(GamePlayActivity.EXTRA_ROW_COUNT, dim);
        intent.putExtra(GamePlayActivity.EXTRA_COL_COUNT, dim);
        startActivity(intent);
    }

}
