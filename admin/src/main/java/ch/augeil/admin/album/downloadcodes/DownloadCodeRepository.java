package ch.augeil.admin.album.downloadcodes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DownloadCodeRepository extends JpaRepository<DownloadCode, String> {

    Optional<DownloadCode> findByCode(String code);
    List<DownloadCode> findAllByAlbumId(String albumId);

}
