package first.kdTree;

public class Rectangle {
	private int dimensions;
	private double[] minimums;
	private double[] maximums;
	
	public Rectangle(int dimensions, double[] minimums, double[] maximums) {
		super();
		this.dimensions = dimensions;
		this.minimums = minimums;
		this.maximums = maximums;
	}

	public Rectangle getSmaller(StateInterface state, int splitByDimension) {
		double[] newMaximums = maximums.clone();
		newMaximums[splitByDimension] = state.getValue(splitByDimension);
		

		return new Rectangle(state.dimensionsCount(), minimums, newMaximums);
	}

	public Rectangle getGreater(StateInterface state, int splitByDimension) {
		double[] newMinimums = minimums.clone();
		newMinimums[splitByDimension] = state.getValue(splitByDimension);
		

		return new Rectangle(state.dimensionsCount(), newMinimums, maximums);	
	}

	public double getDistance(StateInterface state) {
		double result = 0;

		for (int i = 0; i < state.dimensionsCount(); i++) {
			if (state.getValue(i) < minimums[i]) {
				result += (minimums[i] - state.getValue(i)) * (minimums[i] - state.getValue(i));
			}

			if (state.getValue(i) > maximums[i]) {
				result += (maximums[i] - state.getValue(i)) * (maximums[i] - state.getValue(i));
			}
		}

		return result;
	}

	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public double[] getMinimums() {
		return minimums;
	}

	public void setMinimums(double[] minimums) {
		this.minimums = minimums;
	}

	public double[] getMaximums() {
		return maximums;
	}

	public void setMaximums(double[] maximums) {
		this.maximums = maximums;
	}
}