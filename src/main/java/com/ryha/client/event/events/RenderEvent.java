package com.ryha.client.event.events;

import com.ryha.client.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;

public class RenderEvent extends Event {
    private final PoseStack matrices;
    private final float tickDelta;

    public RenderEvent(PoseStack matrices, float tickDelta) {
        this.matrices  = matrices;
        this.tickDelta = tickDelta;
    }

    public PoseStack getMatrices() { return matrices; }
    public float getTickDelta()      { return tickDelta; }
}
