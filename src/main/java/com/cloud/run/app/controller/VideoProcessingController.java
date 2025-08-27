/*
 * @Author ANIL LALAM
 */
package com.cloud.run.app.controller;

import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cloud.run.app.services.PullVideoFromGCSExtractFramesWriteBackToGCSBucket;

@Controller
@RequestMapping(value = "/")
public class VideoProcessingController {

	private static final Logger LOGGER = Logger.getLogger(VideoProcessingController.class.getName());

	@Autowired
	private PullVideoFromGCSExtractFramesWriteBackToGCSBucket integrateService;

	@RequestMapping(value = "/healthCheck", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<String> healthCheck() {
		String response = "Health check is success";
		return ResponseEntity.ok().body(response);
	}

	@RequestMapping(value = "/processVideoToFrames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> processVideoFrames(
			@RequestParam(name = "bucket", required = true) String bucketName,
			@RequestParam(name = "name", required = true) String name) {
		LOGGER.info("bucket:" + bucketName + " name:" + name);
		System.out.println(integrateService);
		Map<String, Object> responseMap = integrateService.integrationLogic(bucketName, name);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}
	
	

}
