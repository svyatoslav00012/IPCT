package model.helpers;

import javafx.stage.Screen;
import model.Project;
import view.windows.notifications.NotificationsController;

import java.io.File;
import java.io.IOException;

public class OpenCVUtils {

	public static void opencv_annotation(Project project){
		opencv_annotation(project.getLocation(), project.getBufferPos(), project.getBufferInfo());
	}

	public static void opencv_annotation(File projectDir, File images, File info) {
		if (images == null || !images.isDirectory() || !images.exists())
			NotificationsController.showError("Images is wrong");
		if (info == null || !info.isFile() || !info.exists())
			NotificationsController.showError("Wrong info param");

		try {
			String[] cmd = {"opencv_annotation",
					"--images=" + images.getName(),
					"--annotations=" + info.getName(),
					"--maxWindowHeight=" + Screen.getPrimary().getVisualBounds().getHeight(),
					"--resizeFactor=3"};
			System.out.println("\n === opencv_annotation === \n");
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			pb.redirectError(ProcessBuilder.Redirect.INHERIT);
			pb.directory(projectDir);
			Process p = pb.start();
			p.waitFor();
			System.out.println("\n === opencv_annotation FINISHED === \n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void opencv_createsamples(Project project, int w, int h){
		opencv_createsamples(project.getLocation(), project.getInfo(), project.getVecFile(), h, w, project.getPos().listFiles().length);
	}

	public static void opencv_createsamples(File workDir, File info, File vec, int h, int w, int nums) {
		if (info == null || !info.isFile() || !info.exists())
			NotificationsController.showError("info is wrong");

		try {
//			String[] cmd = {
//					"opencv_createsamples",
//					"-info " + info.getAbsolutePath(),
//					"-vec " + vec.getAbsolutePath(),
//					"-h " + h,
//					"-w " + w,
//					"-nums " + nums
//			};
			String command = "C://opencv/build/x64/vc14/bin/opencv_createsamples.exe " +
					"-info D:/Drive/p1/info.txt " +
					"-vec D:/Drive/p1vecFile.vec";
			System.out.println("\n === opencv_createsamples === \n");
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			pb.redirectError(ProcessBuilder.Redirect.INHERIT);
			//pb.directory(workDir);
			Process p = pb.start();
			p.waitFor();
			System.out.println("\n === opencv_createsamples FINISHED === \n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void opencv_traincascade(File workDir, File data, File vec, File bg, int numPos, int numNeg, int numStages,
										   int precalcVal, int precalcIdx, int numThreads) {
		if (vec == null)
			NotificationsController.showError("Vec is wrong");

		try {
			String[] cmd = {
					"opencv_traincascade",
					"-data " + data.getName(),
					"-vec " + vec.getName(),
					"-bg " + bg.getName(),
					"-numPos " + numPos,
					"-numNeg " + numNeg,
					"-numStages " + numStages,
					"-precalcValBufSize " + precalcVal,
					"-precalcIdxBufSize " + precalcIdx,
					"-numThreads " + numThreads
			};

			System.out.println("\n === opencv_traincascade === \n");
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.directory(workDir);
			pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			pb.redirectError(ProcessBuilder.Redirect.INHERIT);
			Process p = pb.start();
			p.waitFor();
			System.out.println("\n === opencv_traincascade FINISHED === \n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
