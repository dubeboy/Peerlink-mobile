package com.dubedivine.samples.common.injection.component

import com.dubedivine.samples.common.injection.module.ApplicationTestModule
import com.dubedivine.samples.injection.component.ApplicationComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationTestModule::class))
interface TestComponent : ApplicationComponent