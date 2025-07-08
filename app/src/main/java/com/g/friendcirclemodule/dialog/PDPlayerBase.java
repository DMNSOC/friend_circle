package com.g.friendcirclemodule.dialog;

import androidx.media3.exoplayer.ExoPlayer;

public class PDPlayerBase {
    public ExoPlayer exoPlayer;
    public int pos;
    public PDPlayerBase(ExoPlayer exoPlayer, int pos) {
        this.exoPlayer = exoPlayer;
        this.pos = pos;
    }
}
