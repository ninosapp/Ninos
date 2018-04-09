package in.ninos.notification;

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
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import in.ninos.R;
import in.ninos.activities.ChallengeActivity;
import in.ninos.activities.LoginActivity;
import in.ninos.activities.QuizActivity;
import in.ninos.utils.CrashUtil;

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

        NotificationCompat.BigPictureStyle notificationCompat = new NotificationCompat.BigPictureStyle().setBigContentTitle(title).setSummaryText(message);

        if (bitmap != null) {
            notificationCompat.bigPicture(bitmap);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(notificationCompat)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        } else {
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
}
