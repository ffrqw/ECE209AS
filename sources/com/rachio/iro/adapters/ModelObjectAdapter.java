package com.rachio.iro.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.rachio.iro.binder.CardInfo;
import com.rachio.iro.binder.ModelObjectViewHolder;
import com.rachio.iro.binder.ModelViewType;
import java.util.ArrayList;
import java.util.List;

public class ModelObjectAdapter extends Adapter<ModelObjectViewHolder> {
    private List<CardInfo> mData = new ArrayList();

    public /* bridge */ /* synthetic */ void onBindViewHolder(ViewHolder viewHolder, int i) {
        CardInfo cardInfo = (CardInfo) this.mData.get(i);
        cardInfo.type.getBinder().bind((ModelObjectViewHolder) viewHolder, cardInfo.data);
    }

    public ModelObjectAdapter() {
        setHasStableIds(true);
    }

    public int getItemCount() {
        return this.mData == null ? 0 : this.mData.size();
    }

    public long getItemId(int position) {
        return (long) ((CardInfo) this.mData.get(position)).type.ordinal();
    }

    public int getItemViewType(int position) {
        return ((CardInfo) this.mData.get(position)).type.ordinal();
    }

    public final void setData(List<CardInfo> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public final void addCard(CardInfo cardInfo) {
        int insertPosition = cardInfo.desiredPosition != -1 ? cardInfo.desiredPosition : this.mData.size();
        this.mData.add(Math.min(insertPosition, this.mData.size()), cardInfo);
        if (cardInfo.desiredPosition != -1) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(insertPosition);
        }
    }

    public final void removeCard(CardInfo cardInfo) {
        int position = -1;
        for (int i = 0; i < this.mData.size(); i++) {
            if (((CardInfo) this.mData.get(i)).type == cardInfo.type) {
                position = i;
            }
        }
        if (position != -1) {
            this.mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public final void updateCard(CardInfo cardInfo) {
        int insertPosition;
        if (this.mData != null) {
            for (int i = 0; i < this.mData.size(); i++) {
                if (((CardInfo) this.mData.get(i)).type.equals(cardInfo.type)) {
                    insertPosition = i;
                    break;
                }
            }
        }
        insertPosition = -1;
        if (insertPosition == -1) {
            addCard(cardInfo);
            notifyItemChanged(this.mData.size() - 1);
            return;
        }
        ((CardInfo) this.mData.get(insertPosition)).data = cardInfo.data;
        notifyItemChanged(insertPosition);
    }

    public final void clear() {
        int itemCount = this.mData.size();
        this.mData.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public /* bridge */ /* synthetic */ ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ModelViewType fromOrdinal = ModelViewType.fromOrdinal(i);
        return fromOrdinal.getBinder().createViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(fromOrdinal.getBinder().getLayoutId(), viewGroup, false));
    }
}
