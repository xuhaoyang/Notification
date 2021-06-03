package hk.xhy.notification.ui.base.rv

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2020/11/17
 * Description: No Description
 */
open class RecycleViewDivider @JvmOverloads constructor(
    context: Context?,
    orientation: Int,
    divider: Drawable,
    showFooterDivider: Boolean = false
) : RecyclerView.ItemDecoration() {
    protected var mDivider: Drawable
    protected var mOrientation = 0
    protected var mShowFooterDivider: Boolean
    protected val mBounds = Rect()

    @JvmOverloads
    constructor(
        context: Context?,
        orientation: Int,
        @DrawableRes resId: Int,
        showFooterDivider: Boolean = false
    ) : this(
        context, orientation, ContextCompat.getDrawable(
            context!!, resId
        )!!, showFooterDivider
    ) {
    }

    private fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL && orientation != VERTICAL)) { "Invalid orientation. It should be either HORIZONTAL or VERTICAL" }
        mOrientation = orientation
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) {
            return
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    @SuppressLint("NewApi")
    protected fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (i == childCount - 1 && !mShowFooterDivider) continue
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child))
            val top = bottom - mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(canvas)
        }
        canvas.restore()
    }

    @SuppressLint("NewApi")
    protected fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (i == childCount - 1 && !mShowFooterDivider) continue
            val child = parent.getChildAt(i)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + Math.round(ViewCompat.getTranslationX(child))
            val left = right - mDivider.intrinsicWidth
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (mOrientation == VERTICAL) {
            outRect[0, 0, 0] = mDivider.intrinsicHeight
        } else {
            outRect[0, 0, mDivider.intrinsicWidth] = 0
        }
    }

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
    }

    init {
        setOrientation(orientation)
        mDivider = divider
        mShowFooterDivider = showFooterDivider
    }
}