package team.hiddenark.stickmangame.stickman;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

class Joint{
	public Vector2d pos;
	private double theta;
	public double getTheta() {
		return theta;
	}
	
	private int length;
	public int getLength() {
		return length;
	}
	public Joint parent;
	public ArrayList<Joint> children = new ArrayList<>();
	
	public Joint(double theta, int length, Joint parent) {
		this.theta = theta;
		this.length = length;
		this.parent = parent;
		this.parent.children.add(this);
		calculatePos();
	}
	public void setTheta(double theta) {
		this.theta = theta;
	}
	private void calculatePos() {
		pos = new Vector2d((int)Math.round(parent.pos.x+length*Math.cos(theta)),(int)Math.round(parent.pos.y+length*Math.sin(theta)));
	}
	public Joint(int x, int y) {
		pos = new Vector2d(x,y);
	}
	public void calculateAllPos() {
		if(this.parent!=null) {
			calculatePos();
			
//			shapes.line(window, pos, parent.pos,5);
			
		}
		if(children.size()!=0)
			for(Joint child : children)
				child.calculateAllPos();
//		window.fillOval(pos.x-2, pos.y-3, 5, 5);
		
	}
	public static class shapes{
		public static void line(Graphics2D window, Vector2d pos1, Vector2d pos2, int stroke){
			window.setStroke(new BasicStroke(stroke));
			window.drawLine(pos1.x, pos1.y, pos2.x, pos2.y);
			//end point 1
			window.setStroke(new BasicStroke(0));
			circle(window, pos1, 2.5);
			circle(window, pos2, 2.5);
		}
		public static void circle(Graphics2D window, Vector2d pos, double radius) {
			Shape circle  = new Ellipse2D.Double(pos.x - radius, pos.y - radius, 2.0 * radius, 2.0 * radius);
			window.draw(circle);
		}
	}
	
	
	
	
	public static void inverseKinematic(Joint vJoint, Joint bJoint, Joint eJoint, Vector2d target, int s) {
//		target = new Vector2d(vJoint.pos.x+target.x,vJoint.pos.y+target.y);
		double bLength = bJoint.length;
		double eLength = eJoint.length;
		
		double distance = Math.sqrt((target.y-vJoint.pos.y)*(target.y-vJoint.pos.y)+(target.x-vJoint.pos.x)*(target.x-vJoint.pos.x));
		distance = constrain(distance, Math.abs(bLength-eLength),bLength+eLength);
		double baseAngle = Math.atan2(target.y-vJoint.pos.y, target.x-vJoint.pos.x);
		
		double angleB = Math.acos((bLength * bLength + distance * distance - eLength * eLength) / (2 * bLength * distance));
		 
		  // Step 4: Calculate j.angle relative to p, then update jPos
		bJoint.theta = baseAngle - angleB*s;
		
		  // Step 5: Calculate e.angle based on the new jPos and ePos
		double eAngle = Math.acos((bLength *bLength + eLength * eLength - distance * distance) / (2 * bLength * eLength));
		eJoint.theta = bJoint.theta - eAngle*s +Math.PI;
		
	}
	public static double constrain(double a, double b, double c) {
		if(a<b) {
			return b;
		}else if(a>c) {
			return c;
		}
		else return a;
	}
	public String toString() {
		return "theta:"+ theta+" pos: "+pos+" length: " + length;
	}
}