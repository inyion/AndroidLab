package com.rel.csam.lab.view

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rel.csam.lab.viewmodel.ListModel

class SectionItemDeco(private val headerOffset: Int, private val sticky: Boolean, private val listModel: ListModel<*>) : RecyclerView.ItemDecoration() {

    private var headerView: View? = null
    private var header: TextView? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val pos = parent.getChildAdapterPosition(view)
        if (listModel.getSectionCallBack()!!.isSection(pos)) {
            outRect.top = headerOffset
        }
    }

    var preStartDrawView: HashMap<Int, View?> = HashMap()
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (headerView == null) {
//            headerView = inflateHeaderView(parent)
//            header = headerView!!.findViewById(R.id.date)
            fixLayoutSize(headerView!!, parent)
        }

        var previousHeader = ""
        val count = listModel.getItemCount()
        var drawStartPosition = 0

        try {
            for (i in 0 until parent.childCount) {
                var child = parent.getChildAt(i)
                val position = parent.getChildAdapterPosition(child)
                if (drawStartPosition == 0) {
                    drawStartPosition = position
                }

                if (parent.isAnimating && preStartDrawView.containsKey(position)) {
                    // 셀추가 애니메이션으로 그리는 시점은 headerView 를 그리는 시작포지션이 잘못나와서 조정해줘야함
                    child = preStartDrawView[position]
                }

                if (position in 0 until count && child != null) {//
                    val title = listModel.getSectionCallBack()!!.getSectionHeader(position)
                    header!!.text = title

                    if (previousHeader != title || (position != 0 && listModel.getSectionCallBack()!!.isSection(position))) {
                        drawHeader(c, child, headerView!!)
                        previousHeader = title
                        preStartDrawView[position] = child
                    }
                }
            }
        } catch (e: Exception) {
            //IndexOutOfBoundException

        }
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        if (sticky) {
            c.translate(0f, Math.max(0, child.top - headerView.height).toFloat())
        } else {
            c.translate(0f, (child.top - headerView.height).toFloat())
        }
        headerView.draw(c)
        c.restore()
    }

//    private fun inflateHeaderView(parent: RecyclerView): View {
//        return LayoutInflater.from(parent.context)
//                .inflate(R.layout.chat_date_view, parent, false)
//    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width,
                View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height,
                View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.paddingLeft + parent.paddingRight,
                view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height)

        view.measure(childWidth, childHeight)

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface SectionCallback {

        fun isSection(position: Int): Boolean

        fun getSectionHeader(position: Int): String
    }
}
