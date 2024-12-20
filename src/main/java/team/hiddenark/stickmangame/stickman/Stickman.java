package team.hiddenark.stickmangame.stickman;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;



public class Stickman {
	public boolean jump = false;
	public int strokeSize=7;
	public int ground = 0;
	public Color color = new Color(0,0,0);
	Vector2dDouble position = new Vector2dDouble(0.0,0.0);
	public void setY(int y){
		position.y=y;
	}
	public int getY(){
		return (int)position.y;
	}
	public void setX(int x){
		
		position.x=x;
	}
	public int getX(){
		return (int)position.x;
	}
	public void setStrokeSize(int strokeSize) {
		this.strokeSize=strokeSize;
	}
	Joint hip, lknee,rknee, lfoot, rfoot, lelbow, shoulder,relbow,lhand,rhand;
	Joint backArch;
	Head head;
	double rotation = 0;
	AnimationHandler ah;
	public Stickman(int x, int y, boolean headType) {
		this.position = new Vector2dDouble(x,y);
		//lower body
		hip = new Joint((int)position.x,(int)position.y);
		//left leg
		lknee = new Joint(Math.toRadians(80),45,hip);
		lfoot = new Joint(Math.toRadians(90),35,lknee);
		
		
		
		//right leg 
		rknee = new Joint(Math.toRadians(350),45,hip);
		rfoot = new Joint(Math.toRadians(90),35,rknee);
		//upper body
		backArch = new Joint(Math.toRadians(-90),25,hip);
		shoulder = new Joint(Math.toRadians(-60),25,backArch);
		
		//left arm
		lelbow= new Joint(Math.toRadians(0),25,shoulder);
		lhand= new Joint(Math.toRadians(-90),25,lelbow);
		//right arm
		relbow= new Joint(Math.toRadians(180),25,shoulder);
		rhand= new Joint(Math.toRadians(90),25,relbow);
		head = new Head(shoulder, headType);
		head.setTheta(Math.toRadians(0));
		
		ah = new AnimationHandler();
		
	}
	
	public double velocityX = 0;
	public double maxSpeed = 5;
	private int tick;
	public boolean action=false;
	private double targetVelocity = 0;
	private GaitCycle moveAnimation = new GaitCycle("run/walk",velocityX, (int)Math.signum(velocityX), this);
	public double velocityY=0;
	private Direction dir;
	public boolean pushing = false;
	public int pushReach = 40;
	
	public int getDirection(){
		if (velocityX > 0){
			return -1;
		} else{
			return 1;
		}
	}
	public void draw(Graphics2D window) {

		moveAnimation.velocity = velocityX;
		
		if(velocityX!=0) {
			ah.setCurrentAnimation(moveAnimation);
		}else {
//			System.out.println("idle");
			moveAnimation = new GaitCycle("run/walk",velocityX, (int)Math.signum(velocityX), this);
			ah.setCurrentAnimation(new Idle("idle",this));

		}

		ah.tick();
	    hip.calculateAllPos();

	    // Draw joints for the legs and arms
	    
	    //left leg
	    window.setColor(color);
	    //window.setColor(new Color(255, 0, 0));
	    drawJoint(window, hip, lknee, lfoot, -0.1);
	    //right leg
	    //window.setColor(new Color(0, 0, 255));
	    drawJoint(window, hip, rknee, rfoot, -0.1);
	    //window.setColor(new Color(255, 100, 0));
	    drawJoint(window, shoulder, lelbow, lhand, -0.1);
	    drawJoint(window, shoulder, relbow, rhand, -0.1);

	    // Draw joint for the back/spine
	    drawJoint(window, hip, backArch, shoulder, 0.5);

	    // Draw the head
	    head.draw(window);
	}
	private void drawJoint(Graphics2D window, Joint anchor1, Joint joint, Joint anchor2, double sharpness) {
		window.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		CubicCurve2D q = new CubicCurve2D.Double();
		
		// draw QuadCurve2D.Float with set coordinates
		Vector2d control1= Vector2d.ratioPoint(anchor1.pos, joint.pos, sharpness);
		Vector2d control2= Vector2d.ratioPoint(anchor2.pos, joint.pos, sharpness);
		q.setCurve(anchor1.pos.x,anchor1.pos.y, control1.x,control1.y, control2.x,control2.y, anchor2.pos.x,anchor2.pos.y);
		window.draw(q);
		
	}


	
	public static int clamp(int val, int min, int max) {
	    return Math.max(min, Math.min(max, val));
	}
	public void moveRight() {
		dir = Direction.RIGHT;
		targetVelocity= maxSpeed;
	}
	public void moveLeft() {
		dir = Direction.LEFT;
		targetVelocity= -1*maxSpeed;
	}
	public void jump(double power) {
		action = true;
		//System.out.println("Jump");
		//velocityY = -20;
		ah.setCurrentAnimation(new Jump("Jump",20,this), true);
	}
	public void idle() {
		targetVelocity= 0;
	}
}