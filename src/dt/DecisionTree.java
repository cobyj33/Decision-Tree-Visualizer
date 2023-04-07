package dt;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DecisionTree {
  Node head;
  int maxDepth;
  Graph graph;

  DecisionTree(Graph graph, int maxDepth) {
    this.graph = graph;
    this.maxDepth = maxDepth;
  }
  
  public void setMaxDepth(int depth) {
	  System.out.println("SET MAX DEPTH TO " + depth);
	  maxDepth = depth;
	  reconstruct();
  }

  public void predictData(Point p) {
	if (!head.contains(p)) {
		p.type = Point.UNKNOWN;
		return;
	}
	
    p.type = head.test(p);
    System.out.println("DETERMINED TYPE: " + p.type);
  }

  public void reconstruct() {
    head = new Node(0, 0, graph.getWidth() + 0.1, graph.getHeight() + 0.1);
//    System.out.println("HEAD HEIGHT: " + head.height );
    head.currentDepth = 0;
    construct(head);
  }

  private void construct(Node current) {
    setNodeType(current);
    if (current.currentDepth > maxDepth || nodeIsSingleton(current) || current == null) {
    	return;
    }

    Node leftNode;
    Node rightNode;
    
    if (current.currentDepth % 2 == 0) { //split horizontally
      double horizontalPartition = findHorizontalPartition(current);
      leftNode = new Node(current.x, current.y, current.width, Math.abs(current.y - horizontalPartition));
      rightNode = new Node(current.x, horizontalPartition, current.width, Math.abs(current.height - leftNode.height));
    } else { //split vertically
      double verticalPartition = findVerticalPartition(current);
      leftNode = new Node(current.x, current.y, Math.abs(current.x - verticalPartition), current.height);

      rightNode = new Node(verticalPartition, current.y, Math.abs(current.width - leftNode.width), current.height);
    }

    leftNode.currentDepth = current.currentDepth + 1;
    rightNode.currentDepth = current.currentDepth + 1;
    
    current.left = leftNode;
    current.right = rightNode;
    construct(leftNode);
    construct(rightNode);
  }

  private void setNodeType(Node node) {
    List<Point> pointsInNode = getPointsInNode(node);
    
    int numOfRed = (int) pointsInNode.stream().filter(point -> point.type == Point.RED).count();
    int numOfBlue = (int) pointsInNode.stream().filter(point -> point.type == Point.BLUE).count();
    
    
    System.out.println("NUM OF RED: " + numOfRed + " NUM OF BLUE: " + numOfBlue);
    node.type = numOfRed > numOfBlue ? Point.RED : Point.BLUE;
  }

  public List<Point> getPointsInNode(Node node) {
    return graph.getTrainingPoints().stream().filter(point -> node.contains(point)).collect(Collectors.toList());
  }

  public boolean nodeIsSingleton(Node node) {
    List<Point> pointsInNode = getPointsInNode(node);
    if (pointsInNode.stream().allMatch(point -> point.type == Point.RED) || pointsInNode.stream().allMatch(point -> point.type == Point.BLUE)) {
      return true;
    }
    return false;
  }

  public double findHorizontalPartition(Node node) {
    List<Point> pointsInNode = getPointsInNode(node);
    
    List<Point> reds = pointsInNode.stream().filter(point -> point.type == Point.RED).collect(Collectors.toList());
    List<Point> blues = pointsInNode.stream().filter(point -> point.type == Point.BLUE).collect(Collectors.toList());

    double redYAverage = reds.stream().mapToDouble(point -> point.y).sum() / reds.size();

    double blueYAverage = blues.stream().mapToDouble(point -> point.y).sum() / blues.size();
    
    double partition = (redYAverage + blueYAverage) / 2;
    System.out.println("CURRENT NODE: " + node);
    System.out.println("HORIZONTAL PARTITION: " + partition);
    return partition;
  }

  public double findVerticalPartition(Node node) {
    List<Point> pointsInNode = getPointsInNode(node);

    List<Point> reds = pointsInNode.stream().filter(point -> point.type == Point.RED).collect(Collectors.toList());
    List<Point> blues = pointsInNode.stream().filter(point -> point.type == Point.BLUE).collect(Collectors.toList());
    
    double redXAverage = reds.stream().mapToDouble(point -> point.x).sum() / reds.size();

    double blueXAverage = blues.stream().mapToDouble(point -> point.x).sum() / blues.size();
    
    double partition = (redXAverage + blueXAverage) / 2;
    System.out.println("CURRENT NODE: " + node);
    System.out.println("VERTICAL PARTITION: " + partition);
    return partition;
  }

  public List<Node> getNodes() {
	  List<Node> nodes = new ArrayList<>();
	  nodes.add(head);
	  nodes.addAll(head.getChildren());
	  return nodes;
  }

  public List<Node> getLeafNodes() {
    List<Node> nodes = getNodes();
    return nodes.stream().filter(node -> node.left == null && node.right == null).collect(Collectors.toList());
  }
}

class Node extends Rectangle2D.Double {
  public int currentDepth;
  Node left;
  Node right;
  int type;

  Node(double x, double y, double width, double height) {
    super(x, y, width, height);
  }

  public int test(Point point) {
    if (left != null) {
      if (left.contains(point)) {
        return left.test(point);
      }
    }

    if (right != null) {
      if (right.contains(point)) {
        return right.test(point);
      }
    }
    
    return this.type;
  }

  public boolean contains(Point point) {
    return this.contains(point.x, point.y);
  }

  public List<Node> getChildren() {
    List<Node> children = new ArrayList<>();
//    System.out.println("GETTING CHILDREN");
    if (left != null) {
      children.add(left);
      children.addAll(left.getChildren());
    }

    if (right != null) {
      children.add(right);
      children.addAll(right.getChildren());
    }

    return children;
  }

  public String toString() {
    String typeStr;
    switch (type) {
      case Point.BLUE: typeStr = "Blue"; break;
      case Point.RED: typeStr = "Red"; break;
      default: typeStr = "Unknown"; break;
    }

    return String.format("NODE: TYPE: %s, X: %f, Y: %f, WIDTH: %f, HEIGHT: %f", typeStr, x, y, width, height);
  }
}

