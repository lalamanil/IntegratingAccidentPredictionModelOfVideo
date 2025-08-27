/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.utility;

import java.util.Set;

public class ConstructFrameJsonl {

	public static String formFrameJsonl(String bucketName, String prefix, Set<String> keys) {

		StringBuilder builder = new StringBuilder();

		for (String name : keys) {

			String jsonl = "{\"content\":\"gs://" + bucketName + "/" + prefix + "/" + name + ".jpg\"}";
			builder.append(jsonl);
			builder.append("\r\n");
		}

		return builder.toString();

	}

}
