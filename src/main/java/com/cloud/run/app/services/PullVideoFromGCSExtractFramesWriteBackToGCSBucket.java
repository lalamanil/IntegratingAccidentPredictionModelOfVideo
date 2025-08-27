/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.services;

import java.util.Map;

public interface PullVideoFromGCSExtractFramesWriteBackToGCSBucket {

	public Map<String, Object> integrationLogic(String bucket, String name);

}
