package map.mapexample;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alpha on 9/19/2017.
 */

public class Photo implements Parcelable {

    private String url;
    private String name;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;
    public Photo(String url, String title) {
       url= url;
       name = title;
    }
    protected Photo(Parcel in) {
        url = in.readString();
        name = in.readString();
    }
    public Photo() {

    }
    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static  Photo[] getPhotos() {

        return new Photo[]{
               // new Photo("C:\\Users\\Alpha\\Documents\\temp UPSuroy\\UPSuroy -11-20-16\\gallery\\Admin Building\\AdminBuilding.jpg", "Galaxy"),
               new Photo("http://i.imgur.com/ovr0NAF.jpg", "Space Shuttle"),
              new Photo("http://i.imgur.com/n6RfJX2.jpg", "Galaxy Orion"),
                new Photo("http://i.imgur.com/qpr5LR2.jpg", "Earth"),
                new Photo("http://i.imgur.com/pSHXfu5.jpg", "Astronaut"),
                new Photo("http://i.imgur.com/3wQcZeY.jpg", "Satellite"),
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(name);
    }
    @Override
    public String toString(){
        return "name="+name+",url=" + url + ",description=" + description;
    }
}
