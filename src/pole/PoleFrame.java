package pole;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class PoleFrame extends JFrame implements Runnable {
    // next three are for double-buffering
    Dimension      offDimension;
    Image          offImage;
    Graphics       offGraphics;
    Thread         animatorThread;
    int            delay;

    protected Pole pole = new Pole();

    public void end() {
        try {
            animatorThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void init(double... data) {

        pole.init();
        pole.setData(data);

        // Event handlers
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
                    pole.setAction(-1);
                } else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
                    pole.setAction(0);
                    pole.resetPole();
                } else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                    pole.setAction(1);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pole.setAction(0);
                // System.out.println("mouse is " + e.getModifiers());
            }

        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // System.out.println("keycode is " + e.getKeyCode() + " id " +
                // e.getID());
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    pole.setAction(-1);
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    pole.setAction(1);
                else if (e.getKeyChar() == ' ') {
                    pole.setAction(0);
                    pole.resetPole();
                }
                // System.out.println("in keyDown action is " + action);
            }
        });

        int fps = 60;
        delay = (fps > 0) ? (1000 / fps) : 100;

        setVisible(true);
        setSize(600, 600);
    }

    @Override
    public void paint(Graphics g) {
        update(g);
    }

    public int pixDX(Dimension d, double v) {
        return (int) Math.round(v / 5.0 * d.width);
    }

    public int pixDY(Dimension d, double v) {
        return (int) Math.round(-v / 5.0 * d.height);
    }

    public int pixX(Dimension d, double v) {
        return (int) Math.round((v + 2.5) / 5.0 * d.width);
    }

    public int pixY(Dimension d, double v) {
        return (int) Math.round(d.height - (v + 2.5) / 5.0 * d.height);
    }

    @Override
    public void run() {
        // This is the animation loop.
        // Remember the starting time.
        long startTime = System.currentTimeMillis();

        while (Thread.currentThread() == animatorThread) {
            if (!pole.step())
                stop();

            // Delay depending on how far we are behind.
            repaint();
            try {
                startTime += delay;
                Thread.sleep(Math.max(0,
                        startTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
            }
        }
        setVisible(false);
        pole.gameOver();
    }

    public void setController(PoleController controller) {
        pole.setController(controller);
    }

    public void start(double... data) {
        init(data);

        // Start animating!
        if (animatorThread == null) {
            animatorThread = new Thread(this);
        }
        animatorThread.start();
    }

    public void stop() {
        // Stop the animating thread.
        animatorThread = null;
        // Get rid of the objects necessary for double buffering.
        offGraphics = null;
        offImage = null;
    }

    @Override
    public void update(Graphics g) {
        Color bg = getBackground();
        Color fg = getForeground();
        Dimension d = getSize();
        Color cartColor = new Color(0, 20, 255);
        Color arrowColor = new Color(255, 255, 0);
        Color trackColor = new Color(100, 100, 50);

        // Create the offscreen graphics context, if no good one exists.
        if ((offGraphics == null)
                || (d.width != offDimension.width)
                || (d.height != offDimension.height)) {
            offDimension = d;
            offImage = createImage(d.width, d.height);
            offGraphics = offImage.getGraphics();
        }

        // Erase the previous image.
        offGraphics.setColor(getBackground());
        offGraphics.fillRect(0, 0, d.width, d.height);

        // Draw Track.
        double xs[] = { -2.5, 2.5, 2.5, 2.3, 2.3, -2.3, -2.3, -2.5 };
        double ys[] = { -0.4, -0.4, 0., 0., -0.2, -0.2, 0, 0 };
        int pixxs[] = new int[8], pixys[] = new int[8];
        for (int i = 0; i < 8; i++) {
            pixxs[i] = pixX(d, xs[i]);
            pixys[i] = pixY(d, ys[i]);
        }
        offGraphics.setColor(trackColor);
        offGraphics.fillPolygon(pixxs, pixys, 8);

        // Draw message
        String msg = "Left Mouse Button: push left    Right Mouse Button: push right     Middle Button: PANIC";
        offGraphics.drawString(msg, 20, d.height - 20);

        // Draw cart.
        offGraphics.setColor(cartColor);
        offGraphics.fillRect(pixX(d, pole.pos - 0.2), pixY(d, 0), pixDX(d, 0.4), pixDY(d, -0.2));

        // Draw pole.
        // offGraphics.setColor(cartColor);
        double shift = 0.3;
        offGraphics.drawLine(pixX(d, pole.pos - shift / 2), pixY(d, 0),
                pixX(d, pole.pos - shift / 2 + Math.sin(pole.angle) * Pole.poleLength),
                pixY(d, Pole.poleLength * Math.cos(pole.angle)));

        offGraphics.drawLine(pixX(d, pole.pos + shift / 2), pixY(d, 0),
                pixX(d, pole.pos + shift / 2 + Math.sin(pole.angle2) * Pole.pole2Length),
                pixY(d, Pole.pole2Length * Math.cos(pole.angle2)));

        // Draw action arrow.
        if (pole.action != 0) {
            int signAction = (pole.action > 0 ? 1 : (pole.action < 0) ? -1 : 0);
            int tipx = pixX(d, pole.pos + 0.2 * signAction);
            int tipy = pixY(d, -0.1);
            offGraphics.setColor(arrowColor);
            offGraphics.drawLine(pixX(d, pole.pos), pixY(d, -0.1), tipx, tipy);
            offGraphics.drawLine(tipx, tipy, tipx - 4 * signAction, tipy + 4);
            offGraphics.drawLine(tipx, tipy, tipx - 4 * signAction, tipy - 4);
        }

        // Last thing: Paint the image onto the screen.
        g.drawImage(offImage, 0, 0, this);

    }

}
