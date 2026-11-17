package com.ryha.client.event.events;

import com.ryha.client.event.Event;
import net.minecraft.client.Minecraft;

public class TickEvent extends Event {
    private final Minecraft client;
    public TickEvent(Minecraft client) { this.client = client; }
    public Minecraft getClient()       { return client; }
}
