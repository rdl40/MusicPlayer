{\rtf1\ansi\ansicpg932\deff0\deflang1033\deflangfe1041{\fonttbl{\f0\fswiss\fcharset0 Arial;}}
{\*\generator Msftedit 5.41.21.2509;}\viewkind4\uc1\pard\f0\fs20\par
import android.content.ContentResolver;\par
import android.content.ContentUris;\par
import android.database.Cursor;\par
import android.net.Uri;\par
import android.provider.MediaStore;\par
import android.util.Log;\par
\par
import java.util.ArrayList;\par
import java.util.List;\par
import java.util.Random;\par
\par
/**\par
 * Retrieves and organizes media to play. Before being used, you must call \{@link #prepare()\},\par
 * which will retrieve all of the music on the user's device (by performing a query on a content\par
 * resolver). After that, it's ready to retrieve a random song, with its title and URI, upon\par
 * request.\par
 */\par
public class MusicRetriever \{\par
    final String TAG = "MusicRetriever";\par
\par
    ContentResolver mContentResolver;\par
\par
    // the items (songs) we have queried\par
    List<Item> mItems = new ArrayList<Item>();\par
\par
    Random mRandom = new Random();\par
\par
    public MusicRetriever(ContentResolver cr) \{\par
        mContentResolver = cr;\par
    \}\par
\par
    /**\par
     * Loads music data. This method may take long, so be sure to call it asynchronously without\par
     * blocking the main thread.\par
     */\par
    public void prepare() \{\par
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;\par
        Log.i(TAG, "Querying media...");\par
        Log.i(TAG, "URI: " + uri.toString());\par
\par
        // Perform a query on the content resolver. The URI we're passing specifies that we\par
        // want to query for all audio media on external storage (e.g. SD card)\par
        Cursor cur = mContentResolver.query(uri, null,\par
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);\par
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));\par
\par
        if (cur == null) \{\par
            // Query failed...\par
            Log.e(TAG, "Failed to retrieve music: cursor is null :-(");\par
            return;\par
        \}\par
        if (!cur.moveToFirst()) \{\par
            // Nothing to query. There is no music on the device. How boring.\par
            Log.e(TAG, "Failed to move cursor to first row (no query results).");\par
            return;\par
        \}\par
\par
        Log.i(TAG, "Listing...");\par
\par
        // retrieve the indices of the columns where the ID, title, etc. of the song are\par
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);\par
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);\par
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);\par
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);\par
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);\par
\par
        Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));\par
        Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));\par
\par
        // add each song to mItems\par
        do \{\par
            Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn));\par
            mItems.add(new Item(\par
                    cur.getLong(idColumn),\par
                    cur.getString(artistColumn),\par
                    cur.getString(titleColumn),\par
                    cur.getString(albumColumn),\par
                    cur.getLong(durationColumn)));\par
        \} while (cur.moveToNext());\par
\par
        Log.i(TAG, "Done querying media. MusicRetriever is ready.");\par
    \}\par
\par
    public ContentResolver getContentResolver() \{\par
        return mContentResolver;\par
    \}\par
\par
    /** Returns a random Item. If there are no items available, returns null. */\par
    public Item getRandomItem() \{\par
        if (mItems.size() <= 0) return null;\par
        return mItems.get(mRandom.nextInt(mItems.size()));\par
    \}\par
\par
    public static class Item \{\par
        long id;\par
        String artist;\par
        String title;\par
        String album;\par
        long duration;\par
\par
        public Item(long id, String artist, String title, String album, long duration) \{\par
            this.id = id;\par
            this.artist = artist;\par
            this.title = title;\par
            this.album = album;\par
            this.duration = duration;\par
        \}\par
\par
        public long getId() \{\par
            return id;\par
        \}\par
\par
        public String getArtist() \{\par
            return artist;\par
        \}\par
\par
        public String getTitle() \{\par
            return title;\par
        \}\par
\par
        public String getAlbum() \{\par
            return album;\par
        \}\par
\par
        public long getDuration() \{\par
            return duration;\par
        \}\par
\par
        public Uri getURI() \{\par
            return ContentUris.withAppendedId(\par
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);\par
        \}\par
    \}\par
\}\par
}
 