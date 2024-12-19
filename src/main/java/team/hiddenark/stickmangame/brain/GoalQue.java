package team.hiddenark.stickmangame.brain;

import java.util.LinkedList;

public class GoalQue extends LinkedList<Goal> implements Goal{

    public boolean parallel;
    public Runnable onCompleteTask;

    public GoalQue(){
        super();
        parallel = false;
    }

    @Override
    public boolean isComplete() {
        return isEmpty();
    }

    @Override
    public boolean act() {
        if (!isEmpty()){
            boolean didRun = false;
            boolean runNext;
            do {
                Goal currentGoal = peek();

                assert currentGoal != null;
                runNext = currentGoal.isParallel();

                if (currentGoal.isComplete()){
                    currentGoal.onComplete();
                    poll();
                } else {
                    System.out.println("acting out: " + currentGoal);
                    if (currentGoal.act())
                        didRun = true;
                }
            } while (runNext);
            return didRun;
        }
        return false;
    }

    @Override
    public boolean isParallel() {
        return false;
    }

    @Override
    public void onComplete() {
        if (onCompleteTask != null)
            onCompleteTask.run();
    }
}
