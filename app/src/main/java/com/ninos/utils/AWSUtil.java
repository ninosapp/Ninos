//package com.ninos.utils;
//
//import android.content.Context;
//
//import com.amazonaws.auth.CognitoCachingCredentialsProvider;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
//import com.amazonaws.regions.Region;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.ninos.BuildConfig;
//
///**
// * Created by FAMILY on 28-12-2017.
// */
//
//public class AWSUtil {
//
//    public void credentialsProvider(Context context) {
//
//        // Initialize the Amazon Cognito credentials provider
//        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                context,
//                BuildConfig.AWS_IDENTITY_POOL, // Identity Pool ID
//                Regions.US_EAST_1 // Region
//        );
//
//        setAmazonS3Client(credentialsProvider);
//    }
//
//    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){
//
//        // Create an S3 client
//        s3 = new AmazonS3Client(credentialsProvider);
//
//        // Set the region of your S3 bucket
//        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
//
//    }
//
//    public void setTransferUtility(){
//
//        transferUtility = new TransferUtility(s3, getApplicationContext());
//    }
//
//}
