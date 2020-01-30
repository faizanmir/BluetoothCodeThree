package avishkaar.com.bluetoothcodethree.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import avishkaar.com.bluetoothcodethree.R
import avishkaar.com.bluetoothcodethree.modelClasses.Face

class SpinnerAdapter(context: Context,val layRes:Int, var faceList:ArrayList<Face>) : ArrayAdapter<Face>(context,layRes,faceList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position,convertView,parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position,convertView,parent)
    }
    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
          var listItem: View? = convertView
            if(listItem==null) {
                listItem = LayoutInflater.from(context).inflate(layRes, parent, false)
            }
                var imageView = listItem?.findViewById<ImageView>(R.id.spinnerImage)
                val textView = listItem?.findViewById<TextView>(R.id.spinnerText)
                imageView?.setImageResource(faceList[position].picID)
                textView?.text = faceList[position].title
           return listItem!!
    }
}