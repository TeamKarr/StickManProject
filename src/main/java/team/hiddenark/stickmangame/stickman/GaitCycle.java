package team.hiddenark.stickmangame.stickman;

//fix arms
public class GaitCycle extends Animation{
	public double velocity;
	
	private int direct;
	private double tick;
	private double changeInX = 0;
	private int initialPosition;
	private Stickman man;

	private boolean pushing;
	
	public GaitCycle(String label, double speed, int dir, Stickman man) {
		
		super(label);
		this.velocity = speed;
		
		this.man = man;
		initialPosition = (int)man.position.x;
//		System.out.println(super.getLabel());
		direct = dir;
		
		
		
	}
	
	
	public void tick() {
		direct = (int)Math.signum(velocity);
		double speed = Math.abs(velocity);
		double strideLength = Math.min(speed*speed+10, 40)+5; // Adjust this value for shorter or longer strides

	    // Increment position to simulate forward movement
//		man.position.x += direct*speed;
	    
	    int footOffset = (int)Math.min(speed*2*direct,20);
	    // Calculate the rotation angle for the walking cycle
	    if(Math.abs(tick)>2*Math.PI) {
	    	tick=0;
	    	changeInX = 0;
	    }else {
	    	changeInX+=direct*speed;
	    	tick = changeInX/strideLength;
	    }
	    
	    
	    man.backArch.setTheta(Math.toRadians(-90-speed*direct));
		man.shoulder.setTheta(Math.toRadians(-90+speed*direct*4));
	    // Add sinusoidal motion to the hip to simulate walking dynamics
	    double hipVerticalOffset;
	    double stepHeight = speed*3;
	    if(speed>2) {
	    	hipVerticalOffset = speed/2* Math.sin(tick * 2); // Vertical bobbing
	    }else {
	    	hipVerticalOffset = 2.5 *(Math.cos(tick*2)+1)/2;
	    }
	    
	    //
	    //double hipHorizontalSwing = Math.abs(10 * Math.cos(rotation)); // Side-to-side motion

	    // Calculate target positions for feet using parametric equations
	    Vector2d rightFootTarget = new Vector2d(
	    		(int)man.position.x + (int) (Helper.waveFunction(tick, true) * strideLength)-footOffset,
	    		(int)man.position.y + (int) (Math.min(Math.sin(tick), 0) * stepHeight)
	    );
	    
	    Vector2d leftFootTarget = new Vector2d(
	    		(int)man.position.x + (int) (Helper.waveFunction(tick+Math.PI, true) * strideLength)-footOffset,
	    		(int)man.position.y + (int) (Math.min(Math.sin(tick + Math.PI), 0) * stepHeight)
	    );

	    // Calculate target positions for hands to simulate arm swinging
	    man.hip.pos.x = (int)man.position.x;
	    man.hip.pos.y = (int) (man.position.y + hipVerticalOffset)-80;

	    // Apply inverse kinematics for feet
	    Joint.inverseKinematic(man.hip, man.rknee, man.rfoot, rightFootTarget, direct);
	    Joint.inverseKinematic(man.hip, man.lknee, man.lfoot, leftFootTarget, direct);

	    // Apply inverse kinematics for hands
	    
 // Maximum swing angle scales with speed
	    double swingLevel = Math.PI/8; // Offset to simulate natural arm swinging
	    double offset = 0.1;
	    // Calculate arm angles based on walking cycle and direction
	    if(man.pushing) {
	    	//man.lhand.pos.x=(int) (man.position.x+40*direct);
	    	man.lhand.pos.x = (int)Helper.incrementTowards(man.lhand.pos.x,man.position.x+man.pushReach*direct ,2 );
	    	man.rhand.pos.x = (int)Helper.incrementTowards(man.rhand.pos.x,man.position.x+man.pushReach*direct ,2 );
	    	
	    	man.rhand.pos.y=(int) (man.position.y-100);
	    	man.lhand.pos.y=(int) (man.position.y-120);
	    }else {
	    if(direct<0) {
	    	man.lelbow.setTheta(Math.min(-1*man.backArch.getTheta()-(speed*0.3)*Math.cos(tick)*swingLevel,-1*man.backArch.getTheta()+speed/180));
		    man.relbow.setTheta(Math.min(-1*man.backArch.getTheta()-(speed*0.3)*Math.cos(tick+Math.PI)*swingLevel,-1*man.backArch.getTheta()+speed/180));
	    	man.lhand.setTheta(Math.max(-1*man.backArch.getTheta()-2*(Math.cos(tick)*swingLevel)+speed/9,man.lelbow.getTheta()+offset));
		    man.rhand.setTheta(Math.max(-1*man.backArch.getTheta()-2*(Math.cos(tick+Math.PI)*swingLevel)+speed/9,man.relbow.getTheta()+offset));
	    }else if(direct>0) {
	    	man.lelbow.setTheta(Math.max(-1*man.backArch.getTheta()-(speed*0.3)*Math.cos(tick)*swingLevel,-1*man.backArch.getTheta()-speed/180));
		    man.relbow.setTheta(Math.max(-1*man.backArch.getTheta()-(speed*0.3)*Math.cos(tick+Math.PI)*swingLevel,-1*man.backArch.getTheta()-speed/180));
	    	man.lhand.setTheta(Math.min(-1*man.backArch.getTheta()-2*(Math.cos(tick)*swingLevel)-speed/9,man.lelbow.getTheta()-offset));
		    man.rhand.setTheta(Math.min(-1*man.backArch.getTheta()-2*(Math.cos(tick+Math.PI)*swingLevel)-speed/9,man.relbow.getTheta()-offset));
	    
	    }
	    }
	    // Update hip position with sinusoidal motion
	    //hip.pos.x = (int)(position.x + hipHorizontalSwing);
	    if(man.pushing) {
	    Joint.inverseKinematic(man.shoulder, man.relbow, man.rhand, man.rhand.pos, -direct);
	    Joint.inverseKinematic(man.shoulder, man.lelbow, man.lhand, man.lhand.pos, -direct);
	    }
	}
	
}