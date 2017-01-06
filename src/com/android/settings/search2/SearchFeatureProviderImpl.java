/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.settings.search2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.applications.PackageManagerWrapperImpl;
import com.android.settings.search.Index;

/**
 * FeatureProvider for the refactored search code.
 */
public class SearchFeatureProviderImpl implements SearchFeatureProvider {

    private static final String TAG = "SearchFeatureProvider";

    private DatabaseIndexingManager mDatabaseIndexingManager;

    @Override
    public boolean isEnabled(Context context) {
        return false;
    }

    @Override
    public void setUpSearchMenu(Menu menu, final Activity activity) {
        if (menu == null || activity == null) {
            return;
        }
        String menuTitle = activity.getString(R.string.search_menu);
        MenuItem menuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, menuTitle)
                .setIcon(R.drawable.ic_search_24dp)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(activity, SearchActivity.class);
                        activity.startActivity(intent);
                        return true;
                    }
                });

        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public DatabaseResultLoader getDatabaseSearchLoader(Context context, String query) {
        return new DatabaseResultLoader(context, query);
    }

    @Override
    public InstalledAppResultLoader getInstalledAppSearchLoader(Context context, String query) {
        return new InstalledAppResultLoader(
                context, new PackageManagerWrapperImpl(context.getPackageManager()), query);
    }

    @Override
    public DatabaseIndexingManager getIndexingManager(Context context) {
        if (mDatabaseIndexingManager == null) {
            mDatabaseIndexingManager = new DatabaseIndexingManager(context.getApplicationContext(),
                    context.getPackageName());
        }
        return mDatabaseIndexingManager;
    }

    @Override
    public void updateIndex(Context context) {
        long indexStartTime = System.currentTimeMillis();
        if (isEnabled(context)) {
            getIndexingManager(context).update();
        } else {
            Index.getInstance(context).update();
        }
        Log.d(TAG, "Index.update() took " + (System.currentTimeMillis() - indexStartTime) + " ms");
    }
}
