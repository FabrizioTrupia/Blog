package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Avatar;
import it.cgmconsulting.myblog.entity.AvatarId;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.repository.AvatarRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {

    @Value("${app.avatar.size}")
    private long avatarSize;
    @Value("${app.avatar.width}")
    private int avatarWidth;
    @Value("${app.avatar.height}")
    private int avatarHeight;
    @Value("${app.avatar.extensions}")
    private String[] extensions;

    private final AvatarRepository avatarRepoitory;


    private boolean checkSize(MultipartFile file){
        if(file.getSize() > avatarSize || file.isEmpty())
            return false;
        return true;
    }

    protected BufferedImage fromMultipartFileToBufferedImage(MultipartFile file){
        BufferedImage bf = null;
        try {
            bf = ImageIO.read(file.getInputStream());
            return bf;
        } catch (IOException e) {
            return null;
        }
    }

    private boolean checkDimension(MultipartFile file) {
        BufferedImage bf = fromMultipartFileToBufferedImage(file);
        if(bf !=  null) {
            if (bf.getHeight() > avatarHeight || bf.getWidth() > avatarWidth)
                return false;
            return true;
        } else
            return false;
    }

    private boolean checkExtension(MultipartFile file) throws IOException {

        String filename = file.getOriginalFilename();
        String ext = null;
        try {
            ext = filename.substring(filename.lastIndexOf(".") + 1);
            if (Arrays.stream(extensions).anyMatch(ext::equalsIgnoreCase))
                return true;
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    public ResponseEntity<?> avatar(MultipartFile file, UserPrincipal principal) throws IOException {
        if(!checkSize(file))
            return new ResponseEntity("File too large", HttpStatus.BAD_REQUEST);
        if(!checkDimension(file))
            return new ResponseEntity("Wrong file height or width", HttpStatus.BAD_REQUEST);
        if(!checkExtension(file))
            return new ResponseEntity("File type not allowed", HttpStatus.BAD_REQUEST);

        Avatar avatar = new Avatar(new AvatarId(new User(principal.getId())), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        avatarRepoitory.save(avatar);
        return new ResponseEntity("Avatar successfully uploaded", HttpStatus.OK);

    }
}
