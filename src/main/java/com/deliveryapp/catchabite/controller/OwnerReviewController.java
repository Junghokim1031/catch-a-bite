package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.OwnerReviewDTO;
import com.deliveryapp.catchabite.dto.OwnerReviewReplyRequestDTO;
import com.deliveryapp.catchabite.dto.PageResponseDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.OwnerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/reviews")
public class OwnerReviewController {

    private final OwnerReviewService ownerReviewService;
    private final OwnerContext ownerContext;

    // 리뷰 목록 조회 (페이징/최신순)
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<OwnerReviewDTO>>> list(
            Principal principal,
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "reviewCreatedAt")
        );

        return ResponseEntity.ok(ApiResponse.ok(
                ownerReviewService.listReviews(storeOwnerId, storeId, pageable)
        ));
    }

    // 리뷰 답글 작성 (B안: review_reply 테이블)
    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<ApiResponse<Object>> reply(
            Principal principal,
            @PathVariable Long storeId,
            @PathVariable Long reviewId,
            @RequestBody OwnerReviewReplyRequestDTO dto
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        ownerReviewService.writeReply(storeOwnerId, storeId, reviewId, dto.getContent());
        return ResponseEntity.ok(ApiResponse.ok(null, "review reply created"));
    }
}
