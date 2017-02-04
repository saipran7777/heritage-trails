package in.ac.iitm.students.heritagetrails;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sai_praneeth7777 on 17-Dec-16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    Boolean _isFirstTime=true;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "heritage_trails_welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstLaunchCompleted() {
        _isFirstTime=false;
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, _isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}