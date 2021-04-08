package robotics;

import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.utility.Delay;
import utilities.LCDUtilities;

/**
 * A subclass of LeJOS' {@code WheeledChassis} object which uses an {@code EV3GyroSensor} object to measure angles.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class GyroWheeledChassis extends WheeledChassis {
	// Private Classes
	
	/**
	 * A {@code Thread} subclass which is used to monitor the heading of the associated {@code GyroWheeledChassis} object, adjusting {@code Wheel} speeds to correct it to match the desired heading if necessary.
	 */
	private class HeadingCorrectingMonitor extends Thread {
		// Private Fields
		
		/**
		 * Whether the thread should be suspended.
		 */
		private boolean suspended;
		
		/**
		 * Record the number of active suspensions;
		 */
		private int suspensionCount;
		
		// Public Constructors
		
		/**
		 * Creates a {@code HeadingCorrectingMonitor} thread.
		 */
		public HeadingCorrectingMonitor() {
			setDaemon(true);
		}
		
		// Private Methods
		
		/**
		 * Sets given {@code Wheel}s' speeds to a given linear speed.
		 * 
		 * @param wheels The {@code Wheel}s whose speeds are to be set.
		 * @param speed The linear speed to set.
		 */
		private void setLinearWheelSpeeds(List<Wheel> wheels, double speed) {
		    master.startSynchronization();
			for (Wheel wheel : wheels) {
				wheel.getMotor().setSpeed((int)(speed * Math.abs(wheel.getFactors().get(0, 0))));
			}
		    master.endSynchronization();
		}
		
		/**
		 * Adjusts the {@code GyroWheeledChassis}' {@code Wheel}s' motor speeds to correct the heading to match the desired heading.
		 * 
		 * @param deviationDirection The direction of the deviation of the heading from the desired heading:
		 * <ul>
		 *     <li>< 0 if < the desired heading,</li>
		 *     <li>> 0 if > the desired heading,</li>
		 *     <li>otherwise 0.</li>
		 * </ul>
		 */
		private void adjustMotorSpeeds(int deviationDirection) {
			if (deviationDirection != 0) {
				setLinearWheelSpeeds(leftWheels, linearSpeed * (deviationDirection > 0 ? .9 : 1)); // Reduce left wheels' speeds if > 0, otherwise restore.
				setLinearWheelSpeeds(rightWheels, linearSpeed * (deviationDirection < 0 ? .9 : 1)); // Reduce right wheels' speeds if < 0, otherwise restore.
			} else {
				setLinearWheelSpeeds(allWheels, linearSpeed); // Restore all wheels' original speeds.
			}
		}
		
		/**
		 * Rotates the {@code GyroWheeledChassis} to correct the heading to match the desired heading if needed.
		 * 
		 * @param deviationDirection The direction of the deviation of the heading from the desired heading:
		 * <ul>
		 *     <li>< 0 if < the desired heading,</li>
		 *     <li>> 0 if > the desired heading,</li>
		 *     <li>otherwise 0.</li>
		 * </ul>
		 */
		private void rotateIfNeeded(int deviationDirection) {
			if (deviationDirection != 0) {
				rotate(0, 10);
			}
		}
		
		// Public Methods
		
		/**
		 * Suspends the run loop.
		 */
		public void suspendRunLoop() {
			suspended = true;
			suspensionCount++;
		}
		
		/**
		 * Resumes the run loop.
		 */
		public void resumeRunLoop() {
			if (suspensionCount > 0 && --suspensionCount == 0) {
				synchronized(this) {
					suspended = false;
					notify();
				}
			}
		}

		// Override Methods
		
		@Override
		public void run() {
			while (true) {
				try {
					synchronized(this) {
	                    while (suspended) {
	                        wait();
	                    }
	                }
				} catch (InterruptedException e) {}
				
				int deviationDirection = Integer.compare(getGyroHeading(), desiredHeading);
				switch (currentMoveType) {
				case TRAVEL:
					// Adjust the motor speeds if the chassis is moving…
					if (isMoving()) {
						adjustMotorSpeeds(deviationDirection);
						break;
					}
				case STOP:
					// …otherwise rotate if needed.
					rotateIfNeeded(deviationDirection);
					break;
				default:
				}
			}
		}
	}
	
	// Public Enumerations
	
	/**
	 * An enumeration to represent all possible orientations of an {@code EV3GyroSensor} object.
	 */
	public enum GyroOrientation {
		GLYPH_TOP(-1),
		GLYPH_BOTTOM(1);
		
		// Public Fields
		
		/**
		 * The multiplier associated with the current orientation.
		 */
		public final double multiplier;
		
		// Constructors
		
		/**
		 * Creates a {@code GyroOrientation} instance.
		 * 
		 * @param multiplier The multiplier associated with the orientation.
		 */
		GyroOrientation(double multiplier) {
			this.multiplier = multiplier;
		}
	}
	
	
	// Private Fields
	
	/**
	 * The type of the current move.
	 */
	private Move.MoveType currentMoveType = Move.MoveType.STOP;
	
	/**
	 * The desired heading of the {@code EV3GyroSensor}.
	 */
	private int desiredHeading = 0;
	
	/**
	 * The {@code EV3GyroSensor} used to measure angles.
	 */
	private EV3GyroSensor gyro;
	
	/**
	 * The {@code SampleProvider} for the {@code EV3GyroSensor}.
	 */
	private SampleProvider gyroAngleProvider;
	
	/**
	 * A multiplier set when calling {@code calibrateGyroSensor()} which is used for correcting the heading received from the {@code EV3GyroSensor}.
	 */
	private double gyroHeadingCalibrationMultiplier = 1;
	
	/**
	 * The orientation of the {@code EV3GyroSensor}.
	 */
	private GyroOrientation gyroOrientation;
	
	/**
	 * The {@code Thread} running the {@code HeadingCorrectingMonitor}.
	 */
	private HeadingCorrectingMonitor headingCorrectingMonitor;
	
	/**
	 * A list of all {@code Wheel}s on the chassis.
	 */
	private List<Wheel> allWheels = new ArrayList<>();
	
	/**
	 * A list of {@code Wheel}s on the left of the chassis.
	 */
	private List<Wheel> leftWheels = new ArrayList<>();
	
	/**
	 * A list of {@code Wheel}s on the right of the chassis.
	 */
	private List<Wheel> rightWheels = new ArrayList<>();
	
	// Public Constructors

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

		// Set up wheel lists.
		for (Wheel wheel : wheels) {
			double offsetSign = wheel.getFactors().get(0, 2) * -1;
			if (offsetSign > 0) {
				rightWheels.add(wheel);
			} else if (offsetSign < 0) {
				leftWheels.add(wheel);
			}
			allWheels.add(wheel);
		}
	    
		// Set up heading and monitoring.
		desiredHeading = getGyroHeading();
		headingCorrectingMonitor = this.new HeadingCorrectingMonitor();
		headingCorrectingMonitor.start();
	}
	
	// Private Methods

	/**
	 * Rotates the chassis a given angle with a given speed.
	 * 
	 * @param angle The angle to rotate.
	 * @param speed The speed to rotate.
	 * @param level The level of recursion.
	 */
	private void _rotate(double angle, double speed, int level) {
	    setVelocity(0, Math.signum(angle) * (level > 0 ? Math.min(Math.abs(angle), 2) : speed));
		if (Double.isInfinite(angle)) {
			return;
		}
		int currentHeading;
		while ((currentHeading = getGyroHeading()) != desiredHeading) {
			if (Math.signum(angle) != Math.signum(Integer.compare(desiredHeading, currentHeading))) {
				break;
			}
		}
		stop();
		Delay.msDelay(level);
		currentHeading = getGyroHeading();
		double deviation = currentHeading - desiredHeading;
		if (deviation != 0) {
			_rotate(-deviation, speed, level + 1);
		}
	}

	/**
	 * Rotates the chassis a given angle with a given speed.
	 * 
	 * @param angle The angle to rotate.
	 * @param speed The speed to rotate.
	 */
    private void rotate(double angle, double speed) {
		headingCorrectingMonitor.suspendRunLoop();
		
		currentMoveType = Move.MoveType.ROTATE;
		desiredHeading += angle;
		_rotate(angle, speed, 0);
		
		headingCorrectingMonitor.resumeRunLoop();
    }
	
	// Public Methods
	
	/**
	 * Resets and calibrates the {@code EV3GyroSensor}.<br/><br/>
	 * 
	 * <b>This method requires user interaction.</b>
	 */
	public void calibrateGyroSensor() {
		headingCorrectingMonitor.suspendRunLoop();
		
		LCDUtilities.drawLongString("Please point the EV3 in the desired direction of 0 degrees, then press ENTER to begin gyro caibration.");
		Button.ENTER.waitForPressAndRelease();
		LCD.clear();
		
		LCDUtilities.drawLongString("Rotating the EV3 720 degrees...");
		resetGyroSensor();
		gyroHeadingCalibrationMultiplier = 1;
		rotate(720, 10);
		
		LCDUtilities.drawLongString("Please position the EV3 so that it points in the desired direction of 0 degrees, then press ENTER to complete calibration.");
		Button.ENTER.waitForPressAndRelease();
		LCD.clear();
		
		gyroHeadingCalibrationMultiplier = 720 / (double)getGyroHeading();
		resetGyroSensor();

		headingCorrectingMonitor.resumeRunLoop();
	}
	
	/**
	 * Fetches a sample from the {@code EV3GyroSensor}.
	 * 
	 * @return The current heading of the {@code EV3GyroSensor}.
	 */
	public int getGyroHeading() {
		float[] angle = new float[1];
		gyroAngleProvider.fetchSample(angle, 0);
		return (int)(gyroOrientation.multiplier * gyroHeadingCalibrationMultiplier * (double)angle[0]);
	}
    
	/**
	 * Resets the {@code EV3GyroSensor}.
	 */
    public void resetGyroSensor() {
		gyro.reset();
		desiredHeading = 0;
    }
	
	// Override Methods
	
	@Override
    public void rotate(double angle) {
		headingCorrectingMonitor.suspendRunLoop();
		
		currentMoveType = Move.MoveType.ROTATE;
		desiredHeading += angle;
		_rotate(angle, angularSpeed, 0);
		
		headingCorrectingMonitor.resumeRunLoop();
    }
	
	@Override
	public void stop() {
		currentMoveType = Move.MoveType.STOP;
		super.stop();
	}
	
	@Override
	public void travel(double distance) {
		currentMoveType = Move.MoveType.TRAVEL;
		super.travel(distance);
	}
	
	@Override
    public void arc(double radius, double angle) {
		headingCorrectingMonitor.suspendRunLoop();
		
		currentMoveType = Move.MoveType.ARC;
		if (angle == 0) {
			headingCorrectingMonitor.resumeRunLoop();
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
    		headingCorrectingMonitor.resumeRunLoop();
            return;
        } else if (radius == 0) {
            rotate(angle);
    		headingCorrectingMonitor.resumeRunLoop();
            return;
        }
		System.err.printf("Warning: GyroWheeledChassis.arc(double radius, double angle) where radius (%f) != 0 is unimplemented; calling WheeledChassis.arc(radius, angle), which does not use an EV3GyroSensor.", radius);
		super.arc(radius, angle);
		
		headingCorrectingMonitor.resumeRunLoop();
    }
	
}
