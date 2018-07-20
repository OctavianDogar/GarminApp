package garmin.com.academyshop;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

/**
 * Created by Octavian on 4/24/2017.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        setupPreferences();
    }

    private void setupPreferences() {
        // Setup export preference
        ListPreference sortPreference = (ListPreference) findPreference(getString(R.string.key_storage_export));
        // Set summary to show currently selected entry
        sortPreference.setSummary(sortPreference.getEntry());
        // Add change listener to update summary when value is updated
        sortPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // We know this is a ListPreference so can cast the preference to it
                ListPreference listPreference = (ListPreference) preference;
                // Because ListPreference works with array of values and entries, get the currently selected value's index
                int valueIndex = listPreference.findIndexOfValue((String) newValue);
                // Set the entry at index of currently selected value
                preference.setSummary(listPreference.getEntries()[valueIndex]);
                return true;
            }
        });
    }

}
