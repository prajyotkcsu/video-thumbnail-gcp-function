package gcfv2;

import java.io.BufferedWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import gcfv2.model.AssetDetails;
import gcfv2.model.PlaybackDetails;
import gcfv2.model.UploadDetails;
import gcfv2.video.VideoMetadata;
import org.bson.Document;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class HelloHttpFunction implements HttpFunction {
  private static final Logger log = Logger.getLogger(HelloHttpFunction.class.getName());

  public void service(final HttpRequest request, final HttpResponse response) throws Exception {
    final BufferedWriter writer = response.getWriter();

    System.out.println("******Webhook triggered*****");
    System.out.println("******Webhook triggered-v2*****");
    String connectionString = "mongodb+srv://livepeer-webhook:yYk4b7EobdvMD3ikLQKFKbAm5XXMfdYmNj2VYMDF2RzCDifsJJEPFFFuJTEQjcZ4TPkotj9wEGjuG8jPpPYLhoGrNDrGfDL6XX@orb-user-db.bxckw.mongodb.net/?retryWrites=true&w=majority";
    MongoClient mongoClient = MongoClients.create(connectionString);
    MongoDatabase database = mongoClient.getDatabase("orbdb");
    MongoCollection<Document> collection = database.getCollection("upload-video");
    writer.write("Hello world!");

    ObjectMapper objectMapper = new ObjectMapper();
    AssetDetails assetDetails = null;
    PlaybackDetails playbackDetails = null;
    UploadDetails asset=null;
    String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
    log.info("requestBody: "+requestBody);
    String eventType = objectMapper.readTree(requestBody).get("event").asText();
    if (eventType.equals("asset.ready")) {
    String assetId = objectMapper.readTree(requestBody).get("payload").get("id").asText();
    log.info("assetId: "+assetId);
    Document query = new Document("assetId","a1188fc2-4bbf-45de-8050-0e5ef405c7e1");
    FindIterable<Document> docs = collection.find(query);
      log.info("Doc"+docs);
      String playbackId = null;
      for(Document doc:docs){
        log.info("infom"+doc.toJson());
        log.info("+"+doc.get("playbackId"));
         playbackId = doc.getString("playbackId");
         log.info("pay: "+playbackId);
        log.info("******playbackId******" + playbackId);
        assert asset != null;
        String playbackUrl = "https://livepeer.studio/api/playback/"+playbackId;
        AssetUploadController assetUploadController=new AssetUploadController();
        String output=assetUploadController.fetchDataFromUrl(playbackUrl);
        VideoMetadata videoMetadata = objectMapper.readValue(output, VideoMetadata.class);
        playbackDetails = videoMetadata.extractVideoList(videoMetadata);
        doc.put("source",playbackDetails.getSource());
        doc.put("playbackURL",playbackDetails.getPlaybackURL());
        doc.put("thumbnails",playbackDetails.getThumbnails());
        doc.put("livepeer:",false);
        doc.put("timeModified:",System.currentTimeMillis());
      /*Document updatedDocument = Document.parse(objectMapper.writeValueAsString(asset));
      collection.replaceOne(query, updatedDocument);*/
        log.info("Asset ready: "+ asset);
        log.info("Asset uploaded!!!");
        collection.replaceOne(query, doc);
        log.info("DOc"+doc);
      }
      /*for (Document doc : result) {

      String record = doc.toJson();
      try {
        log.info("record: "+record);
        asset =objectMapper.readValue(record, UploadDetails.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }*/




      mongoClient.close();
    /*asset.setSource(playbackDetails.getSource());
    asset.setPlaybackURL(playbackDetails.getPlaybackURL());
    asset.setThumbnails(playbackDetails.getThumbnails());
    asset.setTranscodingCompleted(true);
    asset.setTimeModified(System.currentTimeMillis());
    Document updatedDocument = Document.parse(objectMapper.writeValueAsString(asset));
    collection.replaceOne(query, updatedDocument);
    mongoClient.close();
    log.info("Asset ready: "+ asset);
    log.info("Asset uploaded!!!");*/
  }
}
}