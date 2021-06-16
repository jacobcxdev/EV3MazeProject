package com.jacobcxdev.ev3mazeproject.subsumption;

import com.jacobcxdev.ev3mazeproject.robotics.MazeDriver;
import com.jacobcxdev.ev3mazeproject.sensors.BaseUltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import org.slf4j.LoggerFactory;

/**
 * An implementation of LeJOS' {@code Behavior} interface used to make the EV3 move forward.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class MoveForward implements Behavior {
	// Private Classes
	
	/**
	 * A {@code Thread} subclass which is used to monitor the width of the road using two {@code BaseUltrasonicSensor}s, keeping track of the narrowest width measured.
	 */
	private class RoadWidthMonitor extends Thread {
		// Private Fields
		
		/**
		 * The {@code SampleProvider} used for measuring the distance with the left {@code BaseUltrasonicSensor}.
		 */
		private final SampleProvider leftUltrasonic;
		
		/**
		 * The {@code SampleProvider} used for measuring the distance with the right {@code BaseUltrasonicSensor}.
		 */
		private final SampleProvider rightUltrasonic;
		
		/**
		 * The width between the two {@code BaseUltrasonicSensor}s (in millimetres).
		 */
		private final float ultrasonicSensorGap;
		
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
		 * Creates a {@code RoadWidthMonitor} thread.
		 * 
		 * @param leftUltrasonicSensor The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
		 * @param rightUltrasonicSensor The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its right.
		 * @param ultrasonicSensorGap The width between the two {@code BaseUltrasonicSensor}s (in millimetres).
		 */
		public RoadWidthMonitor(BaseUltrasonicSensor leftUltrasonicSensor, BaseUltrasonicSensor rightUltrasonicSensor, float ultrasonicSensorGap) {
			leftUltrasonic = leftUltrasonicSensor.getDistanceMode();
			rightUltrasonic = rightUltrasonicSensor.getDistanceMode();
			this.ultrasonicSensorGap = ultrasonicSensorGap;
			setDaemon(true);
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

		// Thread Override Methods
		
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
				
				var distance = new float[2];
				leftUltrasonic.fetchSample(distance, 0);
				rightUltrasonic.fetchSample(distance, 1);
				driver.recordRoadWidth(distance[0] * 1000 + distance[1] * 1000 + ultrasonicSensorGap);
			}
		}
	}
	
	// Private Fields
	
	/**
	 * The {@code MazeDriver} used for controlling the EV3.
	 */
	private final MazeDriver driver;
	
	/**
	 * The {@code RoadWidthMonitor} used for measuring the road width.
	 */
	private final RoadWidthMonitor roadWidthMonitor;

	/**
	 * Whether the action should be suppressed.
	 */
	private boolean shouldSuppress = false;

	// Public Constructors
	
	/**
	 * Creates a {@code MoveForward} object.<br/><br/>
	 * 
	 * This behaviour will move the EV3 forward and record the movement with a {@code MazeDriver} object.<br/>
	 * This behaviour will always take control.<br/>
	 * This behaviour is suppressible.
	 * 
	 * @param driver The {@code MazeDriver} used for controlling the EV3.
	 * @param leftUltrasonicSensor The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its left.
	 * @param rightUltrasonicSensor The {@code BaseUltrasonicSensor} used to measure the distance between the EV3 and the nearest object on its right.
	 * @param ultrasonicSensorGap The width between the two {@code BaseUltrasonicSensor}s (in millimetres).
	 */
	public MoveForward(MazeDriver driver, BaseUltrasonicSensor leftUltrasonicSensor, BaseUltrasonicSensor rightUltrasonicSensor, float ultrasonicSensorGap) {
		this.driver = driver;
		roadWidthMonitor = this.new RoadWidthMonitor(leftUltrasonicSensor, rightUltrasonicSensor, ultrasonicSensorGap);
	}
	
	// Behavior Override Methods

	@Override
	public void action() {
		shouldSuppress = false;
		driver.moveForward();
		if (!roadWidthMonitor.isAlive()) {
			roadWidthMonitor.start();
		}
		roadWidthMonitor.resumeRunLoop();
		while (!shouldSuppress) {
			Thread.yield();
		}
		roadWidthMonitor.suspendRunLoop();
		driver.moveStop();
	}

	@Override
	public void suppress() {
		shouldSuppress = true;
	}

	@Override
	public boolean takeControl() {
		return driver.getState() == MazeDriver.State.MAPPING;
	}
}
