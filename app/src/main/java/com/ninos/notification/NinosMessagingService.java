package com.ninos.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ninos.R;
import com.ninos.activities.ChallengeActivity;
import com.ninos.activities.LoginActivity;
import com.ninos.activities.QuizActivity;
import com.ninos.utils.CrashUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by smeesala on 6/26/2017.
 */

public class NinosMessagingService extends FirebaseMessagingService {

    private static final String TAG = NinosMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String imageUri = remoteMessage.getData().get("image");
        String challengeId = remoteMessage.getData().get("challengeId");
        String challengeTitle = remoteMessage.getData().get("challengeTitle");
        String quizId = remoteMessage.getData().get("quizId");
        String quizDuration = remoteMessage.getData().get("quizDuration");
        String quizTitle = remoteMessage.getData().get("quizTitle");
        Bitmap bitmap = getBitmapfromUrl(imageUri);

        Intent intent;
        if (challengeId == null && quizId == null) {
            intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (challengeId != null) {
            intent = new Intent(this, ChallengeActivity.class);
            intent.putExtra(ChallengeActivity.CHALLENGE_ID, challengeId);
            intent.putExtra(ChallengeActivity.CHALLENGE_TITLE, challengeTitle);

        } else {
            intent = new Intent(this, QuizActivity.class);
            intent.putExtra(QuizActivity.QUIZ_ID, quizId);
            intent.putExtra(QuizActivity.QUIZ_DURATION, quizDuration);
            intent.putExtra(QuizActivity.QUIZ_TITLE, quizTitle);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setBigContentTitle(title).setSummaryText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            CrashUtil.report(e);
            return null;
        }
    }
}
