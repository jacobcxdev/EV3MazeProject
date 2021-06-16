package com.jacobcxdev.ev3mazeproject.robotics;

import com.jacobcxdev.ev3mazeproject.sensors.BaseGyroSensor;
import ev3dev.actuators.Sound;
import ev3dev.sensors.Button;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.utility.Delay;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A subclass of LeJOS' {@code WheeledChassis} object which uses a {@code BaseGyroSensor} object to measure angles.
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
//		    master.startSynchronization();
			for (Wheel wheel : wheels) {
				wheel.getMotor().setSpeed((int)(speed * Math.abs(wheel.getFactors().get(0, 0))));
			}
//		    master.endSynchronization();
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
				setLinearWheelSpeeds(leftWheels, linearSpeed * (deviationDirection > 0 ? .8 : 1)); // Reduce left wheels' speeds if > 0, otherwise restore.
				setLinearWheelSpeeds(rightWheels, linearSpeed * (deviationDirection < 0 ? .8 : 1)); // Reduce right wheels' speeds if < 0, otherwise restore.
			} else {
				setLinearWheelSpeeds(allWheels, linearSpeed); // Restore all wheels' original speeds.
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
				synchronized (this) {
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
					synchronized (this) {
	                    while (suspended) {
	                        wait();
	                    }
	                }
				} catch (InterruptedException ignored) {}

				var deviationDirection = Integer.compare(getGyroHeading(), desiredHeading);
				if (currentMoveType == Move.MoveType.TRAVEL) { // Adjust the motor speeds if the chassis is moving…
					if (isMoving()) {
						adjustMotorSpeeds(deviationDirection);
					}
				}
			}
		}
	}

	// Private Static Fields

	/**
	 * The {@code Logger} for {@code GyroWheeledChassis} instances.
	 */
	private static final Logger log = LoggerFactory.getLogger(GyroWheeledChassis.class);
	
	// Private Fields
	
	/**
	 * The {@code BaseGyroSensor} used to measure angles.
	 */
	private final BaseGyroSensor gyro;
	
	/**
	 * The {@code SampleProvider} for the {@code BaseGyroSensor}.
	 */
	private final SampleProvider gyroAngleProvider;
	
	/**
	 * The {@code Thread} running the {@code HeadingCorrectingMonitor}.
	 */
	private final HeadingCorrectingMonitor headingCorrectingMonitor;
	
	/**
	 * A list of all {@code Wheel}s on the chassis.
	 */
	private final List<Wheel> allWheels = new ArrayList<>();
	
	/**
	 * A list of {@code Wheel}s on the left of the chassis.
	 */
	private final List<Wheel> leftWheels = new ArrayList<>();
	
	/**
	 * A list of {@code Wheel}s on the right of the chassis.
	 */
	private final List<Wheel> rightWheels = new ArrayList<>();

	/**
	 * The type of the current move.
	 */
	private Move.MoveType currentMoveType = Move.MoveType.STOP;

	/**
	 * The desired heading of the {@code BaseGyroSensor}.
	 */
	private int desiredHeading;

	/**
	 * A multiplier set when calling {@code calibrateGyroSensor()} which is used for correcting the heading received from the {@code BaseGyroSensor}.
	 */
	private double gyroHeadingCalibrationMultiplier = 1;
	
	// Public Constructors

	/**
	 * Creates a {@code GyroWheeledChassis} object.
	 * 
	 * @param wheels The wheels associated with the chassis.
	 * @param dim The chassis type.
	 * @param gyro The {@code BaseGyroSensor} used to measure angles.
	 */
	public GyroWheeledChassis(Wheel[] wheels, int dim, BaseGyroSensor gyro) {
		super(wheels, dim);
		this.gyro = gyro;
		this.gyroAngleProvider = gyro.getAngleMode();

		// Set up wheel lists.
		for (Wheel wheel : wheels) {
			var offsetSign = wheel.getFactors().get(0, 2) * -1;
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
	    setVelocity(0, Math.signum(angle) * (level > 0 ? Math.min(Math.abs(angle), 10) : speed));
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
		var deviation = currentHeading - desiredHeading;
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
	 * Resets and calibrates the {@code BaseGyroSensor}.<br/><br/>
	 * 
	 * <b>This method requires user interaction.</b>
	 */
	public void calibrateGyroSensor() {
		headingCorrectingMonitor.suspendRunLoop();

		Sound.getInstance().twoBeeps();
		System.out.println("Please point the EV3 in the desired direction of 0 degrees, then press ENTER to begin gyro calibration.");
		Button.ENTER.waitForPressAndRelease();

		System.out.println("Rotating the EV3 720 degrees...");
		resetGyroSensor();
		gyroHeadingCalibrationMultiplier = 1;
		rotate(720, 30);

		Sound.getInstance().twoBeeps();
		System.out.println("Please position the EV3 so that it points in the desired direction of 0 degrees again, then press ENTER to complete calibration.");
		Button.ENTER.waitForPressAndRelease();
		
		gyroHeadingCalibrationMultiplier = 720 / (double)getGyroHeading();
		resetGyroSensor();

		System.out.println("Calibrated gyro sensor.");
		headingCorrectingMonitor.resumeRunLoop();
	}
	
	/**
	 * Fetches a sample from the {@code BaseGyroSensor}.
	 * 
	 * @return The current heading of the {@code BaseGyroSensor}.
	 */
	public int getGyroHeading() {
		var angle = new float[1];
		gyroAngleProvider.fetchSample(angle, 0);
		return (int)(gyro.getGyroOrientation().multiplier * gyroHeadingCalibrationMultiplier * (double)angle[0]);
	}
    
	/**
	 * Resets the {@code BaseGyroSensor}.
	 */
    public void resetGyroSensor() {
		gyro.reset();
		desiredHeading = 0;
    }
	
	// WheeledChassis Override Methods
	
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
        var ratio = Math.abs(Math.PI * radius / 180); // The ratio between linear and angular speed that corresponds with the radius.
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
		log.warn("Warning: GyroWheeledChassis.arc(double radius, double angle) where radius ({}) != 0 is unimplemented; calling WheeledChassis.arc(radius, angle), which does not use an BaseGyroSensor.", radius);
        super.arc(radius, angle);
		
		headingCorrectingMonitor.resumeRunLoop();
    }
}
