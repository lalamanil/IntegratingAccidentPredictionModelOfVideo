/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.utility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import com.cloud.run.app.constants.ApplicationConstants;

public class ExtractFramesUsingFFmpeg {

	public static Map<String, byte[]> extractAndUpload(String videopath) {

		FFmpegFrameGrabber grabber = null;
		Java2DFrameConverter converter = null;
		Map<String, byte[]> frameBytesMap = new HashMap<String, byte[]>();
		try {
			grabber = new FFmpegFrameGrabber(videopath);
			grabber.start();
			converter = new Java2DFrameConverter();
			Frame frame;
			int frameNumber = 0;
			while ((frame = grabber.grabImage()) != null) {
				BufferedImage image = converter.convert(frame);
				if (frameNumber % ApplicationConstants.FRAME_LIMIT == 0) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					try {
						ImageIO.write(image, "jpg", byteArrayOutputStream);
						byte[] imageBytes = byteArrayOutputStream.toByteArray();
						frameBytesMap.put("frame_" + frameNumber / ApplicationConstants.FRAME_LIMIT, imageBytes);
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				frameNumber++;
			}

			grabber.stop();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (null != grabber) {

				try {
					grabber.close();
					System.out.println("grabber closed..");
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}

			if (null != converter) {

				converter.close();
				System.out.println("Converter closed...");

			}
		}

		return frameBytesMap;

	}

	public static void main(String[] args) {

		Map<String, byte[]> frameMap = extractAndUpload("/Users/lalamanil/voiceanalyzer/Obama.mp4");

		System.out.println(frameMap.size());

	}

}
