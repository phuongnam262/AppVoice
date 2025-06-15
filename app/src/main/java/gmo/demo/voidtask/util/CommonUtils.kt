package gmo.demo.voidtask.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import gmo.demo.voidtask.R

object CommonUtils {

    private var isDialogShowing = false

    /**
     * Showing dialog with message and OK button
     */
    fun showDialog(
        context: Context?,
        title: String = "",
        message: String,
        theme: Int = androidx.appcompat.R.style.Base_AlertDialog_AppCompat_Light
    ) {
        if (message.isEmpty() || isDialogShowing) return
        context?.let {
            isDialogShowing = true
            MaterialAlertDialogBuilder(it, theme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                    it.resources.getString(R.string.close)
                ) { dialog, _ ->
                    dialog.dismiss()
                    isDialogShowing = false
                }
                .setOnDismissListener {
                    isDialogShowing = false
                }
                .show()
        }
    }

    /**
     * Delay to run process
     */
    fun runTimeDelay(timeMoveDelay: Long, process: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            process()
        }, timeMoveDelay)
    }

}