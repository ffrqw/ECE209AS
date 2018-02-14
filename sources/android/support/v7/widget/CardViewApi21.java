package android.support.v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

@TargetApi(21)
final class CardViewApi21 implements CardViewImpl {
    CardViewApi21() {
    }

    public final void initialize(CardViewDelegate cardView, Context context, ColorStateList backgroundColor, float radius, float elevation, float maxElevation) {
        cardView.setCardBackground(new RoundRectDrawable(backgroundColor, radius));
        View view = cardView.getCardView();
        view.setClipToOutline(true);
        view.setElevation(elevation);
        setMaxElevation(cardView, maxElevation);
    }

    public final void setRadius(CardViewDelegate cardView, float radius) {
        getCardBackground(cardView).setRadius(radius);
    }

    public final void initStatic() {
    }

    public final void setMaxElevation(CardViewDelegate cardView, float maxElevation) {
        getCardBackground(cardView).setPadding(maxElevation, cardView.getUseCompatPadding(), cardView.getPreventCornerOverlap());
        updatePadding(cardView);
    }

    public final float getMaxElevation(CardViewDelegate cardView) {
        return getCardBackground(cardView).getPadding();
    }

    public final float getMinWidth(CardViewDelegate cardView) {
        return getRadius(cardView) * 2.0f;
    }

    public final float getMinHeight(CardViewDelegate cardView) {
        return getRadius(cardView) * 2.0f;
    }

    public final float getRadius(CardViewDelegate cardView) {
        return getCardBackground(cardView).getRadius();
    }

    public final void setElevation(CardViewDelegate cardView, float elevation) {
        cardView.getCardView().setElevation(elevation);
    }

    public final float getElevation(CardViewDelegate cardView) {
        return cardView.getCardView().getElevation();
    }

    public final void updatePadding(CardViewDelegate cardView) {
        if (cardView.getUseCompatPadding()) {
            float elevation = getMaxElevation(cardView);
            float radius = getRadius(cardView);
            int hPadding = (int) Math.ceil((double) RoundRectDrawableWithShadow.calculateHorizontalPadding(elevation, radius, cardView.getPreventCornerOverlap()));
            int vPadding = (int) Math.ceil((double) RoundRectDrawableWithShadow.calculateVerticalPadding(elevation, radius, cardView.getPreventCornerOverlap()));
            cardView.setShadowPadding(hPadding, vPadding, hPadding, vPadding);
            return;
        }
        cardView.setShadowPadding(0, 0, 0, 0);
    }

    public final void onCompatPaddingChanged(CardViewDelegate cardView) {
        setMaxElevation(cardView, getMaxElevation(cardView));
    }

    public final void onPreventCornerOverlapChanged(CardViewDelegate cardView) {
        setMaxElevation(cardView, getMaxElevation(cardView));
    }

    public final void setBackgroundColor(CardViewDelegate cardView, ColorStateList color) {
        getCardBackground(cardView).setColor(color);
    }

    public final ColorStateList getBackgroundColor(CardViewDelegate cardView) {
        return getCardBackground(cardView).getColor();
    }

    private RoundRectDrawable getCardBackground(CardViewDelegate cardView) {
        return (RoundRectDrawable) cardView.getCardBackground();
    }
}
