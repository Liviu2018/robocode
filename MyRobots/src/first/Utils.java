package first;

import robocode.BattleEndedEvent;
import robocode.BulletMissedEvent;
import robocode.Rules;

import java.awt.geom.Point2D;


public class Utils {
    // do something at the end of each battle
    public void OnBattleEnded(BattleEndedEvent evnt) {
        System.out.println("The battle has ended");
    }
    
    // do something when a bullet misses
    public void OnBulletMissed(BulletMissedEvent evnt) {
    	String robotThatFiredMe = evnt.getBullet().getName();
        
    	System.out.println(robotThatFiredMe + " :I missed.");
    }
    
	public static boolean isNear(double value1, double value2) {
		return (Math.abs(value1 - value2) < 0.00001);
	}
	
	// Returns the turn rate of a robot given a specific velocity, measured in radians/turn
    public static double getTurnRateRadians(double velocity) {
		  return Math.toRadians(Rules.getTurnRate(velocity));
	}
    
	// normalizes a bearing to between +180 and -180
	public static double normalizeBearing(double angle) {
		while (angle > 180) {
			angle -= 360;
		}
		
		while (angle < -180) {
			angle += 360;
		}
		
		return angle;
	}

	// computes the absolute bearing between two points
	public static double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
	}
}
