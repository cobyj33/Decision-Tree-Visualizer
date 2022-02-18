
public class Point {
  public double x;
  public double y;
  public int type; //either 0 or 1 for now
  public boolean forTraining;

  public static final int RED = 0, BLUE = 1, UNKNOWN = -1;

  public Point(double x, double y, int type, boolean forTraining) {
    this.x = x;
    this.y = y;
    this.type = type;
    this.forTraining = forTraining;
  }

  public static Point createTestingPoint(double x, double y) {
    return new Point(x, y, UNKNOWN, false);
  }

  public static Point createTrainingPoint(double x, double y, int type) {
    return new Point(x, y, type, true);
  }
  
}