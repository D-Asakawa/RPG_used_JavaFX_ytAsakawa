package com.ytasakawa.rpg_used_javafx_ytasakawa;

public class SpriteData {
    private final int frameWidth;
    private final int frameHeight;
    private final int numFrames;
    private final int columns;

    public SpriteData(int frameWidth, int frameHeight, int numFrames, int columns) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.numFrames = numFrames;
        this.columns = columns;
    }

    public int getFrameWidth() { return frameWidth; }
    public int getFrameHeight() { return frameHeight; }
    public int getNumFrames() { return numFrames; }
    public int getColumns() { return columns; }
}
