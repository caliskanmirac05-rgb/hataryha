package com.ryha.client.module.render;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;

public class FullBright extends Module {

    private float savedGamma = 1.0f;

    public FullBright() {
        super("FullBright", "Maximum brightness — no more dark caves", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        RyHaClient.getInstance().getEventBus().registerObject(this);
        if (mc.options != null) {
            savedGamma = (float)(double) mc.options.gamma().getValue();
            mc.options.gamma().setValue(100.0);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.gamma().setValue((double) savedGamma);
        }
    }

    @Subscribe
    public void onTick(TickEvent e) {
        // Persist against any external reset
        if (mc.options != null) mc.options.gamma().setValue(100.0);
    }
}
