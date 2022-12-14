package com.dbd.market.adapters.main_category

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.dbd.market.R
import com.dbd.market.data.Product
import com.dbd.market.utils.OnRecyclerViewItemClickInterface
import com.dbd.market.utils.getNewPriceAfterDiscount

class ProfitableCategoryProductsAdapter: RecyclerView.Adapter<ProfitableCategoryProductsAdapter.SuitsProfitableProductsViewHolder>(), OnRecyclerViewItemClickInterface {

    inner class SuitsProfitableProductsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val goToProductButton = itemView.findViewById<CircularProgressButton>(R.id.beneficialItemGoToProductButton)

        fun bind(product: Product) {
            val name = product.name
            val price = product.price
            val discount = product.discount
            val image = product.images[0]
            val nameTextView = itemView.findViewById<TextView>(R.id.beneficialItemProductNameTextView)
            val priceBeforeDiscountTextView = itemView.findViewById<TextView>(R.id.beneficialItemProductPriceBeforeDiscount)
            val priceAfterDiscountTextView = itemView.findViewById<TextView>(R.id.beneficialItemProductPriceAfterDiscount)
            val imageView = itemView.findViewById<ImageView>(R.id.beneficialProductImageView)
            discount?.let { discountValue -> priceAfterDiscountTextView.text = getNewPriceAfterDiscount(price, discountValue) }
            nameTextView.text = name
            priceBeforeDiscountTextView.apply {
                text = price.toString().plus("$")
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            Glide.with(itemView).load(image).into(imageView)
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuitsProfitableProductsViewHolder =
        SuitsProfitableProductsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.beneficial_product_recycler_view_layout, parent, false))

    override fun onBindViewHolder(holder: SuitsProfitableProductsViewHolder, position: Int) {
        val currentSuit = differ.currentList[position]
        holder.bind(currentSuit)

        holder.goToProductButton.setOnClickListener { onItemClick?.let { it(currentSuit) } }
    }

    override fun getItemCount() = differ.currentList.size

    private var onItemClick: ((Product) -> Unit)? = null

    override fun onRecyclerViewItemClick(onClick: (T: Any) -> Unit) { onItemClick = onClick }
}