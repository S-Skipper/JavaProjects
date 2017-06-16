package dtt;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.imgcodecs.Imgcodecs;


public class TestCV {

	public static void main(String[] args) {
		System.out.println("Welcome to OpenCV " + Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());
        
        
		//showImage();
        FaceDetector.Detector();
	}
	
	public static void showImage(){
		Mat mat = Imgcodecs.imread("./libs/lena.jpg");
		ImageViewer imageViewer = new ImageViewer(mat, "lena");
		imageViewer.imshow(); 
	}
}
