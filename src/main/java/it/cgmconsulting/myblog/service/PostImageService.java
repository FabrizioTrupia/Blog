package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.PostImage;
import it.cgmconsulting.myblog.entity.PostImageId;
import it.cgmconsulting.myblog.repository.PostImageRepository;
import it.cgmconsulting.myblog.entity.common.ImagePosition;
import it.cgmconsulting.myblog.payload.response.UploadFileResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final AvatarService avatarService;

    @Value("${app.post.extensions}")
    String[] extensions;

    @Value("${app.post.path}")
    String imagePath;

    private List<UploadFileResponse> checkMaxNumberImages(long postId, MultipartFile[] files, ImagePosition position){
        log.info("----- number --- "+files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        long countUploadedImages = postImageRepository.countByPostImageIdPostIdAndImagePosition(postId, position);
        if( (countUploadedImages + files.length) > position.getMaxImages()) {
            result.add(new UploadFileResponse(position.getDescription(), null, null, "Max file number threshold for preview image is " + position.getMaxImages()));
            for ( int i = 0; i < files.length; i++){
                files[i] = null;
            }
        } else
            result.add(new UploadFileResponse(position.getDescription(), null, "Check max file number: ok", null));

        return result;
    }

    private List<UploadFileResponse> checkSize(MultipartFile[] files, ImagePosition imagePosition){
        log.info("----- size --- "+files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        for(int i=0; i < files.length; i++) {
            if (files[i] != null)
                if(files[i].getSize() > imagePosition.getSize() || files[i].isEmpty()) {
                    result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), null, "File too large or empty"));
                    files[i] = null;
                } else
                    result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), "File size ok", null));
        }
        return result;
    }

    private List<UploadFileResponse> checkDimension(MultipartFile[] files, ImagePosition imagePosition) {
        log.info("----- dimension --- "+files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        for(int i=0; i < files.length; i++) {
            BufferedImage bf = null;
            if(files[i] != null)
                bf = avatarService.fromMultipartFileToBufferedImage(files[i]);
                if (bf != null) {
                    if (bf.getHeight() > imagePosition.getHeight() || bf.getWidth() > imagePosition.getWidth()) {
                        result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), null, "Wrong width or height"));
                        files[i] = null;
                    } else
                        result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), "Correct width and height", null));
                }
        }
        return result;
    }

    private List<UploadFileResponse> checkExtension(MultipartFile[] files, ImagePosition imagePosition) throws IOException {
        log.info("----- extensions --- "+files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        for(int i=0; i < files.length; i++) {
            if(files[i] != null) {
                String filename = files[i].getOriginalFilename();
                String ext = null;
                try {
                    ext = filename.substring(filename.lastIndexOf(".") + 1);
                    if (!Arrays.stream(extensions).anyMatch(ext::equalsIgnoreCase))
                        result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), "Allowed extension", null));
                } catch (NullPointerException e) {
                    result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), null, "Something went wrong during extension check"));
                }
            }
        }
        return result;
    }

        @Transactional
        public List<UploadFileResponse> uploadImages(MultipartFile[] files, long postId, ImagePosition imagePosition){
            log.info("----- upload files --- "+files.toString());
            List<UploadFileResponse> result = new ArrayList<>();
            for(int i=0; i < files.length; i++) {
                if(files[i] != null) {
                    try {
                        String newFilename = postId + "_" + files[i].getOriginalFilename();
                        Path path = Paths.get(imagePath + newFilename);
                        Files.write(path, files[i].getBytes());
                        result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), "File uploaded", null));
                        postImageRepository.save(new PostImage(new PostImageId(new Post(postId), newFilename), imagePosition ));
                    } catch (IOException e) {
                        result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(), null, "Error uploading file: " + e.getMessage()));
                    }
                }
            }
            return result;
        }

    public List<UploadFileResponse> callGlobalCheckImages(long postId, MultipartFile[] filesP, MultipartFile[] filesH, MultipartFile[] filesC) throws IOException {

        List<UploadFileResponse> result = new ArrayList<>();
        result.addAll(globalCheckImages(postId, filesP, ImagePosition.PRE));
        result.addAll(globalCheckImages(postId, filesH, ImagePosition.HDR));
        result.addAll(globalCheckImages(postId, filesC, ImagePosition.CON));

        return result;
    }

    public List<UploadFileResponse> globalCheckImages(long postId, MultipartFile[] files, ImagePosition position) throws IOException {

        List<UploadFileResponse> finalResult = new ArrayList<>();

        if(files != null){
            finalResult.addAll(checkMaxNumberImages(postId, files, position));
            finalResult.addAll(checkSize(files, position));
            finalResult.addAll(checkDimension(files, position));
            finalResult.addAll(checkExtension(files, position));
            finalResult.addAll(uploadImages(files, postId, position));
        }
        return finalResult;
    }


    @Transactional
    public ResponseEntity<?> delete(long postId, Set<String> filesToDelete){

        postImageRepository.deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(postId, filesToDelete); // postId_nomefile.estensione
        Map<String, List<String>> map = deleteImages(filesToDelete);
        String error = map.get("KO").toString();
        return new ResponseEntity<>("Deleted images: "+map.get("OK").size()+"\nDelete failed: "+map.get("KO").size()+" -> "+error, HttpStatus.OK);
    }


   public Map<String, List<String>> deleteImages(Set<String> filesToDelete){
       Map<String, List<String>> map = new HashMap<>();
       map.put("OK", new ArrayList<>());
       map.put("KO", new ArrayList<>());
       for (String file : filesToDelete) {
           try {
               Path path = Paths.get(imagePath+file);
               Files.delete(path);
               map.get("OK").add(file);
           } catch (IOException e) {
               map.get("KO").add(file+": "+e.getMessage());
           }
       }
       return map;
   }


}



