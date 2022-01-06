/*
 * Copyright (C) 2021-2021 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.home_page.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import android.widget.LinearLayout
import androidx.core.content.edit
import androidx.core.graphics.ColorUtils
import com.amaze.fileutilities.R
import com.amaze.fileutilities.home_page.ui.files.MediaFileAdapter
import com.amaze.fileutilities.home_page.ui.files.MediaFileListSorter
import com.amaze.fileutilities.utilis.*
import com.google.android.material.progressindicator.LinearProgressIndicator

class MediaTypeHeaderView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val typeImageView: ImageView
    private val infoLayoutParent: LinearLayout
    private val usedSpaceTextView: TextView
    private val progressIndicatorsParent: LinearLayout
    private val mediaProgressIndicator: LinearProgressIndicator
    private val progressPercentTextView: TextView
    private val storageCountsParent: RelativeLayout
    private val itemsCountTextView: TextView
    private val internalStorageTextView: TextView
    private val optionsParentLayout: LinearLayout
    private val optionsItemsScroll: HorizontalScrollView
    private val optionsIndexImage: ImageView
    private val optionsSwitchView: ImageView
    private val optionsGroupView: ImageView
    private val optionsSortView: ImageView
    private val optionsListParent: LinearLayout

    init {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.media_type_header_layout, this, true)
        typeImageView = getChildAt(0) as ImageView
        infoLayoutParent = getChildAt(1) as LinearLayout
        optionsParentLayout = getChildAt(2) as LinearLayout
        optionsItemsScroll = getChildAt(3) as HorizontalScrollView
        optionsItemsScroll.isHorizontalScrollBarEnabled = false
        usedSpaceTextView = infoLayoutParent.findViewById(R.id.usedSpaceTextView)
        progressIndicatorsParent = infoLayoutParent.findViewById(R.id.progressIndicatorsParent)
        mediaProgressIndicator = progressIndicatorsParent.findViewById(R.id.mediaProgress)
        progressPercentTextView = progressIndicatorsParent
            .findViewById(R.id.progressPercentTextView)
        storageCountsParent = infoLayoutParent.findViewById(R.id.storageCountsParent)
        itemsCountTextView = storageCountsParent.findViewById(R.id.itemsCountTextView)
        internalStorageTextView = storageCountsParent.findViewById(R.id.internalStorageTextView)
        optionsIndexImage = optionsParentLayout.findViewById(R.id.index_image)
        optionsSwitchView = optionsParentLayout.findViewById(R.id.switch_view)
        optionsGroupView = optionsParentLayout.findViewById(R.id.group_view)
        optionsSortView = optionsParentLayout.findViewById(R.id.sort_view)
        optionsListParent = optionsItemsScroll.findViewById(R.id.options_list_parent)

        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        // init values
        usedSpaceTextView.text = resources.getString(R.string.used_space)
        typeImageView.setColorFilter(context.resources.getColor(R.color.white))
        progressPercentTextView.text = "--"
    }

    fun setProgress(mediaTypeContent: MediaTypeView.MediaTypeContent) {
        mediaTypeContent.run {
            usedSpaceTextView.text = resources.getString(
                R.string.used_space, size
            )
            progressPercentTextView.text = "$progress %"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaProgressIndicator.setProgress(progress, true)
            } else {
                mediaProgressIndicator.progress = progress
            }
            itemsCountTextView.text = resources.getString(
                R.string.num_of_files, itemsCount.toString()
            )
            internalStorageTextView.text = resources.getString(
                R.string.internal_storage_subs, totalSpace
            )
        }
        invalidate()
    }

    fun setTypeImageSrc(imageRes: Drawable) {
        typeImageView.setImageDrawable(imageRes)
    }

    fun setMediaImageSrc(mediaRes: Drawable) {
        background = mediaRes
    }

    fun setHeaderColor(headerColor: Int, headerRes: Int) {
        setBackgroundResource(headerRes)
        mediaProgressIndicator.trackColor = ColorUtils.blendARGB(
            headerColor,
            Color.BLACK, .5f
        )
    }

    fun initOptionsItems(
        optionsMenuSelected: MediaFileAdapter.OptionsMenuSelected,
        headerListItems: MutableList<AbstractMediaFilesAdapter.ListItem>
    ) {
        clickOptionsIndex(optionsMenuSelected, headerListItems)
        val sharedPreferences = context.getAppCommonSharedPreferences()
        optionsIndexImage.setOnClickListener {
            clickOptionsIndex(optionsMenuSelected, headerListItems)
        }
        optionsSwitchView.setOnClickListener {
            clickOptionsSwitchView(optionsMenuSelected, sharedPreferences)
        }
        optionsGroupView.setOnClickListener {
            clickOptionsGroupView(optionsMenuSelected, sharedPreferences)
        }
        optionsSortView.setOnClickListener {
            clickOptionsSortView(optionsMenuSelected, sharedPreferences)
        }
    }

    private fun clickOptionsIndex(
        optionsMenuSelected: MediaFileAdapter.OptionsMenuSelected,
        headerListItems: MutableList<AbstractMediaFilesAdapter.ListItem>
    ) {
        clearOptionItemsBackgrounds()
        optionsIndexImage.background = resources.getDrawable(R.drawable.button_selected_dark)
        headerListItems.forEach {
            listItem ->
            val button = getSelectedTextButton(
                listItem.header
                    ?: resources.getString(R.string.undetermined)
            )
            button.setOnClickListener {
                optionsMenuSelected.select(listItem.position)
            }
            optionsListParent.addView(button)
        }
    }

    private fun clickOptionsSwitchView(
        optionsMenuSelected: MediaFileAdapter.OptionsMenuSelected,
        sharedPreferences: SharedPreferences
    ) {
        clearOptionItemsBackgrounds()
        optionsSwitchView.background = resources.getDrawable(R.drawable.button_selected_dark)
        val listViewButton: Button
        val gridViewButton: Button
        if (sharedPreferences.getBoolean(
                PreferencesConstants.KEY_MEDIA_LIST_TYPE,
                PreferencesConstants.DEFAULT_MEDIA_LIST_TYPE
            )
        ) {
            optionsSwitchView.setImageDrawable(
                resources
                    .getDrawable(R.drawable.ic_round_view_list_32)
            )
            listViewButton = getSelectedTextButton(resources.getString(R.string.list_view))
            gridViewButton = getUnSelectedTextButton(resources.getString(R.string.grid_view))
        } else {
            optionsSwitchView.setImageDrawable(
                resources
                    .getDrawable(R.drawable.ic_round_grid_on_32)
            )
            listViewButton = getUnSelectedTextButton(resources.getString(R.string.list_view))
            gridViewButton = getSelectedTextButton(resources.getString(R.string.grid_view))
        }

        listViewButton.setOnClickListener {
            setSelectButton(listViewButton)
            setUnSelectButton(gridViewButton)
            sharedPreferences.edit {
                this.putBoolean(PreferencesConstants.KEY_MEDIA_LIST_TYPE, true).apply()
            }
            optionsMenuSelected.switchView(true)
        }
        gridViewButton.setOnClickListener {
            setSelectButton(gridViewButton)
            setUnSelectButton(listViewButton)
            sharedPreferences.edit {
                this.putBoolean(PreferencesConstants.KEY_MEDIA_LIST_TYPE, false).apply()
            }
            optionsMenuSelected.switchView(false)
        }

        optionsListParent.addView(listViewButton)
        optionsListParent.addView(gridViewButton)
    }

    private fun clickOptionsGroupView(
        optionsMenuSelected: MediaFileAdapter.OptionsMenuSelected,
        sharedPreferences: SharedPreferences
    ) {
        clearOptionItemsBackgrounds()
        optionsGroupView.background = resources.getDrawable(R.drawable.button_selected_dark)
        val groupByPref = sharedPreferences.getInt(
            PreferencesConstants.KEY_MEDIA_LIST_GROUP_BY,
            PreferencesConstants.DEFAULT_MEDIA_LIST_GROUP_BY
        )
        var groupParent: Button? = null
        var groupDate: Button? = null
        var groupName: Button? = null
        when (groupByPref) {
            MediaFileListSorter.GROUP_NAME -> {
                groupName = getSelectedTextButton(resources.getString(R.string.name))
                groupParent = getUnSelectedTextButton(resources.getString(R.string.parent))
                groupDate = getUnSelectedTextButton(resources.getString(R.string.date))
            }
            MediaFileListSorter.GROUP_PARENT -> {
                groupName = getUnSelectedTextButton(resources.getString(R.string.name))
                groupParent = getSelectedTextButton(resources.getString(R.string.parent))
                groupDate = getUnSelectedTextButton(resources.getString(R.string.date))
            }
            MediaFileListSorter.GROUP_DATE -> {
                groupName = getUnSelectedTextButton(resources.getString(R.string.name))
                groupParent = getUnSelectedTextButton(resources.getString(R.string.parent))
                groupDate = getSelectedTextButton(resources.getString(R.string.date))
            }
        }
        groupName?.setOnClickListener {
            setSelectButton(groupName)
            setUnSelectButton(groupDate!!)
            setSelectButton(groupParent!!)
            sharedPreferences.edit {
                this.putInt(
                    PreferencesConstants.KEY_MEDIA_LIST_GROUP_BY,
                    MediaFileListSorter.GROUP_NAME
                ).apply()
            }
            var isAsc = sharedPreferences
                .getBoolean(
                    PreferencesConstants.KEY_MEDIA_LIST_GROUP_BY_IS_ASC,
                    PreferencesConstants.DEFAULT_MEDIA_LIST_GROUP_BY_ASC
                )
            isAsc = !isAsc
            sharedPreferences.edit().putBoolean(
                PreferencesConstants
                    .KEY_MEDIA_LIST_GROUP_BY_IS_ASC,
                isAsc
            ).apply()
            optionsMenuSelected.groupBy(MediaFileListSorter.GROUP_NAME, isAsc)
        }
        groupDate?.setOnClickListener {
            setUnSelectButton(groupName!!)
            setSelectButton(groupDate)
            setUnSelectButton(groupParent!!)
            sharedPreferences.edit {
                this.putInt(
                    PreferencesConstants.KEY_MEDIA_LIST_GROUP_BY,
                    MediaFileListSorter.GROUP_DATE
                ).apply()
            }
            var isAsc = sharedPreferences
                .getBoolean(
                    PreferencesConstants.KEY_MEDIA_LIST_GROUP_BY_IS_ASC,
                    PreferencesConstants.DEFAULT_MEDIA_LIST_GROUP_BY_ASC
                )
            isAsc = !isAsc
            sharedPreferences.edit().putBoolean(
                PreferencesConstants
                    .KEY_MEDIA_LIST_GROUP_BY_IS_ASC,
                isAsc
            ).apply()
            optionsMenuSelected.groupBy(MediaFileListSorter.GROUP_DATE, isAsc)
        }
        groupParent?.setOnClickListener {
            setUnSelectButton(groupName!!)
            setUnSelectButton(groupDate!!)
            setSelectButton(groupParent)
            sharedPreferences.edit {
                this.putInt(
                    PreferencesConstants.KEY_MEDIA_LIST_GROUP_BY,
                    MediaFileListSorter.GROUP_PARENT
                ).apply()
            }
            var isAsc = sharedPreferences
                .getBoolean(
                    PreferencesConstants.KEY_MEDIA_LIST_GROUP_BY_IS_ASC,
                    PreferencesConstants.DEFAULT_MEDIA_LIST_GROUP_BY_ASC
                )
            isAsc = !isAsc
            sharedPreferences.edit().putBoolean(
                PreferencesConstants
                    .KEY_MEDIA_LIST_GROUP_BY_IS_ASC,
                isAsc
            ).apply()
            optionsMenuSelected.groupBy(MediaFileListSorter.GROUP_PARENT, isAsc)
        }
        optionsListParent.addView(groupName)
        optionsListParent.addView(groupDate)
        optionsListParent.addView(groupParent)
    }

    private fun clickOptionsSortView(
        optionsMenuSelected: MediaFileAdapter.OptionsMenuSelected,
        sharedPreferences: SharedPreferences
    ) {
        clearOptionItemsBackgrounds()
        optionsSortView.background = resources.getDrawable(R.drawable.button_selected_dark)
        val sortByPref = sharedPreferences.getInt(
            PreferencesConstants.KEY_MEDIA_LIST_SORT_BY,
            PreferencesConstants.DEFAULT_MEDIA_LIST_SORT_BY
        )
        var sortSize: Button? = null
        var sortDate: Button? = null
        var sortName: Button? = null
        when (sortByPref) {
            MediaFileListSorter.SORT_NAME -> {
                sortName = getSelectedTextButton(resources.getString(R.string.name))
                sortSize = getUnSelectedTextButton(resources.getString(R.string.size))
                sortDate = getUnSelectedTextButton(resources.getString(R.string.date))
            }
            MediaFileListSorter.SORT_SIZE -> {
                sortName = getUnSelectedTextButton(resources.getString(R.string.name))
                sortSize = getSelectedTextButton(resources.getString(R.string.size))
                sortDate = getUnSelectedTextButton(resources.getString(R.string.date))
            }
            MediaFileListSorter.SORT_MODIF -> {
                sortName = getUnSelectedTextButton(resources.getString(R.string.name))
                sortSize = getUnSelectedTextButton(resources.getString(R.string.size))
                sortDate = getSelectedTextButton(resources.getString(R.string.date))
            }
        }
        sortName?.setOnClickListener {
            setSelectButton(sortName)
            setUnSelectButton(sortDate!!)
            setSelectButton(sortSize!!)
            sharedPreferences.edit {
                this.putInt(
                    PreferencesConstants.KEY_MEDIA_LIST_SORT_BY,
                    MediaFileListSorter.SORT_NAME
                ).apply()
            }
            var isAsc = sharedPreferences
                .getBoolean(
                    PreferencesConstants.KEY_MEDIA_LIST_SORT_BY_IS_ASC,
                    PreferencesConstants.DEFAULT_MEDIA_LIST_SORT_BY_ASC
                )
            isAsc = !isAsc
            sharedPreferences.edit().putBoolean(
                PreferencesConstants
                    .KEY_MEDIA_LIST_SORT_BY_IS_ASC,
                isAsc
            ).apply()
            optionsMenuSelected.sortBy(MediaFileListSorter.SORT_NAME, isAsc)
        }
        sortDate?.setOnClickListener {
            setUnSelectButton(sortName!!)
            setSelectButton(sortDate)
            setUnSelectButton(sortSize!!)
            sharedPreferences.edit {
                this.putInt(
                    PreferencesConstants.KEY_MEDIA_LIST_SORT_BY,
                    MediaFileListSorter.SORT_MODIF
                ).apply()
            }
            var isAsc = sharedPreferences
                .getBoolean(
                    PreferencesConstants.KEY_MEDIA_LIST_SORT_BY_IS_ASC,
                    PreferencesConstants.DEFAULT_MEDIA_LIST_SORT_BY_ASC
                )
            isAsc = !isAsc
            sharedPreferences.edit().putBoolean(
                PreferencesConstants
                    .KEY_MEDIA_LIST_SORT_BY_IS_ASC,
                isAsc
            ).apply()
            optionsMenuSelected.sortBy(MediaFileListSorter.SORT_MODIF, isAsc)
        }
        sortSize?.setOnClickListener {
            setUnSelectButton(sortName!!)
            setUnSelectButton(sortDate!!)
            setSelectButton(sortSize)
            sharedPreferences.edit {
                this.putInt(
                    PreferencesConstants.KEY_MEDIA_LIST_SORT_BY,
                    MediaFileListSorter.SORT_SIZE
                ).apply()
            }
            var isAsc = sharedPreferences
                .getBoolean(
                    PreferencesConstants.KEY_MEDIA_LIST_SORT_BY_IS_ASC,
                    PreferencesConstants.DEFAULT_MEDIA_LIST_SORT_BY_ASC
                )
            isAsc = !isAsc
            sharedPreferences.edit().putBoolean(
                PreferencesConstants
                    .KEY_MEDIA_LIST_SORT_BY_IS_ASC,
                isAsc
            ).apply()
            optionsMenuSelected.sortBy(MediaFileListSorter.SORT_SIZE, isAsc)
        }
        optionsListParent.addView(sortName)
        optionsListParent.addView(sortDate)
        optionsListParent.addView(sortSize)
    }

    private fun getSelectedTextButton(text: String): Button {
        val button = Button(context)
        setSelectButton(button)
        setParams(button)
        button.text = text
        return button
    }

    private fun getUnSelectedTextButton(text: String): Button {
        val button = Button(context)
        setUnSelectButton(button)
        setParams(button)
        button.text = text
        return button
    }

    private fun setParams(button: Button) {
        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 16.px.toInt()
        button.layoutParams = params
    }

    private fun setSelectButton(button: Button) {
        button.background = resources.getDrawable(R.drawable.button_curved_selected)
        button.setTextColor(resources.getColor(R.color.navy_blue))
    }

    private fun setUnSelectButton(button: Button) {
        button.background = resources.getDrawable(R.drawable.button_curved_unselected)
        button.setTextColor(resources.getColor(R.color.white))
    }

    private fun clearOptionItemsBackgrounds() {
        optionsIndexImage.background = null
        optionsListParent.removeAllViews()
        optionsSwitchView.background = null
        optionsGroupView.background = null
        optionsSortView.background = null
    }
}
