package model.manager;

public class Manager extends Thread {

	private boolean wait = true;
	private boolean working = true;
	private ManagerQueue managerQueue;

	public Manager(){
		managerQueue = new ManagerQueue();
	}

	public void run(){
		work();
	}

	public void work() {
		if (!managerQueue.isEmpty()) {
			managerQueue.poll().run();
		}
		if (working) wait_for_tasks();
	}

	public void wait_for_tasks() {
		int i = 0;
		while (managerQueue.isEmpty())
			try {
				++i;
				if(i == 100000){
					System.out.println("wait");
					i = 0;
				}
				wait();
			} catch (Exception e) {
				//	e.printStackTrace();
			}
		work();
	}

	public void work(Runnable r) {
		System.out.println("work");
		managerQueue.push(r);
	}

	public void off(){
		working = false;
	}
}
