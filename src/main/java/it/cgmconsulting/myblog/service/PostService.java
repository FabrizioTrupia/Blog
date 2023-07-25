package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.response.BestRatedPost;
import it.cgmconsulting.myblog.payload.response.PostBoxesResponse;
import it.cgmconsulting.myblog.repository.CategoryRepository;
import it.cgmconsulting.myblog.repository.PostRepository;
import it.cgmconsulting.myblog.entity.common.ImagePosition;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.payload.response.PostSearchResponse;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> createPost(PostRequest request, UserPrincipal principal){
        if(postRepository.existsByTitle(request.getTitle()))
            return new ResponseEntity("A post with this title already present", HttpStatus.BAD_REQUEST);
        Post p = new Post(request.getTitle(), request.getOverview(), request.getContent(), new User(principal.getId()));
        postRepository.save(p);
        return new ResponseEntity("New Post created", HttpStatus.CREATED); // code: 201
    }

    @Transactional
    public ResponseEntity<?> updatePost(long postId, PostRequest request, UserPrincipal principal){
        // verifica unicità title
        if(postRepository.existsByTitleAndIdNot(request.getTitle(), postId))
            return new ResponseEntity("Title already present in another post", HttpStatus.BAD_REQUEST);
        // verifica esistenza post
        Post p = findPost(postId);
        // settare a null publishedAt + i valori della request
        p.setTitle(request.getTitle());
        p.setOverview(request.getOverview());
        p.setContent(request.getContent());
        p.setAuthor(new User(principal.getId()));
        p.setPublishedAt(null);

        return new ResponseEntity("Post has been updated", HttpStatus.OK);

    }

    @Transactional
    public ResponseEntity<?> publishPost(long postId, LocalDateTime publishedAt){

        if(publishedAt.isBefore(LocalDateTime.now()))
            return new ResponseEntity("Selected publication date is in the past", HttpStatus.BAD_REQUEST);

        Post p = findPost(postId);
        String msg = "Post published";
        if(publishedAt == null)
            p.setPublishedAt(LocalDateTime.now());
        else {
            p.setPublishedAt(publishedAt);
            msg = "Post will be published in future";
        }
        return new ResponseEntity(msg, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<?> addCategories(long postId, Set<String> categories) {
        Post p = findPost(postId);
        Set<Category> categoriesToAdd = categoryRepository.getCategoriesIn(categories);
        if(categoriesToAdd.isEmpty())
            return new ResponseEntity("No categories found", HttpStatus.NOT_FOUND);
        p.setCategories(categoriesToAdd);
        return new ResponseEntity("Categories added to post", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> removeCategories(long postId) {
        Post p = findPost(postId);
        p.getCategories().clear();
        return new ResponseEntity("All Categories removed from post", HttpStatus.OK);
    }


    protected Post findPost(long postId){
        Post p = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );
        return p;
    }

    protected Post findVisiblePost(long postId, LocalDateTime now){
        Post p = postRepository.findByIdAndPublishedAtNotNullAndPublishedAtBefore(postId, LocalDateTime.now()).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );
        return p;
    }

    public ResponseEntity<?> getPostBoxes(int pageNumber, int pageSize, String direction, String sortBy, String imagePosition)  {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<PostBoxesResponse> result = postRepository.getPostBoxes(pageable, LocalDateTime.now(), ImagePosition.valueOf(imagePosition));
        List<PostBoxesResponse> list = new ArrayList<>();
        if(result.hasContent()) {
            list = result.getContent();
            for(PostBoxesResponse pbr : list){
                pbr.setCategories(postRepository.getCategoriesByPost(pbr.getId()));
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    public ResponseEntity<?> getPostDetail(long postId, String imagePosition){
        return new ResponseEntity(postRepository.getPostDetail(postId, LocalDateTime.now(), ImagePosition.valueOf(imagePosition)), HttpStatus.OK);
    }

    public ResponseEntity<?> getPostsByCategory(String categoryName){
        return new ResponseEntity(postRepository.getPostsByCategory(categoryName, LocalDateTime.now(), ImagePosition.PRE), HttpStatus.OK);
    }

    public ResponseEntity<?> getPostByKeyword(String keyword, boolean isCaseSensitive, boolean isExactMatch, ImagePosition position){
        List<PostSearchResponse> listaCompleta = postRepository.getPublishedPostsByKeyword("%"+keyword+"%", LocalDateTime.now(), position);

        List<PostSearchResponse> nuovaLista = new ArrayList<>();
        Pattern pattern = null;

        if(!isCaseSensitive && !isExactMatch) {
            pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        } else if (!isCaseSensitive && isExactMatch){
            pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
        } else if (isCaseSensitive && !isExactMatch){
            pattern = Pattern.compile(keyword);
        } else if (isCaseSensitive && isExactMatch){
            pattern = Pattern.compile("\\b" + keyword + "\\b");
        }

        Pattern finalPattern = pattern;
        nuovaLista = listaCompleta.stream()
                .filter(post -> finalPattern.matcher(post.getTitle().concat(" ").concat(post.getOverview().concat(" ").concat(post.getContent()))).find())
                .collect(Collectors.toList());

        return new ResponseEntity(nuovaLista, HttpStatus.OK);
    }

    public ResponseEntity<?> getPostsByAuthor(String author) {
        return new ResponseEntity(postRepository.getPostsByAuthor(author, LocalDateTime.now(), ImagePosition.PRE), HttpStatus.OK);
    }

    public ResponseEntity<?> getMostRatedInPeriod(LocalDate start, LocalDate end) {
        LocalDateTime s = null;
        LocalDateTime e = null;

        if(start == null && end == null){
            LocalDateTime initial = LocalDateTime.now();
             s = initial.withDayOfMonth(1);
             e = initial.withDayOfMonth(initial.toLocalDate().getDayOfMonth());
        } else {
            s = start.atTime(0,0,0);
            e = end.atTime(23,59,59);
        }
        List<BestRatedPost> brp = postRepository.getMostRatedInPeriod(s, e);
        return new ResponseEntity(brp, HttpStatus.OK);
    }
}
