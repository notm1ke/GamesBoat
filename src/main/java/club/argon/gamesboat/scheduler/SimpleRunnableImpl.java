package club.argon.gamesboat.scheduler;

import club.argon.gamesboat.Base;

import co.m1ke.basic.scheduler.SimpleRunnable;

public abstract class SimpleRunnableImpl extends SimpleRunnable implements Runnable {

    private int taskId;

    public SimpleRunnableImpl() {
        this.taskId = Base.getScheduler().getRunnables().size() + 1;
    }

    @Override
    public void cancel() {
        Base.getScheduler().cancelTask(taskId);
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

}
