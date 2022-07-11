package ch.augeil.admin.album.downloadcodes;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DownloadCodeGeneratorTest {

    private final DownloadCodeGenerator downloadCodeGenerator = new DownloadCodeGenerator();

    @Test
    void generateUniqueRandomDownloadCodes() {
        int numberOfCodes = 1_000_000;
        List<DownloadCode> downloadCodes = downloadCodeGenerator.generate(numberOfCodes, "TestAlbum");
        Set<String> uniqueCodes = downloadCodes.stream()
                .map(DownloadCode::getCode)
                .collect(Collectors.toSet());
        assertEquals(numberOfCodes, uniqueCodes.size());
    }
}