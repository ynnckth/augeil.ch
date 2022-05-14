package ch.augeil.admin.album.downloadcodes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadCodeRepository extends JpaRepository<DownloadCode, String> {

    List<DownloadCode> findAllByAlbumId(String albumId);

}
