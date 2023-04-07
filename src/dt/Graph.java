package dt;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

class Graph {
  ArrayList<Point> points;
  DecisionTree currentTree;

  Graph() {
    points = new ArrayList<>();
    currentTree = new DecisionTree(this, 2);
  }

  public void addTrainingPoint(Point point) {
    point.forTraining = true;
    points.add(point);
    currentTree.reconstruct();
  }

  public void addTestingPoint(Point point) {
    point.forTraining = false;
    currentTree.predictData(point);
    points.add(point);
  }

  public void insertData(double[] xVals, double[] yVals, int[] typeVals) {

    if (xVals.length != yVals.length || yVals.length != typeVals.length || typeVals.length != xVals.length) {
      System.out.println("[Graph.insertData()] Parameters not of same length");
      return;
    }

    for (int i = 0; i < xVals.length; i++) {
      addPoint(Point.createTrainingPoint(xVals[i], yVals[i], typeVals[i]));
    }
    currentTree.reconstruct();
  }

  // public DecisionTree constructTree(int depth) {

  // }

  public java.util.List<Point> getPoints() {
	  return points;
  }

  public java.util.List<Point> getTrainingPoints() {
    return points.stream().filter(point -> point.forTraining).collect(Collectors.toList());
  }

  public java.util.List<Point> getTestingPoints() {
    return points.stream().filter(point -> !point.forTraining).collect(Collectors.toList());
  }

  //this is just for testing for now, use the addtest and addtraining ones for later
  public void addPoint(double x, double y, int type, boolean forTraining) {
	  points.add(new Point(x, y, type, forTraining));
  }
  
  public void addPoint(Point point) {
	  points.add(point);
  }

  public void removeTestingPoints() {
    points = new ArrayList<>(points.stream().filter(point -> point.forTraining).collect(Collectors.toList()));
  }
  
  public void removeTrainingPoints() {
	  points = new ArrayList<>(points.stream().filter(point -> !point.forTraining).collect(Collectors.toList()));
  }

  public double getWidth() {
    try {
	    double width = points.stream().mapToDouble(point -> (point.x)).max().getAsDouble();
        System.out.println("GRAPH WIDTH: " + width);
	    return width;
	    } catch (NoSuchElementException e) {
	    	System.out.println("EMPTY GRAPH");
	    }
      return 0.0;
  }

  public double getHeight() {
    try {
	    double height = points.stream().mapToDouble(point -> (point.y)).max().getAsDouble();
	    System.out.println("GRAPH HEIGHT: " + height);
        return height;
	    } catch (NoSuchElementException e) {
	    	System.out.println("EMPTY GRAPH");
	    }
      return 0.0;
  }

  public DecisionTree getDecisionTree() {
    return currentTree;
  }

}
