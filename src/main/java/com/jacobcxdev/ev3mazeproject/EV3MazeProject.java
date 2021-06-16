package com.jacobcxdev.ev3mazeproject;

import com.jacobcxdev.ev3mazeproject.hardware.*;
import com.jacobcxdev.ev3mazeproject.mapping.MazeStore;
import com.jacobcxdev.ev3mazeproject.networking.ServerThread;
import com.jacobcxdev.ev3mazeproject.robotics.MazeDriver;
import com.jacobcxdev.ev3mazeproject.sensors.BaseGyroSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * A class used to store the {@code main} method invoked by the EV3's JVM, along with the shared state of the EV3.<br/><br/>
 * 
 * The EV3 Maze Project is our first year CS1822 project @ <a href="https://www.royalholloway.ac.uk">Royal Holloway College, University of London</a> (group R5).
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 * @author Roshan Gajjar (<a href="https://github.com/roshan-RG">@roshan-RG</a>)
 * @author Aman Vaidya (<a href="https://github.com/Amanv16">@Amanv16</a>)
 * @author Adam Zemiri (<a href="https://github.com/Xpl0itR">@Xpl0itR</a>)
 */
public class EV3MazeProject {
	// Public Enumerations
	
	/**
	 * An enumeration to represent the different possible states of the {@code EV3MazeProject}.
	 */
	public enum State {
		STARTUP,
		STANDBY,
		MOBILE
	}
	
	// Private Static Fields

	/**
	 * The {@code BuildDescription} of the EV3.
	 */
	private static BuildDescription buildDescription;

	/**
	 * The {@code PortConfiguration} of the EV3.
	 */
	private static PortConfiguration portConfiguration;

	/**
	 * The current {@code State} of the {@code EV3MazeProject}.
	 */
	private static State state = State.STARTUP;

	// Private Static Methods

	/**
	 * Initialises the hardware of the EV3.
	 *
	 * @throws ClassNotFoundException If an error is encountered during hardware initialisation.
	 * @throws NoSuchMethodException If an error is encountered during hardware initialisation.
	 * @throws InstantiationException If an error is encountered during hardware initialisation.
	 * @throws IllegalAccessException If an error is encountered during hardware initialisation.
	 * @throws InvocationTargetException If an error is encountered during hardware initialisation.
	 */
	private static void initHardware() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, SensorDefinition.InvalidFamilyForTypeException {
		buildDescription = new BuildDescription(150, 45, 80, BaseGyroSensor.GyroOrientation.GLYPH_TOP);
		portConfiguration = new PortConfiguration(
			new SensorDefinition(SensorPort.S3, PartDefinition.Family.EV3, SensorDefinition.Type.GYRO),
			new SensorDefinition(SensorPort.S2, PartDefinition.Family.EV3, SensorDefinition.Type.TOUCH),
			new SensorDefinition(SensorPort.S4, PartDefinition.Family.EV3, SensorDefinition.Type.ULTRASONIC),
			new SensorDefinition(SensorPort.S1, PartDefinition.Family.NXT, SensorDefinition.Type.ULTRASONIC),
			new RegulatedMotorDefinition(MotorPort.D, PartDefinition.Family.EV3, RegulatedMotorDefinition.Size.LARGE),
			new RegulatedMotorDefinition(MotorPort.B, PartDefinition.Family.EV3, RegulatedMotorDefinition.Size.LARGE),
			new RegulatedMotorDefinition(MotorPort.C, PartDefinition.Family.EV3, RegulatedMotorDefinition.Size.MEDIUM)
		);
	}

	// Public Static Getters/Setters
	
	/**
	 * Gets the current {@code State} of the {@code EV3MazeProject}.
	 * 
	 * @return The current {@code State} of the {@code EV3MazeProject}.
	 */
	public static State getState() {
		return state;
	}
	
	/**
	 * Sets the current {@code State} of the {@code EV3MazeProject} to a given new {@code State}.
	 * 
	 * @param state The new {@code State} of the {@code EV3MazeProject}.
	 */
	public static void setState(State state) {
		EV3MazeProject.state = state;
	}
	
	// Public Static Methods

	/**
	 * The main method which is invoked by the EV3's JVM.
	 * 
	 * @param args The {@code String} arguments passed to the program.
	 * @throws ClassNotFoundException If an error is encountered during hardware initialisation.
	 * @throws NoSuchMethodException If an error is encountered during hardware initialisation.
	 * @throws InvocationTargetException If an error is encountered during hardware initialisation.
	 * @throws InstantiationException If an error is encountered during hardware initialisation.
	 * @throws IllegalAccessException If an error is encountered during hardware initialisation.
	 */
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SensorDefinition.InvalidFamilyForTypeException {
		initHardware();

//		ServerThread server = new ServerThread();
//		server.start();
////		server.run();

		MazeStore mazeStore = new MazeStore();
		MazeDriver driver = new MazeDriver(buildDescription, portConfiguration, mazeStore);
		try {
			driver.startMapping().createSVGFile("maze.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}

		LoggerFactory.getLogger(EV3MazeProject.class).info("Exiting cleanly...");
	}
}
