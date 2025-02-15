package com.bu.selfstudy.ui.recentword

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.bu.selfstudy.ui.main.MainActivity
import com.bu.selfstudy.NavGraphDirections
import com.bu.selfstudy.R
import com.bu.selfstudy.data.model.RecentWord
import com.bu.selfstudy.databinding.FragmentRecentWordBinding
import com.bu.selfstudy.tool.*


class RecentWordFragment : Fragment() {
    private val viewModel: RecentWordViewModel by viewModels()
    private val binding: FragmentRecentWordBinding by viewBinding()
    private val listAdapter = RecentWordAdapter(fragment = this)


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        binding.recyclerView.let {
            it.adapter = listAdapter
            it.setHasFixedSize(true)
        }

        return binding.root
    }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

         viewModel.recentWordLiveData.observe(viewLifecycleOwner){
             binding.recentNotFound.isVisible = it.isEmpty()

             listAdapter.submitList(it)
         }



         binding.toolbar.setOnClickListener {
             findNavController().navigate(R.id.searchFragment)
         }

         initSpeedDial()

     }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        (activity as MainActivity).let {
            it.setSupportActionBar(binding.toolbar)

            NavigationUI.setupActionBarWithNavController(
                it, findNavController(), it.appBarConfiguration)
        }

    }
    private fun initSpeedDial() {
        with(binding.speedDialButton){
            createChildButtonAndText(
                R.id.book_fragment_fab_add_word,
                R.drawable.ic_baseline_search_24,
                "新增單字"
            ){ button, textView ->
                findNavController().navigate(R.id.searchFragment)
            }
            createChildButtonAndText(
                R.id.book_fragment_fab_add_book,
                R.drawable.ic_baseline_bookmark_24,
                "新增題庫"
            ){ button, textView ->
                findNavController().navigate(R.id.addBookFragment)
            }
            mainButton.setOnLongClickListener {
                resources.getString(R.string.FAB_main).showToast()
                true
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this){
            if(binding.speedDialButton.mainButtonIsOpen){
                binding.speedDialButton.toggleChange(true)
                return@addCallback
            }

            findNavController().popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //自動連結與item id相同的fragment id
        return item.onNavDestinationSelected(findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.book_toolbar, menu)
    }



    fun navigateWordCardFragment(recentWord: RecentWord) {
        findNavController().navigate(
                NavGraphDirections.actionGlobalWordFragment(
                        bookId = recentWord.bookId,
                        wordId = recentWord.wordId
                )
        )
    }

    fun refreshRecentWord(recentWord: RecentWord) {
        viewModel.refreshRecentWord(recentWord)
    }
}

