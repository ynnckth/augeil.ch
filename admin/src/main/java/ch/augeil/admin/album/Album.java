package ch.augeil.admin.album;

import ch.augeil.admin.album.downloadcodes.DownloadCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "`albums`")
public class Album {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String artist;
    private String albumName;
    private String filePath;
    @OneToMany
    private List<DownloadCode> downloadCodes;
}
