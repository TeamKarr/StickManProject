package team.hiddenark.stickmangame.stickman;

class AnimationHandler{
	
	private Animation currentAnimation;
	private boolean lock = false;
	private Animation SecondaryAnimation;
	public void setCurrentAnimation(Animation animation) {
		if (currentAnimation!=null&&animation.getLabel().equals(currentAnimation.getLabel()))return;
		if(lock)return;
		System.out.println(animation.getLabel());
		currentAnimation = animation;
	}
	public void setCurrentAnimation(Animation animation, boolean lock) {
		
		setCurrentAnimation(animation);
		this.lock = lock;
	}
	public void setSecondaryAnimation(Animation animation) {
		if (SecondaryAnimation!=null&&animation.getLabel().equals(SecondaryAnimation.getLabel()))return;
//		System.out.println(animation.getLabel());
		SecondaryAnimation = animation;
	}
	void tick() {
		if(currentAnimation!=null) {
			if(currentAnimation.purge) {currentAnimation = null; lock = false;}
			else currentAnimation.tick();
		}
		if(SecondaryAnimation!=null) {
			if(SecondaryAnimation.purge)SecondaryAnimation = null;
			
			else SecondaryAnimation.tick();
			
		}
	}
}