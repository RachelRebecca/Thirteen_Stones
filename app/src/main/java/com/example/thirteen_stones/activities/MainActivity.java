package com.example.thirteen_stones.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.thirteen_stones.R;
import com.example.thirteen_stones.databinding.MainIncludeAllContentItemsBinding;
import com.example.thirteen_stones.databinding.MainIncludeBottomBarAndFabBinding;
import com.example.thirteen_stones.models.ThirteenStones;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.View;


import com.example.thirteen_stones.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.thirteen_stones.lib.Utils.showInfoDialog;
import static com.example.thirteen_stones.models.ThirteenStones.getGameFromJSON;
import static com.example.thirteen_stones.models.ThirteenStones.getJSONFromGame;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainIncludeBottomBarAndFabBinding bottomBarAndFabBinding;
    private MainIncludeAllContentItemsBinding allContentItemsBinding;

    private ThirteenStones mGame;
    private TextView mTvStatusBarCurrentPlayer, mTvStatusBarStonesRemaining;
    private ImageView mImageViewStones;
    private Snackbar mSnackBar;

    private int[] mImages;

    private boolean mUseAutoSave; //win on last pick is set in the model

    private final String mKEY_GAME = "GAME";
    private String mKEY_AUTO_SAVE;
    private String mKEY_WIN_ON_LAST_PICK;

    @Override
    protected void onStop() {
        super.onStop();
        saveOrDeleteGameInSharedPrefs();
    }

    private void saveOrDeleteGameInSharedPrefs() {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Save current game or remove any prior game to/from default shared preferences
        if (mUseAutoSave)
            editor.putString(mKEY_GAME, mGame.getJSONFromCurrentGame());
        else
            editor.remove(mKEY_GAME);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        restoreFromPreferences_SavedGameIfAutoSaveWasSetOn();
        restoreOrSetFromPreferences_AllAppAndGameSettings();
    }

    private void restoreFromPreferences_SavedGameIfAutoSaveWasSetOn() {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getBoolean(mKEY_AUTO_SAVE,true)) {
            String gameString = defaultSharedPreferences.getString(mKEY_GAME, null);
            if (gameString!=null) {
                mGame = ThirteenStones.getGameFromJSON(gameString);
                updateUI();
            }
        }
    }

    private void restoreOrSetFromPreferences_AllAppAndGameSettings() {
        SharedPreferences sp = getDefaultSharedPreferences(this);
        mUseAutoSave = sp.getBoolean(mKEY_AUTO_SAVE, true);
        mGame.setWinnerIsLastPlayerToPick(sp.getBoolean(mKEY_WIN_ON_LAST_PICK, false));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(mKEY_GAME, getJSONFromGame(mGame));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame = getGameFromJSON(savedInstanceState.getString(mKEY_GAME));
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setToolbar();
        setUpViews();
        setUpImagesIntArray();
        setUpFAB();
        setupFields();
        startFirstGame();
    }


    private void setupFields() {
        mKEY_AUTO_SAVE = getString(R.string.auto_save_key);
        mKEY_WIN_ON_LAST_PICK = getString(R.string.win_on_last_pick_key);
    }

    private void setToolbar() {
        //setSupportActionBar(binding.getRoot().findViewById(R.id.toolbar));
        // the above works, but this is better:
        setSupportActionBar(binding.includeToolbar.toolbar);
    }

    private void setContentView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bottomBarAndFabBinding = MainIncludeBottomBarAndFabBinding.bind(binding.getRoot());
        allContentItemsBinding = MainIncludeAllContentItemsBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());
    }

    private void setUpFAB() {
        //binding.getRoot().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
        bottomBarAndFabBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Win the game by making the other player remove the final" +
                        " stone. Have fun!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setUpViews() {
        mTvStatusBarCurrentPlayer = bottomBarAndFabBinding.tvStatusCurrentPlayer;
        mTvStatusBarStonesRemaining = bottomBarAndFabBinding.tvStatusStonesRemaining;

        mSnackBar =
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.welcome),
                        Snackbar.LENGTH_LONG);
        mImageViewStones = allContentItemsBinding.cardImageViewStones.imageViewStones;

    }

    private void setUpImagesIntArray() {
        mImages = new int[]{R.drawable.stones_00, R.drawable.stones_01, R.drawable.stones_02,
                R.drawable.stones_03, R.drawable.stones_04, R.drawable.stones_05,
                R.drawable.stones_06, R.drawable.stones_07, R.drawable.stones_08,
                R.drawable.stones_09, R.drawable.stones_10, R.drawable.stones_11,
                R.drawable.stones_12, R.drawable.stones_13};
    }

    @SuppressWarnings("unused")
    public void pick123(View view) {
        try {
            mGame.takeTurn(Integer.parseInt(((Button) view).getText().toString()));
            updateUI();
            showGameOverMessageIfGameNowOver();
        } catch (IllegalStateException | IllegalArgumentException e) {
            if (e.getMessage() != null) {
                mSnackBar.setText(e.getMessage());
                mSnackBar.show();
            }
        }
    }

    private void dismissSnackBarIfShown() {
        if (mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }

    private void showGameOverMessageIfGameNowOver() {
        if (mGame.isGameOver()) {
            dismissSnackBarIfShown();
            showInfoDialog(this, getString(R.string.game_over),
                    getString(R.string.winner_is_player_number) +
                            mGame.getWinningPlayerNumberIfGameOver() +
                            ". " + getString(
                            R.string.games_played) + " " + mGame.getNumberOfGamesPlayed());
        }
    }

    private void updateUI() {
        dismissSnackBarIfShown();
        mTvStatusBarCurrentPlayer.setText(
                String.format(Locale.getDefault(), "%s: %d",
                        getString(R.string.current_player),
                        mGame.getCurrentPlayerNumber()));
        mTvStatusBarStonesRemaining.setText
                (String.format(Locale.getDefault(), "%s: %d",
                        getString(R.string.stones_remaining_in_pile),
                        mGame.getStonesRemaining()));
        try {
            mImageViewStones.setImageDrawable(
                    ContextCompat.getDrawable(this, mImages[mGame.getStonesRemaining()]));
        } catch (ArrayIndexOutOfBoundsException e) {
            mSnackBar.setText(R.string.error_msg_could_not_update_image);
            mSnackBar.show();
        }
    }

    private void startFirstGame() {
        mGame = new ThirteenStones();
        updateUI();
    }

    private void startNextNewGame() {
        mGame.startGame();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        if (itemId == R.id.action_new_game) {
            startNextNewGame();
            return true;
        } else if (itemId == R.id.action_statistics) {
            showStatistics();
            return true;
        } else if (itemId == R.id.action_reset_stats) {
            mGame.resetStatistics();
            return true;
        } else if (itemId == R.id.action_settings) {
            showSettings();
            return true;
        } else if (itemId == R.id.action_about) {
            showAbout();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void showStatistics() {
        dismissSnackBarIfShown();
        Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
        intent.putExtra("GAME", mGame.getJSONFromCurrentGame());
        startActivity(intent);
    }

    private void showAbout() {
        dismissSnackBarIfShown();
        showInfoDialog(MainActivity.this, "About 13 Stones",
                "A quick two-player game; have fun!\n" +
                        "\nAndroid game by SA.\nmintedtech@gmail.com");
    }


    private void showSettings() {
        dismissSnackBarIfShown();
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        settingsLauncher.launch (intent);
       // startActivityForResult(intent, 1);
    }

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult (
            new ActivityResultContracts.StartActivityForResult (),
            result -> restoreOrSetFromPreferences_AllAppAndGameSettings ());

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            restoreOrSetFromPreferences_AllAppAndGameSettings();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
