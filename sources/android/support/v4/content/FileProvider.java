package android.support.v4.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParserException;

public class FileProvider extends ContentProvider {
    private static final String[] COLUMNS = new String[]{"_display_name", "_size"};
    private static final File DEVICE_ROOT = new File("/");
    private static HashMap<String, PathStrategy> sCache = new HashMap();
    private PathStrategy mStrategy;

    interface PathStrategy {
        File getFileForUri(Uri uri);

        Uri getUriForFile(File file);
    }

    static class SimplePathStrategy implements PathStrategy {
        private final String mAuthority;
        private final HashMap<String, File> mRoots = new HashMap();

        public SimplePathStrategy(String authority) {
            this.mAuthority = authority;
        }

        public final void addRoot(String name, File root) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Name must not be empty");
            }
            try {
                this.mRoots.put(name, root.getCanonicalFile());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + root, e);
            }
        }

        public final Uri getUriForFile(File file) {
            try {
                String rootPath;
                String path = file.getCanonicalPath();
                Entry<String, File> mostSpecific = null;
                for (Entry<String, File> root : this.mRoots.entrySet()) {
                    rootPath = ((File) root.getValue()).getPath();
                    if (path.startsWith(rootPath) && (mostSpecific == null || rootPath.length() > ((File) mostSpecific.getValue()).getPath().length())) {
                        mostSpecific = root;
                    }
                }
                if (mostSpecific == null) {
                    throw new IllegalArgumentException("Failed to find configured root that contains " + path);
                }
                rootPath = ((File) mostSpecific.getValue()).getPath();
                if (rootPath.endsWith("/")) {
                    path = path.substring(rootPath.length());
                } else {
                    path = path.substring(rootPath.length() + 1);
                }
                return new Builder().scheme("content").authority(this.mAuthority).encodedPath(Uri.encode((String) mostSpecific.getKey()) + '/' + Uri.encode(path, "/")).build();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
            }
        }

        public final File getFileForUri(Uri uri) {
            String path = uri.getEncodedPath();
            int splitIndex = path.indexOf(47, 1);
            String tag = Uri.decode(path.substring(1, splitIndex));
            path = Uri.decode(path.substring(splitIndex + 1));
            File root = (File) this.mRoots.get(tag);
            if (root == null) {
                throw new IllegalArgumentException("Unable to find configured root for " + uri);
            }
            File file = new File(root, path);
            try {
                file = file.getCanonicalFile();
                if (file.getPath().startsWith(root.getPath())) {
                    return file;
                }
                throw new SecurityException("Resolved path jumped beyond configured root");
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
            }
        }
    }

    public boolean onCreate() {
        return true;
    }

    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        if (info.exported) {
            throw new SecurityException("Provider must not be exported");
        } else if (info.grantUriPermissions) {
            this.mStrategy = getPathStrategy(context, info.authority);
        } else {
            throw new SecurityException("Provider must grant uri permissions");
        }
    }

    public static Uri getUriForFile(Context context, String authority, File file) {
        return getPathStrategy(context, authority).getUriForFile(file);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        File file = this.mStrategy.getFileForUri(uri);
        if (projection == null) {
            projection = COLUMNS;
        }
        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int length = projection.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            int i3;
            String col = projection[i];
            if ("_display_name".equals(col)) {
                cols[i2] = "_display_name";
                i3 = i2 + 1;
                values[i2] = file.getName();
            } else if ("_size".equals(col)) {
                cols[i2] = "_size";
                i3 = i2 + 1;
                values[i2] = Long.valueOf(file.length());
            } else {
                i3 = i2;
            }
            i++;
            i2 = i3;
        }
        String[] cols2 = new String[i2];
        System.arraycopy(cols, 0, cols2, 0, i2);
        Object[] values2 = new Object[i2];
        System.arraycopy(values, 0, values2, 0, i2);
        MatrixCursor cursor = new MatrixCursor(cols2, 1);
        cursor.addRow(values2);
        return cursor;
    }

    public String getType(Uri uri) {
        File file = this.mStrategy.getFileForUri(uri);
        int lastDot = file.getName().lastIndexOf(46);
        if (lastDot >= 0) {
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.getName().substring(lastDot + 1));
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external inserts");
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external updates");
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return this.mStrategy.getFileForUri(uri).delete() ? 1 : 0;
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        int fileMode;
        File file = this.mStrategy.getFileForUri(uri);
        if ("r".equals(mode)) {
            fileMode = 268435456;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            fileMode = 738197504;
        } else if ("wa".equals(mode)) {
            fileMode = 704643072;
        } else if ("rw".equals(mode)) {
            fileMode = 939524096;
        } else if ("rwt".equals(mode)) {
            fileMode = 1006632960;
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        return ParcelFileDescriptor.open(file, fileMode);
    }

    private static PathStrategy getPathStrategy(Context context, String authority) {
        PathStrategy strat;
        synchronized (sCache) {
            strat = (PathStrategy) sCache.get(authority);
            if (strat == null) {
                try {
                    strat = new SimplePathStrategy(authority);
                    XmlResourceParser loadXmlMetaData = context.getPackageManager().resolveContentProvider(authority, 128).loadXmlMetaData(context.getPackageManager(), "android.support.FILE_PROVIDER_PATHS");
                    if (loadXmlMetaData == null) {
                        throw new IllegalArgumentException("Missing android.support.FILE_PROVIDER_PATHS meta-data");
                    }
                    while (true) {
                        int next = loadXmlMetaData.next();
                        if (next == 1) {
                            break;
                        } else if (next == 2) {
                            File file;
                            String name = loadXmlMetaData.getName();
                            String attributeValue = loadXmlMetaData.getAttributeValue(null, "name");
                            String attributeValue2 = loadXmlMetaData.getAttributeValue(null, "path");
                            if ("root-path".equals(name)) {
                                file = DEVICE_ROOT;
                            } else if ("files-path".equals(name)) {
                                file = context.getFilesDir();
                            } else if ("cache-path".equals(name)) {
                                file = context.getCacheDir();
                            } else if ("external-path".equals(name)) {
                                file = Environment.getExternalStorageDirectory();
                            } else {
                                File[] externalFilesDirs;
                                if ("external-files-path".equals(name)) {
                                    externalFilesDirs = ContextCompat.getExternalFilesDirs(context, null);
                                    if (externalFilesDirs.length > 0) {
                                        file = externalFilesDirs[0];
                                    }
                                } else if ("external-cache-path".equals(name)) {
                                    externalFilesDirs = ContextCompat.getExternalCacheDirs(context);
                                    if (externalFilesDirs.length > 0) {
                                        file = externalFilesDirs[0];
                                    }
                                }
                                file = null;
                            }
                            if (file != null) {
                                strat.addRoot(attributeValue, buildPath(file, attributeValue2));
                            }
                        }
                    }
                    sCache.put(authority, strat);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", e);
                } catch (XmlPullParserException e2) {
                    throw new IllegalArgumentException("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", e2);
                }
            }
        }
        return strat;
    }

    private static File buildPath(File base, String... segments) {
        int i = 0;
        File cur = base;
        while (i <= 0) {
            File cur2;
            String segment = segments[0];
            if (segment != null) {
                cur2 = new File(cur, segment);
            } else {
                cur2 = cur;
            }
            i++;
            cur = cur2;
        }
        return cur;
    }
}
