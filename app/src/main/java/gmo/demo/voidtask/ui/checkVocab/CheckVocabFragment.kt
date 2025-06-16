package gmo.demo.voidtask.ui.checkVocab

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.FragmentCheckVocabBinding
import gmo.demo.voidtask.ui.base.BaseFragment
import gmo.demo.voidtask.BR
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 101

class CheckVocabFragment : BaseFragment<FragmentCheckVocabBinding, CheckVocabViewModel>(), KodeinAware {
    override val kodein by kodein()

    private val factory: CheckVocabViewModelFactory by instance()

    override val layoutId: Int
        get() = R.layout.fragment_check_vocab
    override val bindingVariable: Int
        get() = BR.viewModel
    override val viewModel: CheckVocabViewModel by lazy {
        ViewModelProvider(this, factory)[CheckVocabViewModel::class.java]
    }

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo speech recognizer
        viewModel.setupSpeechRecognizer(requireContext())

        // Tải dữ liệu từ file
        viewModel.loadVocabFromFile()

        setupUIListeners()
        setupObservers()
    }

    private fun setupUIListeners() {
        // Xử lý sự kiện lật thẻ
        mViewDataBinding?.cardVocab?.setOnClickListener {
            viewModel.flipCard()
        }

        // Xử lý sự kiện nút mic
        mViewDataBinding?.btnMic?.setOnClickListener {
            if (checkAudioPermission()) {
                toggleRecording()
            } else {
                requestAudioPermission()
            }
        }

        // Xử lý sự kiện nút next/previous
        mViewDataBinding?.btnNext?.setOnClickListener {
            viewModel.showNextCard()
        }

        mViewDataBinding?.btnPrevious?.setOnClickListener {
            viewModel.showPreviousCard()
        }
    }

    private fun setupObservers() {
        // Quan sát trạng thái lật thẻ
        viewModel.isCardFlipped.observe(viewLifecycleOwner) { isFlipped ->
            mViewDataBinding?.tvFrontText?.visibility = if (isFlipped) View.GONE else View.VISIBLE
            mViewDataBinding?.tvBackText?.visibility = if (isFlipped) View.VISIBLE else View.GONE
        }

        // Quan sát trạng thái ghi âm
        viewModel.isRecording.observe(viewLifecycleOwner) { isRecording ->
            updateMicButtonState(isRecording)
            updateRecordingUI(isRecording)
        }

        // Quan sát trạng thái animation
        viewModel.showWaveAnimation.observe(viewLifecycleOwner) { showAnimation ->
            mViewDataBinding?.ivWaveAnimation?.apply {
                visibility = if (showAnimation) View.VISIBLE else View.GONE
                if (showAnimation) {
                    (drawable as? android.graphics.drawable.AnimationDrawable)?.start()
                } else {
                    (drawable as? android.graphics.drawable.AnimationDrawable)?.stop()
                }
            }
        }

        // Quan sát thời gian ghi âm
        viewModel.recordingTime.observe(viewLifecycleOwner) { time ->
            mViewDataBinding?.tvRecordingTime?.text = time
        }

        // Quan sát kết quả nhận diện giọng nói
        viewModel.speechStatus.observe(viewLifecycleOwner) { status ->
            mViewDataBinding?.tvSpeechStatus?.text = status
        }
    }

    private fun updateMicButtonState(isRecording: Boolean) {
        mViewDataBinding?.btnMic?.apply {
            setImageResource(
                if (isRecording) R.drawable.ic_mic_off else R.drawable.ic_mic
            )
            animate().scaleX(if (isRecording) 1.2f else 1f)
                .scaleY(if (isRecording) 1.2f else 1f)
                .setDuration(300)
                .start()
        }
    }

    private fun updateRecordingUI(isRecording: Boolean) {
        mViewDataBinding?.tvRecordingTime?.visibility =
            if (isRecording) View.VISIBLE else View.GONE
    }

    private fun toggleRecording() {
        if (viewModel.isRecording.value == true) {
            viewModel.stopRecording()
        } else {
            viewModel.startRecording(requireContext())
        }
    }

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.startRecording(requireContext())
            } else {
                viewModel.speechStatus.postValue(getString(R.string.permission_required))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopRecording()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CheckVocabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}