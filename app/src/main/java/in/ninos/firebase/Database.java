package in.ninos.firebase;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.ninos.BuildConfig;
import in.ninos.models.Profile;
import in.ninos.utils.FireBasePath;


/**
 * Created by sumanth on 14/12/2017.
 */
public class Database {


    private static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    private static FirebaseUser getFireBaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUserId() {

        FirebaseUser user = getFireBaseUser();
        String userId = null;

        if (user != null) {
            userId = user.getUid();
        }

        return userId;
    }

    public static boolean isValidUser() {
        return getFireBaseUser() != null;
    }

    public static String getEmail() {

        FirebaseUser user = getFireBaseUser();
        String email = null;

        if (user != null) {
            email = user.getEmail();
        }

        return email;
    }

    private static String getUserRootPath() {
        return String.format(BuildConfig.USER_PATH_FMT, getUserId());
    }

    private static DatabaseReference getUserRootPathRef() {
        return getDatabaseReference().child(getUserRootPath());
    }

    private static String getRootPath() {
        return BuildConfig.ROOT_PATH;
    }

    private static DatabaseReference getRootPathRef() {
        return getDatabaseReference().child(getRootPath());
    }

    public static synchronized void initUser(Context context, Profile profile) {

        if (context != null && profile != null) {
            addUpdateProfile(profile);
        }
    }

    private static void addUpdateProfile(Profile profile) {
        String root = getUserRootPath();

        String profilePath = root + "/" + FireBasePath.PROFILE;

        getDatabaseReference().child(profilePath).setValue(profile);
    }

}
