package com.worknow.backend.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Set;

@Service
public class VideoValidationService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "video/mp4",
            "video/quicktime",    // .mov
            "video/x-msvideo",    // .avi
            "video/webm",
            "video/x-matroska"    // .mkv
    );

    private static final long MAX_DURATION_SECONDS = 60; // 1 minute
    private static final long MAX_FILE_SIZE_BYTES = 100 * 1024 * 1024; // 100MB

    /**
     * Validates video file type, size, and duration.
     *
     * @param file The video file to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateVideo(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Video file is required");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Video file size exceeds maximum allowed (100MB)");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid video format. Allowed formats: MP4, MOV, AVI, WebM, MKV");
        }

        // Check duration using Apache Tika
        try {
            double durationSeconds = extractDuration(file);
            if (durationSeconds > MAX_DURATION_SECONDS) {
                throw new IllegalArgumentException(
                        "Video duration exceeds maximum allowed (1 minute). Your video is " +
                                Math.round(durationSeconds) + " seconds.");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            // If we can't extract duration, log warning but allow upload
            // Cloudinary will handle further validation
            System.err.println("Warning: Could not extract video duration: " + e.getMessage());
        }
    }

    private double extractDuration(MultipartFile file) throws Exception {
        try (InputStream stream = file.getInputStream()) {
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();

            parser.parse(stream, handler, metadata);

            String duration = metadata.get(XMPDM.DURATION);
            if (duration != null) {
                // Duration is in milliseconds
                return Double.parseDouble(duration) / 1000.0;
            }

            // Fallback: try other duration fields
            String altDuration = metadata.get("duration");
            if (altDuration != null) {
                return Double.parseDouble(altDuration) / 1000.0;
            }

            throw new Exception("Duration metadata not found");
        }
    }
}
