package dk.mifu.pmos.vegetablegardening.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Constraints
import dk.mifu.pmos.vegetablegardening.R
import dk.mifu.pmos.vegetablegardening.databinding.FragmentCreateGridBinding

@SuppressLint("ViewConstructor")
class GridTile(context: Context,
               onClickListener: OnClickListener,
               private val binding: FragmentCreateGridBinding,
               private val tileSideLength: Int): androidx.appcompat.widget.AppCompatButton(context) {

    private val START = ConstraintSet.START
    private val END = ConstraintSet.END
    private val TOP = ConstraintSet.TOP
    private val BOTTOM = ConstraintSet.BOTTOM

    init {
        id = View.generateViewId()
        setBackgroundResource(R.drawable.grid_tile)
        setPadding(0,0,0,0)
        layoutParams = setParams()
        setOnClickListener(onClickListener)
    }

    fun snapToGrid(prevTileId: Int?, upperTileId: Int?, row: Boolean) {
        val constraintSet = ConstraintSet()

        constraintSet.apply{
            clone(binding.parentLayout)

            if(prevTileId!=null) connect(id, START, prevTileId, END)
            else connect(id, START, binding.parentLayout.id, START)

            if(upperTileId!=null) connect(id, TOP, upperTileId, BOTTOM)
            else connect(id, TOP, binding.parentLayout.id, TOP)

            if(row) connect(binding.addColumnButton.id, START, id, END)
            else connect(binding.addRowButton.id, TOP, id, BOTTOM)
            applyTo(binding.parentLayout)
        }
    }

    private fun setParams(): Constraints.LayoutParams{
        val params = Constraints.LayoutParams(
            tileSideLength,
            tileSideLength
        )
        params.setMargins(0,0,0,0)
        return params
    }
}