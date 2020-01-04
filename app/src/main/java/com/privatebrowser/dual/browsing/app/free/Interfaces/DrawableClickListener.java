package com.privatebrowser.dual.browsing.app.free.Interfaces;

public interface DrawableClickListener {

    enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    void onClick(DrawablePosition target);
}
