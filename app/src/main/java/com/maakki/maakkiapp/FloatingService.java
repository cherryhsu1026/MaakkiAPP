package com.maakki.maakkiapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;

public class FloatingService extends FloatingBubbleService {
    Drawable bubbleDrawable= ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_notifications_active_999_14dp);
    Drawable removeIconDrawable=bubbleDrawable;
    View yourViewAfterClick;
    @Override
    protected FloatingBubbleConfig getConfig() {
        return new FloatingBubbleConfig.Builder()
                // Set the drawable for the bubble
                .bubbleIcon(bubbleDrawable)

                // Set the drawable for the remove bubble
                .removeBubbleIcon(removeIconDrawable)

                // Set the size of the bubble in dp
                .bubbleIconDp(64)

                // Set the size of the remove bubble in dp
                .removeBubbleIconDp(64)

                // Set the padding of the view from the boundary
                .paddingDp(4)

                // Set the radius of the border of the expandable view
                .borderRadiusDp(4)

                // Does the bubble attract towards the walls
                .physicsEnabled(true)

                // The color of background of the layout
                .expandableColor(Color.WHITE)

                // The color of the triangular layout
                .triangleColor(Color.WHITE)

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.END)

                // The view which is visible in the expanded view
                .expandableView(yourViewAfterClick)

                // Set the alpha value for the remove bubble icon
                .removeBubbleAlpha(0.75f)

                // Building
                .build();
    }
}