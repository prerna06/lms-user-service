package com.tekcapzule.lms.user.domain.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import com.tekcapzule.lms.user.domain.command.GetCertificateCommand;
import com.tekcapzule.lms.user.domain.util.StringUtility;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PdfServiceImpl implements PdfService{
    private static final String NAME = "%s, %s";

    @Value("#{environment.CLOUD_REGION}")
    private String region;
    @Value("#{environment.CERTIFICATE_BUCKET}")
    private String extCertificateS3Bucket;

    public byte[] generateCertificate(GetCertificateCommand getCertificateCommand) {
        log.info(String.format("Inside generating Certificate for user : %s", getCertificateCommand.getFirstName()));
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat. format(currentDate);
        Map<String, String> certificateData = new HashMap<>();
        certificateData.put("#NAME#", String.format(NAME, getCertificateCommand.getLastName(), getCertificateCommand.getFirstName()));
        certificateData.put("#COURSENAME#", getCertificateCommand.getCourseName());
        certificateData.put("#COURSEDURATION#", getCertificateCommand.getCourseDuration());
        certificateData.put("#COURSEINSTRUCTOR#", getCertificateCommand.getCourseInstructor());
        certificateData.put("#INSTRUCTORSIGN#", getCertificateCommand.getCourseInstructor());
        certificateData.put("#DATEANDTIME#", currentDateTime);
        try {
            return generateCertificate(certificateData, getCertificateCommand.getCertificateType());
        } catch (MalformedURLException e) {
            log.error("Error Generating log:: ", e);
            log.error("Error Generating Certificate {}, {}", getCertificateCommand.getUserId(), getCertificateCommand.getCourseId());
        }
        return null;
    }
    @SneakyThrows
    private byte[] generateCertificate(Map<String, String> dataMap, String certificateType) throws MalformedURLException {
        byte[] writtenData = null;
        Path resourceDirectory = Paths.get("src","main","resources");
        String baseUrl = resourceDirectory.toFile().getAbsolutePath();
        String fileAsString = readFileAsString(certificateType);
        fileAsString = StringUtility.replaceKeys(fileAsString, dataMap);
        log.info(String.format("baseUrl :: %s", baseUrl));
        log.info(String.format("fileAsString baseUrl :: %s", fileAsString));
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Document document = Jsoup.parse(fileAsString);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useDefaultPageSize(9.90f, 7.0800f, BaseRendererBuilder.PageSizeUnits.INCHES);
            builder.withW3cDocument(new W3CDom().fromJsoup(document), baseUrl);
            builder.useSVGDrawer(new BatikSVGDrawer());
            builder.toStream(os);
            builder.run();
            writtenData = os.toByteArray();
        } catch (FileNotFoundException e) {
            log.error("File not found :: ", e);
        } catch (IOException e) {
            log.error("IOException :: ", e);
        }
        //putS3InputStream(dataMap, writtenData);
        return writtenData;
    }

    public String readFileAsString(String certificateType) throws Exception {
        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(getCertificateTemplateFile(certificateType)));

            StringBuilder fileContentBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                if (fileContentBuilder.length() > 0) {
                    fileContentBuilder.append(System.getProperty("line.separator"));
                }
                fileContentBuilder.append(line);
            }

            return fileContentBuilder.toString();

        } catch (Exception e) {
            log.error("readFileAsString - the method got an error :: ", e);
            //new Exception("readFileAsString - the method got an error." + e.getMessage(), e);
            return null;
        } finally {
            safeCloseBufferedReader(br);
        }
    }
    public void safeCloseBufferedReader(BufferedReader bufferedReader) throws Exception {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            throw new Exception("safeCloseBufferedReader  - the method got an error. " + e.getMessage());
        }
    }

    private String putS3InputStream(Map<String, String> dataMap, byte[] fileData) {
        log.info(String.format("Entering puts3InputStream - Uploading object to bucket %s", dataMap.get("Bucket")));
        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.fromName(dataMap.get("Region")))
                .build();
        InputStream in = new ByteArrayInputStream(fileData);
        String imageName = "";
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileData.length);
            PutObjectRequest putOb = new PutObjectRequest(extCertificateS3Bucket, imageName, in, objectMetadata);
            amazonS3.putObject(putOb);
            log.info(String.format("Successfully placed %s into bucket %s", imageName, dataMap.get("Bucket")));
            AmazonS3Client amazonS3Client = (AmazonS3Client) amazonS3;
            return amazonS3Client.getResourceUrl(extCertificateS3Bucket, imageName);
        } catch (AmazonS3Exception e) {
            log.error("Error uploading image, error connecting S3", e);
        }
        return null;
    }

    private InputStream getCertificateTemplateFile(String certificateType){
        log.info(String.format("Entering getCertificateTemplateFile - Getting certificate template from bucket %s - certificate type : %s", extCertificateS3Bucket, certificateType));
        String s3bucketKey = String.format("template/%s.html", certificateType);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.fromName(region))
                .build();

        GetObjectRequest getObjectRequest = new GetObjectRequest(extCertificateS3Bucket, s3bucketKey);
        S3Object templateFile = amazonS3.getObject(getObjectRequest);
        return templateFile.getObjectContent();
    }
}
