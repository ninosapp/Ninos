package in.ninos.models;

import android.net.Uri;

/**
 * Created by FAMILY on 30-12-2017.
 */

public class MediaObject {
    private int id;
    private String path;
    private String duration;

    public MediaObject(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getMediaUri() {
        return Uri.parse(path);
    }
}
