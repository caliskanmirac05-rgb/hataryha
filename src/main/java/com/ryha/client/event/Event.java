package com.ryha.client.event;

/** Base class for all RyHa events. */
public abstract class Event {
    private boolean cancelled = false;

    public boolean isCancelled()         { return cancelled; }
    public void setCancelled(boolean c)  { this.cancelled = c; }
    public void cancel()                 { this.cancelled = true; }
}
