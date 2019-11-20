package first;

import first.kdTree.Rectangle;
import first.kdTree.StateInterface;

public class State implements StateInterface {
	public static final Rectangle MAX_RECTANGLE = new Rectangle(2, new double[] {0, 0}, new double[] {1000, 1000});
	double x, y;
	
	@Override	
	public double[]  values()   {
		return new double[] {x, y};
	}
	
	@Override
	public int dimensionsCount() {
		return 2;
	}
	
	@Override
	public double getValue(int dimension) {
		switch (dimension) {
		case 0:
			return x;
		case 1:
			return y;
			
		default:
			return x;
		}

	}

	public State(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
}
