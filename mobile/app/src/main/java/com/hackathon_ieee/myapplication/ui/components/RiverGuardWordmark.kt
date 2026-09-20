package com.hackathon_ieee.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.hackathon_ieee.myapplication.R

@Composable
fun RiverGuardWordmark(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(R.drawable.riverguard_wordmark),
        contentDescription = "RiverGuard",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
