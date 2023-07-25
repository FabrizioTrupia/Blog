package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.PostImage;
import it.cgmconsulting.myblog.entity.PostImageId;
import it.cgmconsulting.myblog.entity.common.ImagePosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PostImageRepository extends JpaRepository<PostImage, PostImageId> {

    long countByPostImageIdPostIdAndImagePosition(long postId, ImagePosition imagePosition);

    void deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(long postId, Set<String> filenames);
}
