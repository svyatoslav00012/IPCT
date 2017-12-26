package model.manager;

public class Manager extends Thread {

	private boolean wait = true;
	private boolean working = true;
	private ManagerQueue managerQueue;

	public Manager(){
		managerQueue = new ManagerQueue();
	}

	public void run(){
		while (working){
			if(managerQueue.isEmpty())
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
			else
			{
				managerQueue.poll().run();
			}
		}
	}

	public synchronized void work(Runnable r) {
		managerQueue.push(r);
	}

	public void off(){
		working = false;
	}
}
