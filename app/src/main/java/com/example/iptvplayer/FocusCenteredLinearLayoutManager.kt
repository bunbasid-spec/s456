package com.example.iptvplayer

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FocusCenteredLinearLayoutManager(
    context: Context,
    orientation: Int = VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun requestChildRectangleOnScreen(
        parent: RecyclerView,
        child: View,
        rect: Rect,
        immediate: Boolean,
        focusedChildVisible: Boolean
    ): Boolean {
        val parentHeight = height
        val childCenter = child.top + (child.height / 2)
        val targetCenter = parentHeight / 2

        // Ekran ortası ile seçili öge merkezi arasındaki fark
        val scrollAmount = childCenter - targetCenter

        if (scrollAmount != 0) {
            if (immediate) {
                parent.scrollBy(0, scrollAmount)
            } else {
                parent.smoothScrollBy(0, scrollAmount)
            }
            return true
        }

        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible)
    }
}
