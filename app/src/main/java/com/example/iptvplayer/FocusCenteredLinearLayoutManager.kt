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
        val childCenter = (child.top + child.bottom) / 2
        val targetCenter = parentHeight / 2

        val scrollAmount = childCenter - targetCenter

        if (scrollAmount != 0) {
            // Anlık kaydırma yaparak odak ile liste hareketinin çakışmasını engelliyoruz
            parent.scrollBy(0, scrollAmount)
        }

        // Android'in varsayılan kaydırma mekanizmasını ezip odağın işlendiğini bildiriyoruz
        return true
    }
}