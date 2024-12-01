package team.hiddenark.stickmangame;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Queue;

public class Stickman extends PhysicsObject implements Thinker{

    private int x,y,w,h;

    private GameWindow window;

    private Color color;

    private ArrayList<Goal> goals = new ArrayList<Goal>();

    public Stickman(GameWindow window, int x, int y, int s, Color color){
        this.x = x;
        this.y = y;
        this.w = s;
        this.h = (int)(s*3);
        this.color = color;
        this.window = window;

        Body b = new Body();

        Rectangle rect = new Rectangle(window.toPUnits(w),window.toPUnits(h));

        BodyFixture f = new BodyFixture(rect);
        f.setFriction(2);
//        f.setRestitution(0.4);

        b.addFixture(f);
        b.translate(window.toVector2(x,y));
        b.setAtRestDetectionEnabled(false);


//        b.setLinearDamping(4);

        b.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        this.body = b;

        this.setVisible(true);
    }

    public void addGoal(Goal g){
        this.goals.add(g);
    }

    public void tick(double deltaTime){
        Point p = window.toGraphicsPoint(body.getWorldCenter());
        this.x = p.x-this.w/2;
        this.y = p.y-this.h/2;

        if (!goals.isEmpty()){
            Goal currentGoal = goals.get(0);
            if (currentGoal.isGoalCompleted()){
                currentGoal.onComplete();
                goals.remove(0);
            } else {
                currentGoal.act();
            }
        }



    }

    // Actions

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void moveSide(double targetVelocity, double accelerationRate) {

        System.out.println(targetVelocity);
        // Get the current velocity of the object
        Vector2 currentVelocity = this.body.getLinearVelocity();

        // Extract the horizontal velocity (x-component)
        double currentVelocityX = currentVelocity.x;

        // Calculate the difference between the current and target velocity
        double velocityDifference = targetVelocity - currentVelocityX;

        // Calculate the acceleration (clamped to the maximum allowed change)
        double acceleration = Math.signum(velocityDifference) * Math.min(Math.abs(velocityDifference), accelerationRate);

        // Update the horizontal velocity
        double newVelocityX = currentVelocityX + acceleration;

        // Set the new velocity, keeping the vertical velocity unchanged
        this.body.setLinearVelocity(new Vector2(newVelocityX, currentVelocity.y));
//        System.out.println(new Vector2(newVelocityX, currentVelocity.y));
    }


    public void moveTo(double targetX) {
        double maxVelocity = 40.0;    // Maximum velocity
        double easeStart = 50.0;     // Distance for ease-in
        double easeEnd = 30.0;       // Distance for ease-out

        // Current position and velocity
        double currentX = this.body.getWorldCenter().x;
        double currentVelocityX = this.body.getLinearVelocity().x;

        // Calculate the distance to the target
        double distanceToTarget = Math.abs(targetX - currentX);

        // Calculate the easing factor
        double easingFactor;
        if (distanceToTarget < easeStart) {
            // Ease-in: Increase velocity proportionally
            easingFactor = distanceToTarget / easeStart;
        } else if (distanceToTarget > easeEnd) {
            // Ease-out: Decrease velocity proportionally
            easingFactor = (easeEnd - distanceToTarget) / easeEnd;
        } else {
            // Constant speed
            easingFactor = 1.0;
        }

        // Calculate the velocity
        double targetVelocity = maxVelocity * easingFactor;

        // Set the velocity in the direction of the target
        double newVelocityX = targetX > currentX ? targetVelocity : -targetVelocity;

        // Update the body's velocity
        this.body.setLinearVelocity(new Vector2(newVelocityX, this.body.getLinearVelocity().y));
    }




    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x,y,w,h);
    }
}
