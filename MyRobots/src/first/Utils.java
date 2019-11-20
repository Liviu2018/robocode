package first;

import robocode.Rules;

public class Utils {
	// Returns the turn rate of a robot given a specific velocity, measured in radians/turn
	// velocity - the velocity of the robot
    public static double getTurnRateRadians(double velocity) {
		  return Math.toRadians(Rules.getTurnRate(velocity));
	}
    
    
}
