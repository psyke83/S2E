package ru.krikun.s2e;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
import ru.krikun.s2e.Helper;

public class Settings extends PreferenceActivity {

	SharedPreferences prefs;
	Resources res;

	private void setMountState(boolean mounts_mode) {
		if (Helper.checkConfigDir()) {
			if (mounts_mode) Helper.createMountFile("mounts_ext4");
			else Helper.deleteMountFile("mounts_ext4");
		}
	}

	private void setReadAhead(boolean ReadAhead) {
		if (Helper.checkConfigDir()) {
			if (ReadAhead) {
				Helper.createReadAheadFile();
				String value = prefs.getString("read_ahead_values", null);
				if (value != null) Helper.writeReadAheadValue(value);
			} else Helper.deleteReadAheadFile();
		}
	}

	private void setReadAheadValue(String read_ahead_value) {
		if (Helper.checkConfigDir()) Helper.writeReadAheadValue(read_ahead_value);
	}

	private void setTargetPref(String target, boolean result) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(target, result);
		editor.commit();
	}

	private void doImport() {

		for (String target : res.getStringArray(R.array.targets)) {

			boolean result = Helper.checkFileExists("/sd-ext/" + target);
			if (result) {
				Helper.createStatusFile(target);
				setTargetPref(target, result);
			}
		}
	}

	private void setOnPreferenceChange() {

		Preference importPref = findPreference("import");
		importPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				doImport();
				Toast.makeText(Settings.this, res.getString(R.string.import_done), Toast.LENGTH_LONG).show();
				return true;
			}
		}

		);

		Preference mounts = findPreference("mounts_ext4");
		mounts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object object) {
				boolean value = object.equals(true);
				setMountState(value);
				return true;
			}
		});

		Preference ReadAhead = findPreference("set_read_ahead");
		ReadAhead.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object object) {
				boolean value = object.equals(true);
				setReadAhead(value);
				return true;
			}
		});

		Preference ReadAheadValue = findPreference("read_ahead_values");
		ReadAheadValue.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object object) {
				String value = object.toString();
				setReadAheadValue(value);
				return true;
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);


		res = getResources();
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		setOnPreferenceChange();
	}

}