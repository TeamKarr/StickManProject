package team.hiddenark.stickmangame.brain;

public interface Goal {
    boolean isComplete();
    boolean act();
    boolean isParallel();
    void onComplete();
}
