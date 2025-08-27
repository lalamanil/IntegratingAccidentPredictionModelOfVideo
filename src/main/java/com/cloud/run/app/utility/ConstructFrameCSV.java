/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.utility;

import java.util.Set;

public class ConstructFrameCSV {

	 public static String formFrameCSV(String bucketName, String prefix, Set<String> keys) {

		StringBuilder builder = new StringBuilder();

		for (String name : keys) {
			builder.append("gs://" + bucketName + "/" + prefix + "/" + name + ".jpg");
			builder.append("\r\n");
		}

		return builder.toString();

	}

}
