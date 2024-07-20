import java.awt.*;

public class Point {

    private int x;
    private int y;
    public static final int RADIUS = 7;

    public Point(int x,int y){
        this.x = x;
        this.y = y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setX(int newX){
        this.x = newX;
    }
    public void setY(int newY){
        this.y = newY;
    }
    public void paint(Graphics graphics){
        graphics.setColor(Color.WHITE);
        int diameter = 2 * RADIUS;
        int x = this.x - RADIUS;
        int y = this.y - RADIUS;
        graphics.drawOval(x,y, diameter, diameter);
        graphics.setColor(Color.CYAN);
        graphics.fillOval(x,y,diameter,diameter);
    }
}
