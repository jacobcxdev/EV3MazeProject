package lejos.robotics.localization;

import lejos.utility.Matrix;

public interface DynamicPoseProvider extends PoseProvider {
  
  /** Gives the X component of the robots position
   * @return
   */
  public double getX();
  
  /** Gives the Y component of the robots position
   * @return
   */
  public double getY();
  
  /** Gives the heading of the robot in degrees
   * @return
   */
  public double getHeading();
  
  /** Gives the linear speed of the robot
   * @return
   */
  public double getLinearSpeed();
  
  /** Gives the direction the robot is going to
   * @return
   */
  public double getDirectionOfLinearSpeed();
  
  /** Gives the angular speed of the robot in degrees/second
   * @return
   */
  public double getAngularSpeed();
  
  /** Gives a Matrix holding the robots speed components 
   * @return A 3 x 1 matrix (X speed, Y speed, angular speed)
   */
  public Matrix getSpeed();
  
  /** Gives the linear acceleration of the robot
   * @return
   */
  public double getLinearAcceleration();
  
  /** Gives the direction the robot is going to
   * @return
   */
  public double getDirectionOfLinearAcceleration();

  /** Gives the angular acceleration of the robot
   * @return
   */
  public double getAngularAcceleration();
  
  /**Gives a Matrix holding the robots acceleration components 
   * @return A 3 x 1 matrix (X acceleration, Y acceleration, angular acceleration)
   */
  public Matrix getAcceleration();

}
