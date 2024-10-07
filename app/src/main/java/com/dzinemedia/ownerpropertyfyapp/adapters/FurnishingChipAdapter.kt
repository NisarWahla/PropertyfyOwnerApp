package com.dzinemedia.ownerpropertyfyapp.adapters

import android.content.Context
import android.view.LayoutInflater
import com.dzinemedia.ownerpropertyfyapp.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FurnishingChipAdapter (context: Context, private val chipGroup: ChipGroup, private val chipData: ArrayList<Any>) {
    private val initialItemCount = 5
    private var isShowingAllItems = false
    val inflater = LayoutInflater.from(context)
    fun displayInitialItems() {
        chipGroup.removeAllViews()
        val initialItems = chipData.take(initialItemCount)
        for (chipText in initialItems) {
            val chip = inflater.inflate(R.layout.furnishing_item, chipGroup, false) as Chip
            chip.text = chipText.toString()
            chipGroup.addView(chip)
        }
        if (chipData.size > 5) {
            addMoreChip()
        }
    }

    private fun displayAllItems() {
        chipGroup.removeAllViews()
        for (chipText in chipData) {
            val chip = inflater.inflate(R.layout.furnishing_item, chipGroup, false) as Chip
            chip.text = chipText.toString()
            chipGroup.addView(chip)
        }
        addShowLessChip()
    }

    private fun addMoreChip() {
        val moreChip = inflater.inflate(R.layout.more_furnishing_item, chipGroup, false) as Chip
        moreChip.text = "+ More Items"
        moreChip.setOnClickListener {
            isShowingAllItems = true
            displayAllItems()
        }
        chipGroup.addView(moreChip)
    }

    private fun addShowLessChip() {
        val showLessChip = inflater.inflate(R.layout.more_furnishing_item, chipGroup, false) as Chip
        showLessChip.text = "- Show Less"
        showLessChip.setOnClickListener {
            isShowingAllItems = false
            displayInitialItems()
        }
        chipGroup.addView(showLessChip)
    }
}