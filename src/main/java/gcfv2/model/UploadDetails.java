package gcfv2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "upload-video")
public class UploadDetails {
    @Id
    private String id;
    private String assetId;
    private boolean livepeer;
    private String playbackId;
    private String playbackURL;
    private List<MP4> source;
    private List<Thumbnail> thumbnails;
    private boolean transcodingCompleted;
    private long timestamp;

    public UploadDetails(String id,String assetId, String playbackId) {
        this.id = id;
        this.assetId = assetId;
        this.playbackId = playbackId;
    }
}
