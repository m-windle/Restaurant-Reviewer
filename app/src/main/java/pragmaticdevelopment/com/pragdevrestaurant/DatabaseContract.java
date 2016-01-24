package pragmaticdevelopment.com.pragdevrestaurant;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class DatabaseContract {
    public static final class RestaurantsTable implements BaseColumns{
        public static final String TABLE_NAME = "Restaurants_T";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ADDRESS = "Address";
        public static final String COLUMN_DESCRIPTION = "Description";
        public static final String[] ALL_COLUMNS = {_ID, COLUMN_NAME, COLUMN_ADDRESS, COLUMN_DESCRIPTION};
    }

    public static final class TagsTable implements BaseColumns{
        public static final String TABLE_NAME = "Tags_T";
        public static final String COLUMN_TAG = "Tag";
        public static final String[] ALL_COLUMNS = {_ID, COLUMN_TAG};
    }

    public static final class RestaurantTagsTable{
        public static final String TABLE_NAME = "RestaurantTags_T";
        public static final String COLUMN_RESTAURANTID = "RestaurantID";
        public static final String COLUMN_TAGID = "TagID";
        public static final String[] ALL_COLUMNS = {COLUMN_RESTAURANTID, COLUMN_TAGID};
    }

    public static final class DBHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "Restaurants.db";
        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_RESTURAUNTS = String.format(
                "CREATE TABLE %s (%s INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL)",
                RestaurantsTable.TABLE_NAME,
                RestaurantsTable._ID,
                RestaurantsTable.COLUMN_NAME,
                RestaurantsTable.COLUMN_ADDRESS,
                RestaurantsTable.COLUMN_DESCRIPTION);

        private static final String CREATE_TAGS = String.format(
                "CREATE TABLE %s (%s INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL UNIQUE)",
                TagsTable.TABLE_NAME,
                TagsTable._ID,
                TagsTable.COLUMN_TAG);

        private static final String CREATE_RESTURAUNTTAGS = String.format(
                "CREATE TABLE %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL, PRIMARY KEY (%s, %s), FOREIGN KEY (%s) REFERENCES %s (%s), FOREIGN KEY (%s) REFERENCES %s (%s))",
                RestaurantTagsTable.TABLE_NAME,
                RestaurantTagsTable.COLUMN_RESTAURANTID,
                RestaurantTagsTable.COLUMN_TAGID,
                RestaurantTagsTable.COLUMN_RESTAURANTID,
                RestaurantTagsTable.COLUMN_TAGID,
                RestaurantTagsTable.COLUMN_RESTAURANTID,
                RestaurantsTable.TABLE_NAME,
                RestaurantsTable._ID,
                RestaurantTagsTable.COLUMN_TAGID,
                TagsTable.TABLE_NAME,
                TagsTable._ID);

        private static String getDropTableString(String tableName){
            return "DROP TABLE IF EXISTS " + tableName;
        }

        public DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_RESTURAUNTS);
            db.execSQL(CREATE_TAGS);
            db.execSQL(CREATE_RESTURAUNTTAGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(getDropTableString(RestaurantTagsTable.TABLE_NAME));
            db.execSQL(getDropTableString(TagsTable.TABLE_NAME));
            db.execSQL(getDropTableString(RestaurantsTable.TABLE_NAME));

            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
            onUpgrade(db, oldVersion, newVersion);
        }

        public long addRestaurant(String name, String address, String description){
            SQLiteDatabase db = getWritableDatabase();
            return addRestaurant(db, name, address, description);
        }

        public long addRestaurant(SQLiteDatabase db, String name, String address, String description){
            ContentValues cv = new ContentValues();

            cv.put(RestaurantsTable.COLUMN_NAME, name);
            cv.put(RestaurantsTable.COLUMN_ADDRESS, address);
            cv.put(RestaurantsTable.COLUMN_DESCRIPTION, description);

            long id = db.insert(RestaurantsTable.TABLE_NAME, null, cv);

            return id;
        }

        public Cursor getRestaurantsBySearchQuery(String searchQuery){
            SQLiteDatabase db = getReadableDatabase();
            return getRestaurantsBySearchQuery(db, searchQuery);
        }

        public Cursor getRestaurantsBySearchQuery(SQLiteDatabase db, String searchQuery){
            if(searchQuery.equals(""))
                return getAllRestaurants(db);

            String query = String.format(
                    "SELECT DISTINCT * FROM (SELECT t1.* FROM %s t1 LEFT JOIN %s t2 on t1.%s = t2.%s LEFT JOIN %s t3 ON t2.%s = t3.%s"
                    + " WHERE t1.%s LIKE ? OR t3.%s LIKE ?)",
                    RestaurantsTable.TABLE_NAME,
                    RestaurantTagsTable.TABLE_NAME,
                    RestaurantsTable._ID,
                    RestaurantTagsTable.COLUMN_RESTAURANTID,
                    TagsTable.TABLE_NAME,
                    RestaurantTagsTable.COLUMN_TAGID,
                    TagsTable._ID,
                    RestaurantsTable.COLUMN_NAME,
                    TagsTable.COLUMN_TAG);

            Cursor c = db.rawQuery(query, new String[]{"%" + searchQuery + "%", "%" + searchQuery + "%"});
            return c;
        }

        public boolean removeRestaurant(long restaurantId){
            SQLiteDatabase db = getWritableDatabase();
            return removeRestaurant(db, restaurantId);
        }

        public boolean removeRestaurant(SQLiteDatabase db, long restaurantId){
            db.delete(RestaurantTagsTable.TABLE_NAME, RestaurantTagsTable.COLUMN_RESTAURANTID + " = ?", new String[]{""+restaurantId});
            long id = db.delete(RestaurantsTable.TABLE_NAME, RestaurantsTable._ID + " = ?", new String[]{"" + restaurantId});

            return id > 0;
        }

        public Cursor getRestaurantById(long restaurantId){
            SQLiteDatabase db = getReadableDatabase();
            return getRestaurantById(db, restaurantId);
        }

        public Cursor getRestaurantById(SQLiteDatabase db, long restaurantId){
            Cursor c = db.query(RestaurantsTable.TABLE_NAME,
                    RestaurantsTable.ALL_COLUMNS,
                    RestaurantsTable._ID + " = ?",
                    new String[]{"" + restaurantId},
                    null,
                    null,
                    null);

            return c;
        }

        public Cursor getAllRestaurants(){
            SQLiteDatabase db = getReadableDatabase();
            return getAllRestaurants(db);
        }

        public Cursor getAllRestaurants(SQLiteDatabase db){
            Cursor c = db.query(RestaurantsTable.TABLE_NAME,
                    RestaurantsTable.ALL_COLUMNS,
                    "",
                    new String[]{},
                    null,
                    null,
                    null);

            return c;
        }

        public boolean updateRestaurant(long id, String name, String address, String description){
            SQLiteDatabase db = getWritableDatabase();
            return updateRestaurant(db, id, name, address, description);
        }

        public boolean updateRestaurant(SQLiteDatabase db, long id, String name, String address, String description){
            ContentValues cv = new ContentValues();

            cv.put(RestaurantsTable.COLUMN_NAME, name);
            cv.put(RestaurantsTable.COLUMN_ADDRESS, address);
            cv.put(RestaurantsTable.COLUMN_DESCRIPTION, description);

            int rowsUpdated = db.update(RestaurantsTable.TABLE_NAME, cv, RestaurantsTable._ID + " = ?", new String[]{"" + id});
            return rowsUpdated > 0;
        }

        public Restaurant cursorToRestaurant(Cursor c){
            long id;
            String name, address, description;

            id = c.getLong(c.getColumnIndexOrThrow(RestaurantsTable._ID));
            name = c.getString(c.getColumnIndexOrThrow(RestaurantsTable.COLUMN_NAME));
            address = c.getString(c.getColumnIndexOrThrow(RestaurantsTable.COLUMN_ADDRESS));
            description = c.getString(c.getColumnIndexOrThrow(RestaurantsTable.COLUMN_DESCRIPTION));

            return new Restaurant(id, name, address, description);
        }

        public Restaurant[] cursorToRestaurantArray(Cursor c){
            Restaurant[] array = new Restaurant[c.getCount()];

            int i = 0;
            if(c.moveToFirst()){
                do{
                    array[i++] = cursorToRestaurant(c);
                    c.moveToNext();
                }while(!c.isAfterLast());
            }

            return array;
        }


        public boolean tagExists(String tagName){
            SQLiteDatabase db = getReadableDatabase();
            return tagExists(db, tagName);
        }

        public boolean tagExists(SQLiteDatabase db, String tagName){
            Cursor c = db.query(TagsTable.TABLE_NAME,
                        TagsTable.ALL_COLUMNS,
                        TagsTable.COLUMN_TAG + " = ?",
                        new String[] { tagName },
                        null,
                        null,
                        null);

            boolean hasTag = c.getCount() > 0;

            c.close();

            return hasTag;
        }

        public long insertTag(String tagName){
            SQLiteDatabase db = getWritableDatabase();
            return insertTag(db, tagName);
        }

        public long insertTag(SQLiteDatabase db, String tagName){
            if(tagExists(db, tagName))
                return -1;

            ContentValues cv = new ContentValues();

            cv.put(TagsTable.COLUMN_TAG, tagName);

            long id = db.insert(TagsTable.TABLE_NAME, null, cv);

            return id;
        }

        public boolean removeTag(String tagName){
            SQLiteDatabase db = getWritableDatabase();
            return removeTag(db, tagName);
        }

        public boolean removeTag(SQLiteDatabase db, String tagName){
            if(!tagExists(db, tagName))
                return true;


            long tagId = getTagId(db, tagName);
            if(tagId == -1)
                return true;

            db.delete(RestaurantTagsTable.TABLE_NAME, RestaurantTagsTable.COLUMN_TAGID + " = ?", new String[]{""+tagId});
            long rowsDeleted = db.delete(TagsTable.TABLE_NAME, TagsTable._ID + " = ?", new String[]{""+tagId});

            return rowsDeleted > 0;
        }

        public long getTagId(String tagName){
            SQLiteDatabase db = getReadableDatabase();
            return getTagId(db, tagName);
        }

        public long getTagId(SQLiteDatabase db, String tagName){
            if(!tagExists(db, tagName))
                return -1;

            Cursor c = db.query(TagsTable.TABLE_NAME,
                    TagsTable.ALL_COLUMNS,
                    TagsTable.COLUMN_TAG + " = ?",
                    new String[] { tagName },
                    null,
                    null,
                    null);

            c.moveToFirst();

            long id = c.getLong(c.getColumnIndexOrThrow(TagsTable._ID));

            c.close();

            return id;
        }

        public boolean addRestaurantTag(long restaurantId, String tagName){
            SQLiteDatabase db = getWritableDatabase();
            return addRestaurantTag(db, restaurantId, tagName);
        }

        public boolean addRestaurantTag(SQLiteDatabase db, long restaurantId, String tagName){
            long tagId;

            if (tagExists(db, tagName)) {
                tagId = getTagId(db, tagName);
            }else{
                tagId = insertTag(tagName);
            }

            if(tagId == -1) return false;

            ContentValues cv = new ContentValues();
            cv.put(RestaurantTagsTable.COLUMN_RESTAURANTID, restaurantId);
            cv.put(RestaurantTagsTable.COLUMN_TAGID, tagId);

            db.insert(RestaurantTagsTable.TABLE_NAME, null, cv);

            return true;
        }

        public boolean removeRestaurantTag(long restaurantId, String tagName){
            SQLiteDatabase db = getWritableDatabase();
            return removeRestaurantTag(db, restaurantId, tagName);
        }

        public boolean removeRestaurantTag(SQLiteDatabase db, long restaurantId, String tagName){
            long tagId;

            if (tagExists(db, tagName)) {
                tagId = getTagId(db, tagName);
            }else{
                return true;
            }

            if(tagId == -1) return true;

            db.delete(RestaurantTagsTable.TABLE_NAME,
                    RestaurantTagsTable.COLUMN_RESTAURANTID + "= ? AND " + RestaurantTagsTable.COLUMN_TAGID + " = ?",
                    new String[] {"" + restaurantId, ""+tagId});

            return true;
        }

        public String[] getRestaurantTags(long restaurantId){
            SQLiteDatabase db = getReadableDatabase();
            return getRestaurantTags(db, restaurantId);
        }

        public String[] getRestaurantTags(SQLiteDatabase db, long restaurantId){
            String query = String.format(
                    "SELECT t1.%s As %s FROM %s t1 INNER JOIN %s t2 ON t1.%s = t2.%s WHERE t2.%s = ?",
                    TagsTable.COLUMN_TAG,
                    TagsTable.COLUMN_TAG,
                    TagsTable.TABLE_NAME,
                    RestaurantTagsTable.TABLE_NAME,
                    TagsTable._ID,
                    RestaurantTagsTable.COLUMN_TAGID,
                    RestaurantTagsTable.COLUMN_RESTAURANTID);

            Cursor c = db.rawQuery(query, new String[]{"" + restaurantId});

            int elementCount = c.getCount();
            String[] tags = new String[elementCount];

            int i = 0;
            if(c.moveToFirst()){
                do{
                    tags[i++] = c.getString(c.getColumnIndexOrThrow(TagsTable.COLUMN_TAG));
                }while(c.moveToNext());
            }

            c.close();
            return tags;
        }
    }
}
