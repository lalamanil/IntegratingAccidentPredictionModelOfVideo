/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class CleanUpUtility {

	private static final Logger LOGGER = Logger.getLogger(CleanUpUtility.class.getName());

	public static void deleteFileStoreInTempLocation(String filepath) {
		Path path = Paths.get(filepath);
		try {
			boolean deleteflag = Files.deleteIfExists(path);

			if (deleteflag) {
				LOGGER.info(filepath + " is deleted.");
			}

		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
