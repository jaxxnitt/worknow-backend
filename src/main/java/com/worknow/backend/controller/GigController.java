package com.worknow.backend.controller;

import com.worknow.backend.dto.VideoUploadResponse;
import com.worknow.backend.model.Gig;
import com.worknow.backend.model.User;
import com.worknow.backend.repository.GigRepository;
import com.worknow.backend.service.CloudinaryService;
import com.worknow.backend.service.VideoValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/gigs")
@CrossOrigin
public class GigController {

    private final GigRepository repo;
    private final CloudinaryService cloudinaryService;
    private final VideoValidationService validationService;

    public GigController(
            GigRepository repo,
            CloudinaryService cloudinaryService,
            VideoValidationService validationService
    ) {
        this.repo = repo;
        this.cloudinaryService = cloudinaryService;
        this.validationService = validationService;
    }

    // ===============================
    // CREATE GIG (AUTH REQUIRED)
    // ===============================
    @PostMapping
    public Gig create(
            @RequestBody Gig gig,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        LocalDateTime now = LocalDateTime.now();

        // Server owns these fields
        gig.setPosterId(user.getId());
        gig.setPosterName(user.getName());
        gig.setCreatedAt(now);
        gig.setActive(true);

        // Convert "Today / Tomorrow" into real expiry
        if ("Tomorrow".equalsIgnoreCase(gig.getDeadline())) {
            gig.setExpiresAt(now.plusHours(48));
        } else {
            gig.setExpiresAt(now.plusHours(24));
        }

        return repo.save(gig);
    }

    // ===============================
    // LIST GIGS (PUBLIC)
    // ===============================
    @GetMapping
    public List<Gig> list(@RequestParam(required = false) String city) {

        LocalDateTime now = LocalDateTime.now();

        if (city != null && !city.trim().isEmpty()) {
            return repo
                    .findTop50ByActiveTrueAndExpiresAtAfterAndCityContainingIgnoreCaseOrderByCreatedAtDesc(
                            now,
                            city.trim()
                    );
        }

        return repo.findTop50ByActiveTrueAndExpiresAtAfterOrderByCreatedAtDesc(now);
    }

    // ===============================
    // UPLOAD GIG VIDEO (AUTH REQUIRED)
    // ===============================
    @PostMapping("/{gigId}/video")
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @PathVariable Long gigId,
            @RequestParam("video") MultipartFile videoFile,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(VideoUploadResponse.error("Authentication required"));
        }

        Gig gig = repo.findById(gigId).orElse(null);
        if (gig == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(VideoUploadResponse.error("Gig not found"));
        }

        // Only the poster can upload video
        if (!gig.getPosterId().equals(user.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(VideoUploadResponse.error("Only the gig poster can upload video"));
        }

        try {
            // Validate video file
            validationService.validateVideo(videoFile);

            // Delete existing video if present
            String existingVideoUrl = gig.getVideoUrl();
            if (existingVideoUrl != null && !existingVideoUrl.isEmpty()) {
                try {
                    cloudinaryService.deleteVideo(existingVideoUrl);
                } catch (Exception e) {
                    System.err.println("Warning: Could not delete old gig video: " + e.getMessage());
                }
            }

            // Upload new video
            String videoUrl = cloudinaryService.uploadGigVideo(videoFile, gigId);

            // Update gig record
            gig.setVideoUrl(videoUrl);
            repo.save(gig);

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

    // ===============================
    // DELETE GIG VIDEO (AUTH REQUIRED)
    // ===============================
    @DeleteMapping("/{gigId}/video")
    public ResponseEntity<VideoUploadResponse> deleteVideo(
            @PathVariable Long gigId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(VideoUploadResponse.error("Authentication required"));
        }

        Gig gig = repo.findById(gigId).orElse(null);
        if (gig == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(VideoUploadResponse.error("Gig not found"));
        }

        // Only the poster can delete video
        if (!gig.getPosterId().equals(user.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(VideoUploadResponse.error("Only the gig poster can delete video"));
        }

        try {
            String videoUrl = gig.getVideoUrl();

            if (videoUrl == null || videoUrl.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(VideoUploadResponse.error("No video to delete"));
            }

            // Delete from Cloudinary
            cloudinaryService.deleteVideo(videoUrl);

            // Update gig record
            gig.setVideoUrl(null);
            repo.save(gig);

            return ResponseEntity.ok(new VideoUploadResponse(null, "Video deleted successfully", true));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(VideoUploadResponse.error("Failed to delete video. Please try again."));
        }
    }
}
