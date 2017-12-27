package model.manager;

import java.util.ArrayList;
import java.util.Queue;

public class ManagerQueue extends ArrayList<Runnable> {

	public ManagerQueue(){

	}

	public void push(Runnable r){
		System.out.println("PUSH");
		add(r);
	}

	public Runnable peek(){
		return isEmpty() ? null : get(0);
	}

	public synchronized Runnable poll(){
		Runnable r = isEmpty() ? null : get(0);
		remove(0);
		return r;
	}
}
