package es.juanavila.liverss.presentation.common.list

import android.graphics.Color
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import es.juanavila.liverss.R
import es.juanavila.liverss.application.Page
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.framework.App
import javax.inject.Inject


class HeadlinesAdapter : ListAdapter<ListElement<Headline>, RecyclerView.ViewHolder> {

    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val adapterViewModel : HeadlinesAdapterViewModel
    private val listener : Listener

    constructor(act : FragmentActivity, lifecycleOwner: LifecycleOwner = act,
                listener : Listener
    ) : super(DiffCallback()) {
        this.listener = listener
        adapterViewModel = ViewModelProviders.of(act,viewModelFactory).get(HeadlinesAdapterViewModel::class.java)

        adapterViewModel.adapterEvents.observe(lifecycleOwner, Observer {
            println("Observer newlistevents triggered: $it")
            when (it) {
                is LoadMore -> listener.onLoadMore(it.cursor,it.sinceId)
                is Clicked -> listener.onItemClicked(it.item)
                is Liked -> listener.onLikeClick(it.item)
                null -> TODO()
                is Disliked -> listener.onDislikeClick(it.item)
            }
        })

        adapterViewModel.listVisObs.observe(lifecycleOwner, Observer {
            println("Observer newlistevents triggered: $it")
            when (it) {
                is Empty -> listener.onEmpty()
                is Filled -> listener.onFilled()
            }
        })

        adapterViewModel.listStateObs.observe(lifecycleOwner, object : Observer<ListState> {
            override fun onChanged(it: ListState?) {
                println("Observer ${this.hashCode()} newlistevents triggered: $it")
                update(it!!)
            }
        })
    }

    constructor(act : Fragment, lifecycleOwner: LifecycleOwner = act.viewLifecycleOwner,listener : Listener) : super(
        DiffCallback()
    ) {
        this.listener = listener

        App.appComponent.inject(this)

        adapterViewModel = ViewModelProviders.of(act,viewModelFactory).get(HeadlinesAdapterViewModel::class.java)

        adapterViewModel.adapterEvents.observe(lifecycleOwner, Observer {
            println("Observer newlistevents triggered: $it")
            when (it) {
                is LoadMore -> listener.onLoadMore(it.cursor,it.sinceId)
                is Clicked -> listener.onItemClicked(it.item)
                is Liked -> listener.onLikeClick(it.item)
                null -> TODO()
                is Disliked -> listener.onDislikeClick(it.item)
            }!!
        })

        adapterViewModel.listVisObs.observe(lifecycleOwner, Observer {
            println("Observer newlistevents triggered: $it")
            when (it) {
                is Empty -> listener.onEmpty()
                is Filled -> listener.onFilled()
            }
        })

        adapterViewModel.listStateObs.observe(lifecycleOwner, object : Observer<ListState> {
            override fun onChanged(it: ListState?) {
                println("Observer ${this.hashCode()} newlistevents triggered: $it")
                update(it!!)
            }
        })
    }

    interface Listener {
        fun onEmpty()
        fun onFilled()
        fun onScrollTop()
        fun onItemClicked(item : Headline)
        fun onLikeClick(item : Headline)
        fun onDislikeClick(item : Headline)
        fun onLoadMore(cursor: String = "", sinceId: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.headline_item, parent, false)
            HeadlineVH(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.placeholder_headline_item, parent, false)
            PlaceholderHeadlineVH(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] is Item) 1 else 2
    }

    fun first(): Headline? {
        val elem =  currentList.firstOrNull()
        return if(elem is Item) elem.item else null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val elem = currentList[position]
        if(holder is HeadlineVH && elem is Item) {
            val item = elem.item
            holder.author.text = item.author
            holder.author.text = "${item.id} | Pos: $position"
            holder.headline.text = item.headline

            Glide.with(holder.itemView.context)
                .load(item.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image)

            val providerImg = when (item.provider) {
                "EL_PAIS" -> R.drawable.ic_elpais
                "EL_MUNDO" -> R.drawable.ic_elmundo_negativo
                "EL_CONFIDENCIAL" -> R.drawable.ic_confidencial
                "EL_DIARIO" -> R.drawable.ic_el_diario
                "_20_MINUTOS" -> R.drawable.ic_20_minutos_mini
                "AS" -> R.drawable.ic_logo_diario_as
                "MARCA" -> R.drawable.ic_marca
                "EUROPAPRESS" -> R.drawable.ic_logo_europapress
                else -> R.drawable.ic_generic_feed_icon
            }

            holder.itemView.setOnClickListener {
                adapterViewModel.onItemClicked(item)
            }

            holder.timestamp.text = DateUtils.getRelativeTimeSpanString(
                item.timestamp.millis,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL
            )

            holder.provider.setImageDrawable(holder.image.context.getDrawable(providerImg)!!.mutate())

            if (item.liked) {
                holder.like.setColorFilter(Color.RED)
            } else {
                holder.like.setColorFilter(Color.LTGRAY)
            }

            holder.like.setOnClickListener {
                if(item.liked)
                    adapterViewModel.onDislikeClick(item)
                else
                    adapterViewModel.onLikeClick(item)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager
                val lastVisibleItemPosition : Int = when (layoutManager) {
                    is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                    is StaggeredGridLayoutManager -> layoutManager.findLastVisibleItemPositions(null).last()
                    is GridLayoutManager ->  layoutManager.findLastVisibleItemPosition()
                    else -> throw NotImplementedError("Layout manager not contemplated")
                }
                val firstCompletelyVisiblePosition : Int = when (layoutManager) {
                    is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                    is StaggeredGridLayoutManager -> layoutManager.findFirstVisibleItemPositions(null).last()
                    is GridLayoutManager ->  layoutManager.findFirstVisibleItemPosition()
                    else -> throw NotImplementedError("Layout manager not contemplated")
                }
                if(firstCompletelyVisiblePosition == 0)
                    listener.onScrollTop()
                adapterViewModel.onScrolled(lastVisibleItemPosition)
            }
        })
    }

    fun append(page : Page<Headline>) {
        adapterViewModel.append(page)
    }

    fun prepend(page : Page<Headline>) {
        adapterViewModel.prepend(page)
    }

    private fun update(newState: ListState) {
        submitList(newState.items)
    }

    fun update(item: Headline) {
        adapterViewModel.update(item)
    }

    fun removeAll() {
        adapterViewModel.removeAll()
    }

    fun remove(fromId: Long, toId: Long) {
        adapterViewModel.remove(fromId,toId)
    }

    fun remove(item : Headline) {
        adapterViewModel.remove(item)
    }

    fun prepend(item: Headline) {
        adapterViewModel.prepend(item)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ListElement<Headline>>() {

        override fun areContentsTheSame(oldItem: ListElement<Headline>, newItem: ListElement<Headline>): Boolean {
            return if(oldItem is Gap && newItem is Gap) {
                oldItem.beforeCursor == newItem.beforeCursor
            } else if(oldItem is Item && newItem is Item) {
                oldItem.item.liked == newItem.item.liked
            } else
                false
        }

        override fun areItemsTheSame(oldItem: ListElement<Headline>, newItem: ListElement<Headline>): Boolean {
            return if(oldItem is Gap && newItem is Gap) {
                oldItem.beforeCursor == newItem.beforeCursor
            } else if(oldItem is Item && newItem is Item) {
                oldItem.item.id == newItem.item.id
            } else
                false
        }

        override fun getChangePayload(oldItem: ListElement<Headline>, newItem: ListElement<Headline>): Any? {
            return if(oldItem is Item && newItem is Item) {
                return newItem.item.liked
            } else
                null
        }

    }
}

class HeadlineVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val image : ImageView = itemView.findViewById(R.id.image)
    val headline : TextView = itemView.findViewById(R.id.headlineTv)
    val author : TextView = itemView.findViewById(R.id.authorTv)
    val like : ImageView = itemView.findViewById(R.id.likeIv)
    val timestamp : TextView = itemView.findViewById(R.id.timestampTv)
    val provider : ImageView = itemView.findViewById(R.id.providerIv)

}

class PlaceholderHeadlineVH(itemView: View) : RecyclerView.ViewHolder(itemView)

