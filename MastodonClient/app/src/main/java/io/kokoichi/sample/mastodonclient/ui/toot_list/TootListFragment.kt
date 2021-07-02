package io.kokoichi.sample.mastodonclient.ui.toot_list

import android.app.Activity
import android.content.Intent
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
import io.kokoichi.sample.mastodonclient.ui.toot_list.TootListViewModelFactory
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.kokoichi.sample.mastodonclient.databinding.FragmentTootListBinding
import io.kokoichi.sample.mastodonclient.entity.Account
import io.kokoichi.sample.mastodonclient.entity.Toot
import io.kokoichi.sample.mastodonclient.ui.login.LoginActivity
import io.kokoichi.sample.mastodonclient.ui.toot_detail.TootDetailActivity
import io.kokoichi.sample.mastodonclient.ui.toot_edit.TootEditActivity

//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.cancel
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory

class TootListFragment : Fragment(R.layout.fragment_toot_list),
    TootListAdapter.Callback {

    companion object {
        val TAG = TootListFragment::class.java.simpleName

        private const val REQUEST_CODE_TOOT_EDIT = 0x01
        private const val REQUEST_CODE_LOGIN = 0x02

        private const val BUNDLE_KEY_TIMELINE_TYPE_ORDINAL = "timeline_type_ordinal"

        @JvmStatic
        fun newInstance(timelineType: TimelineType): TootListFragment {
            val args = Bundle().apply {
                putInt(BUNDLE_KEY_TIMELINE_TYPE_ORDINAL, timelineType.ordinal)
            }
            return TootListFragment().apply {
                arguments = args
            }
        }

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

    private var timelineType = TimelineType.PublicTimeline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().also {
            val typeOrdinal = it.getInt(
                BUNDLE_KEY_TIMELINE_TYPE_ORDINAL,
                TimelineType.PublicTimeline.ordinal
            )
            timelineType = TimelineType.values()[typeOrdinal]
        }
    }

//    private var isLoading = AtomicBoolean()
//    private var isLoading = MutableLiveData<Boolean>()
//    private var hasNext = AtomicBoolean().apply { set(true) }

    private val viewModel: TootListViewModel by viewModels {
        TootListViewModelFactory(
            BuildConfig.INSTANCE_URL,
            BuildConfig.USERNAME,
            timelineType,
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
            viewModel.clear()
            viewModel.loadNext()
        }
        bindingData.fab.setOnClickListener {
            launchTootEditActivity()
        }
        viewModel.loginRequired.observe(viewLifecycleOwner, Observer {
            if (it) {
                launchLoginActivity()
            }
        })
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

    private fun launchLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_LOGIN)
    }

    private fun launchTootEditActivity() {
        val intent = TootEditActivity.newIntent(requireContext())
        startActivityForResult(intent, REQUEST_CODE_TOOT_EDIT)
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
        val intent = TootDetailActivity.newIntent(requireContext(), toot)
        startActivity(intent)
    }

    override fun delete(toot: Toot) {
        viewModel.delete(toot)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_TOOT_EDIT
            && resultCode == Activity.RESULT_OK) {
            viewModel.clear()
            viewModel.loadNext()
        }
    }
}