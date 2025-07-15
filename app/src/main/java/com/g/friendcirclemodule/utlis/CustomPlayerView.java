package com.g.friendcirclemodule.utlis;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.ui.PlayerView;
import com.g.friendcirclemodule.databinding.CustomPlayerControllerBinding;
import com.g.friendcirclemodule.R;
import java.util.Objects;

public class CustomPlayerView extends PlayerView {

    private GestureDetector gestureDetector;
    private boolean isLongPressing = false; // 是否倍速
    private boolean isShowBtn = false; // 按钮是否显示
    private CustomPlayerControllerBinding cpcb;
    private float volume = 0;     // 音量范围 0.0 - 1.0

    public CustomPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public CustomPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        cpcb = CustomPlayerControllerBinding.inflate(LayoutInflater.from(context), this, true);
        gestureDetector = new GestureDetector(context, new GestureListener()); // 手势检测器
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isLongPressing) {
                        isLongPressing = false;
                        isShowBtn = true;
                        Objects.requireNonNull(getPlayer()).setPlaybackParameters(getPlayer().getPlaybackParameters().withSpeed(1));
                        cpcb.speedButton.setText("1.0x");
                    }
                    if (isShowBtn) {
                        isShowBtn = false;
                        cpcb.playPauseButton.setVisibility(View.GONE);
                        cpcb.speedButton.setVisibility(View.GONE);
                    } else {
                        isShowBtn = true;
                        cpcb.playPauseButton.setVisibility(View.VISIBLE);
                        cpcb.speedButton.setVisibility(View.VISIBLE);
                    }
                }
                return gestureDetector.onTouchEvent(event);
            }
        });

        cpcb.playPauseButton.setImageResource(R.mipmap.pause);
        cpcb.playPauseButton.setOnClickListener(v -> {
            if (getPlayer() != null) {
                Player player = getPlayer();
                if (player.getPlaybackState() == Player.STATE_ENDED) {// 播放完重置
                    Objects.requireNonNull(getPlayer()).setPlaybackParameters(getPlayer().getPlaybackParameters().withSpeed(1));
                    player.seekTo(0);
                    player.play();
                    cpcb.speedButton.setText("1.0x");
                    cpcb.playPauseButton.setImageResource(R.mipmap.pause);
                } else if (player.isPlaying()) {
                    player.pause();
                    cpcb.playPauseButton.setImageResource(R.mipmap.play_arrow);
                } else {
                    player.play();
                    cpcb.playPauseButton.setImageResource(R.mipmap.pause);
                }
            }
        });

        // 倍速调节按钮点击事件
        cpcb.speedButton.setOnClickListener(v -> {
            if (getPlayer() != null) {
                float currentSpeed = getPlayer().getPlaybackParameters().speed;
                float newSpeed = currentSpeed >= 7 ? 1 : currentSpeed + 2;
                getPlayer().setPlaybackParameters(getPlayer().getPlaybackParameters().withSpeed(newSpeed));
                cpcb.speedButton.setText(newSpeed + "x");
            }
        });

        cpcb.customSeekBar.setVisibility(View.VISIBLE);
        // 设置自定义进度条的拖动事件
        cpcb.customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Player player = getPlayer();
                if (fromUser && player != null) {
                    long duration = player.getDuration();
                    long newPosition = (duration * progress) / 100;
                    player.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void setPlayer(@Nullable Player player) {
        super.setPlayer(player);

        // 更新进度条的逻辑
        if (player != null) {
            post(updateProgressRunnable);
        }
    }

    private final Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            Player player = getPlayer();
            if (player != null) {
                long duration = player.getDuration();
                long position = player.getCurrentPosition();
                if (duration > 0) {
                    int progress = (int) ((position * 100) / duration);
                    cpcb.customSeekBar.setProgress(progress);
                }
                postDelayed(this, 1000);
            }
        }
    };

    public void setPlaybackStateListener() { // 监听视频播放状态
        if (getPlayer() != null) {
            getPlayer().addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        cpcb.playPauseButton.setImageResource(R.mipmap.forward_media);
                        isShowBtn = true;
                        cpcb.playPauseButton.setVisibility(View.VISIBLE);
                        cpcb.speedButton.setVisibility(View.VISIBLE);
                    }else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                        cpcb.playPauseButton.setImageResource(R.mipmap.pause);
                    } else {
                        cpcb.playPauseButton.setImageResource(R.mipmap.play_arrow);
                    }
                }
            });
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(@NonNull MotionEvent e) { // 长按倍数播放
            isLongPressing = true;
            cpcb.speedButton.setVisibility(View.VISIBLE);
            cpcb.playPauseButton.setVisibility(View.GONE);
            if (getPlayer() != null) {
                getPlayer().setPlaybackParameters(getPlayer().getPlaybackParameters().withSpeed(3));
                cpcb.speedButton.setText("3.0x");
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) { // 判断滑动方向，上下调整音量
            if (Math.abs(distanceX) < Math.abs(distanceY)) {
                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                int streamType = AudioManager.STREAM_MUSIC;
                int maxVolume = audioManager.getStreamMaxVolume(streamType);
                int currentVolume = audioManager.getStreamVolume(streamType);
                if (volume == 0) {
                    volume = (float) currentVolume / maxVolume;
                }
                volume = Math.max(0, Math.min(1, volume + distanceY / getHeight()));
                int newVolume = (int) Math.max(0, Math.min(maxVolume, (volume * maxVolume)));
                audioManager.setStreamVolume(streamType, newVolume, AudioManager.FLAG_SHOW_UI);
            }
            return true;
        }
    }

}