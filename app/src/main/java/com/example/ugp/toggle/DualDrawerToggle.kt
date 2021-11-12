package com.example.ugp.toggle

import android.app.Activity
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class DualDrawerToggle(
    activity : Activity,
    private val drawerLayout : DrawerLayout,
    private val toolbar : Toolbar,
    drawable : Drawable,
    startDrawerOpenContDescRes : Int,
    startDrawerCloseContDescRes : Int,
    endDrawerOpenContDescRes : Int,
    endDrawerCloseContDescRes : Int
) : DrawerLayout.DrawerListener {

    private val actionBarDrawerToggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar,
        startDrawerOpenContDescRes, startDrawerCloseContDescRes)
    private val endDrawerToggle = EndDrawerToggle(drawerLayout, toolbar, drawable, endDrawerOpenContDescRes,
        endDrawerCloseContDescRes)

    fun syncState() {
        actionBarDrawerToggle.syncState()
        endDrawerToggle.syncState()
    }

    fun onConfigurationChanged(newConfig : Configuration) {
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
        val drawerArrowDrawable = DrawerArrowDrawable(toolbar.context)
        actionBarDrawerToggle.drawerArrowDrawable = drawerArrowDrawable

        endDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        if(isStartDrawerView(drawerView, drawerLayout.layoutDirection)) {
            actionBarDrawerToggle.onDrawerSlide(drawerView, slideOffset)
        }
        else {
            endDrawerToggle.onDrawerSlide(drawerView, slideOffset)
        }
    }

    override fun onDrawerOpened(drawerView: View) {
        if (isStartDrawerView(drawerView, drawerLayout.layoutDirection)) {
            actionBarDrawerToggle.onDrawerOpened(drawerView)
        } else {
            endDrawerToggle.onDrawerOpened(drawerView)
        }
    }

    override fun onDrawerClosed(drawerView: View) {
        if(isStartDrawerView(drawerView, drawerLayout.layoutDirection)) {
            actionBarDrawerToggle.onDrawerClosed(drawerView)
        }
        else {
            endDrawerToggle.onDrawerClosed(drawerView)
        }
    }

    override fun onDrawerStateChanged(newState: Int) {}

    private fun isStartDrawerView(drawerView : View, layoutDirection : Int): Boolean {
        val gravity = DrawerLayout.LayoutParams(drawerView.layoutParams).gravity
        val horizontalGravity = gravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK

        return if((horizontalGravity and GravityCompat.RELATIVE_LAYOUT_DIRECTION) > 0) {
            horizontalGravity == GravityCompat.START
        } else {
            if(layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                horizontalGravity == Gravity.END
            } else {
                horizontalGravity == Gravity.START
            }
        }
    }

}