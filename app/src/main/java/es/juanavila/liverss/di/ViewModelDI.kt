package es.juanavila.liverss.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import es.juanavila.liverss.presentation.common.list.HeadlinesAdapterViewModel
import es.juanavila.liverss.presentation.main.MainScreenViewModel
import es.juanavila.liverss.presentation.common.FavsSharedViewModel
import es.juanavila.liverss.presentation.favs.FavsViewModel
import es.juanavila.liverss.presentation.headlines.HeadlinesViewModel
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
     abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HeadlinesViewModel::class)
     abstract fun headlinesViewModel(viewModel: HeadlinesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavsViewModel::class)
     abstract fun favsVM(viewModel: FavsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavsSharedViewModel::class)
     abstract fun favsSharedVM(viewModel: FavsSharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainScreenViewModel::class)
    abstract fun mainScreenVM(viewModel: MainScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HeadlinesAdapterViewModel::class)
    abstract fun headlinesAdapterVM(viewModel: HeadlinesAdapterViewModel): ViewModel
}