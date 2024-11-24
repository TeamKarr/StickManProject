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

import javax.swing.*;

public class Main extends GameWindow {

    public Main(){
        super("Stickmen");

        PhysicsBall ball = new PhysicsBall(this,200,200,20);

        this.addObject(ball);


    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        SwingUtilities.invokeLater(() -> {
            Main game = new Main();
            game.start();
        });

    }
}