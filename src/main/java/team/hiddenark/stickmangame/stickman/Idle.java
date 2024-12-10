package team.hiddenark.stickmangame.stickman;

//work on:
class Idle extends Animation{
	
	private Stickman man;
	private Vector2d rft = new Vector2d(0,0);
	private Vector2d lft=new Vector2d(0,0);
	private Vector2d targrft;
	private Vector2d targlft;
	
	public Idle(String label, Stickman man) {
		
		super(label);
		
		this.man = man;
		rft.y = (int) man.position.y;
		lft.y = (int) man.position.y;
		rft = man.rfoot.pos;
		lft = man.lfoot.pos;
		
		man.lelbow.setTheta(Helper.incrementTowards(man.lelbow.getTheta(), Math.toRadians(90), 0.1));
		man.relbow.setTheta(Helper.incrementTowards(man.relbow.getTheta(), Math.toRadians(90), 0.1));
		// TODO Auto-generated constructor stub
	}
	private double tick = 0;

	@Override
	public void tick() {
	
//		if(targrft.x>targlft.x) {
//			targrft.x = (int) man.position.x+5;
//			targlft.x = (int) man.position.x-5;
//		}
//		else {
//			targrft.x = (int) man.position.x+5;
//			targlft.x = (int) man.position.x-5;
//		}
		lft.y = (int) man.position.y;
		rft.y = (int) man.position.y;
//		rft = man.rfoot.pos;
//		lft = man.lfoot.pos;
//		
//		targlft.y = (int) man.position.y;
		
		man.hip.pos.x=(int)(man.position.x+Math.cos(tick )*2);
		man.hip.pos.y=(int)(man.position.y-80+Math.sin(tick)*1);
//		lft.x=(int) Helper.incrementTowards(lft.x, targlft.x, 10);
//		lft.y=(int) Helper.incrementTowards(lft.y, targlft.y, 10);
//		rft.x=(int) Helper.incrementTowards(rft.x, targrft.x, 10);
//		rft.y=(int) Helper.incrementTowards(rft.y, targrft.y, 10);
		Joint.inverseKinematic(man.hip, man.rknee, man.rfoot, rft, man.getDirection());
	    Joint.inverseKinematic(man.hip, man.lknee, man.lfoot, lft, man.getDirection());
		// TODO Auto-generated method stub
		tick=(tick<Math.PI*2)?tick+0.05:0;
	}
	
}