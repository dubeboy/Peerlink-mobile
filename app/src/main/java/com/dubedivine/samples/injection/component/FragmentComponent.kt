package com.dubedivine.samples.injection.component

import com.dubedivine.samples.features.detail.dialog.ShowVideoFragment
import com.dubedivine.samples.features.main.fragment.subscribe.TagsSubscribedFragment
import com.dubedivine.samples.injection.PerFragment
import com.dubedivine.samples.injection.module.FragmentModule
import dagger.Subcomponent

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = [(FragmentModule::class)])
interface FragmentComponent {

    fun inject(showVideoFragment: ShowVideoFragment)

    fun inject(tagsSubscribedFragment: TagsSubscribedFragment)

}