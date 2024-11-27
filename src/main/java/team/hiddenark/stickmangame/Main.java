package team.hiddenark.stickmangame;


/* TODO Create classes
AbstractWindow class
- the more technical frame that holds everything
- figure out how to make it work on multiple screens.
- Handle physics
WindowHandle class
- Contains the handle
Abstract GameObject class
- Tick
- Draw
- Visible
- Mark for Removal
*/

import com.sun.jna.*;
import com.sun.jna.platform.mac.CoreFoundation;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Rectangle;

import javax.swing.*;
import java.awt.event.KeyEvent;
import com.sun.jna.Library;
import com.sun.jna.Native;
import team.hiddenark.stickmangame.mac.JNAUtil;
import team.hiddenark.stickmangame.mac.JNAUtil.*;

public class Main extends GameWindow {

    public Main(){
        super("Stickmen");

        Rectangle rect = new Rectangle(this.getWidth(),2);
        Body b = new Body();
        b.addFixture(rect);
        b.translate(this.toPUnits(this.getWidth())/2.0,-1);
        addPhysics(b);



    }

    @Override
    public void gameLoop(double deltaTime) {
        super.gameLoop(deltaTime);
//
//        System.out.println(this.getBottomBarHeight());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        PhysicsBall ball = new PhysicsBall(this,getMousePosition().x,getMousePosition().y,20);

        this.addObject(ball);
    }

    public static void main(String[] args) {
//        System.out.println("Hello world!");
//        SwingUtilities.invokeLater(() -> {
//            Main game = new Main();
//            game.start();
//        });
        // Call CGWindowListCopyWindowInfo to get all open windows


        CoreFoundation.CFDictionaryRef windowInfo = Quartz.INSTANCE.CGWindowListCopyWindowInfo(
                Quartz.kCGWindowListOptionAll | Quartz.kCGWindowListExcludeDesktopElements, 0);

        if (windowInfo == null) {
            System.out.println("No windows found or unable to retrieve window info.");
            return;
        }

        // Convert the result to a CFArrayRef
        CoreFoundation.CFArrayRef windowArray = new CoreFoundation.CFArrayRef(windowInfo.getPointer());
        int windowCount = windowArray.getCount();
        System.out.println("Number of windows: " + windowCount);

        // Iterate through each window and print details
        for (int i = 0; i < windowCount; i++) {
            Pointer windowPointer = windowArray.getValueAtIndex(i);
            CoreFoundation.CFDictionaryRef windowDict = new CoreFoundation.CFDictionaryRef(windowPointer);

            System.out.println("Window Attributes:");
            JNAUtil.printDictionary(windowDict);
            System.out.println("--------------------");
        }


    }

}
