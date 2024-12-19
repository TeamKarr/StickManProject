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

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.sun.jna.*;
import com.sun.jna.platform.mac.CoreFoundation;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import com.sun.jna.Library;
import com.sun.jna.Native;
import team.hiddenark.stickmangame.mac.JNAUtil;
import team.hiddenark.stickmangame.mac.JNAUtil.*;
import team.hiddenark.stickmangame.stickman.StickmanMind;

public class Main extends GameWindow {

    StickmanMind man;

    private final Set<Integer> keysPressed = new HashSet<>();

    public Main(){
        super("Stickmen");



        Rectangle rect = new Rectangle(this.toPUnits(this.getWidth()*5),this.toPUnits(100));

        BodyFixture f = new BodyFixture(rect);
        f.setFriction(1);
        f.setRestitution(0.1);

        Body ground = new Body();
        ground.addFixture(f);
        ground.translate(this.toPUnits(this.getWidth())/2.0,-this.toPUnits(50 ));
        ground.setMass(MassType.INFINITE);
        addPhysics(ground);

        man = new StickmanMind(this,this.getWidth()/2,this.getHeight()-200,60, Color.ORANGE, false);


        this.addObject(man);
        System.out.println(man);
        
        
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(this.getWidth()/2, 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createWaitGoal(5000));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(this.getWidth(), 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(0, 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(this.getWidth()/4, 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(0, 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(this.getWidth()/4, 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(0, 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(this.getWidth()/4, 5, 0.5, 20));
//        man.addGoal((new Goal.GoalGen(man)).createMoveXGoal(0, 5, 0.5, 20));

    }

    @Override
    public void gameLoop(double deltaTime) {
        super.gameLoop(deltaTime);

        if (keysPressed.contains(NativeKeyEvent.VC_LEFT)) {
            man.moveSide(-2, 0.5);
        } else if (keysPressed.contains(NativeKeyEvent.VC_RIGHT)){
            man.moveSide(2,0.5);
        } 
        if (keysPressed.contains(NativeKeyEvent.VC_UP)){
            man.tryJump(2);
        }
//
//        System.out.println(this.getBottomBarHeight());
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        keysPressed.add(e.getKeyCode());
        if (keysPressed.contains(NativeKeyEvent.VC_DOWN))
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_SPACE -> {
                Point p = MouseInfo.getPointerInfo().getLocation();
                PhysicsBall ball = new PhysicsBall(this, p.x, p.y, 20);
                this.addObject(ball);
            }
            case NativeKeyEvent.VC_1 -> {
                man.createPushWindowGoals(windows.getTop(), 1, 2, 1);
                man.addGoal(man.goalGen.createMoveXGoal(getWidth() / 2, 2, 0.5, 20));
            }
            case NativeKeyEvent.VC_2 -> {
                man.createPushWindowGoals(windows.getTop(), -1, 2, 1);
                man.addGoal(man.goalGen.createMoveXGoal(getWidth() / 2, 2, 0.5, 20));
            }
            case NativeKeyEvent.VC_3 -> {
                windows.getTop().sendToBack();
            }
            case NativeKeyEvent.VC_0 -> {
                if (man.waiting){
                    man.goals.remove(0);
                    man.waiting = false;
                } else {
                    man.goals.add(0,man.goalGen.createWaitForGoal(()->false));
                    man.waiting = true;
                }

            }
            case NativeKeyEvent.VC_DELETE -> man.goals.clear();
            case NativeKeyEvent.VC_OPEN_BRACKET -> windows.forEach(PhysicsObject::enableBody);
            case NativeKeyEvent.VC_CLOSE_BRACKET -> windows.forEach(PhysicsObject::disableBody);
        }

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main game = new Main();
            game.start();
        });
        // Call CGWindowListCopyWindowInfo to get all open windows




    }

}
