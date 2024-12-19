package team.hiddenark.stickmangame.window;

import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

import java.awt.*;
import java.lang.reflect.Field;
import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import team.hiddenark.stickmangame.GameWindow;
import team.hiddenark.stickmangame.PhysicsObject;


public class WindowHandle extends PhysicsObject {

    private HWND window;

    private Rectangle lastBound;

    private GameWindow gameWindow;

    private boolean minimized;
    private boolean drawDebug = false;

//    private Body body;

    public WindowHandle(GameWindow gameWindow, HWND window){

        this.window = window;
        this.setVisible(true);

        this.gameWindow = gameWindow;

       
                                                                                        
        this.body = new Body();
        
        body.addFixture(makeFixture());
        body.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        Rectangle bounds = getBounds();
        body.translate(gameWindow.toVector2((int)bounds.getCenterX(),(int)bounds.getCenterY()));
        body.setAtRestDetectionEnabled(false);
        
        this.disableBody();

    }
    
   
    
    private BodyFixture makeFixture() {
    	 Rectangle bounds = getBounds();
         lastBound = bounds;
         org.dyn4j.geometry.Rectangle rect = new org.dyn4j.geometry.Rectangle(gameWindow.toPUnits(bounds.width),gameWindow.toPUnits(bounds.height));
         
         BodyFixture fixture = new BodyFixture(rect);
         fixture.setDensity(0.01);
         return fixture;
    }

    @Override
    public void draw(Graphics g){
        g.setColor(enabled? Color.GREEN : Color.RED);

        if (!User32.INSTANCE.IsWindow(window)){
            return;
        }

        if (drawDebug){
            Rectangle bounds = getBounds();
            g.drawRect(bounds.x,bounds.y, bounds.width, bounds.height);
        }

    }

    @Override
    public void tick(double deltaTime){

    	if (!User32.INSTANCE.IsWindow(window)){
            this.remove();
            return;
        }
        
        
        Rectangle bounds = getBounds();
        if (lastBound.x == bounds.x && lastBound.y == bounds.y){
            Point p = gameWindow.toGraphicsPoint(body.getWorldCenter());
            if (enabled)
            User32.INSTANCE.SetWindowPos(window, null, p.x-bounds.width/2, p.y-bounds.height/2 , 0,0, WinUser.SWP_NOSIZE | WinUser.SWP_NOZORDER);
        } else {
            body.getTransform().setTranslation(gameWindow.toVector2((int)bounds.getCenterX(),(int)bounds.getCenterY()));
            body.clearForce();
            body.clearAccumulatedForce();
            body.setLinearVelocity(Vector2.create(0,0));
        }
        if (bounds.width != lastBound.width || bounds.height != lastBound.height) {
            // Remove existing fixtures

            
            this.body.removeAllFixtures();
            this.body.addFixture(this.makeFixture());
            body.updateMass();
            body.getTransform().setTranslation(gameWindow.toVector2((int)bounds.getCenterX(),(int)bounds.getCenterY()));
            
            if (!enabled) {
            	System.out.println("ran");
            	this.disableBody();
            }
            

            // Ensure the body is part of the world

//            System.out.println("Number of fixtures: " + body.getFixtureCount());
        }

        bounds = getBounds();
        
        lastBound = bounds;

        if (bounds.y>gameWindow.getHeight()+50){
            System.out.println(getTitle() + " bellow " + bounds.y);
            User32.INSTANCE.SetWindowPos(window, null, bounds.x, -bounds.height*2 , 0,0, WinUser.SWP_NOSIZE | WinUser.SWP_NOZORDER);
        }



    }

    public void sendToBack(){
        HWND HWND_BOTTOM = new HWND(Pointer.createConstant(1));
        int SWP_NOSIZE = 0x0001;
        int SWP_NOMOVE = 0x0002;
        int SWP_NOACTIVATE = 0x0010;
        User32.INSTANCE.SetWindowPos(getHWND(),  HWND_BOTTOM, 0, 0, 0, 0,
                SWP_NOSIZE | SWP_NOMOVE | SWP_NOACTIVATE);
    }

    public HWND getHWND(){
        return window;
    }

    public Rectangle getBounds(){
        Rectangle bounds = WindowUtils.getWindowLocationAndSize(window);
        bounds.width -= 14;
        bounds.height -= 7;
        bounds.x += 7;
        return bounds;

    }

    public String getTitle() {
        return WindowUtils.getWindowTitle(window);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WindowHandle){
            return ((WindowHandle) obj).window == this.window;
        }
        return false;
    }

    public static boolean isVisible(WinDef.HWND window){
        return User32.INSTANCE.IsWindowVisible(window);
    }

    public static boolean isWindow(WinDef.HWND window){
        return User32.INSTANCE.IsWindow(window);
    }


}
