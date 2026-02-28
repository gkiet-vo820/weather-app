package com.example.weatherapp.view.components

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit, onSearch: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Tìm thành phố...", color = Color.White.copy(0.5f)) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(0.5f),
                cursorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSearch,
            modifier = Modifier.size(56.dp).background(Color.White.copy(0.2f), RoundedCornerShape(16.dp))
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_menu_search), contentDescription = null, tint = Color.White)
        }
    }
}


@Composable
fun HistorySection(items: List<String>, onItemClick: (String) -> Unit, onItemLongClick: (String) -> Unit, onClear: () -> Unit) {
    val haptic = LocalHapticFeedback.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var cityToDelete by remember { mutableStateOf("") }
    var showClearAllDialog by remember { mutableStateOf(false) }

    if(showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {showDeleteDialog = false},
            title = { Text("Xóa lịch sử") },
            text = { Text("Bạn có chắc chắn muốn xóa '$cityToDelete' khỏi danh sách tìm kiếm không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onItemLongClick(cityToDelete)
                        showDeleteDialog = false
                    }
                ) { Text("Xóa", color = Color.Red) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) { Text("Hủy") }
            },
            containerColor = Color(0xFF1A1C1E),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(0.8f)
        )
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Xóa tất cả lịch sử") },
            text = { Text("Bạn có chắc chắn muốn làm sạch toàn bộ danh sách tìm kiếm không?") },
            confirmButton = {
                TextButton(onClick = {
                    onClear()
                    showClearAllDialog = false
                }) { Text("Xóa sạch", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) { Text("Hủy") }
            },
            containerColor = Color(0xFF1A1C1E),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(0.8f)
        )
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tìm kiếm gần đây", color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
            Text(
                "Xóa hết",
                color = Color.White.copy(0.6f),
                modifier = Modifier.clickable
                {
                    showClearAllDialog = true
                },
                style = MaterialTheme.typography.labelSmall)
        }
        FlowRow (

            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = Int.MAX_VALUE
        ) {
            items.forEach { cityName ->
                Box (
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.1f))
                        .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(12.dp))
                        .combinedClickable(
                            onClick = {onItemClick(cityName)},
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                cityToDelete = cityName
                                showDeleteDialog = true
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cityName,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}