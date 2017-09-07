package com.dubedivine.samples.injection.component

import com.dubedivine.samples.injection.PerFragment
import com.dubedivine.samples.injection.module.FragmentModule
import dagger.Subcomponent

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent