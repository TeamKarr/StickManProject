package team.hiddenark.stickmangame;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameWindow extends JFrame {

    protected Toolkit toolkit = Toolkit.getDefaultToolkit();

    protected ArrayList<GameObject> objects = new ArrayList<GameObject>();
    protected ArrayList<WindowHandle> windows = new ArrayList<WindowHandle>();

    World physics = new World();

    public GameWindow(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setSize(toolkit.getScreenSize());
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);

        ((JComponent) this.getContentPane()).setOpaque(false);
        this.setBackground(new Color(0, 0, 0, 0));

        physics.setGravity(World.EARTH_GRAVITY);
    }

    public void gameLoop(double deltaTime){
//        System.out.println(deltaTime);


        physics.update(deltaTime);

        objects.forEach((o) -> o.tick(deltaTime));

        objects.removeIf(GameObject::isMarkedForRemoval);
    }

    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Call draw methods for objects
        for (GameObject o : objects) {
            if (o.isVisable()) {
                o.draw(g2d);
            }
        }

        g2d.dispose();
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
        Timer gameLoop = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double deltaTime = (System.nanoTime()-last)/1.0E9;
                last = System.nanoTime();
                gameLoop(deltaTime);
                repaint();
            }
        });
        gameLoop.start();
    }

}
