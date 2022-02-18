
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Display extends JPanel {
	String xType;
	String yType;
	int xScale;
	int yScale;
	final int numOfNotches = 10;
	int selectedType;
	Insets screenPadding;
	Graph currentGraph;
	Rectangle graphingArea;
  boolean displayTree;
  
  final int pointDisplaySize = 5;
	
	Mouse mouse;
	Key key;
	
	Display(Graph graph) {
		setBackground(new Color(20, 20, 20));
		currentGraph = graph;
		screenPadding = new Insets(0, 0, 0, 0);
		selectedType = Point.RED;
		graphingArea = new Rectangle(0, 0, 0, 0);
		mouse = new Mouse();
		key = new Key();
		displayTree = true;
		
		setFocusable(true);
		requestFocus();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addKeyListener(key);
		
//		graph.addPoint(5, 9, Point.BLUE);
//		graph.addPoint(50, 10, Point.RED);
//		graph.addPoint(0, 100, Point.RED);
	}
	
  protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2D = (Graphics2D) g;
	    final Composite originalComposite = g2D.getComposite();
	    List<Point> points = currentGraph.getPoints();
	    
	    screenPadding.set(getHeight() / 10, getWidth() / 10, getHeight() / 10, getWidth() / 10);
	    graphingArea.setBounds(screenPadding.left, screenPadding.top, getHeight() - screenPadding.top - screenPadding.bottom, getWidth() - screenPadding.left - screenPadding.right);

	    g2D.setStroke(new BasicStroke(3));
	    g2D.drawLine(graphingArea.x, graphingArea.y, graphingArea.x, graphingArea.y + graphingArea.height);

	    g2D.drawLine(graphingArea.x, graphingArea.y + graphingArea.height, graphingArea.x + graphingArea.width, graphingArea.y + graphingArea.height);
	    try {
	     	yScale = points.stream().mapToInt(point -> (int)point.y).max().getAsInt() / numOfNotches;
	     	if (yScale < 1) yScale = 1;
	    } catch (NoSuchElementException e) {
	    	yScale = 1;
	    }
	    
	    try {
	    	xScale = points.stream().mapToInt(point -> (int)point.x).max().getAsInt() / numOfNotches;
	    	if (xScale < 1) xScale = 1;
	    } catch (NoSuchElementException e) {
	    	xScale = 1;
	    }
	    
	    g2D.setFont(getFont().deriveFont(10f));
	    FontMetrics metrics = g2D.getFontMetrics();
	    
	    for (int i = 0; i < numOfNotches + 1; i++) {
	    	g2D.drawString("" + (yScale * i), graphingArea.x, graphingArea.y + graphingArea.height - (graphingArea.height / numOfNotches * i) );
	    	g2D.drawString("" + (xScale * i), graphingArea.x + graphingArea.width / numOfNotches * i, graphingArea.y + graphingArea.height);
	    }
	    
	    java.awt.Paint originalPaint = g2D.getPaint();
	    for (int i = 0; i < points.size(); i++) {
	    	Point current = points.get(i);
	    	int[] coordinates = getGraphLocationOnScreen(current.x, current.y);
	    	
	    	switch (current.type) {
	    	case Point.RED: g2D.setPaint(Color.RED); break;
	    	case Point.BLUE: g2D.setPaint(Color.BLUE); break;
	    	case Point.UNKNOWN: g2D.setPaint(Color.LIGHT_GRAY); break;
	    	}
	    	 
	    	if (current.forTraining) {
	    		g2D.fillRect(coordinates[0], coordinates[1], pointDisplaySize, pointDisplaySize);
	    	} else {
	    		g2D.fillOval(coordinates[0], coordinates[1], pointDisplaySize, pointDisplaySize);
	    	}
	    }
	    g2D.setPaint(originalPaint);
	    g2D.setComposite(originalComposite);
	    
      if (displayTree) {
//    	 System.out.println("DISPLAYING TREE");
    	  g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        List<Node> nodes = currentGraph.getDecisionTree().getLeafNodes();
        System.out.println("NODES: " + nodes);
        for (int i = 0; i < nodes.size(); i++) {
        	Node current = nodes.get(i);
//        	System.out.println("DISPLAYING NODES");
        	int[] screenLocation = getGraphLocationOnScreen(current.x, current.y + current.height);
        	int[] bounds = getGraphLocationOnScreen(current.width, current.height);
        	double xRatio = graphingArea.width / (double)numOfNotches / xScale;
        	double yRatio = graphingArea.height / (double)numOfNotches / yScale;
        	
        	Color paint = current.type == Point.RED ? Color.RED : Color.BLUE;
        	g2D.setPaint(paint);
        	Rectangle displayNode = new Rectangle(screenLocation[0], screenLocation[1], (int) (current.width * xRatio), (int) (current.height * yRatio));
        	System.out.println("DISPLAY NODE: " + displayNode); 
        	g2D.fill(displayNode);
        	g2D.setPaint(Color.BLACK);
        	g2D.draw(displayNode);
//        	g2D.fillRect(displayNode.x, displayNode.y, displayNode.width, displayNode.height);
        }
        
      }
      
      g2D.setComposite(originalComposite);
      g2D.setFont(g2D.getFont().deriveFont(8f));
      g2D.setPaint(Color.WHITE);
      String controls = "B OR E: SET BLUE TRAINING POINTS | R OR Q: SET RED TRAINING POINTS | C: CLEAR TESTING POINTS |\n Ctrl + C: CLEAR TRAINING POINTS | ENTER: DISPLAY TREE | 1 - 9: SET TREE DEPTH \n (Note: Testing Points set themselves to whatever fits the tree)";
      metrics = g2D.getFontMetrics();
      String[] lines = controls.split("\n");
      int lineHeight = graphingArea.y + graphingArea.height + screenPadding.top / 2;
      for (int l = 0; l < lines.length; l++) {
    	  g2D.drawString(lines[l], getWidth() / 2 - metrics.stringWidth(lines[l]) / 2, lineHeight);
    	  lineHeight += metrics.getHeight();
      }
	    
	  }

	  public double[] getScreenLocationOnGraph(int x, int y) {
	    double[] coordinates = new double[2];
	    
	    coordinates[0] = ((x - graphingArea.x) * ((double)numOfNotches / graphingArea.width)) * xScale;
	    
	    int yPosOnGraph = y - graphingArea.y;
	    double ratio = (double)numOfNotches / graphingArea.height * yScale;
	    coordinates[1] = Math.abs((yPosOnGraph * ratio) - (yScale * numOfNotches));

	    return coordinates;
	  }

	  public int[] getGraphLocationOnScreen(double x, double y) {
		int[] coordinates = new int[2];
		
		coordinates[0] = (int) (graphingArea.x + (graphingArea.width / numOfNotches ) * (x / xScale));
	    coordinates[1] = (int) (graphingArea.y + graphingArea.height - graphingArea.height / numOfNotches * y / yScale);

	    return coordinates;
	  }

  class Mouse extends MouseAdapter {
    Mouse() {}

    public void mousePressed(MouseEvent e) {
      double[] location = getScreenLocationOnGraph(e.getX(), e.getY());
      if (!graphingArea.contains(e.getPoint())) return;
      System.out.println("New point: " + location[0] + " " + location[1]);

      if (SwingUtilities.isRightMouseButton(e) || e.isControlDown()) {
        currentGraph.addTrainingPoint(Point.createTrainingPoint(location[0], location[1], selectedType));
    	  // currentGraph.addPoint(location[0], location[1], selectedType, true);
      } else {
        currentGraph.addTestingPoint(Point.createTestingPoint(location[0], location[1]));
//    	  currentGraph.addPoint(location[0], location[1], selectedType, false);
      }

      repaint();
      
    }
  }

  class Key extends KeyAdapter {
    Key() {}

    public void keyPressed(KeyEvent e) {
    	int keyCode = e.getKeyCode();
    	System.out.println("Key pressed: " + e.getKeyChar());
      switch (keyCode) {
        case KeyEvent.VK_B:
        case KeyEvent.VK_E: selectedType = Point.BLUE; break;
        case KeyEvent.VK_R:
        case KeyEvent.VK_Q: selectedType = Point.RED; break;
        case KeyEvent.VK_C: 
        	if (e.isControlDown()) {
        		currentGraph.removeTrainingPoints();
        	} else {
        		currentGraph.removeTestingPoints();
        	} break;
        case KeyEvent.VK_ENTER: displayTree = !displayTree; break;
      }
      
      if (keyCode >= 48 && keyCode <= 57) {
    	  int depth = keyCode - 48;
    	  currentGraph.getDecisionTree().setMaxDepth(depth);
      }
      repaint();
      
    }
  }
  
  
}
