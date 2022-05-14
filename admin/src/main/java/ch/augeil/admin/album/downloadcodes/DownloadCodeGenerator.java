package ch.augeil.admin.album.downloadcodes;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DownloadCodeGenerator {

    public List<DownloadCode> generate(int numberOfDownloadCodesToGenerate, String albumId) {
        List<DownloadCode> downloadCodes = new ArrayList<>();
        for (int i = 0; i < numberOfDownloadCodesToGenerate; i++) {
            DownloadCode downloadCode = new DownloadCode();
            downloadCode.setAlbumId(albumId);
            downloadCodes.add(downloadCode);
        }
        return downloadCodes;
    }

}
