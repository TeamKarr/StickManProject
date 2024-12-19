package team.hiddenark.stickmangame.stickman;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.listener.ContactListener;

import team.hiddenark.stickmangame.GameWindow;
import team.hiddenark.stickmangame.Main;
import team.hiddenark.stickmangame.PhysicsObject;
import team.hiddenark.stickmangame.brain.*;
import team.hiddenark.stickmangame.window.WindowHandle;

import java.awt.*;
import java.util.Random;

public class StickmanMind extends PhysicsObject implements Thinker {

    private int x,y,w,h;

    private GameWindow window;
    private	Stickman stickman;
    private Color color;

//    private ArrayList<Goal> goals = new ArrayList<Goal>();
    
    public GoalGen goalGen = new GoalGen(this);

    public GoalQue goals = new GoalQue();

    private Random random;

    public StickmanMind(GameWindow window, int x, int y, int s, Color color, boolean smallHead){
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
        b.setLinearDamping(01);

        b.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        this.body = b;
        
        this.window.physics.addContactListener(new ContactListenerInstance());
        
        this.setDefaultFilter();

        this.setVisible(true);
        
        stickman = new Stickman(x,y,smallHead);
        stickman.color = color;
        
        random = new Random();
    }

    public void addGoal(Goal g){
        this.goals.add(g);
    }
    
    private int sign(double a) {
    	if (a > 0) return 1;
    	if (a < 0) return -1;
    	return 0;
    }

    private double boredom = 0;
    private double anger = 0;
    private double curiosity = 0;



    public void tick(double deltaTime){
        Point p = window.toGraphicsPoint(body.getWorldCenter());
        this.x = p.x-this.w/2;
        this.y = p.y-this.h/2;
        stickman.velocityX = window.toGUnits(body.getLinearVelocity().x)/75;
//        stickman.velocityX = sign(stickman.velocityX)*Math.log(Math.abs(stickman.velocityX));
        System.out.println((stickman.pushing?(stickman.pushReach/2-5)*stickman.getDirection():0));
        stickman.setX(getX()+w/2+(stickman.pushing?(stickman.pushReach/2-5)*stickman.getDirection():0));
        stickman.setY(y+h);
//        goals.act();


        if (goals.isEmpty()){
            if (boredom > 50 && random.nextInt(10) > 8){
                createPushWindowGoals(window.getWindowHandleList().getTop(), 1, 2, random.nextInt(2)==1?1:-1);
            } else if (boredom > 10 && random.nextInt(20) > 8){
                this.addGoal(wanderTo(random.nextInt(window.getWidth()),random.nextDouble(0.5,2)));
            }

        }



        if(goals.act()){
            boredom += 0.05;
        } else {
            boredom += 0.2;
        }

        boredom = clamp(boredom, 0.0, 100.0);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private Goal wanderTo(int x, double speed){
        return this.goalGen.createMoveXGoal(x,speed, 0.5, 20,() -> {
            boredom -= random.nextDouble(1,10);
        });
    }

    @Override
    public void draw(Graphics g) {
//        stickman.draw((Graphics2D)g);
//        g.setColor(color);
//        g.fillRect(x,y,w,h);
        
        
        stickman.pushReach = (int)(this.w*0.6);
    	stickman.draw((Graphics2D)g);

        // Antialiasing for smoother text/graphics
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = window.getWidth();

        // Draw boredom bar
        drawBar(g2d, "Boredom", boredom, boredomColor,
                PANEL_PADDING, PANEL_PADDING, width - 2 * PANEL_PADDING);

        // Draw anger bar
        int angerY = PANEL_PADDING + BAR_HEIGHT + BAR_SPACING;
        drawBar(g2d, "Anger", anger, angerColor,
                PANEL_PADDING, angerY, width - 2 * PANEL_PADDING);

        // Draw curiosity bar
        int curiosityY = angerY + BAR_HEIGHT + BAR_SPACING;
        drawBar(g2d, "Curiosity", curiosity, curiosityColor,
                PANEL_PADDING, curiosityY, width - 2 * PANEL_PADDING);


    }

    private final Color boredomColor = new Color(0, 0, 255); // Blue
    private final Color angerColor = new Color(255, 0, 0);   // Red
    private final Color curiosityColor = new Color(0, 200, 0); // Green

    private static final int BAR_HEIGHT = 30;
    private static final int BAR_SPACING = 20;
    private static final int TEXT_SPACING = 10;
    private static final int PANEL_PADDING = 20;
    private static final int MAX_VALUE = 100;

    private void drawBar(Graphics2D g2d, String label, double value, Color barColor,
                         int x, int y, int maxWidth) {
        // Draw the label
        g2d.setColor(Color.BLACK);
        g2d.drawString(label + ": " + (int) value, x, y - TEXT_SPACING);

        // Calculate bar width based on value
        int filledWidth = (int) ((value / MAX_VALUE) * maxWidth);

        // Draw the background bar (light gray)
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(x, y, maxWidth, BAR_HEIGHT);

        // Draw the filled portion of the bar
        g2d.setColor(barColor);
        g2d.fillRect(x, y, filledWidth, BAR_HEIGHT);

        // Draw outline
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, maxWidth, BAR_HEIGHT);
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

//        System.out.println(targetVelocity);
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
        
       
//        System.out.println(window.toGUnits(newVelocityX));

        // Set the new velocity, keeping the vertical velocity unchanged
        this.body.setLinearVelocity(new Vector2(newVelocityX, currentVelocity.y));
//        System.out.println(new Vector2(newVelocityX, currentVelocity.y));
    }
    
    public void createPushWindowGoals(WindowHandle wndh, int direction, double speed, double pushSpeed) {
    	java.awt.Rectangle bounds = wndh.getBounds();
    	int runUpDistance = 200;
    	int runThroughDistance = 100;
    	int startX = (direction > 0? 
    			bounds.x-runUpDistance:
    				bounds.x+bounds.width+runUpDistance);
    	int endX = (direction > 0? 
    			window.getWidth()+runThroughDistance:
    				-runThroughDistance);

        //    	     System.out.println("Enabling: " + wndh.getTitle());
        addGoal(goalGen.createMoveXGoal(startX, speed, 0.5, 20, () -> {
            wndh.enableBody();
            stickman.pushing = true;
            boredom-=25;
        }));
        addGoal(goalGen.createMoveXGoal(endX, pushSpeed, 0.5, 20, ()->{
            wndh.disableBody();
            wndh.sendToBack();
            stickman.pushing = false;
            boredom-=75;
        }));
    	
    	
    	

    	
    }

    public boolean waiting = false;
    
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
