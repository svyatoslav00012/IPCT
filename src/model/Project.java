package model;

import main.Main;
import model.helpers.FileProcessor;
import model.helpers.ImageProcessor;
import model.manager.Manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Project {

	private FileProcessor fileProcessor;
	private ImageProcessor imageProcessor;
	private Manager manager;

	public static final String DATA = "data";
	public static final String POS = "pos";
	public static final String NEG = "neg";
	public static final String INFO = "info.txt";
	public static final String BG = "bg.txt";
	public static final String VEC_FILE = "vecFile.vec";

	//location file
	private File location;

	//directories
	private File data;
	private File pos;
	private File neg;

	//project files
	private File info;
	private File bg;
	private File vecFile;

	public synchronized File getLocation(){
		return location;
	}

	public synchronized void setLocation(File newLocation){
		this.location = location;
		initFiles();
	}

	public synchronized File getData(){
		return data;
	}

	public synchronized File getPos(){
		return  pos;
	}

	public synchronized File getNeg() {
		return neg;
	}

	public synchronized File getInfo() {
		return info;
	}

	public synchronized File getBg() {
		return bg;
	}

	public synchronized File getVecFile(){
		return  vecFile;
	}

	public Project(File dir) {
		location = dir;
		initFiles();
		manager = new Manager();
		manager.start();
		fileProcessor = new FileProcessor();
		imageProcessor = new ImageProcessor();
		Main.PROJECTS.add(this);
	}

	public void initFiles() {
		if (location == null || !location.exists())
			return;
		data = new File(location.getAbsolutePath() + File.separatorChar + DATA);
		pos = new File(location.getAbsolutePath() + File.separatorChar + POS);
		neg = new File(location.getAbsolutePath() + File.separatorChar + NEG);
		info = new File(location.getAbsolutePath() + File.separatorChar + INFO);
		bg = new File(location.getAbsolutePath() + File.separatorChar + BG);
		vecFile = new File(location.getAbsolutePath() + File.separatorChar + VEC_FILE);
	}

	public void createFiles() {
		try {
			if (!data.exists())
				data.mkdir();
			if (!pos.exists())
				pos.mkdir();
			if (!neg.exists())
				neg.mkdir();
			if (!info.exists())
				info.createNewFile();
			if (!bg.exists())
				bg.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initProj() {
		initFiles();
	}

	public void create() {
		createProject(location);
		Main.addProjectToRecent(location.getAbsolutePath());
		if (location == null || !location.exists()) {
			System.out.println("location wrong");
			return;
		}
		initFiles();
		createFiles();
	}

	public void addPositives(ArrayList<File> images){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				fileProcessor.copyFiles(images, pos);
				fileProcessor.updateInfo(pos, info);
			}
		};
		manager.work(r);
	}

	public void removeImages(){

	}

	public void annotate(String[] images){

	}

	public static void createProject(File directory) {
		if(directory == null){
			System.err.println("couldn't create DIR, 'directory' is NULL");
		}
		if (!directory.exists()) {
			directory.mkdirs();
		}

	}

	public static boolean isInfoFile(File file) {
		return file.getName().equals(INFO);
	}

	public static boolean isBgFile(File file){
		return file.getName().equals(BG);
	}

	public synchronized void close() {
		manager.off();
		Main.PROJECTS.remove(this);
	}
}
