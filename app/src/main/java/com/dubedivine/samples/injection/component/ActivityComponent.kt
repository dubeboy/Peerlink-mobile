package com.dubedivine.samples.injection.component

import com.dubedivine.samples.injection.PerActivity
import com.dubedivine.samples.injection.module.ActivityModule
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.detail.DetailActivity
import com.dubedivine.samples.features.main.MainActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(detailActivity: DetailActivity)
}
