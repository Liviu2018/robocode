package second;

import robocode.*;

import java.util.Map;

import second.State;
import second.kdTree.Node;
import second.kdTree.Tree;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;


public class SecondBot extends AdvancedRobot {
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
			move();	
		}
	}

	public void move() {
		setAhead(40000); // move ahead some large number
		movingForward = true;
		
		setTurnRight(90); // turn right 90
		waitFor(new TurnCompleteCondition(this)); // blocks until we finish turning
		
		setTurnLeft(180); // still moving ahead now, but turn right is complete
		waitFor(new TurnCompleteCondition(this)); // wait for the turn left to finish 
		
		setTurnRight(180); // still moving ahead now, but turn left is complete
		waitFor(new TurnCompleteCondition(this)); // wait for turn right to finish
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		State current = computeEnemyState(e);
		storeEnemyState(e, current); // store current state

		double firePower = Math.min(500 / e.getDistance(), 3); // firepower based on distance
		double bulletSpeed = 20 - firePower * 3;
		long timeToHit = (long) (e.getDistance() / bulletSpeed); // distance = rate * time, solved for time

		// predict his next location
		double[] predicted = predictFutureLocation(e, timeToHit, current);

		shootAtLocation(firePower, predicted);

	}

	private void shootAtLocation(double firePower, double[] predicted) {
		double absDeg = absoluteBearing(getX(), getY(), predicted[0], predicted[1]);
		
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading())); // turn the gun to the predicted x,y location

		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}
	}

	private void storeEnemyState(ScannedRobotEvent e, State current) {
		if (!enemies.containsKey(e.getName())) {
			enemies.put(e.getName(), new Tree(State.MAX_RECTANGLE, null));
		}

		if (lastEnemyState.containsKey(e.getName())) {
			enemies.get(e.getName()).add(lastEnemyState.get(e.getName()), current);
		}

		lastEnemyState.put(e.getName(), current);
	}

	private State computeEnemyState(ScannedRobotEvent e) {
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);

		int scannedX = (int) (getX() + Math.sin(angle) * e.getDistance());
		int scannedY = (int) (getY() + Math.cos(angle) * e.getDistance());
		
		return new State(scannedX, scannedY);
	}

	// use data from the tree to predict future state
	double[] predictFutureLocation(ScannedRobotEvent e, long timeToHit, State current) {
		Tree previousEnemyMoves = enemies.get(e.getName());
		Node closestState = previousEnemyMoves.findClosest(current);

		if (closestState == null) {
			System.out.println("closestState == null");

			return new double[] { current.x, current.y };
		}

		State from = (State) closestState.getFrom();
		State to = (State) closestState.getTo();

		System.out.println("predicted: (" + (current.x + to.x - from.x) + ", " + (current.y + to.y - from.y) 
				+ ") to: (" + (current.y + to.y - from.y) + ", " + (current.y + to.y - from.y) + ")");

		return new double[] { current.x + to.x - from.x, current.y + to.y - from.y };
	}

	// normalizes a bearing to between +180 and -180
	double normalizeBearing(double angle) {
		while (angle > 180) {
			angle -= 360;
		}
		
		while (angle < -180) {
			angle += 360;
		}
		
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