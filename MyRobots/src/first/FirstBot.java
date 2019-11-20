package first;

import robocode.*;

import java.util.Map;

import first.kdTree.*;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class FirstBot extends AdvancedRobot {
	private static Map<String, Tree> enemies = new HashMap<String, Tree>();
	private static Map<String, State> lastEnemyState = new HashMap<String, State>();
	private static boolean movingForward;

	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForGunTurn(true);

		setColors(Color.WHITE, Color.YELLOW, Color.YELLOW);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

		while (true) {
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			   LocalDateTime now = LocalDateTime.now();  
			   System.out.println("TIME: " + dtf.format(now));  
			   
			// Tell the game we will want to move ahead 40000 -- some large number
			setAhead(40000);
			movingForward = true;
			// Tell the game we will want to turn right 90
			setTurnRight(90);
			// At this point, we have indicated to the game that *when we do something*,
			// we will want to move ahead and turn right. That's what "set" means.
			// It is important to realize we have not done anything yet!
			// In order to actually move, we'll want to call a method that
			// takes real time, such as waitFor.
			// waitFor actually starts the action -- we start moving and turning.
			// It will not return until we have finished turning.
			waitFor(new TurnCompleteCondition(this));
			// Note: We are still moving ahead now, but the turn is complete.
			// Now we'll turn the other way...
			setTurnLeft(180);
			// ... and wait for the turn to finish ...
			waitFor(new TurnCompleteCondition(this));
			// ... then the other way ...
			setTurnRight(180);
			// .. and wait for that turn to finish.
			waitFor(new TurnCompleteCondition(this));
		}

	}

	public void onScannedRobot(ScannedRobotEvent e) {
		// add current state to tree & lastEnemyState
		if (!enemies.containsKey(e.getName())) {
			enemies.put(e.getName(), new Tree(State.MAX_RECTANGLE, null));
		}

		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);

		int scannedX = (int) (getX() + Math.sin(angle) * e.getDistance());
		int scannedY = (int) (getY() + Math.cos(angle) * e.getDistance());

		State current = new State(scannedX, scannedY);

		if (lastEnemyState.containsKey(e.getName())) {
			enemies.get(e.getName()).add(lastEnemyState.get(e.getName()), current);
		}

		lastEnemyState.put(e.getName(), current);

		// finished adding current state

		System.out.println(e.getName() + " : " + enemies.get(e.getName()).getNodesCount());

		// calculate firepower based on distance
		double firePower = Math.min(500 / e.getDistance(), 3);
		// calculate speed of bullet
		double bulletSpeed = 20 - firePower * 3;
		// distance = rate * time, solved for time
		long timeToHit = (long) (e.getDistance() / bulletSpeed);

		// calculate gun turn to predicted x,y location
		double[] predicted = predictFutureLocation(e, timeToHit, current);

		double absDeg = absoluteBearing(getX(), getY(), predicted[0], predicted[1]);

		// turn the gun to the predicted x,y location
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));

		// HINT: 
		// if the gun is cool and we're pointed in the right direction, shoot!
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			System.out.println("fire:");
			setFire(firePower);
		}

	}

	// uses data from the tree to predict future state
	// HINT: change colors
	double[] predictFutureLocation(ScannedRobotEvent e, long timeToHit, State current) {
		Tree previousEnemyMoves = enemies.get(e.getName());
		Node closestState = previousEnemyMoves.findClosest(current);

		if (closestState == null) {
			System.out.println("closestState == null");

			return new double[] { current.x, current.y };
		}

		State from = (State) closestState.getFrom();
		State to = (State) closestState.getTo();

		System.out.println("predict: (" + (current.x + to.x - from.x) + ", " + (current.y + to.y - from.y) 
				+ ") to: (" + (current.y + to.y - from.y) + ", " + (current.y + to.y - from.y) + ")");

		return new double[] { current.x + to.x - from.x, current.y + to.y - from.y };
	}

	// normalizes a bearing to between +180 and -180
	double normalizeBearing(double angle) {
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	// computes the absolute bearing between two points
	double absoluteBearing(double x1, double y1, double x2, double y2) {
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

	public void onHitWall(HitWallEvent e) {
		reverseDirection();
	}

	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
		} else {
			setAhead(40000);
		}
		
		movingForward = !movingForward;
	}

	public void onHitRobot(HitRobotEvent e) {
		if (e.isMyFault()) {
			reverseDirection();
		}
	}
}