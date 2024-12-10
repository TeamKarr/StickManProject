package team.hiddenark.stickmangame.brain;

public interface Thinker {
    int getX();
    int getY();

    void moveSide(double targetVelocity, double accelerationRate);

}