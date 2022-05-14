package ch.augeil.admin.album.downloadcodes;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "`downloadcodes`")
public class DownloadCode {

    public static final int DEFAULT_AVAILABLE_DOWNLOADS = 3;
    private int availableDownloads = DEFAULT_AVAILABLE_DOWNLOADS;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String albumId;

    public void decrementAvailableDownloads() {
        if (availableDownloads > 0) {
            availableDownloads--;
            return;
        }
        throw new RuntimeException("There are no more downloads available for this download code");
    }
}
