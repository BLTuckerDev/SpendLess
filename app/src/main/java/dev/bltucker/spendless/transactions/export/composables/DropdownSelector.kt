package dev.bltucker.spendless.transactions.export.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest

@Composable
fun DropdownSelector(
    modifier: Modifier = Modifier,
    selectedText: String,
    isExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    content: @Composable () -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "Dropdown Arrow Animation"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = onToggleDropdown
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle dropdown",
                    modifier = Modifier.rotate(rotationState)
                )
            }
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onToggleDropdown,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(SurfaceContainerLowest)
                .align(Alignment.TopStart)
        ) {
            content()
        }
    }
}