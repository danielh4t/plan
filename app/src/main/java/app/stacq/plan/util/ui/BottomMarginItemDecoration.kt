package app.stacq.plan.util.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BottomMarginItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            bottom = margin
        }
    }
}