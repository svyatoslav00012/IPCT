package model.helpers;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import view.windows.alert.AlertController;
import view.windows.notifications.NotificationsController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileProcessor {

	ImageProcessor imageProcessor = new ImageProcessor();

	public void checkDirectory() {
		File dir = new DirectoryChooser().showDialog(null);
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			System.err.println("Dir is wrong");
		}
		System.out.println("\n- - - CHECKING DIR : " + dir.getAbsolutePath() + " - - -");
		Mat mat;
		for (File im : dir.listFiles()) {
			if (im.isFile() && !new Image(im.toURI().toString()).isError()) {
				mat = Imgcodecs.imread(im.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
				System.out.println("Image: " + im.getName() +
						" channels : " + mat.channels() +
						" width : " + mat.width() +
						" height : " + mat.height() +
						" cols : " + mat.cols() +
						" rows : " + mat.rows()
				);
			}
		}
		System.out.println("- - - CHECKING FINISHED - - -");
	}

	public int countLines(File file) {
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while (reader.readLine() != null)
				count++;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	public int countInstances(File info) {
		if (info == null || !info.exists() || !info.isFile()) {
			System.err.println("info file is null, wrong or doesn't exists");
			return -1;
		}
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(info)));
			for (int i = 1; ; i++) {
				String s = reader.readLine();
				System.out.println("LINE " + i + " : " + s);
				for (int j = 0; j < s.length(); ++j)
					System.out.println("\t\t'" + s.charAt(j) + "' " + Character.isDigit(s.charAt(i)));
				if (s == null || s.isEmpty()) break;
				else
					count += Integer.parseInt(s.substring(s.indexOf(' ') + 1, s.substring(s.indexOf(' ') + 1).indexOf(' ')));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	public void replaceAll(File info) {
		if (info == null || !info.exists() || !info.isFile()) {
			System.err.println("info file is null, wrong or doesn't exists");
			return;
		}
		try {
			String s;
			StringBuilder sb = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(info)));
			while ((s = reader.readLine()) != null) {
				sb.append(s + "\r\n");
			}
			reader.close();

			for (int i = 0; i < sb.length(); ++i)
				if (sb.charAt(i) == '/')
					sb.setCharAt(i, '\\');

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info)));
			writer.write(sb.toString());
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeAll(File info, String startString) {
		if (info == null || !info.exists() || !info.isFile()) {
			System.err.println("info file is null, wrong or doesn't exists");
			return;
		}
		try {
			String s;
			StringBuilder sb = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(info)));
			while ((s = reader.readLine()) != null) {
				sb.append(s.substring(s.indexOf(startString)) + "\r\n");
			}
			reader.close();

			for (int i = 0; i < sb.length(); ++i)
				if (sb.charAt(i) == '/')
					sb.setCharAt(i, '\\');

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info)));
			writer.write(sb.toString());
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeAnnotations(File imDir, File to) {
		if (!imDir.exists()) {
			System.err.println("image dir doesn't exist");
			return;
		}
		if (!imDir.isDirectory()) {
			System.err.println("wrong path to image dir (is file not dir)");
			return;
		}
		if (!to.exists()) {
			System.err.println("target file doesn't exist");
			return;
		}
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to)));
			for (File f : imDir.listFiles()) {
				writer.write(f.getAbsolutePath());
				writer.newLine();
			}
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveImage(Image image, File target) {
		try {
			if (!target.exists()) target.createNewFile();
			//Генеруємо зображення із переданого
			BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
			//Записуємо зображення у файл
			ImageIO.write(bImage, "png", target);
			if (Thread.currentThread().getName().equals("JavaFX Application Thread"))
				NotificationsController.showComplete("Зображення вдало збережене");
		} catch (Exception e) {
			NotificationsController.showError("Щось пішло не так!\nЗображення не збережене");
		}
	}

	public void duplicateImages(File srcDir, File targetDir) {
		if (!srcDir.exists()) {
			System.err.println("srcDir doesn't exist");
			return;
		}
		if (!srcDir.isDirectory()) {
			System.err.println("Wrong path to srcDir");
			return;
		}
		if (!targetDir.exists()) {
			System.err.println("targetDir doesn't exist");
			return;
		}
		if (!targetDir.isDirectory()) {
			System.err.println("Wrong path to targetDir");
			return;
		}


		for (File image : srcDir.listFiles()) {
			if (new Image(image.toURI().toString()).isError()) continue;
			new Thread(new Runnable() {
				@Override
				public void run() {
					duplicateImage(image, targetDir);
				}
			}).start();
		}
		System.out.println("FINISHED");
	}

	public void duplicateImage(File srcImage, File targetDir) {
		System.out.println(srcImage.getName());
		ArrayList<Image> duplicates = imageProcessor.generateImages(srcImage.getAbsolutePath());
		File newFile;
		for (int i = 0; i < duplicates.size(); ++i) {
			System.out.println("\tDUP" + (i + 1));
			newFile = renamedFile(srcImage, targetDir, "_" + (i + 1));
			saveImage(duplicates.get(i), newFile);
		}
	}

	public void copyFiles(File dirFrom, File dirTo) {
		if (dirFrom == null || !dirFrom.isDirectory() || !dirFrom.exists()) {
			NotificationsController.showError("wrong dirFrom in copyFiles");
			return;
		}
		if (dirTo == null || !dirTo.isDirectory() || !dirTo.exists()) {
			NotificationsController.showError("wrong dirTo in copyFiles");
			return;
		}
		try {
			for (File f : dirFrom.listFiles()) {
				File target = new File(dirTo.getAbsolutePath() + File.separatorChar + f.getName());
				if (target.exists() && AlertController.showConfirm(target.getName() + " already exist, replace?"))
					continue;
				Files.copy(f.toPath(), target.toPath(), REPLACE_EXISTING);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void copyFiles(File[] files, File dirTo){
		copyFiles((File) Arrays.asList(files), dirTo);
	}

	public void copyFiles(ArrayList<File> files, File dirTo){
		if(files == null){
			System.err.println("FileProcessor.copyFiles(File[], File) File[] is null");
			return;
		}
		if(dirTo == null){
			System.err.println("FileProcessor.copyFiles(File[], File) File is null");
			return;
		}
		if(!dirTo.exists()){
			System.err.println("FileProcessor.copyFiles(File[], File) File doesn't exists");
			return;
		}
		if(!dirTo.isDirectory()){
			System.err.println("FileProcessor.copyFiles(File[], File) File isn't directory");
			return;
		}

		try{
			for(File f : files){
				File target = new File(dirTo.getAbsolutePath() + File.separatorChar + getFreeName(dirTo) + getExtentionWithDot(f));
				Files.copy(f.toPath(), target.toPath(), REPLACE_EXISTING);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void mergeFiles(File from, File to) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(from)));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to, true)));
			String s = null;
			while ((s = reader.readLine()) != null) {
				writer.write(s);
				writer.newLine();
				writer.flush();
			}
			writer.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void updateRelPath(File txtFile, File dir) {
		try {
			ArrayList<String> strings = new ArrayList<>();
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(txtFile), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				//System.out.println("\nBEFORE: " + line);
				line = dir.getName() + line.substring(line.lastIndexOf('\\'));
				//System.out.println("\nAFTER: " + line);
				strings.add(line);
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile)));
			for (String s : strings) {
				writer.write(s);
				writer.newLine();
				writer.flush();
			}
			in.close();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateInfo(File pos, File info) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(info)));
			String in;
			ArrayList<File> filesInInfo = new ArrayList<>();
			while((in = reader.readLine()) != null){
				filesInInfo.add(new File(pos.getAbsolutePath() + File.separatorChar + getImageFileName(in)));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFreeName(File dir) {
		if (!dir.isDirectory())
			return null;
		ArrayList<File> files = (ArrayList<File>)Arrays.asList( dir.listFiles());
		boolean found = false;
		for (int i = 1; ; ++i){
			found = true;
			for(int j = 0; j < files.size(); ++j)
				if(getName_WITHOUT_extension(files.get(j)).equals(i+"")){
					++i;
					found = false;
					break;
				}
			if(found) return i+"";
		}
	}

	private String getName_WITHOUT_extension(File file){
		if(!file.isFile()){
			return null;
		}
		if(!file.getName().contains("."))
			return file.getName();
		return file.getName().substring(0, file.getName().lastIndexOf('.'));
	}
	private String getExtentionWithDot(File file){
		if(!file.isFile())
			return null;
		return file.getName().substring(file.getName().lastIndexOf('.'));
	}

	public File renamedFile(File srcFile, File targetDir, String postfix) {
		String name = srcFile.getName();
		name = name.substring(0, name.lastIndexOf('.')) + postfix + name.substring(name.lastIndexOf('.'));
		return new File(targetDir.getAbsolutePath() + "/" + name);
	}

	public String getImageFileName(String line){
		line = File.separatorChar + line + " ";
		String file = line.substring(0, line.indexOf(" "));
		return file.substring(file.lastIndexOf(File.separatorChar));
	}
}
