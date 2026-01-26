package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    Optional<ReviewReply> findByReview_ReviewId(Long reviewId);

    List<ReviewReply> findByReview_ReviewIdIn(Collection<Long> reviewIds);
}
