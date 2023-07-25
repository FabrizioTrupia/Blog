package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.Rating;
import it.cgmconsulting.myblog.entity.RatingId;
import it.cgmconsulting.myblog.repository.RatingRepository;
import it.cgmconsulting.myblog.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final PostService postService;
    private final RatingRepository ratingRepository;

    public ResponseEntity<?> addRate(long userId, long postId, byte rate) {
        Post p = postService.findVisiblePost(postId, LocalDateTime.now());
        Rating r = new Rating(new RatingId(new User(userId), p), rate);
        ratingRepository.save(r);
        return new ResponseEntity("Your rate has been registered", HttpStatus.OK);
    }
}
