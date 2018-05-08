package ashu.porter.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by apple on 08/05/18.
 */

public class Recent extends RealmObject {

    @PrimaryKey
    private String title;

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
