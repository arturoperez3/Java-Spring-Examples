/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;
import org.magnum.dataup.model.Video;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoCtrl {

	// Store videos
	private HashMap<Long, Video> videos = new HashMap<>();

	private long videoID = 1;

	// Access to VideoFileManager API
	private VideoFileManager videoDataMgr;


	// Get all our videos
	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		return videos.values();
	}


	// Add video meta data to our map, which we can later use to add/retrieve video binary data
	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video) throws IOException {
		videoDataMgr = VideoFileManager.get();

		// Create Video object with video metadata for local storage
		Video local = Video.create().withContentType(video.getContentType())
				.withDuration(video.getDuration()).withSubject(video.getSubject())
				.withTitle(video.getTitle()).build();

		local.setId(videoID);
		local.setDataUrl(getDataUrl(videoID));
		videos.put(videoID, local);
		videoID += 1;

		return local;
	}


	// Save video binary data to disk
	@RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(@PathVariable("id") long id, @RequestParam("data") MultipartFile videoData,
									HttpServletResponse response)
		throws IOException {

		VideoStatus status = new VideoStatus(VideoStatus.VideoState.READY);

		// Error checking
		if (!videos.containsKey(id)) {
			response.setStatus(404);
			return null;
		}

		Video video = videos.get(id);
		saveSomeVideo(video, videoData); // Call API to save to disk
		response.setStatus(200);

		return status;
	}


	// Get video binary data from disk
	@RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET)
	void getData(@PathVariable("id") long id, HttpServletResponse response)
		throws IOException {

		// Error checking
		if (!videos.containsKey(id)) {
			response.setStatus(404);
			return;
		}

		Video video = videos.get(id);
		serveSomeVideo(video, response); /// Call API to retrieve from disk
		response.setStatus(200);
	}

	// Use VideoFileManager API to save and retrieve video binaries from disk
	public void saveSomeVideo(Video video, MultipartFile videoData) throws IOException {
		videoDataMgr.saveVideoData(video, videoData.getInputStream());
	}

	public void serveSomeVideo(Video video, HttpServletResponse response) throws IOException {
		videoDataMgr.copyVideoData(video, response.getOutputStream());
	}

	private String getUrlBaseForLocalServer() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String base = "http://"+request.getServerName()+((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
		return base;
	}

	private String getDataUrl(long id){
		String url = getUrlBaseForLocalServer() + "/video/" + id + "/data";
		return url;
	}
}
