package com.worknow.backend.dto;

public class VideoUploadResponse {

    private String videoUrl;
    private String message;
    private boolean success;

    public VideoUploadResponse() {}

    public VideoUploadResponse(String videoUrl, String message, boolean success) {
        this.videoUrl = videoUrl;
        this.message = message;
        this.success = success;
    }

    public static VideoUploadResponse success(String videoUrl) {
        return new VideoUploadResponse(videoUrl, "Video uploaded successfully", true);
    }

    public static VideoUploadResponse error(String message) {
        return new VideoUploadResponse(null, message, false);
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
