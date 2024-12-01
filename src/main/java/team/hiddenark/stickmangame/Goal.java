package team.hiddenark.stickmangame;

public abstract class Goal {

    protected Thinker thinker;


    public static class GoalGen{

        private Thinker t;

        public GoalGen(Thinker t){
            this.t = t;
        }

        public MoveXGoal createMoveXGoal(int targetX, double maxVelocity, double accelerationRate, double decelerationThreshold, Runnable onComplete){
            return new MoveXGoal(t,targetX, maxVelocity, accelerationRate, decelerationThreshold, onComplete);
        }

        public MoveXGoal createMoveXGoal(int targetX, double maxVelocity, double accelerationRate, double decelerationThreshold){
            return new MoveXGoal(t,targetX, maxVelocity, accelerationRate, decelerationThreshold, null);
        }

        public WaitGoal createWaitGoal(double length, Runnable onComplete){
            return new WaitGoal(t,length, onComplete);
        }

        public WaitGoal createWaitGoal(double length){
            return new WaitGoal(t,length, null);
        }
    }

    protected Runnable onComplete;

    public void onComplete(){
        if (onComplete != null)
            onComplete.run();
    }


    public abstract boolean isGoalCompleted();

    public abstract void act();

    public static class WaitGoal extends Goal{
        private final double length;
        private long start;

        public WaitGoal(Thinker thinker, double length, Runnable onComplete) {
            this.thinker = thinker;
            this.length = length;
            this.start = -1;

            this.onComplete = onComplete;
        }

        @Override
        public boolean isGoalCompleted() {
            return (System.nanoTime()-start >= length*1E6)&&start!=-1;
        }

        @Override
        public void act() {
            // waiting;
            if (start == -1){
                start = System.nanoTime();
            }
        }
    }

    public static class MoveXGoal extends Goal{

        private final int targetX;
        private final double maxVelocity; // Maximum velocity
        private final double accelerationRate; // Acceleration per tick
        private final double decelerationThreshold; // Distance to start decelerating

        public int tolerance = 10;

        public MoveXGoal(Thinker thinker, int targetX, double maxVelocity, double accelerationRate, double decelerationThreshold, Runnable onComplete){
            this.thinker = thinker;
            this.targetX = targetX;
            this.maxVelocity = maxVelocity;
            this.accelerationRate = accelerationRate;
            this.decelerationThreshold = decelerationThreshold;
            this.onComplete = onComplete;
        }

        @Override
        public boolean isGoalCompleted() {
//            System.out.println(targetX-thinker.getX());
            return (Math.abs(targetX-thinker.getX())<tolerance);
        }

        public void act() {
            // Get the current position of the thinker
            int currentX = thinker.getX();

            // Calculate the distance to the target
            double distanceToTarget = targetX - currentX;

            // Determine the desired velocity
            double targetVelocity;
            if (Math.abs(distanceToTarget) > decelerationThreshold) {
                // Far from the target: use maximum velocity
                targetVelocity = Math.signum(distanceToTarget) * maxVelocity;
            } else {
                // Close to the target: scale velocity proportionally
                targetVelocity = maxVelocity * (distanceToTarget / decelerationThreshold);
            }

            // Call the thinker’s moveSide method with calculated velocity
            thinker.moveSide(targetVelocity, accelerationRate);
        }

    }


}

