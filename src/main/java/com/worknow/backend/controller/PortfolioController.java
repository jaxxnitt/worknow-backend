package com.worknow.backend.controller;

import com.worknow.backend.dto.VideoUploadResponse;
import com.worknow.backend.model.User;
import com.worknow.backend.repository.UserRepository;
import com.worknow.backend.service.CloudinaryService;
import com.worknow.backend.service.VideoValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/portfolio")
@CrossOrigin
public class PortfolioController {

    private final CloudinaryService cloudinaryService;
    private final VideoValidationService validationService;
    private final UserRepository userRepo;

    public PortfolioController(
            CloudinaryService cloudinaryService,
            VideoValidationService validationService,
            UserRepository userRepo
    ) {
        this.cloudinaryService = cloudinaryService;
        this.validationService = validationService;
        this.userRepo = userRepo;
    }

    /**
     * Upload or replace portfolio video.
     * POST /portfolio/video
     */
    @PostMapping("/video")
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @RequestParam("video") MultipartFile videoFile,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(VideoUploadResponse.error("Authentication required"));
        }

        try {
            // Validate video file
            validationService.validateVideo(videoFile);

            // Delete existing video if present
            String existingVideoUrl = user.getPortfolioVideoUrl();
            if (existingVideoUrl != null && !existingVideoUrl.isEmpty()) {
                try {
                    cloudinaryService.deleteVideo(existingVideoUrl);
                } catch (Exception e) {
                    // Log but don't fail - old video cleanup is not critical
                    System.err.println("Warning: Could not delete old video: " + e.getMessage());
                }
            }

            // Upload new video
            String videoUrl = cloudinaryService.uploadPortfolioVideo(videoFile, user.getId());

            // Update user record
            user.setPortfolioVideoUrl(videoUrl);
            userRepo.save(user);

            return ResponseEntity.ok(VideoUploadResponse.success(videoUrl));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(VideoUploadResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(VideoUploadResponse.error("Failed to upload video. Please try again."));
        }
    }

    /**
     * Delete portfolio video.
     * DELETE /portfolio/video
     */
    @DeleteMapping("/video")
    public ResponseEntity<VideoUploadResponse> deleteVideo(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(VideoUploadResponse.error("Authentication required"));
        }

        try {
            String videoUrl = user.getPortfolioVideoUrl();

            if (videoUrl == null || videoUrl.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(VideoUploadResponse.error("No portfolio video to delete"));
            }

            // Delete from Cloudinary
            cloudinaryService.deleteVideo(videoUrl);

            // Update user record
            user.setPortfolioVideoUrl(null);
            userRepo.save(user);

            return ResponseEntity.ok(new VideoUploadResponse(null, "Video deleted successfully", true));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(VideoUploadResponse.error("Failed to delete video. Please try again."));
        }
    }

    /**
     * Get current portfolio video URL.
     * GET /portfolio/video
     */
    @GetMapping("/video")
    public ResponseEntity<?> getVideo(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(VideoUploadResponse.error("Authentication required"));
        }

        String videoUrl = user.getPortfolioVideoUrl();
        boolean hasVideo = videoUrl != null && !videoUrl.isEmpty();

        Map<String, Object> response = new HashMap<>();
        response.put("hasVideo", hasVideo);
        response.put("videoUrl", videoUrl);

        return ResponseEntity.ok(response);
    }
}
