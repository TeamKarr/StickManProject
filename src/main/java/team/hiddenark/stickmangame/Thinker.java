package team.hiddenark.stickmangame;

public interface Thinker {
    int getX();
    int getY();

    void moveSide(double targetVelocity, double accelerationRate);

}
