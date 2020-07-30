package com.example.baking.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.baking.R;
import com.example.baking.data.Recipe;
import com.example.baking.data.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import androidx.fragment.app.Fragment;

public class StepFragment extends Fragment {

    TextView textView;
    Button buttonNext;
    Button buttonPrev;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    ProgressBar mProgressBar;


    Recipe mRecipe;
    int mStepNumber;
    long mPosition = 0;

    public StepFragment() {
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setmStepNumber(int number) {
        this.mStepNumber = number;
    }

    OnStepSelectedChangeListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepSelectedChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable("recipe");
            int stepNumberSaved = savedInstanceState.getInt("index");
            //Log.e("step", "saved:" + stepNumberSaved + " propio:" + mStepNumber);
            mStepNumber = stepNumberSaved;
            if (savedInstanceState.containsKey("position")) {
                mPosition = savedInstanceState.getLong("position");
            }
        }


        View rootView = getLayoutInflater().inflate(R.layout.fragment_step, container, false);

        textView = (TextView) rootView.findViewById(R.id.tv_step_description);
        buttonNext = (Button) rootView.findViewById(R.id.next_step_button);
        buttonPrev = (Button) rootView.findViewById(R.id.previous_step_button);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_indicator);

        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        poblateUI(0);


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked(1);
            }
        });

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked(-1);
                // poblateUI(-1);
            }
        });

        return rootView;
    }

    public void clicked(int increment) {

        int recipeNumberSteps = mRecipe.getSteps().size();
        int newStepNumber = mStepNumber + increment;
        newStepNumber = Math.max(0, newStepNumber);
        newStepNumber = Math.min(newStepNumber, recipeNumberSteps - 1);
        //Log.e("fragment", "increment:" + increment + " mStepNumber:" + mStepNumber + " new:" + newStepNumber);
        if (mStepNumber != newStepNumber){
            mStepNumber = newStepNumber;
            //Log.e("stepNumber","stepNumber"+newStepNumber);
            mCallback.onStepSelectedChange(newStepNumber);
        }

    }


    public interface OnStepSelectedChangeListener {
        void onStepSelectedChange(int position);
    }

    void poblateUI(int increment) {
        mPlayerView.setVisibility(View.INVISIBLE);

        if (getResources().getBoolean(R.bool.video_full_screen)) {
            //Log.e("bool", "entro");
            textView.setVisibility( View.INVISIBLE);
            buttonNext.setVisibility(View.INVISIBLE);
            buttonPrev.setVisibility(View.INVISIBLE);

        } else {
            textView.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            buttonPrev.setVisibility(View.VISIBLE);

        }

        int recipeNumberSteps = mRecipe.getSteps().size();
        mStepNumber += increment;
        mStepNumber = Math.max(0, mStepNumber);
        mStepNumber = Math.min(mStepNumber, recipeNumberSteps - 1);
        mCallback.onStepSelectedChange(mStepNumber);


        Step step = mRecipe.getSteps().get(mStepNumber);
        textView.setText(step.getDescription());// + step.getVideoUrl());

        //mStepNumber = step.getId();
       // Log.e("url", step.getVideoUrl());
        buttonPrev.setEnabled(true);
        buttonNext.setEnabled(true);

        if (step.getVideoUrl().equals("")) {
            mPlayerView.setVisibility(View.INVISIBLE);
            //  mExoPlayer.stop();
        } else {
            initializePlayer(Uri.parse(step.getVideoUrl()));
            mProgressBar.setVisibility(View.VISIBLE);
        }

        if (mStepNumber == 0) {
            buttonPrev.setVisibility(View.INVISIBLE);
        } else if (mStepNumber == (recipeNumberSteps - 1)) {
            buttonNext.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
       // Log.e("exo", "init");
        Context context = getContext();
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

        } else {
            mExoPlayer.stop();
        }

        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(context, "baking");
        MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                context, userAgent), new DefaultExtractorsFactory(), null, null);
        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
               // Log.e("exo","playWhenReady "+playWhenReady+" playbackState: "+playbackState);
                if(playWhenReady && playbackState== ExoPlayer.STATE_READY){
                    mProgressBar.setVisibility(View.GONE);
                    mPlayerView.setVisibility(View.VISIBLE);
                    mPlayerView.hideController();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }
        });
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putParcelable("recipe", mRecipe);
        currentState.putInt("index", mStepNumber);
        if (mExoPlayer != null) {
            if (mExoPlayer.getPlaybackState() == ExoPlayer.STATE_READY) {
                currentState.putLong("position", mExoPlayer.getCurrentPosition());
            }
        }
    }
}
