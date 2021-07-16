package com.example.thirteen_stones.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.thirteen_stones.databinding.ActivityMainBinding;
import com.example.thirteen_stones.databinding.MainIncludeBottomBarAndFabBinding;
import com.example.thirteen_stones.models.ThirteenStones;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import com.example.thirteen_stones.databinding.ActivityStatisticsBinding;

import com.example.thirteen_stones.R;

import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private ActivityStatisticsBinding binding;
    private ActivityMainBinding activityMainBinding;
    private MainIncludeBottomBarAndFabBinding bottomBarAndFabBinding;

    private TextView tvDataGamesPlayed,
            tvDataPlayer1Wins, tvDataPlayer1WinsPercent,
            tvDataPlayer2Wins, tvDataPlayer2WinsPercent;

    private ThirteenStones mCurrentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
       // activityMainBinding = ActivityMainBinding.bind(binding.getRoot());
        //bottomBarAndFabBinding = MainIncludeBottomBarAndFabBinding.bind(binding.getRoot());


        //setContentView(R.layout.activity_statistics);
        setContentView(binding.getRoot());

        setupToolbar();
        setupFAB();
        setupViews();
        getIncomingData();
        processAndOutputIncomingData();
    }

    private void setupToolbar() {
     //   Toolbar toolbar = activityMainBinding.includeToolbar.toolbar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() !=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupFAB() {
        //FloatingActionButton fab = bottomBarAndFabBinding.fab;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupViews() {
        /*
        tvDataGamesPlayed = findViewById(R.id.tv_data_games_played);
        tvDataPlayer1Wins = findViewById(R.id.tv_data_player1_wins);
        tvDataPlayer1WinsPercent = findViewById(R.id.tv_data_player1_win_percent);
        tvDataPlayer2Wins = findViewById(R.id.tv_data_player2_wins);
        tvDataPlayer2WinsPercent = findViewById(R.id.tv_data_player2_win_percent);
         */

        tvDataGamesPlayed = binding.includeContentStatistics.tvDataGamesPlayed;
        tvDataPlayer1Wins = binding.includeContentStatistics.tvDataPlayer1Wins;
        tvDataPlayer1WinsPercent = binding.includeContentStatistics.tvDataPlayer1WinPercent;
        tvDataPlayer2Wins = binding.includeContentStatistics.tvDataPlayer2Wins;
        tvDataPlayer2WinsPercent = binding.includeContentStatistics.tvDataPlayer2WinPercent;
    }

    private void getIncomingData() {
        Intent intent = getIntent();
        String gameJSON = intent.getStringExtra("GAME");
        mCurrentGame = ThirteenStones.getGameFromJSON(gameJSON);
    }

    private void processAndOutputIncomingData() {
        final String FORMAT_STRING = "%2.1f%%", N_A = "N/A";
        int numberOfGamesPlayed = mCurrentGame.getNumberOfGamesPlayed();
        if (!mCurrentGame.isGameOver() && numberOfGamesPlayed > 0)
            numberOfGamesPlayed--;
        int p1Wins = mCurrentGame.getNumberOfWinsForPlayer(1);
        int p2Wins = mCurrentGame.getNumberOfWinsForPlayer(2);
        String p1WinPct = numberOfGamesPlayed  == 0 ? N_A :
                String.format(Locale.US, FORMAT_STRING, (p1Wins/(double)numberOfGamesPlayed)*100);
        String p2WinPct = numberOfGamesPlayed == 0 ? N_A :
                String.format(Locale.US, FORMAT_STRING, (p2Wins/(double)numberOfGamesPlayed)*100);
        tvDataGamesPlayed.setText(String.valueOf(numberOfGamesPlayed));     // don't forget String.valueOf()
        tvDataPlayer1Wins.setText(String.valueOf(p1Wins));
        tvDataPlayer2Wins.setText(String.valueOf(p2Wins));
        tvDataPlayer1WinsPercent.setText(p1WinPct);
        tvDataPlayer2WinsPercent.setText(p2WinPct);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}