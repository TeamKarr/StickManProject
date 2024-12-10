package team.hiddenark.stickmangame.stickman;

class Jump extends Animation{
	private Vector2d rft;
	private Vector2d lft;
	
	private Stickman man;
	private double tick=0;
	private int hipPosY;
	private int changeY=0;
	private int hipPosX = 0;
	private double velocity=0;
	private double velocityX;
	private LockedVar<Vector2d> lfootPos = new LockedVar<Vector2d>();
	private LockedVar<Vector2d> rfootPos=new LockedVar<Vector2d>();
	private LockedVar<Vector2d> pos=new LockedVar<Vector2d>();
	Jump(String label, int height, Stickman man) {
		
		super(label);
		
		this.man = man;
		rft = this.man.rfoot.pos;
		lft = this.man.lfoot.pos;
		this.hipPosY = man.hip.pos.y;
		this.hipPosX = man.hip.pos.x;
		this.velocityX=man.velocityX;
		man.jump = true;
		// TODO Auto-generated constructor stub
	}
	private boolean jump=false;
	@Override
	public void tick() {
		
		int depth = 25;
		double speed = 0.5;
		if(!man.jump) {
			super.purge=true;
			return;
		}
		man.hip.pos.y = (int) man.position.y-80;
		man.hip.pos.x += velocityX*1.2;
		man.position.x = man.hip.pos.x;
		
		if(jump==false) {
		if(velocity>20) {
			jump=true;
			man.velocityY=-1*velocity;
			
			//
				
			
		}
		else {
			//down
			if(tick<Math.PI) {
//				System.out.println("hello1");
				changeY=(int)(depth*Math.cos(tick)-depth);
				man.hip.pos.x-=10*speed*Math.signum(velocityX);
				if(man.rfoot.pos.x*man.getDirection()<man.lfoot.pos.x*man.getDirection())
				{
					man.rfoot.pos.x=(int)Helper.incrementTowards(man.rfoot.pos.x, man.lfoot.pos.x, 5);
				}else {
					man.lfoot.pos.x=(int)Helper.incrementTowards(man.lfoot.pos.x, man.rfoot.pos.x, 5);
				}
				lft.x=man.lfoot.pos.x;
				lft.y=man.lfoot.pos.y;
				rft.x=man.rfoot.pos.x;
				rft.y=man.rfoot.pos.y;
				
				
				man.backArch.setTheta(man.backArch.getTheta()+0.08*speed*man.getDirection());
				man.shoulder.setTheta(man.shoulder.getTheta()+0.08*speed*man.getDirection());
				man.lelbow.setTheta(Helper.incrementTowards(man.lelbow.getTheta(),man.shoulder.getTheta()-Math.PI/2*man.getDirection(),-0.1*man.getDirection()*speed));
				man.relbow.setTheta(Helper.incrementTowards(man.relbow.getTheta(),man.shoulder.getTheta()+Math.PI/2*man.getDirection(),-0.1*man.getDirection()*speed));
				
				
			    
				
				    
				
			//up
			}else {
				System.out.println(velocity+" "+man.velocityX);
				if(man.velocityX>0) {
					man.backArch.setTheta(Helper.incrementTowards(man.backArch.getTheta(),Math.atan(velocity/-1*man.velocityX),0.5*speed));
					man.shoulder.setTheta(Helper.incrementTowards(man.shoulder.getTheta(),Math.atan(velocity/-1*man.velocityX),0.5*speed));
				}else {
					man.backArch.setTheta(Helper.incrementTowards(man.backArch.getTheta(),Math.toRadians(-90),0.5*speed));
					man.shoulder.setTheta(Helper.incrementTowards(man.shoulder.getTheta(),Math.toRadians(-90),0.5*speed));
				}
				
//				man.shoulder.setTheta(Helper.incrementTowards(man.shoulder.getTheta(),Math.toRadians(-90),0.5*speed));
//				man.lelbow.setTheta(Helper.incrementTowards(man.lelbow.getTheta(),Math.toRadians(-90),1*speed));
//				man.relbow.setTheta(Helper.incrementTowards(man.relbow.getTheta(),Math.toRadians(-90),1*speed));
//				man.lhand.setTheta(Helper.incrementTowards(man.lelbow.getTheta(),Math.toRadians(-90),1*speed));
//				man.rhand.setTheta(Helper.incrementTowards(man.relbow.getTheta(),Math.toRadians(-90),1*speed));
				changeY+=velocity*speed;
				
				velocity+=6*speed;
				
			}
			
			tick+=0.2*speed*2;
//			System.out.println(changeY);
			man.hip.pos.y=hipPosY-changeY;
			
			
			Joint.inverseKinematic(man.hip, man.rknee, man.rfoot, rft, man.getDirection());
		    Joint.inverseKinematic(man.hip, man.lknee, man.lfoot, lft, man.getDirection());
			
			
		}
		}
		//System.out.println( man.hip.pos.y);
		
		if(jump) {
			//System.out.println("test");
			lfootPos.set(new Vector2d(man.lfoot.pos));
			rfootPos.set(new Vector2d(man.rfoot.pos));
			pos.set(new Vector2d(man.hip.pos));
//			man.lfoot.pos.y = (int) (lfootPos.get().y+pos.get().y-man.position.y);
//			man.lfoot.pos.y +=1;
//			man.rfoot.pos.y = (int) (rfootPos.get().y+pos.get().y-man.position.y);
			a =  (int)Helper.incrementTowards(a, 40, 2);
			man.rfoot.pos.y = man.hip.pos.y+100- a;
			man.lfoot.pos.y = man.hip.pos.y+100- a;
			man.rfoot.pos.x = (int)((man.rfoot.pos.x+man.position.x)/2);
			man.lfoot.pos.x = (int)((man.lfoot.pos.x+man.position.x)/2);
//			man.lfoot.pos.x = lfootPos.get().x-pos.get().x+(int)man.position.x;
			Joint.inverseKinematic(man.hip, man.rknee, man.rfoot, man.rfoot.pos, man.getDirection());
		    Joint.inverseKinematic(man.hip, man.lknee, man.lfoot, man.lfoot.pos, man.getDirection());
		}
		
	}
	private int a=0;

	
}