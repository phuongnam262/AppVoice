package gmo.demo.voidtask.ui.addVocab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.FragmentAddVocabBinding
import android.content.Intent
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import android.util.Log
import gmo.demo.voidtask.ui.base.BaseFragment
import gmo.demo.voidtask.BR

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddVocabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddVocabFragment : BaseFragment<FragmentAddVocabBinding, AddVocabViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override val layoutId: Int
        get() = R.layout.fragment_add_vocab
    override val bindingVariable: Int
        get() = BR.viewModel
    override val viewModel: AddVocabViewModel by lazy {
        ViewModelProvider(this, AddVocabViewModelFactory())[AddVocabViewModel::class.java]
    }

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data
            viewModel.onFileSelected(uri)
        }
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

        mViewDataBinding?.btnAddFolder?.setOnClickListener {
            val intent = mViewModel?.onAddFolderClicked()
            intent?.let { pickFileLauncher.launch(it) }
        }

        mViewModel?.selectedFileUri?.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                Log.d("AddVocabFragment", "Selected file URI: $it")
                // Ở đây bạn có thể thêm logic để đọc nội dung file từ URI
                // Ví dụ:
                // val content = requireContext().contentResolver.openInputStream(it)?.bufferedReader()?.use { reader -> reader.readText() }
                // Log.d("AddVocabFragment", "File content: $content")
            }
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