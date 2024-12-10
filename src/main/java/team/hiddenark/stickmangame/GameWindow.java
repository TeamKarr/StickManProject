package team.hiddenark.stickmangame;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import team.hiddenark.stickmangame.window.WindowHandle;
import team.hiddenark.stickmangame.window.WindowHandleList;

import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.util.ArrayList;

public class GameWindow extends JFrame implements NativeKeyListener {

    protected Toolkit toolkit = Toolkit.getDefaultToolkit();

    protected ArrayList<GameObject> objects = new ArrayList<GameObject>();
    protected ArrayList<GameObject> pendingObjects = new ArrayList<GameObject>();
    protected ArrayList<Body> pendingBody = new ArrayList<Body>();
    protected WindowHandleList windows = new WindowHandleList(this);

    World<Body> physics = new World<Body>();
    private boolean running = false; // Control for the game loop

    private WinDef.HWND mainWindow;
    private boolean reAddOpenWindows;

    public GameWindow(String title) {
        super(title);
        this.setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setSize(toolkit.getScreenSize().width,toolkit.getScreenSize().height-getBottomBarHeight());
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
//        this.setFocusableWindowState(false);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch(NativeHookException e){
            e.printStackTrace();
        }


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


        // get all windows


    }

//    public void manageWindows(){
////        WindowHandleGetter getter = new WindowHandleGetter(this);
////        mainWindow = getter.getWindowId();
////
////        if (this instanceof Component){
////            System.out.println("Component");
////        }
//
//        User32.INSTANCE.EnumWindows((hWnd, data) -> {
//            if (User32.INSTANCE.IsWindow(hWnd) && User32.INSTANCE.IsWindowVisible(hWnd)) {
//
////
//                String title = WindowUtils.getWindowTitle(hWnd);
//                if (!title.isEmpty() && !title.contains("Windows Input Experience") && !title.contains("Program Manager")){
//                    WindowHandle window = new WindowHandle(this,hWnd);
//                    System.out.println(window.getTitle());
//                    this.windows.add(window);
//                    this.addObject(window);
//                }
//
//            }
//            return true; // Continue enumeration
//        }, null);
//        System.out.println(windows.size());
//    }


    public int getBottomBarHeight(){
        // Get the available screen size (excluding the taskbar)
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        // Calculate the taskbar height (bottom inset typically represents the taskbar)
        return screenInsets.bottom;
    }

    public void gameLoop(double deltaTime){
//        System.out.println(deltaTime);
        objects.addAll(pendingObjects);
        pendingObjects = new ArrayList<GameObject>();
        pendingBody.forEach((b) -> this.physics.addBody(b));
        pendingBody = new ArrayList<Body>();

        physics.update(deltaTime);

        objects.forEach((o) -> o.tick(deltaTime));

        objects.forEach((o) -> {
            if (o instanceof PhysicsObject){
                if (o.isMarkedForRemoval())
                    physics.removeBody(((PhysicsObject) o).body);
            }
        });

        if(reAddOpenWindows){
            windows.addOpenWindows(false);
            reAddOpenWindows = false;
        }

        windows.removeIf(GameObject::isMarkedForRemoval);
        objects.removeIf(GameObject::isMarkedForRemoval);
    }




    public void addObject(GameObject o){
        pendingObjects.add(o);
    }

    public void addPhysics(Body b){
        pendingBody.add(b);
    }

    public void addObject(PhysicsObject p){
        addObject((GameObject) p);
        addPhysics(p.getBody());
    }

    double scale = 500;
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



        windows.addOpenWindows(false);
        windows.listenForNewWindows();
        setVisible(true);
        mainWindow = User32.INSTANCE.GetForegroundWindow();


        System.out.println(WindowUtils.getWindowTitle(mainWindow));
        Timer gameLoop = new Timer(16, e -> {
            double deltaTime = (System.nanoTime() - last) / 1.0E9;
            last = System.nanoTime();
            gameLoop(deltaTime);
            repaint();
        });
        gameLoop.start();
    }

    public void runAddAll() {
        reAddOpenWindows = true;
    }
}
