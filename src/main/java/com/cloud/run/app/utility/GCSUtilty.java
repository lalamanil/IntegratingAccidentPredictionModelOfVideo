/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import com.cloud.run.app.constants.ApplicationConstants;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

public class GCSUtilty {

	private static final Logger LOGGER = Logger.getLogger(GCSUtilty.class.getName());
	private static Storage STORAGE;

	static {
		InputStream inputStream = GCSUtilty.class.getClassLoader().getResourceAsStream("ServiceAccount.json");
		if (null != inputStream) {
			try {
				GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream)
						.createScoped("https://www.googleapis.com/auth/cloud-platform");
				STORAGE = StorageOptions.newBuilder().setCredentials(googleCredentials).build().getService();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			LOGGER.info("Inputstream for Service account is null.");
		}
	}

	public static String pullVideoFromBucket(String bucket, String name) {
		String videoPathinCloudRun = null;
		if (null != STORAGE) {
			FileOutputStream fout = null;
			try {
				Blob blob = STORAGE.get(bucket, name);
				if (null != blob) {
					videoPathinCloudRun = ApplicationConstants.LOCAL_PATH + "/" + name;
					fout = new FileOutputStream(new File(videoPathinCloudRun));
					blob.downloadTo(fout);
					LOGGER.info("Saved file in " + videoPathinCloudRun);

				} else {
					LOGGER.info("blob is null. There is no object name:" + name + " in bucket:" + bucket);
				}

			} catch (StorageException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (null != fout) {

					try {
						fout.close();
						LOGGER.info("Closing fileOutputStream...");
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}
			}
		} else {
			LOGGER.info("Storage Object is null. Please check application logs");
		}
		return videoPathinCloudRun;

	}

	public static void writeObjectToBucket(String bucket, String name, byte[] content) {
		if (null != STORAGE) {
			try {
				BlobInfo blobInfo = BlobInfo.newBuilder(bucket, name).build();
				STORAGE.create(blobInfo, content);
				// LOGGER.info("Uploaded:" + name);
			} catch (StorageException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			LOGGER.info("Storage Object is null. Please check application logs.");
		}

	}

	public static String readReadBatchInferenceFrameFromBucket(String bucket, String objectName) {

		String imagePath = null;

		if (null != STORAGE) {
			FileOutputStream fileOutputStream = null;
			try {
				Blob blob = STORAGE.get(BlobId.of(bucket, objectName));
				objectName = objectName.replace("/", "_");
				fileOutputStream = new FileOutputStream(
						new File(ApplicationConstants.batchInferenceOutPutFiles + objectName));
				blob.downloadTo(fileOutputStream);

				System.out.println("saved:" + ApplicationConstants.batchInferenceOutPutFiles + objectName);
				imagePath = ApplicationConstants.batchInferenceOutPutFiles + objectName;
			} catch (StorageException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (null != fileOutputStream) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		} else {
			LOGGER.info("STORAGE is null or empty..");
		}
		
		return imagePath;

	}

}
