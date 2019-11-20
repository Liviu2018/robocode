package first.kdTree;


public class Node {
	private StateInterface from, to;
	private Node left, right;
	private Rectangle bounds;
	

	public double getDistance(StateInterface state) {
		double result = 0;
		
		double[] first = from.values();
		double[] second = state.values();
		
		for (int i = 0; i < state.dimensionsCount(); i++) {
			result += (first[i] - second[i]) * (first[i] - second[i]);
		}

	
		return Math.sqrt(result);
	}


	public Node(StateInterface from, StateInterface to, Node left, Node right, Rectangle bounds) {
		super();
		this.from = from;
		this.to = to;
		this.left = left;
		this.right = right;
		this.bounds = bounds;
	}


	public StateInterface getFrom() {
		return from;
	}


	public void setFrom(StateInterface from) {
		this.from = from;
	}


	public StateInterface getTo() {
		return to;
	}


	public void setTo(StateInterface to) {
		this.to = to;
	}


	public Node getLeft() {
		return left;
	}


	public void setLeft(Node left) {
		this.left = left;
	}


	public Node getRight() {
		return right;
	}


	public void setRight(Node right) {
		this.right = right;
	}


	public Rectangle getBounds() {
		return bounds;
	}


	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}


	@Override
	public String toString() {
		return "Node [from=" + from + ", to=" + to + ", bounds=" + bounds + "]";
	}
}