import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Helper class for accessing the Google Cloud Vision detect labels API.
 * Requires the credentials .json file to be in the src folder with this .java file.
 * <p>
 */
public class GoogleCloudVision {

    /**
     * Helper method that searches the src folder for a .json file containing the Google Cloud Platform credentials.
     * This method is private since you do NOT need to call this method.
     * <p>
     * @return the full path to the (first) .json file.
     * @throws IOException           if an I/O error occurs.
     * @throws FileNotFoundException if no .json file is found.
     */
    private static String findJsonCredentialsFile() throws IOException {
        // construct a Path to the src folder, with platform specific path separators
        Path dir = Path.of(System.getProperty("user.dir"), "src");

        // get all the .json files in the src folder
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
            Iterator<Path> iter = stream.iterator();
            // is there at least 1 .json file?
            if (!iter.hasNext()) {
                // No?!?!? Then throw an exception.
                throw new FileNotFoundException("Could not find JSON credentials file!");
            } // end if

            // found a .json file, return the absolute path to it
            return iter.next().toString();
        } // end try-with-resources
    } // end findJsonCredentialsFile

    /**
     * Returns the objects detected in the image by Google Cloud Vision detect labels API.
     * <p>
     * DO NOT CHANGE ANY CODE IN THIS FILE!
     * <p>
     * This code is a lightly edited version of
     * https://cloud.google.com/vision/docs/libraries#client-libraries-usage-java,
     * last access 12/1/2019.
     *
     * @param filenameOrURL either a filename or a URL to an image
     * @return the list of ObjectLabels that are the objects detected in the image along with how confident GoogleCloudVision is of the its label.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if loading an image from a URL is interrupted.
     */
    public static ArrayList<ObjectLabel> detectImageLabels(String filenameOrURL) throws IOException, InterruptedException {
        // load the GCP credentials from the .json file
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(findJsonCredentialsFile()))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        // attach the credentials to the settings for the ImageAnnotator
        ImageAnnotatorSettings imageAnnotatorSettings =
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build();

        // the InputStream that will be used to read the image either
        // from the file or the URL
        InputStream imageStream;

        // are we dealing with a URL or a filename?
        if (filenameOrURL.startsWith("http")) {
            // using the new HttpClient
            HttpClient client = HttpClient.newHttpClient();
            // create a GET request for the specified image URL
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(filenameOrURL))
                    .build();

            // make the request
            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            imageStream = response.body();
        } // end if URL
        else { // we have a filename, not a URL
            imageStream = new FileInputStream(filenameOrURL);
        } // end else

        // load the Image
        // Neither of these work -- they produce an error about wine type? GRRRR!
//        Image img = Image.parseFrom(imageStream);
//        Image img = Image.parseFrom(imageStream.readAllBytes());
        Image img = Image.newBuilder()
                .setContent(ByteString.copyFrom(imageStream.readAllBytes()))
                .build();

        // specify that we want to detect labels and create the annotate request
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();

        // add our image to the list of requests
        // the API allows multiple images to be batch processed
        ArrayList<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        // we will fill this with the object labels and return it to the caller
        ArrayList<ObjectLabel> results = new ArrayList<>();

        // create the ImageAnnotatorClient to make the call to Google Cloud Vision
        try (ImageAnnotatorClient imageAnnotatorClient = ImageAnnotatorClient.create(imageAnnotatorSettings)) {
            // call the Google Cloud Vision service to detect the labels
            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(requests);

            // process the results
            for (AnnotateImageResponse res : response.getResponsesList()) {
                // if an error occurred, print it and return an empty list
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return results;
                } // end if error

                // put the labels into our results ArrayList
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    results.add(new ObjectLabel(annotation.getDescription(),
                            annotation.getScore()));
                } // end for each annotation
            } // end for each response
        } // end try with resources
        return results;
    } // end labelImage

} // end class GoogleCloudVision
