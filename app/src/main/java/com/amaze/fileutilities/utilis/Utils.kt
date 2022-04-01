/*
 * Copyright (C) 2021-2022 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.utilis

import android.app.Activity
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.net.wifi.WifiManager
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.amaze.fileutilities.BuildConfig
import com.amaze.fileutilities.R
import com.amaze.fileutilities.home_page.database.PathPreferences
import com.amaze.fileutilities.home_page.ui.analyse.ReviewAnalysisAdapter
import com.amaze.fileutilities.home_page.ui.files.MediaFileAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

class Utils {

    companion object {
        var log: Logger = LoggerFactory.getLogger(Utils::class.java)

        const val URL_PRIVACY_POLICY = "https://www.teamamaze.xyz/privacy-policy"
        const val AMAZE_FILE_MANAGER_MAIN = "com.amaze.filemanager.ui.activities.MainActivity"
        const val AMAZE_PACKAGE = "com.amaze.filemanager"

        private const val EMAIL_EMMANUEL = "emmanuelbendavid@gmail.com"
        private const val EMAIL_RAYMOND = "airwave209gt@gmail.com"
        private const val EMAIL_VISHAL = "vishalmeham2@gmail.com"
        private const val URL_TELEGRAM = "https://t.me/AmazeFileManager"

        const val EMAIL_NOREPLY_REPORTS = "no-reply@teamamaze.xyz"
        const val EMAIL_SUPPORT = "support@teamamaze.xyz"

        /**
         * Open url in browser
         *
         * @param url given url
         */
        fun openURL(url: String?, context: Context) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                log.warn("cannot open url activity not found", e)
            }
        }

        fun openActivity(context: Context, packageName: String, className: String) {
            try {
                val intent = Intent()
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.setClassName(packageName, className)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
//                context.showToastOnBottom(context.resources.getString(R.string.install_amaze))
                showAmazeFileManagerDialog(context, packageName)
            }
        }

        fun Context.showProcessingDialog(
            layoutInflater: LayoutInflater,
            message: String
        ): AlertDialog.Builder {
            val dialogBuilder = AlertDialog.Builder(this).setCancelable(false)
            val dialogView: View = layoutInflater.inflate(R.layout.please_wait_dialog, null)
            val textView = dialogView.findViewById<TextView>(R.id.please_wait_text)
            textView.text = message
            dialogBuilder.setView(dialogView)
            return dialogBuilder
        }

        private fun showAmazeFileManagerDialog(context: Context, packageName: String) {
            val dialog = AlertDialog.Builder(
                context,
                R.style.Custom_Dialog_Dark
            ).setTitle(R.string.amaze_file_manager)
                .setPositiveButton(R.string.download) { dialog, _ ->
                    popupPlay(packageName, context)
                    dialog.dismiss()
                }
                .setNegativeButton(
                    R.string.cancel
                ) { dialog, _ -> dialog.dismiss() }
                .setMessage(R.string.amaze_fm_redirect)
                .create()
            dialog.show()
        }

        private fun popupPlay(packageName: String, context: Context) {
            val intent1 =
                Intent(Intent.ACTION_VIEW)
            try {
                intent1.data = Uri.parse(
                    String.format(
                        "market://details?id=%s",
                        packageName
                    )
                )
                context.startActivity(intent1)
            } catch (ifPlayStoreNotInstalled: ActivityNotFoundException) {
                intent1.data = Uri.parse(
                    String.format(
                        "https://play.google.com/store/apps/details?id=%s",
                        packageName
                    )
                )
                context.startActivity(intent1)
            }
        }

        fun copyToClipboard(context: Context, text: String?): Boolean {
            return try {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    context.getString(R.string.clipboard_path_copy), text
                )
                clipboard.setPrimaryClip(clip)
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * Builds a email intent for amaze feedback
         *
         * @param text email content
         * @param supportMail support mail for given intent
         * @return intent
         */
        fun buildEmailIntent(text: String?, supportMail: String): Intent? {
            val emailIntent = Intent(Intent.ACTION_SEND)
            val aEmailList = arrayOf(supportMail)
            val aEmailCCList = arrayOf(
                EMAIL_VISHAL,
                EMAIL_EMMANUEL,
                EMAIL_RAYMOND
            )
            emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList)
            emailIntent.putExtra(Intent.EXTRA_CC, aEmailCCList)
            emailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Feedback : Amaze File Utilities for " + BuildConfig.VERSION_NAME
            )
            if (!isNullOrEmpty(text)) {
                emailIntent.putExtra(Intent.EXTRA_TEXT, text)
            }
            emailIntent.type = "message/rfc822"
            return emailIntent
        }

        /** Open telegram in browser  */
        fun openTelegramURL(context: Context) {
            openURL(
                URL_TELEGRAM,
                context
            )
        }

        fun isNullOrEmpty(list: Collection<*>?): Boolean {
            return list == null || list.isEmpty()
        }

        fun isNullOrEmpty(string: String?): Boolean {
            return string == null || string.isEmpty()
        }

        fun setGridLayoutManagerSpan(
            gridLayoutManager: GridLayoutManager,
            adapter: MediaFileAdapter
        ) {
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        AbstractMediaFilesAdapter.TYPE_ITEM ->
                            1
                        else -> 3
                    }
                }
            }
        }

        fun setGridLayoutManagerSpan(
            gridLayoutManager: GridLayoutManager,
            adapter: ReviewAnalysisAdapter
        ) {
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        AbstractMediaFilesAdapter.TYPE_ITEM ->
                            1
                        else -> 3
                    }
                }
            }
        }

        /**
         * Force disables screen rotation. Useful when we're temporarily in activity because of external
         * intent, and don't have to really deal much with filesystem.
         */
        fun disableScreenRotation(activity: Activity) {
            val screenOrientation = activity.resources.configuration.orientation
            if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        fun enableScreenRotation(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }

        fun setScreenRotationSensor(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }

        fun getScreenHeight(windowManager: WindowManager): Int {
            val deviceDisplay = windowManager.defaultDisplay
            val size = Point()
            deviceDisplay?.getSize(size)
            return size.y
        }

        fun containsInPreferences(
            path: String,
            pathPreferences: List<PathPreferences>,
            inclusive: Boolean
        ):
            Boolean {
            pathPreferences.forEach {
                if (path.contains(it.path, true)) {
                    return !it.excludes
                }
            }
            return !inclusive
        }

        fun generateRandom(min: Int, max: Int): Int {
            return (Math.random() * (max - min + 1) + min).toInt()
        }

        fun wifiIpAddress(context: Context): String? {
            val wifiManager = context.applicationContext
                .getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            var ipAddress = wifiManager.connectionInfo.ipAddress

            // Convert little-endian to big-endianif needed
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                ipAddress = Integer.reverseBytes(ipAddress)
            }
            val ipByteArray: ByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
            val ipAddressString: String? = try {
                InetAddress.getByAddress(ipByteArray).hostAddress
            } catch (ex: UnknownHostException) {
                log.error("Unable to get host address.", ex)
                null
            }
            return ipAddressString
        }

        fun openInMaps(context: Context, latitude: String, longitude: String) {
            val url = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }
}
