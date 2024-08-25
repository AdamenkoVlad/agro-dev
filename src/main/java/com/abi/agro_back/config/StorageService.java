package com.abi.agro_back.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    @Value("${bucket}")
    String bucket;

    private final AmazonS3 space;
    public StorageService(@Value("${DOAccessKey}") String DOAccessKey,
                          @Value("${DOSecretKey}") String DOSecretKey,
                          @Value("${signingRegion}") String signingRegion,
                          @Value("${serviceEndpoint}") String serviceEndpoint
    ){
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(DOAccessKey,
                        DOSecretKey)
        );

        space = AmazonS3ClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion)
                )
                .build();
//        space.createBucket("testagro");
    }

    public List<String> getImageNames() {
        ListObjectsV2Result result = space.listObjectsV2(bucket);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return objects.stream()
                .map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }
    public void deleteBucket() {
        ObjectListing result = space.listObjects(bucket);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        while (true){
            objects.stream()
                        .forEach(o -> space.deleteObject(bucket, o.getKey()));
            if (result.isTruncated()) {
                result = space.listNextBatchOfObjects(result);
            } else {
                break;
            }
        }

    }

    public URL uploadPhoto(MultipartFile file, String key) throws IOException {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        space.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        return space.getUrl(bucket, key);
    }
    public URL uploadPhoto1(String key, String filePath) throws IOException {

        space.putObject(new PutObjectRequest(bucket, key, new File(filePath)).withCannedAcl(CannedAccessControlList.PublicRead));
        return space.getUrl(bucket, key);
    }

    public void deletePhoto(String key) {

        space.deleteObject(new DeleteObjectRequest(bucket, key));
    }

}














