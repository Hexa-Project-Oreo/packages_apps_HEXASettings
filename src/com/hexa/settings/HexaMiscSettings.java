/*
 * Copyright (C) 2017 The KangDroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hexa.settings;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.Utils;

import com.android.internal.logging.nano.MetricsProto;

import java.util.ArrayList;
import java.util.List;

public class HexaMiscSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
	
    private static final String SMS_OUTGOING_CHECK_MAX_COUNT = "sms_outgoing_check_max_count";
	
    private PreferenceCategory mLedsCategory;
    private PreferenceScreen mChargingLeds;

    private ListPreference mSmsCount;
    private int mSmsCountValue;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.hexa_misc_settings);
		
        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

		// SMS Count options
        mSmsCount = (ListPreference) findPreference(SMS_OUTGOING_CHECK_MAX_COUNT);
        mSmsCountValue = Settings.Global.getInt(resolver,
                Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, 30);
        mSmsCount.setValue(Integer.toString(mSmsCountValue));
        mSmsCount.setSummary(mSmsCount.getEntry());
        mSmsCount.setOnPreferenceChangeListener(this);
        if (!Utils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(mSmsCount);
        }
		
		// Battery LED Settings
        mLedsCategory = (PreferenceCategory) findPreference("hexa_bat_cat");
        mChargingLeds = (PreferenceScreen) findPreference("hexa_bat_settings_cat");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            mLedsCategory.removePreference(mChargingLeds);
        }
        if (mChargingLeds == null) {
            prefScreen.removePreference(mLedsCategory);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preference);
    }
	
    @Override
    public void onResume() {
        super.onResume();
    }
	
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mSmsCount) {
            mSmsCountValue = Integer.valueOf((String) newValue);
            int index = mSmsCount.findIndexOfValue((String) newValue);
            mSmsCount.setSummary(
                    mSmsCount.getEntries()[index]);
            Settings.Global.putInt(resolver,
                    Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, mSmsCountValue);
            return true;
        }
        return false;
    }
	
    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.KANGDROID;
    }
}