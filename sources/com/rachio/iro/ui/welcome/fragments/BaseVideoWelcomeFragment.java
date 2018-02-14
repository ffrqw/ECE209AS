package com.rachio.iro.ui.welcome.fragments;

import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.rachio.iro.R;

public abstract class BaseVideoWelcomeFragment extends BaseWelcomeFragment {
    private static final String TAG = BaseVideoWelcomeFragment.class.getName();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private FrameLayout videoContainer;
    private ImageView videoDummyBottom;
    private TextureView videoSurface;

    protected abstract String getVideoPath();

    protected final void wireUpVideo(View view) {
        this.videoContainer = (FrameLayout) view.findViewById(R.id.welcome_video);
        this.videoSurface = (TextureView) this.videoContainer.findViewById(R.id.welcome_video_surface);
        this.videoDummyBottom = (ImageView) this.videoContainer.findViewById(R.id.welcome_video_dummybottom);
        this.videoSurface.setSurfaceTextureListener(new SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d(BaseVideoWelcomeFragment.TAG, "surface created");
                BaseVideoWelcomeFragment.access$100(BaseVideoWelcomeFragment.this, surface);
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        try {
            this.videoDummyBottom.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(getVideoPath() + ".firstframe.jpg")));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public final void onScrollStopped() {
        super.onScrollStopped();
        if (this.active) {
            if (!this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.start();
            }
        } else if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
        }
    }

    static /* synthetic */ void access$100(BaseVideoWelcomeFragment x0, SurfaceTexture x1) {
        try {
            AssetFileDescriptor openFd = x0.getResources().getAssets().openFd(x0.getVideoPath());
            x0.mediaPlayer.reset();
            x0.mediaPlayer.setSurface(new Surface(x1));
            x0.mediaPlayer.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
            x0.mediaPlayer.prepareAsync();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
