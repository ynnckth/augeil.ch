package ch.augeil.admin.album.downloadcodes;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class DownloadCodeGenerator {

    private static final String ALL_AVAILABLE_CODE_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int DOWNLOAD_CODE_LENGTH = 10;

    private static final SecureRandom random = new SecureRandom();

    public List<DownloadCode> generate(int numberOfDownloadCodesToGenerate, String albumId) {
        List<DownloadCode> downloadCodes = new ArrayList<>();
        for (int i = 0; i < numberOfDownloadCodesToGenerate; i++) {
            DownloadCode downloadCode = new DownloadCode();
            downloadCode.setCode(generateRandomCode());
            downloadCode.setAlbumId(albumId);
            downloadCodes.add(downloadCode);
        }
        return downloadCodes;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(DownloadCodeGenerator.DOWNLOAD_CODE_LENGTH);
        for (int i = 0; i < DownloadCodeGenerator.DOWNLOAD_CODE_LENGTH; i++) {
            sb.append(ALL_AVAILABLE_CODE_CHARACTERS.charAt(random.nextInt(ALL_AVAILABLE_CODE_CHARACTERS.length())));
        }
        return sb.toString();
    }

}
