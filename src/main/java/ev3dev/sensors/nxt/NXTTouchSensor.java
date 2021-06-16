package ev3dev.sensors.nxt;

import ev3dev.sensors.BaseSensor;
import ev3dev.sensors.GenericMode;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.Touch;

public class NXTTouchSensor extends BaseSensor implements Touch {
    // Private Static Fields

    private static final String LEGO_NXT_ANALOG_SENSOR = "nxt-analog";

    // Public Constructors

    public NXTTouchSensor(final Port port) {
        super(port, LEGO_NXT_ANALOG_SENSOR);
        setModes(new SensorMode[] {
            new GenericMode(this.PATH_DEVICE, 1, "Touch")}
        );
    }

    // Public Methods

    public SensorMode getTouchMode() {
        return getMode(0);
    }

    // Touch Override Methods

    @Override
    public boolean isPressed() {
        float[] sample = new float[1];
        getTouchMode().fetchSample(sample, 0);
        return sample[0] != 0.0f;
    }
}