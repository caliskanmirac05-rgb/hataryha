package com.ryha.client.module.setting;

import org.lwjgl.glfw.GLFW;

public class KeybindSetting extends Setting<Integer> {
    public KeybindSetting(String name, String description, int defaultKey) {
        super(name, description, defaultKey);
    }

    public boolean isNone()          { return value == GLFW.GLFW_KEY_UNKNOWN; }
    public String  getKeyName()      { return GLFW.glfwGetKeyName(value, 0) != null
                                              ? GLFW.glfwGetKeyName(value, 0).toUpperCase()
                                              : "KEY_" + value; }

    @Override public String serialise()           { return value.toString(); }
    @Override public void   deserialise(String s) {
        try { value = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
    }
}
