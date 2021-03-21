import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.utility.Matrix;

/**
 * A subclass of LeJOS' {@code WheeledClass} object which uses an {@code EV3GyroSensor} object to measure angles.
 * @author <a href="https://github.com/jacobcxdev">JacobCXDev</a>
 */
public class GyroWheeledChassis extends WheeledChassis {
	
	/**
	 * An enumeration to hold all possible orientations of an {@code EV3GyroSensor} object.
	 */
	public enum GyroOrientation {
		GLYPH_TOP(-1),
		GLYPH_BOTTOM(1);
		
		/**
		 * The multiplier associated with the current orientation.
		 */
		public final float multiplier;
		
		/**
		 * Creates a {@code GyroOrientation} instance.
		 * 
		 * @param multiplier The multiplier associated with the orientation.
		 */
		private GyroOrientation(float multiplier) {
			this.multiplier = multiplier;
		}
	}
	
	/**
	 * The {@code EV3GyroSensor} used to measure angles.
	 */
	private EV3GyroSensor gyro;
	
	/**
	 * The {@code SampleProvider} for the {@code EV3GyroSensor}.
	 */
	private SampleProvider gyroAngleProvider;
	
	/**
	 * The orientation of the {@code EV3GyroSensor}.
	 */
	private GyroOrientation gyroOrientation;

	/**
	 * Creates a {@code GyroWheeledChassis} object.
	 * 
	 * @param wheels The wheels associated with the chassis.
	 * @param dim The chassis type.
	 * @param gyro The {@code EV3GyroSensor} used to measure angles. 
	 * @param gyroOrientation The orientation of the {@code EV3GyroSensor}.
	 */
	public GyroWheeledChassis(Wheel[] wheels, int dim, EV3GyroSensor gyro, GyroOrientation gyroOrientation) {
		super(wheels, dim);
		this.gyro = gyro;
		this.gyroAngleProvider = gyro.getAngleMode();
		this.gyroOrientation = gyroOrientation;
	}
	
	/**
	 * Fetches a sample from the {@code EV3GyroSensor}.
	 * 
	 * @return The current angle of the {@code EV3GyroSensor}.
	 */
	public float getGyroAngle() {
		float[] angle = new float[1];
		gyroAngleProvider.fetchSample(angle, 0);
		return gyroOrientation.multiplier * angle[0];
	}
	
	@Override
    public void rotate(double angle) {
		if (angle == 0) {
			return;
		}
		float start = getGyroAngle();
        setVelocity(0, Math.signum(angle) * angularSpeed);
		if (Double.isInfinite(angle)) {
			return;
		}
		while (angle > 0 ? getGyroAngle() - start < angle : getGyroAngle() - start > angle) {}
		stop();
    }
	
	// TODO: Use gyro in arc method.
	@Override
    public void arc(double radius, double angle) {
        if (angle == 0) {
        	return;
        }
        double ratio = Math.abs(Math.PI * radius / 180); // The ratio between linear and angular speed that corresponds with the radius.
        if (Double.isInfinite(angle)) {
            // Decrease one speed component so that the speed components have the calculated ratio then call the travel method.
            if (ratio > 1) {
                setVelocity(Math.signum(angle) * linearSpeed, 0, Math.signum(radius) * linearSpeed / ratio);
            } else {
                setVelocity(Math.signum(angle) * angularSpeed * ratio, 0, Math.signum(radius) * angularSpeed);
            }
        } else if (radius == 0) {
            rotate(angle);
        } else if (Double.isInfinite(radius)) {
            travel(angle < 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
        } else {
            Matrix displacement = toMatrix(Math.signum(angle) * 2 * Math.PI * Math.abs(radius) * Math.abs(angle) / 360, 0, Math.signum(radius) * angle); // A matrix holding the linear and angular distance matching the specified arc.
            Matrix tSpeed;
            Matrix tAcceleration;
            // Decrease one speed component so that the speed components have the calculated ratio.
            if (ratio > 1) {
                tSpeed = toMatrix(linearSpeed, 0, linearSpeed / ratio);
                tAcceleration = toMatrix(linearAcceleration, 0, linearAcceleration / ratio);
            } else {
                tSpeed = toMatrix(angularSpeed * ratio, 0, angularSpeed);
                tAcceleration = toMatrix(angularAcceleration * ratio, 0, angularAcceleration);
            }
            Matrix motorDelta = forward.times(displacement); // The displacement of the motors from robot displacement.
            Matrix mRatio = motorDelta.times(1 / getMax(motorDelta)); // The ratio between motor displacements when the largest displacement is set to 1.
            Matrix motorSpeed = mRatio.times(getMax(forwardAbs.times(tSpeed))); // The speed of the motors.
            Matrix motorAcceleration = mRatio.times(getMax(forwardAbs.times(tAcceleration))); // The acceleration of the motors.
            setMotors(motorDelta, motorSpeed, motorAcceleration);
        }
    }

}
