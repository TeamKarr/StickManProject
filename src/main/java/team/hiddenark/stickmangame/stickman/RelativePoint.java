package team.hiddenark.stickmangame.stickman;

class RelativePoint extends Vector2d{
	
	public RelativePoint(Joint parent,int x, int y) {
		super(parent.pos.x+x, parent.pos.y+y);
	}
	
	
}