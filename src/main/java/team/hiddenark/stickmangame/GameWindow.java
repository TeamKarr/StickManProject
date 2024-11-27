package team.hiddenark.stickmangame;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.PointerByReference;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameWindow extends JFrame implements KeyListener {

    protected Toolkit toolkit = Toolkit.getDefaultToolkit();

    protected ArrayList<GameObject> objects = new ArrayList<GameObject>();
    protected ArrayList<WindowHandle> windows = new ArrayList<WindowHandle>();

    World physics = new World();
    private boolean running = false; // Control for the game loop



    public GameWindow(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setSize(toolkit.getScreenSize());
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.rootPane.

        addKeyListener(this);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Enable anti-aliasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw game objects
                for (GameObject o : objects) {
                    if (o.isVisable()) {
                        o.draw(g2d);
                    }
                }
            }
        };
        panel.setDoubleBuffered(true);
        panel.setOpaque(false);
        setContentPane(panel);
        this.rootPane.setDoubleBuffered(true);
        ((JComponent) this.getContentPane()).setOpaque(false);
        this.setBackground(new Color(0, 0, 0, 0));

        physics.setGravity(World.EARTH_GRAVITY);



    }



    public int getBottomBarHeight(){
        // Get the available screen size (excluding the taskbar)
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        // Calculate the taskbar height (bottom inset typically represents the taskbar)
        return screenInsets.bottom;
    }

    public void gameLoop(double deltaTime){
//        System.out.println(deltaTime);


        physics.update(deltaTime);

        objects.forEach((o) -> o.tick(deltaTime));

        objects.removeIf(GameObject::isMarkedForRemoval);
    }




    public void addObject(GameObject o){
        this.objects.add(o);
    }

    public void addPhysics(Body b){
        this.physics.addBody(b);
    }

    public void addObject(PhysicsObject p){
        addObject((GameObject) p);
        addPhysics(p.getBody());
    }

    double scale = 100;
    public Point toGraphicsPoint(Vector2 v){
        return new Point(toGUnits(v.x),this.getHeight()-toGUnits(v.y));
    }
    public Vector2 toVector2(Point p){
        return new Vector2(toPUnits(p.x), toPUnits(this.getHeight()-p.y));
    }

    public Point toGraphicsPoint(double x, double y){
        return new Point(toGUnits(x),this.getHeight()-toGUnits(y));
    }
    public Vector2 toVector2(int x, int y){
        return new Vector2(toPUnits(x), toPUnits(this.getHeight()-y));
    }

    public int toGUnits(double pUnits){
        return (int)(pUnits*scale);
    }

    public double toPUnits(int gUnits){
        return gUnits/scale;
    }

    private long last = System.nanoTime();


    public void start(){
        setVisible(true);
        Timer gameLoop = new Timer(16, e -> {
            double deltaTime = (System.nanoTime() - last) / 1.0E9;
            last = System.nanoTime();
            gameLoop(deltaTime);
            repaint();
        });
        gameLoop.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
