package subsumption;

import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import robotics.MazeDriver;
import sensors.BaseUltrasonicSensor;

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
		private SampleProvider leftUltrasonic;
		
		/**
		 * The {@code SampleProvider} used for measuring the distance with the right {@code BaseUltrasonicSensor}.
		 */
		private SampleProvider rightUltrasonic;
		
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
				
				float[] distance = new float[2];
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
	private MazeDriver driver;
	
	/**
	 * The {@code RoadWidthMonitor} used for measuring the road width.
	 */
	private RoadWidthMonitor roadWidthMonitor;
	
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
	
	// Behaviour Methods

	public void action() {
		driver.moveForward();
		if (!roadWidthMonitor.isAlive()) {
			roadWidthMonitor.start();
		}
		roadWidthMonitor.resumeRunLoop();
	}

	public void suppress() {
		roadWidthMonitor.suspendRunLoop();
		driver.moveStop();
	}

	public boolean takeControl() {
		return driver.getState() == MazeDriver.State.MAPPING;
	}
	
}
