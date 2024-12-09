package team.hiddenark.stickmangame;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.listener.ContactListener;

import team.hiddenark.stickmangame.Goal.GoalGen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Queue;

public class Stickman extends PhysicsObject implements Thinker{

    private int x,y,w,h;

    private GameWindow window;

    private Color color;

    private ArrayList<Goal> goals = new ArrayList<Goal>();
    
    public GoalGen goalGen = new GoalGen(this);

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
        f.setFriction(3);
//        f.setRestitution(0.4);

        b.addFixture(f);
        b.translate(window.toVector2(x,y));
        b.setAtRestDetectionEnabled(false);
        b.setLinearDamping(2);

        b.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        this.body = b;
        
        this.window.physics.addContactListener(new ContactListenerInstance());
        
        this.setDefaultFilter();

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
        	
        	boolean runNext;
        	do {
        		Goal currentGoal = goals.get(0);
                
        		runNext = currentGoal.runWithNext;
                
                if (currentGoal.isGoalCompleted()){
                    currentGoal.onComplete();
                    goals.remove(0);
                } else {
                    currentGoal.act();
                }
        	} while (runNext);
            
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
//    	if (!onGround) return;

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
    
    public void createPushWindowGoals(WindowHandle wndh, int direction) {
    	java.awt.Rectangle bounds = wndh.getBounds();
    	int runUpDistance = 200;
    	int runThroughDistance = 100;
    	int startX = (direction > 0? 
    			bounds.x-runUpDistance:
    				bounds.x+bounds.width+runUpDistance);
    	int endX = (direction > 0? 
    			window.getWidth()+runThroughDistance:
    				-runThroughDistance);
    	
    	 addGoal(goalGen.createMoveXGoal(startX, 5, 0.5, 20, () -> {
    		 wndh.enableBody();
    	 }));
    	 addGoal(goalGen.createMoveXGoal(endX, 5, 0.5, 20));
    	
    	
    	
    	// mobe to side
    	// drop window
    	// wait for
    	// 
    	// move to side of window
    	// 
    	
    	
//    	wndh.enableBody();
    	
    }
    
    public void tryJump(double strength) {
    	if(onGround) {
    		System.out.println("on ground");
    		jump(strength);
    	} else {
    		System.out.println("not on ground");
    	}
    }
    
    public void jump(double strength) {
    	this.body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,strength));
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
    

	private boolean onGround;
    
    private class ContactListenerInstance implements ContactListener<Body> {

		@Override
		public void begin(ContactCollisionData<Body> collision, Contact contact) {
			// TODO Auto-generated method stub
			
			if (collision.getBody1() == body || collision.getBody2() == body) {
				onGround = true;
			}
			
			
		}

		@Override
		public void persist(ContactCollisionData<Body> collision, Contact oldContact, Contact newContact) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void end(ContactCollisionData<Body> collision, Contact contact) {
			// TODO Auto-generated method stub
			if (collision.getBody1() == body || collision.getBody2() == body) {
				onGround = false;
			}
		}

		@Override
		public void destroyed(ContactCollisionData<Body> collision, Contact contact) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void collision(ContactCollisionData<Body> collision) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void preSolve(ContactCollisionData<Body> collision, Contact contact) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postSolve(ContactCollisionData<Body> collision, SolvedContact contact) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
