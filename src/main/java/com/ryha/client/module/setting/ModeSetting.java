package com.ryha.client.module.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String name, String description, String defaultMode, String... modes) {
        super(name, description, defaultMode);
        this.modes = Arrays.asList(modes);
    }

    public List<String> getModes() { return modes; }

    public boolean is(String mode) { return value.equalsIgnoreCase(mode); }

    public void next() {
        int idx = modes.indexOf(value);
        value   = modes.get((idx + 1) % modes.size());
    }

    @Override public String serialise()           { return value; }
    @Override public void   deserialise(String s) {
        if (modes.contains(s)) value = s;
    }
}
