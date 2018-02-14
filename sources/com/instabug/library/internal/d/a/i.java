package com.instabug.library.internal.d.a;

import android.content.Context;
import android.util.Base64;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class i extends c<String, Serializable> {
    private File a;
    private final File b;

    public final /* synthetic */ Object a(Object obj) {
        return b((String) obj);
    }

    public i(Context context, String str, String str2) {
        super(str);
        this.b = context.getCacheDir();
        this.a = new File(this.b + str2);
        if (!this.a.exists()) {
            try {
                this.a.createNewFile();
            } catch (Throwable e) {
                InstabugSDKLogger.e(this, "Failed to create", e);
            }
        }
    }

    public final List<Serializable> b() {
        Throwable e;
        Throwable th;
        List arrayList = new ArrayList();
        if (this.a.exists()) {
            BufferedReader bufferedReader = null;
            BufferedReader bufferedReader2;
            try {
                bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(this.a)));
                String readLine;
                do {
                    try {
                        readLine = bufferedReader2.readLine();
                        String d = d(readLine);
                        if (d != null) {
                            arrayList.add(c(d));
                            continue;
                        }
                    } catch (IOException e2) {
                        e = e2;
                    }
                } while (readLine != null);
                try {
                    bufferedReader2.close();
                } catch (Throwable e3) {
                    InstabugSDKLogger.e(this, "Failed to close file reader", e3);
                }
            } catch (IOException e4) {
                e3 = e4;
                bufferedReader2 = null;
                try {
                    InstabugSDKLogger.e(this, "Something went wrong while fetching values", e3);
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (Throwable e32) {
                            InstabugSDKLogger.e(this, "Failed to close file reader", e32);
                        }
                    }
                    return arrayList;
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = bufferedReader2;
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable e322) {
                            InstabugSDKLogger.e(this, "Failed to close file reader", e322);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                throw th;
            }
        }
        InstabugSDKLogger.d(this, "Cache file doesn't exist");
        return arrayList;
    }

    private Serializable a(String str) {
        BufferedReader bufferedReader;
        Throwable e;
        Throwable th;
        Serializable serializable = null;
        if (this.a.exists()) {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.a)));
                String readLine;
                do {
                    try {
                        readLine = bufferedReader.readLine();
                        String e2 = e(readLine);
                        if (e2 != null && e2.equals(str)) {
                            serializable = c(d(readLine));
                            try {
                                bufferedReader.close();
                                break;
                            } catch (Throwable e3) {
                                InstabugSDKLogger.e(this, "Failed to close file reader", e3);
                            }
                        }
                    } catch (IOException e4) {
                        e3 = e4;
                    }
                } while (readLine != null);
                try {
                    bufferedReader.close();
                } catch (Throwable e32) {
                    InstabugSDKLogger.e(this, "Failed to close file reader", e32);
                }
            } catch (IOException e5) {
                e32 = e5;
                bufferedReader = null;
                try {
                    InstabugSDKLogger.e(this, "Something went wrong while fetching value for key " + str, e32);
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable e322) {
                            InstabugSDKLogger.e(this, "Failed to close file reader", e322);
                        }
                    }
                    InstabugSDKLogger.d(this, "No value found for key " + str + ", returning null");
                    return serializable;
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable e3222) {
                            InstabugSDKLogger.e(this, "Failed to close file reader", e3222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable e32222) {
                bufferedReader = null;
                th = e32222;
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                throw th;
            }
            InstabugSDKLogger.d(this, "No value found for key " + str + ", returning null");
        } else {
            InstabugSDKLogger.d(this, "Cache file doesn't exist");
        }
        return serializable;
    }

    private Serializable a(String str, Serializable serializable) {
        Throwable e;
        Throwable th;
        Serializable a = a(str);
        if (a != null) {
            b(str);
        }
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(this.a, true);
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                Writer append = bufferedWriter.append(str).append(":");
                OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(serializable);
                objectOutputStream.close();
                append.append(Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2)).append("\n");
                bufferedWriter.close();
                try {
                    fileWriter.close();
                } catch (Throwable e2) {
                    InstabugSDKLogger.e(this, "Failed to close file writer", e2);
                }
            } catch (IOException e3) {
                e2 = e3;
                try {
                    InstabugSDKLogger.e(this, "Something went wrong while setting value for key " + str, e2);
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (Throwable e22) {
                            InstabugSDKLogger.e(this, "Failed to close file writer", e22);
                        }
                    }
                    return a != null ? serializable : a;
                } catch (Throwable th2) {
                    th = th2;
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (Throwable e222) {
                            InstabugSDKLogger.e(this, "Failed to close file writer", e222);
                        }
                    }
                    throw th;
                }
            }
        } catch (IOException e4) {
            e222 = e4;
            fileWriter = null;
            InstabugSDKLogger.e(this, "Something went wrong while setting value for key " + str, e222);
            if (fileWriter != null) {
                fileWriter.close();
            }
            if (a != null) {
            }
        } catch (Throwable th3) {
            th = th3;
            fileWriter = null;
            if (fileWriter != null) {
                fileWriter.close();
            }
            throw th;
        }
        if (a != null) {
        }
    }

    private Serializable b(String str) {
        IOException iOException;
        Serializable serializable = null;
        if (this.a.exists()) {
            File file = new File(this.b + "/cache.tmp");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(this.a));
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                Serializable serializable2 = null;
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        String e = e(readLine);
                        if (e != null) {
                            if (e.equals(str)) {
                                serializable = c(d(readLine));
                                if (readLine == null) {
                                    break;
                                }
                                serializable2 = serializable;
                            } else {
                                bufferedWriter.write(readLine + "\n");
                            }
                        }
                        serializable = serializable2;
                        if (readLine == null) {
                            break;
                        }
                        serializable2 = serializable;
                    } catch (IOException e2) {
                        IOException iOException2 = e2;
                        serializable = serializable2;
                        iOException = iOException2;
                    }
                }
                bufferedReader.close();
                bufferedWriter.flush();
                bufferedWriter.close();
                if (this.a.delete()) {
                    file.renameTo(this.a);
                }
            } catch (IOException e3) {
                iOException = e3;
                iOException.printStackTrace();
                return serializable;
            }
        }
        InstabugSDKLogger.d(this, "Cache file doesn't exist");
        return serializable;
    }

    public final long c() {
        if (this.a.exists()) {
            return this.a.getTotalSpace();
        }
        InstabugSDKLogger.d(this, "Cache file doesn't exist");
        return -1;
    }

    public final void a() {
        if (this.a.exists()) {
            this.a.delete();
        } else {
            InstabugSDKLogger.d(this, "Cache file doesn't exist");
        }
    }

    private static Serializable c(String str) {
        Serializable serializable;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(str, 2)));
            serializable = (Serializable) objectInputStream.readObject();
            try {
                objectInputStream.close();
            } catch (IOException e) {
                InstabugSDKLogger.d(i.class, "Failed to serialize object " + serializable);
                return serializable;
            } catch (ClassNotFoundException e2) {
                InstabugSDKLogger.d(i.class, "Failed to serialize object " + serializable);
                return serializable;
            }
        } catch (IOException e3) {
            serializable = null;
            InstabugSDKLogger.d(i.class, "Failed to serialize object " + serializable);
            return serializable;
        } catch (ClassNotFoundException e4) {
            serializable = null;
            InstabugSDKLogger.d(i.class, "Failed to serialize object " + serializable);
            return serializable;
        }
        return serializable;
    }

    private static String d(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str.substring(str.indexOf(":") + 1);
    }

    private static String e(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str.substring(0, str.indexOf(":"));
    }
}
