package com.dubedivine.samples.injection.component

import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.injection.module.ActivityModule
import com.dubedivine.samples.injection.module.FragmentModule
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.base.BaseFragment
import dagger.Component

/**
 * A dagger component that will live during the lifecycle of an Activity or Fragment but it won't
 * be destroy during configuration changes. Check [BaseActivity] and [BaseFragment] to
 * see how this components survives configuration changes.
 * Use the [ConfigPersistent] scope to annotate dependencies that need to survive
 * configuration changes (for example Presenters).
 */
@ConfigPersistent
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface ConfigPersistentComponent {

    fun activityComponent(activityModule: ActivityModule): ActivityComponent

    fun fragmentComponent(fragmentModule: FragmentModule): FragmentComponent

}
