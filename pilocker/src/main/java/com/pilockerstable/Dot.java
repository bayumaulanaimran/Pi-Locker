package com.pilockerstable;

/**
 * Created by MY PC on 30/01/2018.
 */

public class Dot {

    private int drawableId;
    private int sequence;
    private int invisibility;

    public Dot(int drawable, int seq, int visibility){
        drawableId = drawable;
        sequence = seq;
        invisibility = visibility;
    }

    public void setDrawableId(int drawable){
        drawableId = drawable;
    }

    public void setSequence(int seq){
        sequence = seq;
    }

    public void setInvisibility(int invisibility) {
        this.invisibility = invisibility;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public int getSequence() {
        return sequence;
    }

    public int getInvisibility() {
        return invisibility;
    }
}