package opencv3test.CascadeClassifier;

import org.opencv.core.Core;  
import org.opencv.core.Mat;  
import org.opencv.core.MatOfRect;  
import org.opencv.core.Point;  
import org.opencv.core.Rect;  
import org.opencv.core.Scalar;  
import org.opencv.imgcodecs.Imgcodecs; 
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;  
// 
// Detects faces in an image, draws boxes around them, and writes the results 
// to "faceDetection.png". 
// 人脸识别

public class OpencvImgDemo {
	public void run() {
		System.out.println("\nRunning DetectFaceDemo");
		System.out.println(getClass().getResource("lbpcascade_frontalface.xml").getPath());
		// Create a face detector from the cascade file in the resources
		// directory.
		// CascadeClassifier faceDetector = new
		// CascadeClassifier(getClass().getResource("lbpcascade_frontalface.xml").getPath());
		// Mat image =
		// Highgui.imread(getClass().getResource("lena.png").getPath());
		// 注意：源程序的路径会多打印一个‘/’，因此总是出现如下错误
		/*
		 * Detected 0 faces Writing faceDetection.png libpng warning: Image
		 * width is zero in IHDR libpng warning: Image height is zero in IHDR
		 * libpng error: Invalid IHDR data
		 */
		// 因此，我们将第一个字符去掉
		String xmlfilePath = getClass().getResource("lbpcascade_frontalface.xml").getPath().substring(1);
		//分类器 如何产生分类器XML??????
		CascadeClassifier faceDetector = new CascadeClassifier(xmlfilePath);
		Mat image = Imgcodecs.imread(getClass().getResource("22.jpg").getPath().substring(1));
		// Detect faces in the image.
		// MatOfRect is a special container class for Rect.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);
		System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
		// Draw a bounding box around each face.
		for (Rect rect : faceDetections.toArray()) {
			//以左上角和右下角为标志位做创建一个矩形
			Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		}
		// Save the visualized detection.
		String filename = "faceDetection.png";
		System.out.println(String.format("Writing %s", filename));
		Imgcodecs.imwrite(filename, image);
	}
	public static void main(String[] args) {  
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
	    new OpencvImgDemo().run();  
	  } 
}  

/*3.编写测试类：
[java] view 
plaincopyprint?
package com.njupt.zhb.test;  
public class TestMain {  
   
}  */
//运行结果:   
//Hello, OpenCV 
//   
//Running DetectFaceDemo 
///E:/eclipse_Jee/workspace/JavaOpenCV246/bin/com/njupt/zhb/test/lbpcascade_frontalface.xml 
//Detected 8 faces 
//Writing faceDetection.png  
/*package com.njupt.zhb.test;
public class TestMain {
 public static void main(String[] args) {
   System.out.println("Hello, OpenCV");
   // Load the native library.
   System.loadLibrary("opencv_java246");
   new DetectFaceDemo().run();
 }
}*/
//运行结果:
//Hello, OpenCV
//
//Running DetectFaceDemo
///E:/eclipse_Jee/workspace/JavaOpenCV246/bin/com/njupt/zhb/test/lbpcascade_frontalface.xml
//Detected 8 faces
//Writing faceDetection.png