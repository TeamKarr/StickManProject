package team.hiddenark.stickmangame.brain;

public interface Goal {
    boolean isComplete();
    void act();
    boolean isParallel();
    void onComplete();
}
