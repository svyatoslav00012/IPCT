package model;

import main.Main;
import model.helpers.FileProcessor;
import model.helpers.ImageProcessor;
import model.helpers.OpenCVUtils;
import model.manager.Manager;
import view.windows.notifications.NotificationsController;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Project {

	private FileProcessor fileProcessor;
	private ImageProcessor imageProcessor;
	private Manager manager;

	public static final String DATA = "data";
	public static final String POS = "pos";
	public static final String BUFFER_POS = "bufferPos";
	public static final String NEG = "neg";
	public static final String INFO = "info.txt";
	public static final String BUFFER_INFO = "bufferInfo.txt";
	public static final String BG = "bg.txt";
	public static final String VEC_FILE = "vecFile.vec";

	//location file
	private File location;

	//directories
	private File data;
	private File pos;
	private File bufferPos;
	private File neg;

	//project files
	private File info;
	private File bufferInfo;
	private File bg;
	private File vecFile;

	public synchronized File getLocation() {
		return location;
	}

	public synchronized void setLocation(File newLocation) {
		this.location = location;
		initFiles();
	}

	public synchronized File getData() {
		return data;
	}

	public synchronized File getPos() {
		return pos;
	}

	public synchronized File getBufferPos(){
		return bufferPos;
	}

	public synchronized File getNeg() {
		return neg;
	}

	public synchronized File getInfo() {
		return info;
	}

	public synchronized File getBufferInfo() {
		return bufferInfo;
	}

	public synchronized File getBg() {
		return bg;
	}

	public synchronized File getVecFile() {
		return vecFile;
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
		bufferPos = new File(location.getAbsolutePath() + File.separatorChar + BUFFER_POS);
		neg = new File(location.getAbsolutePath() + File.separatorChar + NEG);
		info = new File(location.getAbsolutePath() + File.separatorChar + INFO);
		bufferInfo = new File(location.getAbsolutePath() + File.separatorChar + BUFFER_INFO);
		bg = new File(location.getAbsolutePath() + File.separatorChar + BG);
		vecFile = new File(location.getAbsolutePath() + File.separatorChar + VEC_FILE);
	}

	public void createFiles() {
		try {
			if (!data.exists())
				data.mkdir();
			if (!pos.exists())
				pos.mkdir();
			if(!bufferPos.exists())
				bufferPos.mkdir();
			if (!neg.exists())
				neg.mkdir();
			if (!info.exists())
				info.createNewFile();
			if (!bufferInfo.exists())
				bufferInfo.createNewFile();
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

	public void addPositives(ArrayList<File> images) {
		Runnable r = () -> {
			System.out.println("runnable");
			fileProcessor.copyFiles(images, pos, true);
		};
		System.out.println("addPos");
		manager.work(r);
	}

	public void addNegatives(ArrayList<File> images) {
		Runnable r = () -> {
			System.out.println("runnable");
			fileProcessor.copyFiles(images, neg, true);
			updateBG();
		};
		System.out.println("addPos");
		manager.work(r);
	}

	public void removeImages() {

	}

	public void annotate(ArrayList<File> images) {
		try {
			if (!bufferInfo.exists())
				bufferInfo.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fillBufferPos(images);
		OpenCVUtils.opencv_annotation(this);
		addNewAnnotated();
	}

	public void createSamples(ArrayList<File> annotated, int w, int h) {
		//fillBufferPos(annotated);
		OpenCVUtils.opencv_createsamples(this, w, h);
		NotificationsController.showComplete("Samples created successfully, .vec file filled");
	}

	public static void createProject(File directory) {
		if (directory == null) {
			System.err.println("couldn't create DIR, 'directory' is NULL");
		}
		if (!directory.exists()) {
			directory.mkdirs();
		}

	}

	public void fillBufferPos(ArrayList<File> images){
		fileProcessor.clearFolder(bufferPos);
		fileProcessor.copyFiles(images, bufferPos, false);
	}

	/**
	 * using after adding or deleting negatives
	 */

	public void updateBG() {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bg)));

			for (File f : neg.listFiles()) {
				writer.write(neg.getName() + File.separatorChar + f.getName());
				writer.newLine();
				writer.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isEmptyAnnotation(String line){
		line += " ";
		return line.substring(line.indexOf(" ")).length() < 9;
	}

	/**
	 * using after annotation
	 */
	public void addNewAnnotated() {
		try {
			ArrayList<String> infoStrings = getStringsFromDescriptionFile(info, null);
			ArrayList<String> bufferInfoStrings = getStringsFromDescriptionFile(bufferInfo, null);

			for (int i = 0; i < bufferInfoStrings.size(); ++i) {
				File f = getFileFromLine(bufferInfoStrings.get(i));
				for (int j = 0; j < infoStrings.size(); ++j) {
					if(isEmptyAnnotation(bufferInfoStrings.get(i))){
						bufferInfoStrings.remove(i);
						i--;
					}
					else if (getFileFromLine(infoStrings.get(j)).getName().equals(f.getName())) {
						infoStrings.set(j, bufferInfoStrings.get(i));
						bufferInfoStrings.remove(i);
						i--;
					}
				}
			}

			bufferInfoStrings = setPath(bufferInfoStrings);

			infoStrings.addAll(bufferInfoStrings);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info)));
			for (String s : infoStrings) {
				writer.write(s);
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> setPath(ArrayList<String> strings){
		for(int i = 0; i < strings.size(); ++i){
			strings.set(i,
					getPos().getName() +
							strings.get(i).substring(
									strings.get(i).indexOf(File.separatorChar)
							)
			);
		}
		return strings;
	}

	/**
	 * using after removing positive images
	 */
	public void updateInfo() {
		try {
			ArrayList<String> infoStrings = getStringsFromDescriptionFile(info, null);
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(pos.listFiles()));

			for (int i = 0; i < infoStrings.size(); ++i) {
				if (!files.contains(getFileFromLine(infoStrings.get(i)))) {
					infoStrings.remove(i);
					i--;
				}
			}

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info)));
			for (String s : infoStrings) {
				writer.write(s);
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns strings in @param file which contains such @param images
	 *
	 * @return
	 */

	private ArrayList<String> getStringsFromDescriptionFile(File file, ArrayList<File> images) {
		ArrayList<String> strings = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String in;
			while ((in = reader.readLine()) != null) {
				if (images == null)
					strings.add(in);
				else if (images.contains(getFileFromLine(in)))
					strings.add(in);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strings;
	}

	public File getFileFromLine(String line) {
		line += " ";
		return new File(
				getLocation().getAbsolutePath() +
						File.separatorChar +
						line.substring(line.indexOf(" "))
		);
	}

	public static boolean isInfoFile(File file) {
		return file.getName().equals(INFO);
	}

	public static boolean isBgFile(File file) {
		return file.getName().equals(BG);
	}

	public synchronized void close() {
		manager.off();
		Main.PROJECTS.remove(this);
	}

	public String getName() {
		return location.getName();
	}
}
