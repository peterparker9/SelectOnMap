package ashu.porter.db;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import ashu.porter.model.Recent;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by apple on 08/05/18.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }


    public static RealmController with(AppCompatActivity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    public void clearAll() {
        realm.beginTransaction();
        realm.clear(Recent.class);
        realm.commitTransaction();
    }

    public RealmResults<Recent> getPlaces() {

        return realm.where(Recent.class).findAll();
    }


    //check if Book.class is empty
    public boolean hasBooks() {
        return !realm.allObjects(Recent.class).isEmpty();
    }

}
