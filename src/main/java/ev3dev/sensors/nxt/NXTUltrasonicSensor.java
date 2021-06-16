package ev3dev.sensors.nxt;

import ev3dev.sensors.BaseSensor;
import ev3dev.sensors.GenericMode;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

/**
 * An implementation of LeJOS' {@code NXTUltrasonicSensor} within the EV3Dev context.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class NXTUltrasonicSensor extends BaseSensor {
    // Private Static Fields

    private static final String LEGO_NXT_US = "lego-nxt-us";
    private static final String MODE_DISTANCE = "US-DIST-CM";
    private static final String MODE_PING = "US-LISTEN";
    private static final String MODE_SINGLE_MEASURE = "US-SI-CM";

    // Public Static Fields

    public static float MIN_RANGE = 5f;
    public static float MAX_RANGE = 255f;

    // Public Constructors

    public NXTUltrasonicSensor(final Port port) {
        super(port, LEGO_I2C, LEGO_NXT_US);

        setModes(new SensorMode[] {
            new GenericMode(this.PATH_DEVICE, 1, "Distance", MIN_RANGE, MAX_RANGE, 1f),
            new GenericMode(this.PATH_DEVICE, 1, "Ping", MIN_RANGE, MAX_RANGE, 1f)
        });
    }

    // Public Methods

    public void enable() {
        switchMode(MODE_DISTANCE, SWITCH_DELAY);
    }

    public void disable() {
        switchMode(MODE_SINGLE_MEASURE, SWITCH_DELAY);
    }

    public SampleProvider getDistanceMode() {
        switchMode(MODE_DISTANCE, SWITCH_DELAY);
        return getMode(0);
    }

    public SampleProvider getPingMode() {
        switchMode(MODE_PING, SWITCH_DELAY);
        return getMode(1);
    }

    public boolean isEnabled() {
        return !getSystemMode().equals(MODE_SINGLE_MEASURE);
    }
}
