package com.ad.sdk.adserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Util;

public class YeahInArticleVideoAds extends AppCompatActivity implements Player.Listener {

    private ExoPlayer player;
    private ImaAdsLoader adsLoader;

    StyledPlayerView playerView;

    public YeahInArticleVideoAds() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("InArticleVideo", MODE_PRIVATE);
        String ad_check = sharedPreferences.getString("adCheck", "0");

        if (ad_check.equalsIgnoreCase("1")) {
            loadAd(playerView, getApplicationContext());
        } else {
            Log.e("Ad Shown Status :", "Targeting Not Match");
        }
    }

    public void loadAd(StyledPlayerView playerView, Context context) {
        MultiDex.install(context);
        playerView.setControllerAutoShow(false);
        // Create an AdsLoader.
        adsLoader = new ImaAdsLoader.Builder(context).build();
        initializePlayer(context, playerView);
    }


    public void initializePlayer(Context context, StyledPlayerView playerView) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("InArticleVideo", MODE_PRIVATE);
        String ad_url = sharedPreferences.getString("InArticleVideo_URL", "");

        this.playerView = playerView;
        // Set up the factory for media sources, passing the ads loader and ad view providers.
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);

        MediaSourceFactory mediaSourceFactory =
                new DefaultMediaSourceFactory(dataSourceFactory)
                        .setAdsLoaderProvider(unusedAdTagUri -> adsLoader)
                        .setAdViewProvider(playerView);

        // Create an ExoPlayer and set it as the player for content and ads.
        player = new ExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build();
        playerView.setPlayer(player);
        adsLoader.setPlayer(player);
        Uri contentUri = Uri.parse("test");
        Uri adTagUri = Uri.parse(ad_url);
        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setUri(contentUri)
                        .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(adTagUri).build())
                        .build();

        // Prepare the content and ad to be played with the SimpleExoPlayer.
        player.setMediaItem(mediaItem);
        player.prepare();

        // Set PlayWhenReady. If true, content and ads will autoplay.
        player.setPlayWhenReady(true);
        player.addListener(this);


    }


    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (isPlaying) {
            playerView.setVisibility(View.VISIBLE);
        } else {
            playerView.setVisibility(View.GONE);

        }
    }

//    private void releasePlayer() {
//        adsLoader.setPlayer(null);
//        playerView.setPlayer(null);
//        player.release();
//        player = null;
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.SDK_INT > 23) {
//            if (playerView != null) {
//                playerView.onResume();
//                YeahLoadInterstitial.int_show_listener.onYeahAdsShowStart();
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (Util.SDK_INT <= 23) {
////            initializePlayer();
//            if (playerView != null) {
//                playerView.onResume();
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (Util.SDK_INT <= 23) {
//            if (playerView != null) {
//                playerView.onPause();
//            }
////            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23) {
//            if (playerView != null) {
//                playerView.onPause();
//            }
//            releasePlayer();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        adsLoader.release();
//    }

}



