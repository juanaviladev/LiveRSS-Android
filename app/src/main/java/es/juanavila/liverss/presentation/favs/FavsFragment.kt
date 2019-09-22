package es.juanavila.liverss.presentation.favs


import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import es.juanavila.liverss.R
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.usecases.headlines.AutoCleaned
import es.juanavila.liverss.application.usecases.headlines.DeletedAll
import es.juanavila.liverss.framework.App
import es.juanavila.liverss.presentation.common.*
import es.juanavila.liverss.presentation.main.MainScreenViewModel
import es.juanavila.liverss.presentation.common.list.HeadlinesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class FavsFragment : Fragment(), HeadlinesAdapter.Listener {

    private lateinit var adapter: HeadlinesAdapter
    private lateinit var newsList : RecyclerView
    private lateinit var emptyPlaceholder : Group
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var newsPlaceholder: View
    private lateinit var newNews: Button
    private lateinit var downAnimator : ObjectAnimator
    private lateinit var upAnimator : ObjectAnimator

    private lateinit var favsVM : FavsViewModel
    private lateinit var mainScreenViewModel : MainScreenViewModel
    private lateinit var favsSharedViewModel : FavsSharedViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FavsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Headlines","onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Headlines","onCreateView()")
        return inflater.inflate(R.layout.fragment_favs,container,false)
    }

    override fun onStop() {
        super.onStop()
        Log.d("Headlines","onStop()")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Headlines","onDetach()")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Headlines","onViewCreated()")

        newsList = view.findViewById(R.id.favsRv)
        newNews = view.findViewById(R.id.newNewsBt)
        swipeRefreshLayout = view.findViewById(R.id.swipeSrl)
        newsPlaceholder = view.findViewById(R.id.newsPlaceholder)
        emptyPlaceholder = view.findViewById(R.id.emptyPlaceholder)

        downAnimator = ObjectAnimator.ofFloat(newNews,"y",-120F,30F)
        upAnimator = ObjectAnimator.ofFloat(newNews,"y",30F,-120F)
        downAnimator.doOnStart {
            newNews.visibility = View.VISIBLE
        }
        downAnimator.duration = 500
        downAnimator.interpolator = OvershootInterpolator()

        upAnimator.doOnEnd {
            newNews.visibility = View.GONE
        }
        upAnimator.duration = 500
        upAnimator.interpolator = AccelerateDecelerateInterpolator()
        //   filterTags.setChipListener(this)

        newsList.layoutManager = LinearLayoutManager(requireContext())

        activity?.toolbar?.apply {
            findViewById<TextView>(R.id.textView)?.text = "Favorites"
            findViewById<ImageView>(R.id.imageView)?.setImageResource(R.drawable.ic_favorite_black_24dp)
        }
    }

    private fun viewStateObserver(v : View) : Observer<ViewState> = Observer {
        when(it!!){
            Visible -> v.visibility = View.VISIBLE
            Hidden -> v.visibility = View.GONE
        }
    }

    private fun viewAnimationEventObserver(v : Group) : Observer<ViewAnimation?> = Observer {
        val views = v.referencedIds.map { id -> view!!.findViewById<View>(id) } + v
        when(it!!){
            FadeIn -> views.forEach { view -> view.fadeIn() }
            FadeOut -> views.forEach { view -> view.fadeOut() }
        }
    }

    private fun viewAnimationEventObserver(v : View) : Observer<ViewAnimation?> = Observer {
        when(it!!){
            FadeIn -> v.fadeIn()
            FadeOut -> v.fadeOut()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        App.appComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("Headlines","onActivityCreated()")

        favsVM = ViewModelProviders.of(this,viewModelFactory).get(FavsViewModel::class.java)
        adapter =
            HeadlinesAdapter(this, listener = this)
        newsList.adapter = adapter

        mainScreenViewModel = ViewModelProviders.of(requireActivity(),viewModelFactory).get(
            MainScreenViewModel::class.java)
        favsSharedViewModel = ViewModelProviders.of(requireActivity(),viewModelFactory).get(
            FavsSharedViewModel::class.java)

        /*mainScreenViewModel.bottomMenuEvents.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Reselected -> if(it.tag == FavsFragment::class.java.simpleName) favsVM.onReselected()
            }
        })*/

        favsVM.emptyPlaceholderViewState.observe(viewLifecycleOwner, viewStateObserver(emptyPlaceholder))
        favsVM.newsPlaceholderViewState.observe(viewLifecycleOwner, viewStateObserver(newsPlaceholder))
        favsVM.newsListViewState.observe(viewLifecycleOwner, viewStateObserver(swipeRefreshLayout))

        favsVM.emptyPlaceholderViewAnimationEvent.observe(viewLifecycleOwner,viewAnimationEventObserver(emptyPlaceholder))
        favsVM.newsListViewAnimationEvent.observe(viewLifecycleOwner,viewAnimationEventObserver(swipeRefreshLayout))
        favsVM.newsPlaceholderViewAnimationEvent.observe(viewLifecycleOwner,viewAnimationEventObserver(newsPlaceholder))

        favsVM.newsListScrollEvent.observe(viewLifecycleOwner, Observer {
            println("Observer newsListScrollEvent triggered: $it")
            when (it) {
                is ScrollTo -> newsList.scrollToPosition(it.pos)
            }
        })

        favsVM.navigationState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is GoToScreen -> {
                    val intent = Intent(activity, it.screenClazz)
                    intent.replaceExtras(it.data)
                    startActivity(intent)
                }
            }
        })

        favsVM.listClientEvent.observe(viewLifecycleOwner, Observer {
            when(it){
                is PrependPage -> adapter.prepend(it.page)
                is AppendPage -> adapter.append(it.page)
                is RemoveRange -> adapter.remove(it.fromId,it.toId)
                is Purge -> adapter.removeAll()
                is Prepend -> adapter.prepend(it.item)
                is Remove ->  adapter.remove(it.item)
                null -> TODO()
            }!!
        })

        favsSharedViewModel.likesListEvents.observe(viewLifecycleOwner, Observer {
            println("main events triggered (favs) $it")
            when(it) {
                is FavAction.Liked -> favsVM.onLikeClick(it.item)
                is FavAction.Disliked -> favsVM.onDislikeClick(it.item)
            }
        })

        favsVM.appEventsFavs.observe(viewLifecycleOwner, Observer {
            println("(favs) triggered app event $it")
            when(it){
                is AutoCleaned -> favsVM.onAutoClean(it.fromId,it.toId)
                is DeletedAll -> favsVM.onDeletedAll()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Headlines","onDestroy()")
    }

    override fun onEmpty() {
        favsVM.onListEmpty()
    }

    override fun onFilled() {
        favsVM.onListFilled()
    }

    override fun onItemClicked(item: Headline) {
        favsVM.onItemClicked(item)
    }

    override fun onLikeClick(item: Headline) {
        favsSharedViewModel.onLikeClick(item)
    }

    override fun onLoadMore(cursor: String, sinceId: Long) {
        favsVM.onLoadMore(cursor,sinceId)
    }
    override fun onScrollTop() {
       mainScreenViewModel.scrolledToTop.call()
    }

    override fun onDislikeClick(item: Headline) {
        favsSharedViewModel.onDislikeClick(item)
    }

}
