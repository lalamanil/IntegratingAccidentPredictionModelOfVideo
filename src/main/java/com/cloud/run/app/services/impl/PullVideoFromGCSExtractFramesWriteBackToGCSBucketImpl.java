
/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import com.cloud.run.app.constants.ApplicationConstants;
import com.cloud.run.app.services.PullVideoFromGCSExtractFramesWriteBackToGCSBucket;
import com.cloud.run.app.utility.BatchPredictionUtility;
import com.cloud.run.app.utility.CleanUpUtility;
import com.cloud.run.app.utility.ConstructFrameJsonl;
import com.cloud.run.app.utility.ExtractFramesUsingFFmpeg;
import com.cloud.run.app.utility.GCSUtilty;

@Service
public class PullVideoFromGCSExtractFramesWriteBackToGCSBucketImpl
		implements PullVideoFromGCSExtractFramesWriteBackToGCSBucket {

	private static final Logger LOGGER = Logger
			.getLogger(PullVideoFromGCSExtractFramesWriteBackToGCSBucketImpl.class.getName());

	public Map<String, Object> integrationLogic(String bucket, String name) {
		// TODO Auto-generated method stub
		String videoFilePath = GCSUtilty.pullVideoFromBucket(bucket, name);
		String prefixPath = name.split("\\.")[0];
		Map<String, Object> responseMap = new HashMap<String, Object>();
		LOGGER.info("Prefix path of object is:" + prefixPath);
		if (null != videoFilePath) {
			Map<String, byte[]> framebytesMap = ExtractFramesUsingFFmpeg.extractAndUpload(videoFilePath);
			if (null != framebytesMap && !framebytesMap.isEmpty()) {
				Set<Map.Entry<String, byte[]>> entrySet = framebytesMap.entrySet();
				ExecutorService executor = Executors.newFixedThreadPool(10);
				for (Map.Entry<String, byte[]> entry : entrySet) {
					executor.submit(() -> {
						try {
							GCSUtilty.writeObjectToBucket(ApplicationConstants.GCS_BUCKET_TO_WIRTE_FRAMES,
									prefixPath + "/" + entry.getKey() + ".jpg", entry.getValue());

						} catch (Exception e) {
							// TODO: handle exception
							LOGGER.info("Failed to upload frame: " + prefixPath + "/" + entry.getKey());
						}
					});
				}

				responseMap.put("bucket", ApplicationConstants.GCS_BUCKET_TO_WIRTE_FRAMES);
				responseMap.put("parentfolder", prefixPath);
				executor.shutdown();
				try {
					if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
						executor.shutdownNow();
					}
				} catch (InterruptedException e) {
					// TODO: handle exception
					executor.shutdownNow();
					Thread.currentThread().interrupt();
				}
			} else {
				LOGGER.info("framebytes map is null or empty. Please check application logs");
			}
			String jsonlGCSframes = ConstructFrameJsonl.formFrameJsonl(ApplicationConstants.GCS_BUCKET_TO_WIRTE_FRAMES,
					prefixPath, framebytesMap.keySet());
			if (null != jsonlGCSframes && !jsonlGCSframes.isEmpty()) {
				byte[] jsonlbytes = jsonlGCSframes.getBytes();
				// Storing GCS paths for frames to jsonl file
				GCSUtilty.writeObjectToBucket(ApplicationConstants.GCS_BUCKET_JSONL, prefixPath + ".jsonl", jsonlbytes);
				LOGGER.info(
						"file stored in:" + ApplicationConstants.GCS_BUCKET_JSONL + " with " + prefixPath + ".jsonl");
				// Triggering Batch Inference to predict accidents frames
				String gcsSourceInput = "gs://" + ApplicationConstants.GCS_BUCKET_JSONL + "/" + prefixPath + ".jsonl";
				String gcsoutputuri = "gs://" + ApplicationConstants.GCS_BUCKET_BATCH_INFERENCE_OUTPUT + "/";
				// submitting the batch inference Job
				BatchPredictionUtility.batchPrediction(gcsSourceInput, gcsoutputuri);

			} else {
				LOGGER.info("jsonlGCSframes is null or empty");
			}

			// deleting video stored in /tmp/ folder
			CleanUpUtility.deleteFileStoreInTempLocation(videoFilePath);

			LOGGER.info(framebytesMap.size() + " images were uploaded to GCS bucket:"
					+ ApplicationConstants.GCS_BUCKET_TO_WIRTE_FRAMES + " : in folder " + prefixPath);

		} else {
			LOGGER.info("videoFilePath is null. Please check application logs");
		}

		return responseMap;
	}
}
