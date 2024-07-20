import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Controller implements MouseListener {

    private boolean clickPressed;
    private Point pointPres;
    private int mouseX = 0;
    private int mouseY = 0;
    private SquareFilter square;
    private EditorScreen editorScreen;


    public Controller(SquareFilter square,EditorScreen editorScreen) {
        this.square = square;
        this.editorScreen = editorScreen;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && !clickPressed) {
            for (int i = 0; i < square.getPoints().size(); i++) {
                if (square.getPoint(e.getX(), e.getY()) != null) {
                    pointPres = square.getPoint(e.getX(), e.getY());
                    updatePointPosition();
                    break;
                }
            }
            clickPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            clickPressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void updatePointPosition() {
        Thread updateThread = new Thread(() -> {
            while (clickPressed) {
                try {
                mouseX = editorScreen.getMousePosition().x;
                mouseY = editorScreen.getMousePosition().y;

                synchronized (this) {
                    pointPres.setX(mouseX);
                    pointPres.setY(mouseY);
                }
                    Thread.sleep(10);
                } catch (Exception ex) {
                    //ignore
                }
            }
            editorScreen.setConvert(false);
        });
        updateThread.start();
    }
}