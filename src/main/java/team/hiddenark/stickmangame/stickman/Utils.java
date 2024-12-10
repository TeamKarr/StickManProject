package team.hiddenark.stickmangame.stickman;

class Utils {
	
}

class Vector2d{
	public int x,y=0;
	public Vector2d(int x, int y) {
		this.x = x;
		this.y=y;
	}
	public Vector2d(Vector2d copy) {
		this.x = copy.x;
		this.y = copy.y;
	}
	public String toString() {
		return "x: " +x+" y: "+y;
	}
	public static double distance(Vector2d P1, Vector2d P2) {
		return Math.sqrt((P1.y-P2.y)*(P1.y-P2.y)+(P1.y-P2.x)*(P1.x-P2.x));
	}
	public static Vector2d ratioPoint(Vector2d P1, Vector2d P2, double ratio) {
		return new Vector2d((int)((P1.x-P2.x)*ratio+P2.x),(int)((P1.y-P2.y)*ratio+P2.y));
	}
}

class Vector2dDouble{
	public double x,y=0;
	public Vector2dDouble(double x, double y) {
		this.x = x;
		this.y=y;
	}
	
	public String toString() {
		return "x: " +x+" y: "+y;
	}
	public static double distance(Vector2d P1, Vector2d P2) {
		return Math.sqrt((P1.y-P2.y)*(P1.y-P2.y)+(P1.y-P2.x)*(P1.x-P2.x));
	}
	public static Vector2dDouble ratioPoint(Vector2d P1, Vector2d P2, double ratio) {
		return new Vector2dDouble(((P1.x-P2.x)*ratio+P2.x),((P1.y-P2.y)*ratio+P2.y));
	}
}

class Toggle{
	private boolean state = false;
	public void toggle() {
		if(state) state = false;
		else state = true;
	}
	public boolean get() {
		return state;
	}
}

class LockedVar<type>{
	private type value;
	private boolean lock= false;
	public LockedVar(type def){
		value =def;
	}
	public LockedVar(){
		value =null;
	}
	public void set(type value) {
		if(lock)return;
		this.value = value;
		lock=true;
	}
	public type get() {
		return value;
	}
}