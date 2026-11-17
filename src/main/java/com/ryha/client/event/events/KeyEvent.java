package com.ryha.client.event.events;

import com.ryha.client.event.Event;

public class KeyEvent extends Event {
    private final int key;
    private final int action; // GLFW constants: 0 = release, 1 = press, 2 = repeat
    private final int mods;

    public KeyEvent(int key, int action, int mods) {
        this.key    = key;
        this.action = action;
        this.mods   = mods;
    }

    public int  getKey()    { return key; }
    public int  getAction() { return action; }
    public int  getMods()   { return mods; }
    public boolean isPress(){ return action == 1; }
}
