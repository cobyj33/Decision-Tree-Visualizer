import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;

class Main {
  public static void main(String[] args) {
    System.out.println("Hello world!");

    JFrame frame = new JFrame();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int frameSize = (int) Math.min(screenSize.getWidth() * 4 / 5, screenSize.getHeight() * 4 / 5);

    frame.getContentPane().setBackground(Color.BLACK);
    frame.setLayout(new FlowLayout(FlowLayout.CENTER));
    frame.setSize(frameSize, frameSize);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Graph graph = new Graph();

    double[] xVals = new double[] {1, 2, 3, 4, 2.5, 1.5, 3.5, 5, 6, 5, 6, 2, 3, 2, 3};
    double[] yVals = new double[] {1, 2, 3, 4, 2.5, 1.5, 3.5, 7, 8, 8, 7, 4, 5, 5, 4};
    int[] typeVals = new int[] {0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0};
    
    graph.insertData(xVals, yVals, typeVals);

    Display display = new Display(graph);
    
    display.setPreferredSize(new Dimension(frame.getWidth() * 9 / 10, frame.getHeight() * 9 / 10));
    
    frame.add(display);
    frame.setVisible(true);
  }
}