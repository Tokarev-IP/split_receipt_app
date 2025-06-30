package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R

@Composable
internal fun DialogCapView(
    modifier: Modifier = Modifier,
    text: String,
    onDismissClick: () -> Unit,
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            text = text,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(
            onClick = { onDismissClick() }
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.close_the_dialog),
            )
        }
    }
}