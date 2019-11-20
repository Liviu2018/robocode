package first;

import robocode.*;
import java.util.Map;

import first.kdTree.*;

import java.awt.Color;
import java.util.HashMap;

public class FirstBot extends AdvancedRobot {
	private static Map<String, Tree> enemies = new HashMap<String, Tree>();
	private static Map<String, State> lastEnemyState = new HashMap<String, State>();
	private static boolean movingForward;

	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForGunTurn(true);

		setColors(Color.WHITE, Color.YELLOW, Color.YELLOW); // HINT: change bot colours
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY); // HINT: sweep only the 2 enemies, not the whole field

		while (true) {
			move();	
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		State current = computeEnemyState(e);
		storeEnemyState(e, current); // store current state

		double firePower = Math.min(500 / e.getDistance(), 3); // firePower based on distance
		double bulletSpeed = 20 - firePower * 3;
		long timeToHit = (long) (e.getDistance() / bulletSpeed); // distance = rate * time, solved for time

		// predict his next location
		double[] predicted = predictFutureLocation(e, timeToHit, current);

		shootAtLocation(firePower, predicted);

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
		
		// HINT: have a bit more randomness in this bot's movement
	}

	private void shootAtLocation(double firePower, double[] predicted) {
		double absDeg = Utils.absoluteBearing(getX(), getY(), predicted[0], predicted[1]);
		
		setTurnGunRight(Utils.normalizeBearing(absDeg - getGunHeading())); // turn the gun to the predicted x,y location

		// HINT: do not shoot if our energy is less than firePower, it is suicide
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}
		
		// HINT: store the name of the closest enemy, update that each turn, and shoot only at it
		// HINT: if an enemy has very little energy, shoot 1 fast bullet at it
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
		
		// HINT: store more relevant information than enemy x,y coordinates (example: speed, energy)
		// HINT: store more than 2 values, add more dimensions to a state
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

		// HINT: if closestState is not close enough to current, prediction is inaccurate thus return the current state

		State from = (State) closestState.getFrom();
		State to = (State) closestState.getTo();

		System.out.println("predicted: (" + (current.x + to.x - from.x) + ", " + (current.y + to.y - from.y) 
				+ ") to: (" + (current.y + to.y - from.y) + ", " + (current.y + to.y - from.y) + ")");

		return new double[] { current.x + to.x - from.x, current.y + to.y - from.y };
	}

	public void onHitWall(HitWallEvent e) {
		// HINT: try not to hit walls, it causes you damage
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
	
	// HINT: use a KDTree to store your own moves, and based on them predict where SecondBot will assume you are
}