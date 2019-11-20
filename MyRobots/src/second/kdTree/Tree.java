package second.kdTree;

public class Tree {
	private Rectangle maxBoundary;
	
	private Node root;
	private int nodesCount;

	public void add(StateInterface from, StateInterface to) {
		nodesCount++;
		
		if (root == null) {
			root = new Node(from, to, null, null, maxBoundary);
			
			return;
		}
		
		addRecursive(root, from, to, 0);
	}
	
	private void addRecursive(Node current, StateInterface from, StateInterface to, int dimension) {
		boolean goLeft = from.getValue(dimension) < current.getFrom().getValue(dimension);
		Node destination = goLeft ? current.getLeft() : current.getRight();
		
		if (destination == null) {
			Rectangle newBoundary = goLeft ? current.getBounds().getSmaller(from, dimension) : current.getBounds().getGreater(from, dimension);
			Node newNode = new Node(from, to, null, null, newBoundary);
			
			if (goLeft) {
				current.setLeft(newNode);
			} else {
				current.setRight(newNode);
			}
			
			return;
		}
		
		addRecursive(destination, from, to, (1 + dimension) % from.dimensionsCount());
	}
	
	public Node findClosest(StateInterface state) {
		if (root == null) {
			return null;
		}
		
		return findClosest(root, state, 0, root);
	}

	private Node findClosest(Node current, StateInterface state, int dimension, Node bestSoFar) {
		if (current.getDistance(state) < bestSoFar.getDistance(state)) {
			bestSoFar = current;
		}
		
		if (current.getLeft() == null && current.getRight() == null) {
			return bestSoFar;
		}
		
		if (current.getLeft() == null) {
			return findClosest(current.getRight(), state, (1 + dimension) % state.dimensionsCount(), bestSoFar);
		}
		
		if (current.getRight() == null) {
			return findClosest(current.getLeft(), state, (1 + dimension) % state.dimensionsCount(), bestSoFar);
		}
		
		return searchBothNodes(current, state, dimension, bestSoFar);
	}

	private Node searchBothNodes(Node current, StateInterface state, int dimension, Node bestSoFar) {
		boolean goLeft = state.getValue(dimension) < current.getFrom().getValue(dimension);
		Node destination = goLeft ? current.getLeft() : current.getRight();
		Node other = goLeft ? current.getRight() : current.getLeft();
		
		bestSoFar = findClosest(destination, state, (1 + dimension) % state.dimensionsCount(), bestSoFar);
		
		if (bestSoFar.getDistance(state) < other.getBounds().getDistance(state)) {
			return bestSoFar;
		}
		
		return findClosest(other, state, (1 + dimension) % state.dimensionsCount(), bestSoFar);
	}

	public Rectangle getmaxBoundary() {
		return maxBoundary;
	}

	public void setmaxBoundary(Rectangle maxBoundary) {
		this.maxBoundary = maxBoundary;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Tree(Rectangle maxBoundary, Node root) {
		super();
		this.maxBoundary = maxBoundary;
		this.root = root;
	}

	public Rectangle getMaxBoundary() {
		return maxBoundary;
	}

	public void setMaxBoundary(Rectangle maxBoundary) {
		this.maxBoundary = maxBoundary;
	}

	public int getNodesCount() {
		return nodesCount;
	}

	public void setNodesCount(int nodesCount) {
		this.nodesCount = nodesCount;
	}
}
