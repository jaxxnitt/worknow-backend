package com.worknow.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads a video to Cloudinary with portfolio-specific settings.
     *
     * @param file   The video file to upload
     * @param userId The user ID (used for organizing uploads)
     * @return The secure URL of the uploaded video
     */
    public String uploadPortfolioVideo(MultipartFile file, Long userId) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "resource_type", "video",
                "folder", "worknow/portfolio-videos",
                "public_id", "user_" + userId + "_portfolio_" + System.currentTimeMillis(),
                "overwrite", true
        );

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Deletes a video from Cloudinary by extracting public_id from URL.
     *
     * @param videoUrl The Cloudinary video URL
     */
    public void deleteVideo(String videoUrl) throws IOException {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return;
        }

        String publicId = extractPublicId(videoUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
        }
    }

    private String extractPublicId(String url) {
        try {
            // URL format: https://res.cloudinary.com/{cloud}/video/upload/v{version}/{folder}/{public_id}.{ext}
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String path = url.substring(uploadIndex + 8);
            // Remove version if present (v followed by numbers)
            if (path.startsWith("v") && path.contains("/")) {
                path = path.substring(path.indexOf("/") + 1);
            }
            // Remove extension
            int lastDot = path.lastIndexOf(".");
            if (lastDot > 0) {
                path = path.substring(0, lastDot);
            }
            return path;
        } catch (Exception e) {
            return null;
        }
    }
}
