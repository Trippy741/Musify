package com.example.loginpage

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview

class CarouselRecyclerView {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun Bind(adapter : Profile_RecyclerViewAdapter, carouselRecyclerview : CarouselRecyclerview)
    {
        carouselRecyclerview.adapter = adapter
        carouselRecyclerview.set3DItem(false)
        carouselRecyclerview.setInfinite(true)
        carouselRecyclerview.setAlpha(true)
        carouselRecyclerview.setFlat(false)
        carouselRecyclerview.setIsScrollingEnabled(true)

        carouselRecyclerview.getSelectedPosition()

        carouselRecyclerview.setItemSelectListener(object : CarouselLayoutManager.OnSelected{
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onItemSelected(position: Int) {
                adapter.focusImg(position)
                adapter.vibrate()
            }
        })
    }
}