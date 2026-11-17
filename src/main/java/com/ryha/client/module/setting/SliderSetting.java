package com.ryha.client.module.setting;

public class SliderSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public SliderSetting(String name, String description,
                         double defaultValue, double min, double max, double step) {
        super(name, description, defaultValue);
        this.min  = min;
        this.max  = max;
        this.step = step;
    }

    public double getMin()  { return min; }
    public double getMax()  { return max; }
    public double getStep() { return step; }

    @Override
    public void setValue(Double v) {
        this.value = Math.max(min, Math.min(max, v));
    }

    public int intValue() { return value.intValue(); }

    @Override public String serialise()           { return value.toString(); }
    @Override public void   deserialise(String s) {
        try { setValue(Double.parseDouble(s)); } catch (NumberFormatException ignored) {}
    }
}
