/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.utility;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_videoio;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import com.cloud.run.app.constants.ApplicationConstants;

public class ExtractFramesFromVideo {

	private static final Logger LOGGER = Logger.getLogger(ExtractFramesFromVideo.class.getName());

	public static byte[] matToBytes(Mat frame) {
		BytePointer buf = new BytePointer();
		opencv_imgcodecs.imencode(".jpg", frame, buf);
		byte[] bytes = new byte[(int) buf.limit()];
		buf.get(bytes);
		buf.deallocate();
		return bytes;
	}

	public static Map<String, byte[]> extractAndUpload(String videoPath) {
		
		

		File existTest = new File(videoPath);
		if (existTest.exists()) {
			System.out.println(videoPath + " exists");
		} else {
			System.out.println(videoPath + " doesnot exists");
		}

		VideoCapture videoCapture = new VideoCapture(videoPath, opencv_videoio.CAP_FFMPEG);

		System.out.println("Backend name:" + videoCapture.getBackendName());
		Map<String, byte[]> framebytesMap = new LinkedHashMap<String, byte[]>();
		if (!videoCapture.isOpened()) {
			LOGGER.info("Failed to open the video:" + videoPath);
		} else {
			Mat frame = new Mat();
			int framenumber = 0;
			while (videoCapture.read(frame)) {
				if (!frame.empty()) {
					if (framenumber % ApplicationConstants.FRAME_LIMIT == 0) {
						byte[] framebytes = matToBytes(frame);
						System.out.println("frame_" + (framenumber / ApplicationConstants.FRAME_LIMIT));
						framebytesMap.put("frame_" + (framenumber / ApplicationConstants.FRAME_LIMIT), framebytes);
					}
					framenumber++;
				}

			}

		}
		// Closing the videoCapture
		if (null != videoCapture) {
			if (videoCapture.isOpened()) {
				videoCapture.close();
				LOGGER.info("VideoCapture is closed..");
			}
		}

		return framebytesMap;

	}

}
