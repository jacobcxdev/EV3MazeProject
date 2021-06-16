package com.jacobcxdev.ev3mazeproject.hardware;

import com.jacobcxdev.ev3mazeproject.sensors.BaseGyroSensor;
import com.jacobcxdev.ev3mazeproject.sensors.BaseTouchSensor;
import com.jacobcxdev.ev3mazeproject.sensors.BaseUltrasonicSensor;
import ev3dev.actuators.lego.motors.BaseRegulatedMotor;

import java.lang.reflect.InvocationTargetException;

/**
 * A class used to store the configuration of the ports of the EV3.
 *
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class PortConfiguration {
    // Public Fields

    /**
     * The {@code BaseGyroSensor} used to measure angles for the EV3.
     */
    public final BaseGyroSensor gyroSensor;

    /**
     * The {@code BaseTouchSensor} used to detect when an object is in front of the EV3.
     */
    public final BaseTouchSensor touchSensor;

    /**
     * The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
     */
    public final BaseUltrasonicSensor leftUltrasonicSensor;

    /**
     * The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its right.
     */
    public final BaseUltrasonicSensor rightUltrasonicSensor;

    /**
     * The {@code BaseRegulatedMotor} used to control the left track.
     */
    public final BaseRegulatedMotor leftRegulatedMotor;

    /**
     * The {@code BaseRegulatedMotor} used to control the right track.
     */
    public final BaseRegulatedMotor rightRegulatedMotor;

    /**
     * The {@code BaseRegulatedMotor} used to control the claw.
     */
    public final BaseRegulatedMotor clawRegulatedMotor;

    // Public Constructors

    /**
     * Creates a {@code PortConfiguration} object.
     *
     * @param gyroSensor The {@code SensorDefinition} for the gyroscopic sensor, which is used to measure angles for the EV3.
     * @param touchSensor The {@code SensorDefinition} for the touch sensor, which is used to detect when an object is in front of the EV3.
     * @param leftUltrasonicSensor The {@code SensorDefinition} for the left ultrasonic sensor, which is used to measure the distance between the EV3 and the nearest object on its left.
     * @param rightUltrasonicSensor The {@code SensorDefinition} for the right ultrasonic sensor, which is used to measure the distance between the EV3 and the nearest object on its right.
     * @param leftLargeRegulatedMotor The {@code MotorDefinition} for the left large regulated motor, which is used to control the left track.
     * @param rightLargeRegulatedMotor The {@code MotorDefinition} for the right large regulated motor, which is used to control the right track.
     * @param clawMediumRegulatedMotor The {@code MotorDefinition} for the claw's medium regulated motor, which is used to control the claw.
     */
    public PortConfiguration(SensorDefinition gyroSensor, SensorDefinition touchSensor, SensorDefinition leftUltrasonicSensor, SensorDefinition rightUltrasonicSensor, RegulatedMotorDefinition leftLargeRegulatedMotor, RegulatedMotorDefinition rightLargeRegulatedMotor, RegulatedMotorDefinition clawMediumRegulatedMotor) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.gyroSensor = (BaseGyroSensor)gyroSensor.newPartInstance();
        this.touchSensor = (BaseTouchSensor)touchSensor.newPartInstance();
        this.leftUltrasonicSensor = (BaseUltrasonicSensor)leftUltrasonicSensor.newPartInstance();
        this.rightUltrasonicSensor = (BaseUltrasonicSensor)rightUltrasonicSensor.newPartInstance();
        this.leftRegulatedMotor = (BaseRegulatedMotor)leftLargeRegulatedMotor.newPartInstance();
        this.rightRegulatedMotor = (BaseRegulatedMotor)rightLargeRegulatedMotor.newPartInstance();
        this.clawRegulatedMotor = (BaseRegulatedMotor)clawMediumRegulatedMotor.newPartInstance();
    }
}
