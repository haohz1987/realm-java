/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.util.Date;

import io.realm.entities.AllTypes;
import io.realm.entities.Dog;
import io.realm.entities.NonLatinFieldNames;

public class TestHelper {

    public static void populateTestRealm(Realm realm, int numberOfObjects) {
        realm.beginTransaction();
        for (int i = 0; i < numberOfObjects; i++) {
            AllTypes allTypes = realm.createObject(AllTypes.class);
            allTypes.setColumnBoolean((i % 3) == 0);
            allTypes.setColumnBinary(new byte[]{1, 2, 3});
            allTypes.setColumnDate(new Date());
            allTypes.setColumnDouble(3.1415);
            allTypes.setColumnFloat(1.234567f + i);
            allTypes.setColumnString("test data " + i);
            allTypes.setColumnLong(i);
            Dog dog = realm.createObject(Dog.class);
            dog.setName("Foo " + i);
            allTypes.setColumnRealmObject(dog);
        }
        realm.commitTransaction();
    }

    public static void populateTestRealmWithNonLatinData(Realm realm, int objects) {
        realm.beginTransaction();
        realm.allObjects(NonLatinFieldNames.class).clear();
        for (int i = 0; i < objects; ++i) {
            NonLatinFieldNames nonLatinFieldNames = realm.createObject(NonLatinFieldNames.class);
            nonLatinFieldNames.set델타(i);
            nonLatinFieldNames.setΔέλτα(i);
            nonLatinFieldNames.set베타(1.234567f + i);
            nonLatinFieldNames.setΒήτα(1.234567f + i);
        }
        realm.commitTransaction();
    }

    public static String streamToString(InputStream in) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }

        return sb.toString();
    }

    // Copies a Realm file from assets to app files dir
    public static void copyRealmFromAssets(Context context, String realmPath, String newName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream is = assetManager.open(realmPath);
        File file = new File(context.getFilesDir(), newName);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buf)) > -1) {
            outputStream.write(buf, 0, bytesRead);
        }
        outputStream.close();
        is.close();
    }

    // Deletes the old database and copies a new one into its place
    public static void prepareDatabaseFromAssets(Context context, String realmPath, String newName) throws IOException {
        Realm.deleteRealmFile(context, newName);
        TestHelper.copyRealmFromAssets(context, realmPath, newName);
    }

    public static class StubInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return 0; // Stub implementation
        }
    }
}
