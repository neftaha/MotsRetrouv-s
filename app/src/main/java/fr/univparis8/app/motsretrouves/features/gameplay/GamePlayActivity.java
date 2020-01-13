package fr.univparis8.app.motsretrouves.features.gameplay;

import android.animation.Animator;
import android.animation.AnimatorInflater;
 import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;


import fr.univparis8.app.motsretrouves.R;
import fr.univparis8.app.motsretrouves.custom.StreakView;
import fr.univparis8.app.motsretrouves.features.SoundPlayer;
import fr.univparis8.app.motsretrouves.features.ViewModelFactory;
import fr.univparis8.app.motsretrouves.MotsRetrouves;

import fr.univparis8.app.motsretrouves.commons.DurationFormatter;
import fr.univparis8.app.motsretrouves.commons.Util;
import fr.univparis8.app.motsretrouves.model.GameData;
import fr.univparis8.app.motsretrouves.custom.LetterBoard;
import fr.univparis8.app.motsretrouves.features.gameover.GameOverActivity;
import fr.univparis8.app.motsretrouves.features.FullscreenActivity;
import fr.univparis8.app.motsretrouves.model.UsedWord;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GamePlayActivity extends FullscreenActivity {

    public static final String EXTRA_GAME_ROUND_ID =
            "com.aar.app.wordsearch.features.gameplay.GamePlayActivity.ID";
    public static final String EXTRA_ROW_COUNT =
            "com.aar.app.wordsearch.features.gameplay.GamePlayActivity.ROW";
    public static final String EXTRA_COL_COUNT =
            "com.aar.app.wordsearch.features.gameplay.GamePlayActivity.COL";

    private static final fr.univparis8.app.motsretrouves.features.gameplay.StreakLineMapper STREAK_LINE_MAPPER = new fr.univparis8.app.motsretrouves.features.gameplay.StreakLineMapper();

    @Inject
    SoundPlayer mSoundPlayer;

    @Inject ViewModelFactory mViewModelFactory;
    private fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel mViewModel;

    @BindView(R.id.text_duration) TextView mTextDuration;
    @BindView(R.id.letter_board) LetterBoard mLetterBoard;
    @BindView(R.id.flow_layout)
    fr.univparis8.app.motsretrouves.custom.layout.FlowLayout mFlowLayout;

    @BindView(R.id.text_sel_layout) View mTextSelLayout;
    @BindView(R.id.text_selection) TextView mTextSelection;

    @BindView(R.id.answered_text_count) TextView mAnsweredTextCount;
    @BindView(R.id.words_count) TextView mWordsCount;

    @BindView(R.id.finished_text) TextView mFinishedText;

    @BindView(R.id.loading) View mLoading;
    @BindView(R.id.loadingText) TextView mLoadingText;
    @BindView(R.id.content_layout) View mContentLayout;

    @BindColor(R.color.gray) int mGrayColor;

    private ArrayLetterGridDataAdapter mLetterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        ButterKnife.bind(this);
        ((MotsRetrouves) getApplication()).getAppComponent().inject(this);

        mLetterBoard.getStreakView().setEnableOverrideStreakLineColor(getPreferences().grayscale());
        mLetterBoard.getStreakView().setOverrideStreakLineColor(mGrayColor);
        mLetterBoard.setOnLetterSelectionListener(new LetterBoard.OnLetterSelectionListener() {
            @Override
            public void onSelectionBegin(StreakView.StreakLine streakLine, String str) {
                streakLine.setColor(Util.getRandomColorWithAlpha(170));
                mTextSelLayout.setVisibility(View.VISIBLE);
                mTextSelection.setText(str);
            }

            @Override
            public void onSelectionDrag(StreakView.StreakLine streakLine, String str) {
                if (str.isEmpty()) {
                    mTextSelection.setText("...");
                } else {
                    mTextSelection.setText(str);
                }
            }

            @Override
            public void onSelectionEnd(StreakView.StreakLine streakLine, String str) {
                mViewModel.answerWord(str, STREAK_LINE_MAPPER.revMap(streakLine), getPreferences().reverseMatching());
                mTextSelLayout.setVisibility(View.GONE);
                mTextSelection.setText(str);
            }
        });

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.class);
        mViewModel.getOnTimer().observe(this, this::showDuration);
        mViewModel.getOnGameState().observe(this, this::onGameStateChanged);
        mViewModel.getOnAnswerResult().observe(this, this::onAnswerResult);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_GAME_ROUND_ID)) {
                int gid = extras.getInt(EXTRA_GAME_ROUND_ID);
                mViewModel.loadGameRound(gid);
            } else {
                int rowCount = extras.getInt(EXTRA_ROW_COUNT);
                int colCount = extras.getInt(EXTRA_COL_COUNT);
                mViewModel.generateNewGameRound(rowCount, colCount);
            }
        }

        if (!getPreferences().showGridLine()) {
            mLetterBoard.getGridLineBackground().setVisibility(View.INVISIBLE);
        } else {
            mLetterBoard.getGridLineBackground().setVisibility(View.VISIBLE);
        }

        mLetterBoard.getStreakView().setSnapToGrid(getPreferences().getSnapToGrid());
        mFinishedText.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.resumeGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.pauseGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.stopGame();
    }

    private void onAnswerResult(fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.AnswerResult answerResult) {
        if (answerResult.correct) {
            TextView textView = findUsedWordTextViewByUsedWordId(answerResult.usedWordId);
            if (textView != null) {
                UsedWord uw = (UsedWord) textView.getTag();

                if (getPreferences().grayscale()) {
                    uw.getAnswerLine().color = mGrayColor;
                }
                textView.setBackgroundColor(uw.getAnswerLine().color);
                textView.setText(uw.getString());
                textView.setTextColor(Color.WHITE);
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                Animator anim = AnimatorInflater.loadAnimator(this, R.animator.zoom_in_out);
                anim.setTarget(textView);
                anim.start();
            }

            mSoundPlayer.play(SoundPlayer.Sound.Correct);
        }
        else {
            mLetterBoard.popStreakLine();

            mSoundPlayer.play(SoundPlayer.Sound.Wrong);
        }
    }

    private void onGameStateChanged(fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.GameState gameState) {
        showLoading(false, null);
        if (gameState instanceof fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Generating) {
            fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Generating state = (fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Generating) gameState;
            String text = "Generating " + state.rowCount + "x" + state.colCount + " grid";
            showLoading(true, text);
        } else if (gameState instanceof fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Finished) {
            showFinishGame(((fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Finished) gameState).mGameData.getId());
        } else if (gameState instanceof fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Paused) {

        } else if (gameState instanceof fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Playing) {
            onGameRoundLoaded(((fr.univparis8.app.motsretrouves.features.gameplay.GamePlayViewModel.Playing) gameState).mGameData);
        }
    }

    private void onGameRoundLoaded(GameData gameData) {
        if (gameData.isFinished()) {
            setGameAsAlreadyFinished();
        }

        showLetterGrid(gameData.getGrid().getArray());
        showDuration(gameData.getDuration());
        showUsedWords(gameData.getUsedWords());
        showWordsCount(gameData.getUsedWords().size());
        showAnsweredWordsCount(gameData.getAnsweredWordsCount());
        doneLoadingContent();
    }

    private void tryScale() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int boardWidth = mLetterBoard.getWidth();
        int screenWidth = metrics.widthPixels;

        if (getPreferences().autoScaleGrid() || boardWidth > screenWidth) {
            float scale = (float)screenWidth / (float)boardWidth;
            mLetterBoard.scale(scale, scale);
//            mLetterBoard.animate()
//                    .scaleX(scale)
//                    .scaleY(scale)
//                    .setDuration(400)
//                    .setInterpolator(new DecelerateInterpolator())
//                    .start();
        }
    }

    private void doneLoadingContent() {
        // call tryScale() on the next render frame
        new Handler().postDelayed(this::tryScale, 100);
    }

    private void showLoading(boolean enable, String text) {
        if (enable) {
            mLoading.setVisibility(View.VISIBLE);
            mLoadingText.setVisibility(View.VISIBLE);
            mContentLayout.setVisibility(View.GONE);
            mLoadingText.setText(text);
        } else {
            mLoading.setVisibility(View.GONE);
            mLoadingText.setVisibility(View.GONE);
            mContentLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showLetterGrid(char[][] grid) {
        if (mLetterAdapter == null) {
            mLetterAdapter = new ArrayLetterGridDataAdapter(grid);
            mLetterBoard.setDataAdapter(mLetterAdapter);
        }
        else {
            mLetterAdapter.setGrid(grid);
        }
    }

    private void showDuration(int duration) {
        mTextDuration.setText(DurationFormatter.fromInteger(duration));
    }

    private void showUsedWords(List<UsedWord> usedWords) {
        for (UsedWord uw : usedWords) {
            mFlowLayout.addView( createUsedWordTextView(uw) );
        }
    }

    private void showAnsweredWordsCount(int count) {
        mAnsweredTextCount.setText(String.valueOf(count));
    }

    private void showWordsCount(int count) {
        mWordsCount.setText(String.valueOf(count));
    }

    private void showFinishGame(int gameId) {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra(GameOverActivity.EXTRA_GAME_ROUND_ID, gameId);
        startActivity(intent);
        finish();
    }

    private void setGameAsAlreadyFinished() {
        mLetterBoard.getStreakView().setInteractive(false);
        mFinishedText.setVisibility(View.VISIBLE);
    }

    //
    private TextView createUsedWordTextView(UsedWord uw) {
        TextView tv = new TextView(this);
        tv.setPadding(10, 5, 10, 5);
        if (uw.isAnswered()) {
            if (getPreferences().grayscale()) {
                uw.getAnswerLine().color = mGrayColor;
            }
            tv.setBackgroundColor(uw.getAnswerLine().color);
            tv.setText(uw.getString());
            tv.setTextColor(Color.WHITE);
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            mLetterBoard.addStreakLine(STREAK_LINE_MAPPER.map(uw.getAnswerLine()));
        }
        else {
            String str = uw.getString();
            if (uw.isMystery()) {
                int revealCount = uw.getRevealCount();
                String uwString = uw.getString();
                str = "";
                for (int i = 0; i < uwString.length(); i++) {
                    if (revealCount > 0) {
                        str += uwString.charAt(i);
                        revealCount--;
                    }
                    else {
                        str += " ?";
                    }
                }
            }
            tv.setText(str);
        }

        tv.setTag(uw);
        return tv;
    }

    private TextView findUsedWordTextViewByUsedWordId(int usedWordId) {
        for (int i = 0; i < mFlowLayout.getChildCount(); i++) {
            TextView tv = (TextView) mFlowLayout.getChildAt(i);
            UsedWord uw = (UsedWord) tv.getTag();
            if (uw != null && uw.getId() == usedWordId) {
                return tv;
            }
        }

        return null;
    }
}
