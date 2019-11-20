package third;


import robocode.*;

import java.awt.*;

public class ThirdBot extends AdvancedRobot {
	boolean movingForward;

	public void run() {
		setBodyColor(Color.WHITE);
		setGunColor(Color.WHITE);
		setRadarColor(Color.WHITE);
		setBulletColor(Color.WHITE);
		setScanColor(Color.WHITE);

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
		fire(1);
	}
	
	public void onHitWall(HitWallEvent e) {
		reverseDirection();
	}

	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		if (e.isMyFault()) {
			reverseDirection();
		}
	}
	
}