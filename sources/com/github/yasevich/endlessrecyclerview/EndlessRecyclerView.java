package com.github.yasevich.endlessrecyclerview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;

public final class EndlessRecyclerView extends RecyclerView {
    private AdapterWrapper adapterWrapper;
    private EndlessScrollListener endlessScrollListener;
    private final Handler handler;
    private LayoutManagerWrapper layoutManagerWrapper;
    private final Runnable notifyDataSetChangedRunnable;
    private View progressView;
    private boolean refreshing;
    private int threshold;

    private final class AdapterWrapper extends Adapter<ViewHolder> {
        private final Adapter<ViewHolder> adapter;
        private ProgressViewHolder progressViewHolder;

        private final class ProgressViewHolder extends ViewHolder {
            public ProgressViewHolder() {
                super(EndlessRecyclerView.this.progressView);
            }
        }

        public AdapterWrapper(Adapter<ViewHolder> adapter) {
            if (adapter == null) {
                throw new NullPointerException("adapter is null");
            }
            this.adapter = adapter;
            setHasStableIds(adapter.hasStableIds());
        }

        public final int getItemCount() {
            int itemCount = this.adapter.getItemCount();
            int i = (!EndlessRecyclerView.this.refreshing || EndlessRecyclerView.this.progressView == null) ? 0 : 1;
            return i + itemCount;
        }

        public final long getItemId(int position) {
            return position == this.adapter.getItemCount() ? -1 : this.adapter.getItemId(position);
        }

        public final int getItemViewType(int position) {
            if (((position == this.adapter.getItemCount() ? 1 : 0) & EndlessRecyclerView.this.refreshing) != 0) {
                return -1;
            }
            return this.adapter.getItemViewType(position);
        }

        public final void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.adapter.onAttachedToRecyclerView(recyclerView);
        }

        public final void onBindViewHolder(ViewHolder holder, int position) {
            if (position < this.adapter.getItemCount()) {
                this.adapter.onBindViewHolder(holder, position);
            }
        }

        public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType != -1) {
                return this.adapter.onCreateViewHolder(parent, viewType);
            }
            ViewHolder progressViewHolder = new ProgressViewHolder();
            this.progressViewHolder = progressViewHolder;
            return progressViewHolder;
        }

        public final void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            this.adapter.onDetachedFromRecyclerView(recyclerView);
        }

        public final boolean onFailedToRecycleView(ViewHolder holder) {
            return holder == this.progressViewHolder || this.adapter.onFailedToRecycleView(holder);
        }

        public final void onViewAttachedToWindow(ViewHolder holder) {
            if (holder != this.progressViewHolder) {
                this.adapter.onViewAttachedToWindow(holder);
            }
        }

        public final void onViewDetachedFromWindow(ViewHolder holder) {
            if (holder != this.progressViewHolder) {
                this.adapter.onViewDetachedFromWindow(holder);
            }
        }

        public final void onViewRecycled(ViewHolder holder) {
            if (holder != this.progressViewHolder) {
                this.adapter.onViewRecycled(holder);
            }
        }

        public final void registerAdapterDataObserver(AdapterDataObserver observer) {
            super.registerAdapterDataObserver(observer);
            this.adapter.registerAdapterDataObserver(observer);
        }

        public final void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            super.unregisterAdapterDataObserver(observer);
            this.adapter.unregisterAdapterDataObserver(observer);
        }

        public final Adapter<ViewHolder> getAdapter() {
            return this.adapter;
        }
    }

    private final class EndlessScrollListener extends OnScrollListener {
        private final Pager pager;
        private int threshold = 1;

        public EndlessScrollListener(Pager pager) {
            if (pager == null) {
                throw new NullPointerException("pager is null");
            }
            this.pager = pager;
        }

        public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int lastVisibleItemPosition = EndlessRecyclerView.this.layoutManagerWrapper.findLastVisibleItemPosition();
            int lastItemPosition = EndlessRecyclerView.this.getAdapter().getItemCount();
            if (this.pager.shouldLoad() && lastItemPosition - lastVisibleItemPosition <= this.threshold) {
                EndlessRecyclerView.this.setRefreshing(true);
                this.pager.loadNextPage();
            }
        }

        public final void setThreshold(int threshold) {
            if (threshold <= 0) {
                throw new IllegalArgumentException("illegal threshold: " + threshold);
            }
            this.threshold = threshold;
        }
    }

    private static final class LayoutManagerWrapper {
        final LayoutManager layoutManager;
        private final LayoutManagerResolver resolver;

        private interface LayoutManagerResolver {
            int findLastVisibleItemPosition(LayoutManager layoutManager);
        }

        public LayoutManagerWrapper(LayoutManager layoutManager) {
            LayoutManagerResolver anonymousClass1;
            this.layoutManager = layoutManager;
            if (layoutManager instanceof LinearLayoutManager) {
                anonymousClass1 = new LayoutManagerResolver() {
                    public final int findLastVisibleItemPosition(LayoutManager layoutManager) {
                        return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    }
                };
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                anonymousClass1 = new LayoutManagerResolver() {
                    public final int findLastVisibleItemPosition(LayoutManager layoutManager) {
                        int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
                        int lastVisibleItemPosition = lastVisibleItemPositions[0];
                        for (int i = 1; i < lastVisibleItemPositions.length; i++) {
                            if (lastVisibleItemPosition < lastVisibleItemPositions[i]) {
                                lastVisibleItemPosition = lastVisibleItemPositions[i];
                            }
                        }
                        return lastVisibleItemPosition;
                    }
                };
            } else {
                throw new IllegalArgumentException("unsupported layout manager: " + layoutManager);
            }
            this.resolver = anonymousClass1;
        }

        public final int findLastVisibleItemPosition() {
            return this.resolver.findLastVisibleItemPosition(this.layoutManager);
        }
    }

    public interface Pager {
        void loadNextPage();

        boolean shouldLoad();
    }

    public EndlessRecyclerView(Context context) {
        this(context, null);
    }

    public EndlessRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndlessRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.handler = new Handler();
        this.notifyDataSetChangedRunnable = new Runnable() {
            public final void run() {
                EndlessRecyclerView.this.adapterWrapper.notifyDataSetChanged();
            }
        };
        this.threshold = 1;
    }

    public final void setAdapter(Adapter adapter) {
        this.adapterWrapper = new AdapterWrapper(adapter);
        super.setAdapter(this.adapterWrapper);
    }

    public final Adapter getAdapter() {
        return this.adapterWrapper.getAdapter();
    }

    public final void setLayoutManager(LayoutManager layout) {
        this.layoutManagerWrapper = layout == null ? null : new LayoutManagerWrapper(layout);
        super.setLayoutManager(layout);
    }

    public final void setPager(Pager pager) {
        if (pager != null) {
            this.endlessScrollListener = new EndlessScrollListener(pager);
            this.endlessScrollListener.setThreshold(this.threshold);
            addOnScrollListener(this.endlessScrollListener);
        } else if (this.endlessScrollListener != null) {
            removeOnScrollListener(this.endlessScrollListener);
            this.endlessScrollListener = null;
        }
    }

    public final void setProgressView(int layoutResId) {
        this.progressView = LayoutInflater.from(getContext()).inflate(R.layout.view_endlessrecyclerprogress, this, false);
    }

    public final void setRefreshing(boolean refreshing) {
        if (this.refreshing != refreshing) {
            this.refreshing = refreshing;
            if (isComputingLayout()) {
                this.handler.post(this.notifyDataSetChangedRunnable);
            } else {
                this.adapterWrapper.notifyDataSetChanged();
            }
        }
    }
}
