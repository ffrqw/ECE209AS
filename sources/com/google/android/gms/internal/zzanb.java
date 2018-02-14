package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.internal.zzbo;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class zzanb extends zzamh {
    private volatile String zzafd;
    private Future<String> zzagN;

    protected zzanb(zzamj zzamj) {
        super(zzamj);
    }

    private final String zzag(Context context) {
        FileInputStream openFileInput;
        FileInputStream fileInputStream;
        Object e;
        Throwable th;
        zzbo.zzcG("ClientId should be loaded from worker thread");
        try {
            openFileInput = context.openFileInput("gaClientId");
            try {
                byte[] bArr = new byte[36];
                int read = openFileInput.read(bArr, 0, 36);
                if (openFileInput.available() > 0) {
                    zzbr("clientId file seems corrupted, deleting it.");
                    openFileInput.close();
                    context.deleteFile("gaClientId");
                    if (openFileInput == null) {
                        return null;
                    }
                    try {
                        openFileInput.close();
                        return null;
                    } catch (IOException e2) {
                        zze("Failed to close client id reading stream", e2);
                        return null;
                    }
                } else if (read < 14) {
                    zzbr("clientId file is empty, deleting it.");
                    openFileInput.close();
                    context.deleteFile("gaClientId");
                    if (openFileInput == null) {
                        return null;
                    }
                    try {
                        openFileInput.close();
                        return null;
                    } catch (IOException e22) {
                        zze("Failed to close client id reading stream", e22);
                        return null;
                    }
                } else {
                    openFileInput.close();
                    String str = new String(bArr, 0, read);
                    zza("Read client id from disk", str);
                    if (openFileInput != null) {
                        try {
                            openFileInput.close();
                        } catch (IOException e3) {
                            zze("Failed to close client id reading stream", e3);
                        }
                    }
                    return str;
                }
            } catch (FileNotFoundException e4) {
                fileInputStream = openFileInput;
                if (fileInputStream != null) {
                    return null;
                }
                try {
                    fileInputStream.close();
                    return null;
                } catch (IOException e222) {
                    zze("Failed to close client id reading stream", e222);
                    return null;
                }
            } catch (IOException e5) {
                e = e5;
                try {
                    zze("Error reading client id file, deleting it", e);
                    context.deleteFile("gaClientId");
                    if (openFileInput != null) {
                        return null;
                    }
                    try {
                        openFileInput.close();
                        return null;
                    } catch (IOException e2222) {
                        zze("Failed to close client id reading stream", e2222);
                        return null;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (openFileInput != null) {
                        try {
                            openFileInput.close();
                        } catch (IOException e22222) {
                            zze("Failed to close client id reading stream", e22222);
                        }
                    }
                    throw th;
                }
            }
        } catch (FileNotFoundException e6) {
            fileInputStream = null;
            if (fileInputStream != null) {
                return null;
            }
            fileInputStream.close();
            return null;
        } catch (IOException e7) {
            e = e7;
            openFileInput = null;
            zze("Error reading client id file, deleting it", e);
            context.deleteFile("gaClientId");
            if (openFileInput != null) {
                return null;
            }
            openFileInput.close();
            return null;
        } catch (Throwable th3) {
            openFileInput = null;
            th = th3;
            if (openFileInput != null) {
                openFileInput.close();
            }
            throw th;
        }
    }

    private final String zzll() {
        String toLowerCase = UUID.randomUUID().toString().toLowerCase();
        try {
            return !zzv(zzkt().getContext(), toLowerCase) ? "0" : toLowerCase;
        } catch (Exception e) {
            zze("Error saving clientId file", e);
            return "0";
        }
    }

    private final boolean zzv(Context context, String str) {
        zzbo.zzcF(str);
        zzbo.zzcG("ClientId should be saved from worker thread");
        FileOutputStream fileOutputStream = null;
        try {
            zza("Storing clientId", str);
            fileOutputStream = context.openFileOutput("gaClientId", 0);
            fileOutputStream.write(str.getBytes());
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    zze("Failed to close clientId writing stream", e);
                }
            }
            return true;
        } catch (FileNotFoundException e2) {
            zze("Error creating clientId file", e2);
            if (fileOutputStream == null) {
                return false;
            }
            try {
                fileOutputStream.close();
                return false;
            } catch (IOException e3) {
                zze("Failed to close clientId writing stream", e3);
                return false;
            }
        } catch (IOException e32) {
            zze("Error writing to clientId file", e32);
            if (fileOutputStream == null) {
                return false;
            }
            try {
                fileOutputStream.close();
                return false;
            } catch (IOException e322) {
                zze("Failed to close clientId writing stream", e322);
                return false;
            }
        } catch (Throwable th) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e3222) {
                    zze("Failed to close clientId writing stream", e3222);
                }
            }
        }
    }

    protected final void zzjD() {
    }

    public final String zzli() {
        String str;
        zzkD();
        synchronized (this) {
            if (this.zzafd == null) {
                this.zzagN = zzkt().zzd(new zzanc(this));
            }
            if (this.zzagN != null) {
                try {
                    this.zzafd = (String) this.zzagN.get();
                } catch (InterruptedException e) {
                    zzd("ClientId loading or generation was interrupted", e);
                    this.zzafd = "0";
                } catch (ExecutionException e2) {
                    zze("Failed to load or generate client id", e2);
                    this.zzafd = "0";
                }
                if (this.zzafd == null) {
                    this.zzafd = "0";
                }
                zza("Loaded clientId", this.zzafd);
                this.zzagN = null;
            }
            str = this.zzafd;
        }
        return str;
    }

    final String zzlj() {
        synchronized (this) {
            this.zzafd = null;
            this.zzagN = zzkt().zzd(new zzand(this));
        }
        return zzli();
    }

    final String zzlk() {
        String zzag = zzag(zzkt().getContext());
        return zzag == null ? zzll() : zzag;
    }
}
