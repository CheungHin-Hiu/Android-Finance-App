package com.example.androidfinanceapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CategoryItem(
    val id: Int,
    val icon: Int,
    val name: String
)

@Composable
fun CategoryGrid(
    categories: List<CategoryItem>,
    selectedCategory: CategoryItem?,
    onCategorySelected: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // First row with weight-based layout
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            for (position in 0 until 4) {
                Box(modifier = Modifier.weight(1f)) {
                    if (position < categories.size) {
                        val category = categories[position]
                        CategoryItem(
                            category = category,
                            isSelected = category == selectedCategory,
                            onSelected = onCategorySelected
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second row with same weight-based layout
        if (categories.size > 4) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (position in 0 until 4) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = position + 4
                        if (index < categories.size) {
                            val category = categories[index]
                            CategoryItem(
                                category = category,
                                isSelected = category == selectedCategory,
                                onSelected = onCategorySelected
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryItem,
    isSelected: Boolean,
    onSelected: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemWidth = 90.dp
    val itemHeight = 100.dp

    Box(
        modifier = modifier
            .width(itemWidth)
            .height(itemHeight)
            .clickable { onSelected(category) },
        contentAlignment = Alignment.Center
    ) {
        // Background when selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(100.dp)  // Square size
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F7FA))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF7C4DFF),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }

        // Content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            // Icon
            Image(
                painter = painterResource(id = category.icon),
                contentDescription = category.name,
                modifier = Modifier.size(52.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Category name text
            Text(
                text = category.name,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}