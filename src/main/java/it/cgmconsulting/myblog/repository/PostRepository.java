package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.common.ImagePosition;
import it.cgmconsulting.myblog.payload.response.BestRatedPost;
import it.cgmconsulting.myblog.payload.response.PostBoxesResponse;
import it.cgmconsulting.myblog.payload.response.PostDetailResponse;
import it.cgmconsulting.myblog.payload.response.PostSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, long id);

    Optional<Post> findByIdAndPublishedAtNotNullAndPublishedAtBefore(long id, LocalDateTime now);

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.PostBoxesResponse(" +
            "p.id, " +
            "(SELECT pi.postImageId.filename FROM PostImage pi WHERE pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition) as image, " +
            "p.author.username, " +
            "p.publishedAt, " +
            "p.title, " +
            "p.overview) " +
            "FROM Post p " +
            "WHERE p.publishedAt IS NOT NULL AND p.publishedAt < :now " +
            "ORDER BY p.publishedAt DESC",
            countQuery = "SELECT COUNT(pi.postImageId.filename) FROM PostImage pi LEFT JOIN Post p ON pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition AND :now=:now"
            )
    Page<PostBoxesResponse> getPostBoxes(Pageable pageable, @Param("now") LocalDateTime now, @Param("imagePosition") ImagePosition imagePosition);

    @Query(value="SELECT cat.categoryName " +
            "FROM Post p " +
            "INNER JOIN p.categories cat " +
            "WHERE p.id = :postId AND cat.visible = true")
    Set<String> getCategoriesByPost(@Param("postId") long postId);


    @Query(value="SELECT new it.cgmconsulting.myblog.payload.response.PostDetailResponse(" +
            "p.id, " +
            "(SELECT pi.postImageId.filename FROM PostImage pi WHERE pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition) as image, " +
            "p.title, " +
            "p.content,  " +
            "p.author.username, " +
            "p.publishedAt, " +
            "(SELECT COALESCE(ROUND(AVG(r.rate),2), 0.0) FROM Rating r WHERE r.ratingId.post.id = :postId) as average " +
            ") FROM Post p " +
            "WHERE p.id = :postId " +
            "AND p.publishedAt IS NOT NULL AND p.publishedAt < :now "
    )
    PostDetailResponse getPostDetail(@Param("postId") long postId, @Param("now") LocalDateTime now, @Param("imagePosition") ImagePosition imagePosition);


    @Query(value="SELECT new it.cgmconsulting.myblog.payload.response.PostBoxesResponse(" +
            "p.id, " +
            "(SELECT pi.postImageId.filename FROM PostImage pi WHERE pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition) as image, " +
            "p.author.username, " +
            "p.publishedAt, " +
            "p.title, " +
            "p.overview) " +
            "FROM Post p " +
            "INNER JOIN p.categories cat " +
            "WHERE p.publishedAt IS NOT NULL AND p.publishedAt < :now " +
            "AND cat.categoryName = :categoryName AND cat.visible = true " +
            "ORDER BY p.publishedAt DESC"
    )
    List<PostBoxesResponse> getPostsByCategory(@Param("categoryName") String categoryName, @Param("now") LocalDateTime now, @Param("imagePosition") ImagePosition imagePosition);

    @Query (value = "SELECT new it.cgmconsulting.myblog.payload.response.PostSearchResponse(" +
            "p.id, " +
            "(SELECT pi.postImageId.filename FROM PostImage pi WHERE pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition) as image, " +
            "p.title, " +
            "p.content, " +
            "p.author.username, " +
            "p.publishedAt, " +
            "p.overview) " +
            "FROM Post p " +
            "WHERE (p.publishedAt IS NOT NULL AND p.publishedAt < :now) " +
            "AND (p.title LIKE :keyword " +
            "OR p.overview LIKE :keyword " +
            "OR p.content LIKE :keyword ) " +
            "ORDER BY p.publishedAt DESC"
    )
    List<PostSearchResponse> getPublishedPostsByKeyword (@Param("keyword") String keyword, @Param("now") LocalDateTime now, @Param("imagePosition") ImagePosition position);

    @Query(value="SELECT new it.cgmconsulting.myblog.payload.response.PostBoxesResponse(" +
            "p.id, " +
            "(SELECT pi.postImageId.filename FROM PostImage pi WHERE pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition) as image, " +
            "p.author.username, " +
            "p.publishedAt, " +
            "p.title, " +
            "p.overview) " +
            "FROM Post p " +
            "INNER JOIN p.categories cat " +
            "WHERE p.publishedAt IS NOT NULL AND p.publishedAt < :now " +
            "AND p.author.username = :author " +
            "ORDER BY p.publishedAt DESC"
    )
    List<PostBoxesResponse> getPostsByAuthor(@Param("author") String author, @Param("now") LocalDateTime now, @Param("imagePosition") ImagePosition imagePosition);

    @Query(value="SELECT new it.cgmconsulting.myblog.payload.response.BestRatedPost(" +
            "r.ratingId.post.id, " +
            "r.ratingId.post.title, " +
            "ROUND(AVG(r.rate),2) as media " +
            ") FROM Rating r " +
            "WHERE (r.updatedAt >= :start AND r.updatedAt <= :end) " +
            "GROUP BY r.ratingId.post.id, r.ratingId.post.title " +
            "ORDER BY media DESC")
    List<BestRatedPost> getMostRatedInPeriod(@Param("start")LocalDateTime start, @Param("end") LocalDateTime end);
}
