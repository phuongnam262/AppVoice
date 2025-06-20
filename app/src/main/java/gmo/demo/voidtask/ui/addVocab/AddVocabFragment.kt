package gmo.demo.voidtask.ui.addVocab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.FragmentAddVocabBinding
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import gmo.demo.voidtask.ui.base.BaseFragment
import gmo.demo.voidtask.BR
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddVocabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddVocabFragment : BaseFragment<FragmentAddVocabBinding, AddVocabViewModel>(), KodeinAware {
    override val kodein by kodein()
    private val factory: AddVocabViewModelFactory by instance()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override val layoutId: Int
        get() = R.layout.fragment_add_vocab
    override val bindingVariable: Int
        get() = BR.viewModel
    override val viewModel: AddVocabViewModel by lazy {
        ViewModelProvider(this, factory)[AddVocabViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gọi API lấy vocab khi vào màn hình
        mViewModel?.fetchVocabFromApiAndSave("KienTT-1836")


        mViewDataBinding?.btnAddFolder?.setOnClickListener {
            Toast.makeText(requireContext(), "Dữ liệu API đã được nạp vào", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddVocabFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddVocabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}