package com.ryha.client.module.setting;

/**
 * Generic setting base. Each module declares its own settings; the ClickGUI
 * reads them reflectively and renders an appropriate control.
 */
public abstract class Setting<T> {
    private final String name;
    private final String description;
    protected T value;

    protected Setting(String name, String description, T defaultValue) {
        this.name        = name;
        this.description = description;
        this.value       = defaultValue;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public T      getValue()       { return value; }
    public void   setValue(T v)    { this.value = v; }

    /** Serialise to string for config persistence. */
    public abstract String serialise();
    /** Deserialise from string. */
    public abstract void deserialise(String raw);
}
