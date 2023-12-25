package gcfv2;

import java.io.BufferedWriter;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

public class HelloHttpFunction implements HttpFunction {
  public void service(final HttpRequest request, final HttpResponse response) throws Exception {
    final BufferedWriter writer = response.getWriter();
    System.out.println("******Webhook triggered*****");
    writer.write("Hello world!");
    String playbackUrl = "https://livepeer.studio/api/playback/09ee1iihioqf4zon";
    AssetUploadController assetUploadController=new AssetUploadController();
    String output=assetUploadController.fetchDataFromUrl(playbackUrl);
    System.out.println("******Webhook triggered***** "+output);
  }
}
