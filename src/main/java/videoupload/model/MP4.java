package videoupload.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MP4 {
    private String hrn;
    private String type;
    private String url;
    private long size;
    private long width;
    private long height;
    private long bitrate;
}
