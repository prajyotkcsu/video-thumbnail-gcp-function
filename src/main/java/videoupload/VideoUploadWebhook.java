package videoupload;

import java.io.BufferedWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import videoupload.model.AssetDetails;
import videoupload.model.PlaybackDetails;
import videoupload.model.UploadDetails;
import videoupload.service.AssetUploadUtility;
import videoupload.model.VideoMetadata;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Optional;
import java.util.logging.Logger;

public class VideoUploadWebhook implements HttpFunction {
  private static final Logger log = Logger.getLogger(VideoUploadWebhook.class.getName());

  public void service(final HttpRequest request, final HttpResponse response) throws Exception {
    final BufferedWriter writer = response.getWriter();

    System.out.println("******Webhook triggered*****");
    System.out.println("******Webhook triggered-v2*****");
    String connectionStrings = Optional.ofNullable(System.getenv("CONNECTION_STRING"))
            .orElseThrow(() -> new IllegalStateException("CONNECTION_STRING environment variable not set."));

    log.info("Connection s" + connectionStrings);
    CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );

    MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(codecRegistry)
            .applyConnectionString(new ConnectionString(connectionStrings))
            .build();

    MongoClient mongoClient = MongoClients.create(settings);

    String database_name = Optional.ofNullable(System.getenv("DATABASE"))
            .orElseThrow(() -> new IllegalStateException("DATABASE environment variable not set."));

    String collection_name = Optional.ofNullable(System.getenv("COLLECTION"))
            .orElseThrow(() -> new IllegalStateException("COLLECTION environment variable not set."));
    MongoDatabase database = mongoClient.getDatabase(database_name);
    MongoCollection<Document> collection = database.getCollection(collection_name);

    ObjectMapper objectMapper = new ObjectMapper();
    AssetDetails assetDetails = null;
    PlaybackDetails playbackDetails = null;
    UploadDetails asset = null;
    String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
    log.info("New message from Livepeer " + requestBody);
    String eventType = objectMapper.readTree(requestBody).get("event").asText();
    if (eventType.equals("asset.ready")) {
      String assetId = objectMapper.readTree(requestBody).get("payload").get("id").asText();
      log.info("assetId: " + assetId);
      Document query = new Document("assetId", "a1188fc2-4bbf-45de-8050-0e5ef405c7e1"); //TODO: remove this
      FindIterable<Document> docs = collection.find(query);
      log.info("Doc" + docs);
      String playbackId = null;
      for (Document doc : docs) {
        log.info("Doc representation" + doc.toJson());
        playbackId = doc.getString("playbackId");
        log.info("******playbackId******" + playbackId);
        assert asset != null;
        String playbackUrl = "https://livepeer.studio/api/playback/" + playbackId;
        AssetUploadUtility assetUploadController = new AssetUploadUtility();
        String output = assetUploadController.fetchDataFromUrl(playbackUrl);
        log.info("output: " + output);
        VideoMetadata videoMetadata = objectMapper.readValue(output, VideoMetadata.class);
        playbackDetails = videoMetadata.extractVideoList(videoMetadata);
        doc.put("source", playbackDetails.getSource());
        doc.put("playbackURL", playbackDetails.getPlaybackURL());
        doc.put("thumbnails", playbackDetails.getThumbnails());
        doc.put("livepeer:", true);
        doc.put("timeModified:", System.currentTimeMillis());

        collection.replaceOne(query, doc);
        log.info("Asset uploaded and thumbnails are ready!!!");
        mongoClient.close();

      }
    }
  }
}