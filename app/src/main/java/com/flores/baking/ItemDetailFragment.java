package com.flores.baking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flores.baking.data.model.Step;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import static com.flores.baking.ItemDetailActivity.ARG_ITEM_POSITION;
import static com.flores.baking.ItemListActivity.ARG_RECIPE;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements ExoPlayer.EventListener {

    static final String ARG_ITEM_POSITION_PREVIOUS = "previous_item_position";

    /**
     * The fragment argument representing the item that this fragment
     * represents.
     */
    static final String ARG_ITEM = "item_object";
    static final String ARG_ITEM_POSITION_NEXT = "next_item_position";
    private static final String LOG_TAG = ItemDetailFragment.class.getSimpleName();

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Step mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(ARG_ITEM)) {
            mItem = (Step) getArguments().getSerializable(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        //Control navigation buttons
        View previous = rootView.findViewById(R.id.bt_previous);
        View next = rootView.findViewById(R.id.bt_next);
        if (getArguments().containsKey(ARG_ITEM_POSITION_PREVIOUS)) {
            Log.d(LOG_TAG, "Previous " + getArguments().getInt(ARG_ITEM_POSITION_PREVIOUS));
            if (previous != null) {
                previous.setOnClickListener(listener -> {
                    Intent intent = new Intent(requireContext(), ItemDetailActivity.class);
                    intent.putExtra(ARG_ITEM_POSITION, getArguments().getInt(ARG_ITEM_POSITION_PREVIOUS));
                    intent.putExtra(ARG_RECIPE, getArguments().getSerializable(ARG_RECIPE));
                    requireContext().startActivity(intent);
                });
            }
        } else {
            if (previous != null) {
                previous.setVisibility(View.INVISIBLE);
            }
        }
        if (getArguments().containsKey(ARG_ITEM_POSITION_NEXT)) {
            Log.d(LOG_TAG, "Next " + getArguments().getInt(ARG_ITEM_POSITION_NEXT));
            if (next != null) {
                next.setOnClickListener(listener -> {
                    Intent intent = new Intent(requireContext(), ItemDetailActivity.class);
                    intent.putExtra(ARG_ITEM_POSITION, getArguments().getInt(ARG_ITEM_POSITION_NEXT));
                    intent.putExtra(ARG_RECIPE, getArguments().getSerializable(ARG_RECIPE));
                    requireContext().startActivity(intent);
                });
            }
        } else {
            if (next != null) {
                next.setVisibility(View.INVISIBLE);
            }
        }

        if (mItem != null) {
            TextView itemDetail = rootView.findViewById(R.id.item_detail);
            if (itemDetail != null) itemDetail.setText(mItem.getDescription());

            // Initialize the player view.
            mPlayerView = rootView.findViewById(R.id.playerView);

            if (!TextUtils.isEmpty(mItem.getVideoURL())) {
                // Initialize the Media Session.
                initializeMediaSession();

                // Initialize the player.
                initializePlayer(Uri.parse(mItem.getVideoURL()));
            } else {
                ImageView videoNotFound = rootView.findViewById(R.id.iv_video_not_found);
                mPlayerView.setVisibility(View.GONE);
                videoNotFound.setVisibility(View.VISIBLE);

                // Load Thumbnail image if it exists
                if (!TextUtils.isEmpty(mItem.getThumbnailURL())) {
                    Picasso.get()
                            .load(mItem.getThumbnailURL())
                            .placeholder(R.drawable.ic_video_not_found)
                            .into(videoNotFound);
                }
            }
        }

        return rootView;

    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(requireContext(), LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(requireContext(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    requireContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if (mMediaSession != null) mMediaSession.setActive(false);
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    // ExoPlayer Event Listeners

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }


    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

}
