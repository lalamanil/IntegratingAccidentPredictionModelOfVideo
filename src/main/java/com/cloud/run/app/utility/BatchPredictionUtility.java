/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import com.cloud.run.app.constants.ApplicationConstants;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.util.ValueConverter;
import com.google.cloud.aiplatform.v1.AcceleratorType;
import com.google.cloud.aiplatform.v1.BatchDedicatedResources;
import com.google.cloud.aiplatform.v1.BatchPredictionJob;
import com.google.cloud.aiplatform.v1.BatchPredictionJob.InputConfig;
import com.google.cloud.aiplatform.v1.BatchPredictionJob.OutputConfig;
import com.google.cloud.aiplatform.v1.GcsDestination;
import com.google.cloud.aiplatform.v1.GcsSource;
import com.google.cloud.aiplatform.v1.JobServiceClient;
import com.google.cloud.aiplatform.v1.JobServiceSettings;
import com.google.cloud.aiplatform.v1.LocationName;
import com.google.cloud.aiplatform.v1.MachineSpec;
import com.google.cloud.aiplatform.v1.ModelName;
import com.google.protobuf.Value;

public class BatchPredictionUtility {

	private static final Logger LOGGER = Logger.getLogger(BatchPredictionUtility.class.getName());

	public static JobServiceClient jobServiceClient;

	static {
		try {
			InputStream inputStream = BatchPredictionUtility.class.getClassLoader()
					.getResourceAsStream("ServiceAccount.json");
			GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream)
					.createScoped("https://www.googleapis.com/auth/cloud-platform");
			JobServiceSettings jobServiceSettings = JobServiceSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
					.setEndpoint("us-central1-aiplatform.googleapis.com:443").build();
			jobServiceClient = JobServiceClient.create(jobServiceSettings);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void batchPrediction(String gcsinputuri, String gcsoutputuri) {

		String project = ApplicationConstants.PROJECT_ID;
		String location = ApplicationConstants.LOCATION;
		String modelId = ApplicationConstants.modelId;
		String model = ApplicationConstants.modelName;
		if (null != jobServiceClient) {

			System.out.println(model);

			// Input Config
			GcsSource gcsSource = GcsSource.newBuilder().addUris(gcsinputuri).build();
			InputConfig inputConfig = InputConfig.newBuilder().setInstancesFormat("jsonl").setGcsSource(gcsSource)
					.build();

			// Out Config
			GcsDestination gcsDestination = GcsDestination.newBuilder().setOutputUriPrefix(gcsoutputuri).build();
			OutputConfig outputConfig = OutputConfig.newBuilder().setPredictionsFormat("jsonl")
					.setGcsDestination(gcsDestination).build();

			String modelName = ModelName.of(project, location, modelId).toString();
			// Batch prediction Job

			Value modelParameters = ValueConverter.EMPTY_VALUE;

			// Machine spec

			MachineSpec machineSpec = MachineSpec.newBuilder().setMachineType("n1-standard-2")
					.setAcceleratorType(AcceleratorType.NVIDIA_TESLA_T4).setAcceleratorCount(1).build();

			BatchDedicatedResources dedicatedResources = BatchDedicatedResources.newBuilder()
					.setMachineSpec(machineSpec).setStartingReplicaCount(1).setMaxReplicaCount(1).build();

			BatchPredictionJob batchPredictionJob = BatchPredictionJob.newBuilder()
					.setDisplayName("accident-detection-batch").setModel(modelName).setModelParameters(modelParameters)
					.setInputConfig(inputConfig).setOutputConfig(outputConfig).setModelParameters(modelParameters)
					.setDedicatedResources(dedicatedResources).build();

			LocationName parent = LocationName.of(project, location);

			BatchPredictionJob response = jobServiceClient.createBatchPredictionJob(parent, batchPredictionJob);

			System.out.println("response:" + response);

			System.out.println("Batch prediction Job created:" + response.getName());

			System.out.println("Results will be stored in :" + gcsoutputuri);

		} else {
			LOGGER.info("predictionServiceClient is null. Please check application logs");
		}

	}

	public static void main(String[] args) {

		String gcsinputuri = "gs://" + ApplicationConstants.GCS_BUCKET_JSONL + "/MOSTBRUTAL.jsonl";

		String gcsoutputuri = "gs://" + ApplicationConstants.GCS_BUCKET_BATCH_INFERENCE_OUTPUT + "/";

		batchPrediction(gcsinputuri, gcsoutputuri);

	}

}
