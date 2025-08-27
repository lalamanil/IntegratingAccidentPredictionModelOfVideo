/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.constants;

public interface ApplicationConstants {

	public static final String LOCAL_PATH = "/tmp";

	public static final int FRAME_LIMIT = 10;

	public static final String GCS_BUCKET_TO_WIRTE_FRAMES = "accident-frames-for-video";

	public static final String GCS_BUCKET_JSONL = "accident-frames-csv";

	public static final String GCS_BUCKET_BATCH_INFERENCE_OUTPUT = "accident-batch-inference-output";

	public static final String PROJECT_ID = "gmemoridev2016";

	public static final String LOCATION = "us-central1";

	public static final String modelId = "8393147299395534848";

	public static final String modelName = "accident-detection-dataset";

	public static final String batchInferenceOutPutFiles = "/Users/lalamanil/voiceanalyzer/BatchInferenceAnnotateImages/";

}
