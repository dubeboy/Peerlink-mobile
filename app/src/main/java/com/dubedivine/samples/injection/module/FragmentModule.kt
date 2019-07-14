package com.dubedivine.samples.injection.module

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.dubedivine.samples.features.main.fragment.subscribe.TagsSubscribedAdapter
import com.dubedivine.samples.injection.ActivityContext
import dagger.Module
import dagger.Provides

@Module
class FragmentModule(private val mFragment: Fragment) {

    @Provides
    internal fun providesFragment(): Fragment {
        return mFragment
    }

    @Provides
    internal fun provideActivity(): Activity {
        return mFragment.activity!!
    }

    @Provides
    @ActivityContext
    internal fun providesContext(): Context {
        return mFragment.activity!!
    }

    // todo: this should be gone
    @Provides
    internal fun providesFragmentAdapter(): TagsSubscribedAdapter {
        return TagsSubscribedAdapter()
    }
}