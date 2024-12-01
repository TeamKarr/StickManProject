package team.hiddenark.stickmangame;

import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;  
import com.sun.jna.platform.win32.WinUser;

import java.awt.*;
import java.lang.reflect.Field;
//import java.lang.reflect.Field;
import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import sun.awt.windows.WComponentPeer;


public class WindowHandle extends PhysicsObject{

    private HWND window;

    private BodyFixture fixture;
    private org.dyn4j.geometry.Rectangle  rect;

    private Rectangle lastBound;

    private GameWindow gameWindow;

    private boolean minimized;

//    private Body body;

    public WindowHandle(GameWindow gameWindow, HWND window){

        this.window = window;
        this.setVisible(true);

        this.gameWindow = gameWindow;

        Rectangle bounds = getBounds();
        lastBound = bounds;
        rect = new org.dyn4j.geometry.Rectangle(gameWindow.toPUnits(bounds.width),gameWindow.toPUnits(bounds.height));

        fixture = new BodyFixture(rect);

        this.body = new Body();
        body.addFixture(fixture);
        body.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        body.translate(gameWindow.toVector2((int)bounds.getCenterX(),(int)bounds.getCenterY()));
        body.setAtRestDetectionEnabled(false);


    }

    @Override
    public void draw(Graphics g){
        g.setColor(Color.RED);

        if (!User32.INSTANCE.IsWindow(window)){
            return;
        }

        Rectangle bounds = getBounds();
        g.drawRect(bounds.x,bounds.y, bounds.width, bounds.height);
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
            User32.INSTANCE.SetWindowPos(window, null, p.x-bounds.width/2, p.y-bounds.height/2 , 0,0, WinUser.SWP_NOSIZE | WinUser.SWP_NOZORDER);
        } else {
            body.getTransform().setTranslation(gameWindow.toVector2((int)bounds.getCenterX(),(int)bounds.getCenterY()));
            body.clearForce();
            body.clearAccumulatedForce();
            body.setLinearVelocity(Vector2.create(0,0));
        }
        if (bounds.width != lastBound.width || bounds.height != lastBound.height) {
            // Remove existing fixtures

            gameWindow.physics.removeBody(body);

            rect = new org.dyn4j.geometry.Rectangle(gameWindow.toPUnits(bounds.width),gameWindow.toPUnits(bounds.height));

            fixture = new BodyFixture(rect);

            this.body = new Body();
            body.addFixture(fixture);
            body.setMass(MassType.FIXED_ANGULAR_VELOCITY);
            body.translate(gameWindow.toVector2((int)bounds.getCenterX(),(int)bounds.getCenterY()));
            body.setAtRestDetectionEnabled(false);
            gameWindow.physics.addBody(body);

            // Ensure the body is part of the world

            System.out.println("Number of fixtures: " + body.getFixtureCount());
        }

        bounds = getBounds();
        lastBound.setLocation(bounds.x,bounds.y);

        if (bounds.y>gameWindow.getHeight()+50){
            System.out.println(getTitle() + " bellow " + bounds.y);
            User32.INSTANCE.SetWindowPos(window, null, bounds.x, -bounds.height*2 , 0,0, WinUser.SWP_NOSIZE | WinUser.SWP_NOZORDER);
        }



    }

    public HWND getHWND(){
        return window;
    }

    public Rectangle getBounds(){
        return WindowUtils.getWindowLocationAndSize(window);

    }

    public String getTitle() {
        return WindowUtils.getWindowTitle(window);
    }




}
