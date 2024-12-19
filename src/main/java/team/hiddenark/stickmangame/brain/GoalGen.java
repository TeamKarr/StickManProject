package team.hiddenark.stickmangame.brain;


import java.util.concurrent.Callable;

public class GoalGen {

    Thinker thinker;

    public GoalGen(Thinker th){
        thinker = th;
    }

    public abstract class AbstractGoal implements Goal{

        private final Runnable onCompleteTask;
        private boolean parallel = false;

        public AbstractGoal(Runnable onCompleteTask){
            this.onCompleteTask = onCompleteTask;
        }

        public void setRunParallel(boolean val){
            parallel = val;
        }

        @Override
        public boolean isParallel() {
            return parallel;
        }

        @Override
        public void onComplete() {
            if (onCompleteTask != null)
                onCompleteTask.run();
        }
    }

    public MoveXGoal createMoveXGoal(int targetX, double maxVelocity, double accelerationRate, double decelerationThreshold, Runnable onComplete){
        return new MoveXGoal(targetX, maxVelocity, accelerationRate, decelerationThreshold, onComplete);
    }

    public MoveXGoal createMoveXGoal(int targetX, double maxVelocity, double accelerationRate, double decelerationThreshold){
        return new MoveXGoal(targetX, maxVelocity, accelerationRate, decelerationThreshold, null);
    }

    public WaitGoal createWaitGoal(double length, Runnable onComplete){
        return new WaitGoal(length, onComplete);
    }

    public WaitGoal createWaitGoal(double length){
        return new WaitGoal(length, null);
    }

    public WaitForGoal createWaitForGoal(Callable<Boolean> check, Runnable onComplete) {
        return new WaitForGoal(check, onComplete);
    }

    public WaitForGoal createWaitForGoal(Callable<Boolean> check) {
        return new WaitForGoal(check, null);
    }

    public class WaitGoal extends AbstractGoal {
        private final double length;
        private long start;

        public WaitGoal(double length, Runnable onCompleteTask) {
            super(onCompleteTask);
            this.length = length;
            this.start = -1;
        }

        @Override
        public boolean isComplete() {
            return (System.nanoTime()-start >= length*1E6)&&start!=-1;
        }

        @Override
        public boolean act() {
            // start the timer on first call;
            if (start == -1){
                start = System.nanoTime();
            }

            return false;
        }

        @Override
        public String toString() {
            return "waiting for "+(System.nanoTime()-start) + "=" + length*1E6;
        }
    }

    public class WaitForGoal extends AbstractGoal {
        private long start;
        private Callable<Boolean> check;

        public WaitForGoal(Callable<Boolean> check, Runnable onCompleteTask) {
            super(onCompleteTask);
            this.check = check;
            this.start = -1;

        }

        @Override
        public boolean isComplete() {
            try {
                return check.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;

        }

        @Override
        public boolean act() {return false;}

        @Override
        public String toString() {
            return "wiating for event";
        }
    }

    public class MoveXGoal extends AbstractGoal{

        private final int targetX;
        private final double maxVelocity; // Maximum velocity
        private final double accelerationRate; // Acceleration per tick
        private final double decelerationThreshold; // Distance to start decelerating
        public int tolerance = 50;

        public MoveXGoal(int targetX, double maxVelocity, double accelerationRate, double decelerationThreshold, Runnable onCompleteTask){
            super(onCompleteTask);
            this.targetX = targetX;
            this.maxVelocity = maxVelocity;
            this.accelerationRate = accelerationRate;
            this.decelerationThreshold = decelerationThreshold;
        }

        @Override
        public boolean isComplete() {
//            System.out.println(targetX-thinker.getX());
            return (Math.abs(targetX-thinker.getX())<tolerance);
        }

        public boolean act() {
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

            return true;
        }

        @Override
        public String toString() {
            return "x = " + thinker.getX() + " traveling to "+targetX;
        }

    }


}
