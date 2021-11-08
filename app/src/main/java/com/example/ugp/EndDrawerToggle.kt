package com.example.ugp

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import kotlin.math.max
import kotlin.math.min


class EndDrawerToggle(
    private val drawerLayout : DrawerLayout,
    toolbar : Toolbar,
    private val drawable : Drawable,
    private val openDrawerContentDescRes : Int,
    private val closeDrawerContentDescRes : Int
) : DrawerLayout.DrawerListener {

    private var toggleButton : AppCompatImageButton = AppCompatImageButton(toolbar.context, null,
        R.attr.toolbarNavigationButtonStyle)

    private lateinit var arrowDrawable : DrawerArrowDrawable

    init {

        toolbar.addView(toggleButton, Toolbar.LayoutParams(GravityCompat.END))
        toggleButton.setOnClickListener {
            toggle()
        }

        loadDrawerArrowDrawable(drawable)
    }

    fun syncState() {

        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            setPosition(1f)
        } else {
            setPosition(0f)
        }

    }

    fun onConfigurationChanged(newConfig: Configuration) {
        loadDrawerArrowDrawable(drawable)
        syncState()
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        setPosition(min(1f, max(0f, slideOffset)))
    }

    override fun onDrawerOpened(drawerView: View) {
        setPosition(1f)
    }

    override fun onDrawerClosed(drawerView: View) {
        setPosition(0f)
    }

    override fun onDrawerStateChanged(newState: Int) {}

    private fun loadDrawerArrowDrawable(drawable : Drawable) {

        arrowDrawable = DrawerArrowDrawable(toggleButton.context)
        arrowDrawable.direction = DrawerArrowDrawable.ARROW_DIRECTION_END
        toggleButton.setImageDrawable(drawable)

    }

    private fun toggle() {

        val drawerLockMode = drawerLayout.getDrawerLockMode(GravityCompat.END)
        if((drawerLayout.isDrawerVisible(GravityCompat.END)) &&
            (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        else if(drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            drawerLayout.openDrawer(GravityCompat.END)
        }

    }

    private fun setPosition(position : Float) {

        if(position == 1f) {
            arrowDrawable.setVerticalMirror(true)
            setContentDescription(closeDrawerContentDescRes)
        }
        else if(position == 0f) {
            arrowDrawable.setVerticalMirror(false)
            setContentDescription(openDrawerContentDescRes)
        }
        arrowDrawable.progress = position

    }

    private fun setContentDescription(resId : Int) {
        toggleButton.contentDescription = toggleButton.context.getText(resId)
    }
}