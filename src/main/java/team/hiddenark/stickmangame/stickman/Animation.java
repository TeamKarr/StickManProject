package team.hiddenark.stickmangame.stickman;

abstract class Animation{
	public boolean purge;
	String label;
	Animation(String label){
		this.label = label;
	}
	abstract public void tick();
	public String getLabel() {
		return label;
	}
}