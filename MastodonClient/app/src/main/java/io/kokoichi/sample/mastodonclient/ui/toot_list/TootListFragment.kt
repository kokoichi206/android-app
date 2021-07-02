package io.kokoichi.sample.mastodonclient.ui.toot_list

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
//import androidx.lifecycle.MutableLiveData
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.kokoichi.sample.mastodonclient.BuildConfig
import io.kokoichi.sample.mastodonclient.R
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.kokoichi.sample.mastodonclient.databinding.FragmentTootListBinding
import io.kokoichi.sample.mastodonclient.entity.Account
import io.kokoichi.sample.mastodonclient.entity.Toot
import io.kokoichi.sample.mastodonclient.ui.toot_detail.TootDetailFragment

//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.cancel
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory

class TootListFragment : Fragment(R.layout.fragment_toot_list),
    TootListAdapter.Callback {

    companion object {
        val TAG = TootListFragment::class.java.simpleName

//        private const val API_BASE_URL = "https://androidbook2020.keiji.io"
    }

    private var binding: FragmentTootListBinding? = null
//
//    private val moshi = Moshi.Builder()
//        .add(KotlinJsonAdapterFactory())
//        .build()
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(API_BASE_URL)
//        .addConverterFactory(MoshiConverterFactory.create(moshi))
//        .build()
//    private val api = retrofit.create(MastodonApi::class.java)

//    private val tootRepository = TootRepository(API_BASE_URL)

//    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var adapter: TootListAdapter
    private lateinit var layoutManager: LinearLayoutManager

//    private var isLoading = AtomicBoolean()
//    private var isLoading = MutableLiveData<Boolean>()
//    private var hasNext = AtomicBoolean().apply { set(true) }

    private val viewModel: TootListViewModel by viewModels {
        TootListViewModelFactory(
            BuildConfig.INSTANCE_URL,
            BuildConfig.USERNAME,
            lifecycleScope,
            requireContext()
        )
    }

    private val loadNextScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

//            val isLoadingSnapshot = isLoading.value ?: return
//            if (isLoadingSnapshot || !hasNext.get()) {
            val isLoadingSnapshot = viewModel.isLoading.value ?: return
            if (isLoadingSnapshot || !viewModel.hasNext) {
                return
            }

            val visibleItemCount = recyclerView.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if ((totalItemCount - visibleItemCount) <=
                firstVisibleItemPosition
            ) {
//                loadNext()
                viewModel.loadNext()
            }
        }
    }

//    private val tootList = ArrayList<Toot>()
//    private val tootList = MutableLiveData<ArrayList<Toot>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val tootListSnapshot = tootList.value ?: ArrayList<Toot>().also {
//            tootList.value = it
//        }
        var tootListSnapshot = viewModel.tootList.value ?: ArrayList<Toot>().also {
            viewModel.tootList.value = it
        }

        adapter = TootListAdapter(layoutInflater, tootListSnapshot,this)
        layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        val bindingData: FragmentTootListBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return

        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.addOnScrollListener(loadNextScrollListener)
        }
        bindingData.swipeRefreshLayout.setOnRefreshListener {
//            tootListSnapshot.clear()
            viewModel.clear()
//            loadNext()
            viewModel.loadNext()
        }
//        isLoading.observe(viewLifecycleOwner, Observer {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding?.swipeRefreshLayout?.isRefreshing = it
        })
        viewModel.accountInfo.observe(viewLifecycleOwner, Observer {
            showAccountInfo(it)
        })
//        tootList.observe(viewLifecycleOwner, Observer {
        viewModel.tootList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun showAccountInfo(accountInfo: Account) {
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.subtitle = accountInfo.username
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

//    private suspend fun showProgress() = withContext(Dispatchers.Main) {
//        binding?.swipeRefreshLayout?.isRefreshing = true
//    }
//
//    private suspend fun dismissProgress() = withContext(Dispatchers.Main) {
//        binding?.swipeRefreshLayout?.isRefreshing = false
//    }

    override fun openDetail(toot: Toot) {
        var fragment = TootDetailFragment.newInstance(toot)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(TootDetailFragment.TAG)
            .commit()
    }
}