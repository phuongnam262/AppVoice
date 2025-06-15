package gmo.demo.voidtask.ui.addtask

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.BR
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.FragmentAddTaskBinding
import gmo.demo.voidtask.ui.base.BaseFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AddTaskFragment : BaseFragment<FragmentAddTaskBinding, AddTaskViewModel>(), KodeinAware{

    override val kodein by kodein()
    private val factory: AddTaskViewModelFactory by instance()
    override val layoutId: Int = R.layout.fragment_add_task
    override val bindingVariable: Int
        get() = BR.viewModel

    override val viewModel: AddTaskViewModel
        get() = ViewModelProvider(this, factory)[AddTaskViewModel::class.java]

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun initView(){
    }

    private fun initData(){
    }
}