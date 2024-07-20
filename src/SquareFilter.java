import java.awt.*;
import java.util.ArrayList;

public class SquareFilter {
    private ArrayList<Point> points = new ArrayList<>();
    private boolean showPoint;

    public SquareFilter(){
        points.add(new Point(300,500));
        points.add(new Point(400,500));
        points.add(new Point(400,600));
        points.add(new Point(300,600));
        showPoint = true;
    }

    public void paint(Graphics graphics){
        int[] xPoints = {points.get(0).getX(), points.get(1).getX(), points.get(2).getX(), points.get(3).getX()};
        int[] yPoints = {points.get(0).getY(), points.get(1).getY(), points.get(2).getY(), points.get(3).getY()};
        graphics.setColor(new Color(225,225,225,128));
        graphics.fillPolygon(xPoints, yPoints, 4);
        if (showPoint) {
            for (Point p : points) {
                p.paint(graphics);
            }
        }
    }
    public ArrayList<Point> getPoints(){
        return points;
    }

    public Point getPoint(int locX,int locY){
        Point p = null;
        for (int i = 0 ; i < points.size();i++){
            double distance = Math.sqrt((locX - points.get(i).getX()) * (locX - points.get(i).getX()) + (locY - points.get(i).getY()) * (locY - points.get(i).getY()));
            if (distance <= Point.RADIUS){
                p = points.get(i);
                break;
            }
        }
        return p;
    }

    public void ChangeToSquare() {
        int minX = points.get(0).getX();
        int minY = points.get(0).getY();
        int maxX = points.get(0).getX();
        int maxY = points.get(0).getY();

        for (Point p : points) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getY() > maxY) maxY = p.getY();
        }

        points.get(0).setX(minX);
        points.get(0).setY(minY);
        points.get(1).setX(maxX);
        points.get(1).setY(minY);
        points.get(2).setX(maxX);
        points.get(2).setY(maxY);
        points.get(3).setX(minX);
        points.get(3).setY(maxY);
    }
    public void stepShowPoint(){
        showPoint = false;
    }
}
