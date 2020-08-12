package org.magnum.dataup;
import java.util.Collection;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * This interface defines an API for a VideoSvc. The
 * interface is used to provide a contract for client/server
 * interactions. */
public interface VideoSvcApi {

	public static final String DATA_PARAMETER = "data";

	public static final String ID_PARAMETER = "id";

	public static final String VIDEO_SVC_PATH = "/video";
	
	public static final String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{id}/data";

	// Get all our videos
	@GET(VIDEO_SVC_PATH)
	public Collection<Video> getVideoList();

	// Add video meta data to our map, which we can later use to add/retrieve video binary data
	@POST(VIDEO_SVC_PATH)
	public Video addVideo(@Body Video v);

	// Save video binary data to disk
	@Multipart
	@POST(VIDEO_DATA_PATH)
	public VideoStatus setVideoData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile videoData);

	// Get video binary data from disk
	@Streaming
    @GET(VIDEO_DATA_PATH)
    Response getData(@Path(ID_PARAMETER) long id);
}
