package gcfv2;

import com.mongodb.client.*;
import org.bson.Document;

public class MongoDBExample {
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("yourDatabaseName");
            MongoCollection<Document> collection = database.getCollection("upload-video");

            String assetIdToQuery = "124";

            // Create a query to find the document with the specified assetId
            Document query = new Document("assetId", assetIdToQuery);

            // Perform the find operation
            FindIterable<Document> result = collection.find(query);

            // Iterate over the result
            try (MongoCursor<Document> cursor = result.iterator()) {
                while (cursor.hasNext()) {
                    Document document = cursor.next();
                    System.out.println("Found Document: " + document.toJson());
                }
            }
        }
    }
}
