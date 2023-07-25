package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, String> {

    boolean existsByCategoryName(String categoryName);

    @Query(value="SELECT cat.categoryName FROM Category cat WHERE cat.visible=true ORDER BY cat.categoryName")
    List<String> getCategoriesVisible();

    @Query(value="SELECT cat FROM Category cat ORDER BY cat.categoryName")
    List<Category> getAllCategories();

    @Query(value="SELECT cat FROM Category cat WHERE cat.visible=true AND cat.categoryName IN(:categories)")
    Set<Category> getCategoriesIn(@Param("categories") Set<String> categories);
}
