package com.dubedivine.samples

import com.dubedivine.samples.common.TestDataFactory
import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.features.main.MainMvpView
import com.dubedivine.samples.features.main.MainPresenter
import com.dubedivine.samples.util.RxSchedulersOverrideRule
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

/**
 * Created by ravindra on 24/12/16.
 */
@RunWith(MockitoJUnitRunner::class)
class MainPresenterTest {

    @Mock lateinit var mMockMainMvpView: MainMvpView
    @Mock lateinit var mMockDataManager: DataManager
    @Inject lateinit var mPreferencesHelper: PreferencesHelper

    private var mMainPresenter: MainPresenter? = null

    @JvmField
    @Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun setUp() {
        mMainPresenter = MainPresenter(mMockDataManager, mPreferencesHelper)
        mMainPresenter?.attachView(mMockMainMvpView)
    }

    @After
    fun tearDown() {
        mMainPresenter?.detachView()
    }

    @Test
    @Throws(Exception::class)
    fun getPokemonReturnsPokemonNames() {
        val pokemonList = TestDataFactory.makePokemonNamesList(10)
        `when`(mMockDataManager.getPokemonList(10))
                .thenReturn(Single.just(pokemonList))

        mMainPresenter?.getPokemon(10)

        verify<MainMvpView>(mMockMainMvpView, times(2)).showProgress(anyBoolean())
        verify<MainMvpView>(mMockMainMvpView).showPokemon(pokemonList)
        verify<MainMvpView>(mMockMainMvpView, never()).showError(RuntimeException())

    }

    @Test
    @Throws(Exception::class)
    fun getPokemonReturnsError() {
        `when`(mMockDataManager.getPokemonList(10))
                .thenReturn(Single.error<List<String>>(RuntimeException()))

        mMainPresenter?.getPokemon(10)

        verify<MainMvpView>(mMockMainMvpView, times(2)).showProgress(anyBoolean())
//        verify<MainMvpView>(mMockMainMvpView).showUserError(RuntimeException())
        verify<MainMvpView>(mMockMainMvpView, never()).showPokemon(ArgumentMatchers.anyList<String>())
    }

}