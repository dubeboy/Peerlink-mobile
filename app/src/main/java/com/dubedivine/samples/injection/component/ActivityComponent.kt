package com.dubedivine.samples.injection.component

import com.dubedivine.samples.features.addQuestion.AddQuestionActivity
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.detail.DetailActivity
import com.dubedivine.samples.features.main.MainActivity
import com.dubedivine.samples.features.searchResults.SearchActivity
import com.dubedivine.samples.features.signIn.SignIn
import com.dubedivine.samples.features.signIn.SignInMoreDetails
import com.dubedivine.samples.injection.PerActivity
import com.dubedivine.samples.injection.module.ActivityModule
import dagger.Subcomponent

//component glue module with the injection
// makes this live for a component only
@PerActivity
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(detailActivity: DetailActivity)

    fun inject(searchActivity: SearchActivity)

    fun inject(addQuestionActivity: AddQuestionActivity)

    fun inject(signIn: SignIn)

    fun inject(signIn: SignInMoreDetails)
}
