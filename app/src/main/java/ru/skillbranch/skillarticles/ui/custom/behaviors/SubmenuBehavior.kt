package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.util.Log
import android.view.SubMenu
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu

class SubmenuBehavior: CoordinatorLayout.Behavior<ArticleSubmenu>() {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ArticleSubmenu,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    //not call if visibility gone
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ArticleSubmenu,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {

        //dy<0 scroll down
        //dy>0 scroll up
        val offset = MathUtils.clamp(child.translationY + dy, 0f, child.minHeight.toFloat())
        //if (child.isClose && offset != child.translationY) {
        child.translationY = offset
        Log.e("SubmenuBehavior", "dy : $dy  translationY  : ${child.translationY}")
        //}
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }


}