package gcfv2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadDetails {
    private ObjectId _id;
    private String cid;
    private String assetId;
    private boolean livepeer;
    private String playbackId;
    private long timeModified;

    private String playbackURL;
    private List<MP4> source;
    private List<Thumbnail> thumbnails;
    private boolean transcodingCompleted;
}