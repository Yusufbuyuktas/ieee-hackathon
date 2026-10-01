package com.hackathon_ieee.myapplication.feature.home

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.R
import com.hackathon_ieee.myapplication.feature.more.StoryCarousel
import com.hackathon_ieee.myapplication.feature.more.StoryDetail
import com.hackathon_ieee.myapplication.feature.more.impactStories
import com.hackathon_ieee.myapplication.ui.components.ThickBackIcon

private data class AwarenessArticle(
    @param:DrawableRes val imageRes: Int,
    val icon: AwarenessIcon,
    val imageDescription: String,
    val title: String,
    val summary: String,
    val body: String,
    val source: String
)

private enum class AwarenessIcon {
    RIVER,
    WATER,
    FLOW,
    STREAM,
    CAMERA
}

private val awarenessArticles = listOf(
    AwarenessArticle(
        imageRes = R.drawable.ergene1,
        icon = AwarenessIcon.RIVER,
        imageDescription = "A wide view of the Ergene River and its surrounding landscape",
        title = "A River Carries More Than Water",
        summary = "The Ergene carries the traces of its soil, cities, industry and every ecosystem along its course.",
        body = "When we look at a river, we see only flowing water. Yet a river carries a trace from everywhere it passes: soil from farmland, stormwater from cities, discharges from industrial areas and the combined effects of life around it.\n\nA change at one point in the Ergene can travel for kilometres with the current. The river's health therefore concerns not only those who live beside it, but also agriculture, groundwater, ecosystems and future generations.\n\nThe first step in protecting a river is learning to notice what it is telling us.",
        source = "Documented by the Ergene River Basin Management Plan"
    ),
    AwarenessArticle(
        imageRes = R.drawable.ergene2,
        icon = AwarenessIcon.WATER,
        imageDescription = "Dark wastewater flowing through drainage pipes",
        title = "Can Water Color Tell the Whole Story?",
        summary = "Dark water, foam or an unusual odor may be a warning—but observation and measurement must work together.",
        body = "Not every body of polluted-looking water contains the same substances. Mud may change its color, algae may cover its surface, and invisible contaminants may sometimes create a risk even when the water looks completely clear.\n\nA photograph alone cannot answer the question, “What is in this water?” It can, however, record the location, time and visible signs of an event, creating a valuable starting point for investigation.\n\nRiverGuard does not use a photograph to deliver a final verdict. It helps detect suspicious conditions early, preserve the observation and turn it into environmental evidence that can be verified.",
        source = "Documented in World Health Organization water-quality guidance"
    ),
    AwarenessArticle(
        imageRes = R.drawable.ergene3,
        icon = AwarenessIcon.FLOW,
        imageDescription = "An aerial view of the Ergene River near a bridge",
        title = "Pollution Knows No Boundaries",
        summary = "Waste entering a river does not stay where it was released; the current carries it toward other living spaces.",
        body = "Rivers do not recognize city borders, district signs or property lines. The current carries water, sediment and the substances mixed into them from one place to another.\n\nA small problem beginning upstream can become a much greater pressure on farmland, wildlife and water resources downstream. Looking at only one location is therefore never enough.\n\nWhen citizen observations from different places come together, reports that appear isolated can form a meaningful pattern. Sometimes a few photographs taken by different people are what finally make the bigger picture visible.",
        source = "Documented by the European Environment Agency"
    ),
    AwarenessArticle(
        imageRes = R.drawable.ergene4,
        icon = AwarenessIcon.STREAM,
        imageDescription = "A neighborhood stream passing through concrete drainage channels",
        title = "Great Rivers Begin with Small Streams",
        summary = "A channel beside a neighborhood may be part of a water system far larger than it appears.",
        body = "Streams beside homes, stormwater channels and culverts may seem insignificant. Yet these small waterways eventually connect to larger streams and, ultimately, to the Ergene.\n\nWaste left on a street, a blocked channel or an uncontrolled flow does not necessarily remain in that neighborhood. Rain can carry it into water systems many kilometres away.\n\nProtecting the environment therefore does not begin only at the riverbank. Sometimes the most important observation starts when we truly notice the small stream we pass every day.",
        source = "Documented by the U.S. Environmental Protection Agency"
    ),
    AwarenessArticle(
        imageRes = R.drawable.ergene5,
        icon = AwarenessIcon.CAMERA,
        imageDescription = "An aerial view of a reservoir surrounded by dry land",
        title = "One Photograph Can Start a Change",
        summary = "An ordinary photograph taken today may become the first piece of evidence in tomorrow's investigation.",
        body = "One of the greatest challenges in environmental monitoring is that an event may not be recorded while it is happening. Water flows away within hours, foam disappears, color changes, and all that remains is the sentence, “There was a problem here.”\n\nA photograph submitted with its location, time, category and description turns an observation into a record that can be compared. Reports accumulated in the same area can reveal recurring problems and show how conditions change over time.\n\nOne citizen cannot save an entire river alone. But one well-timed observation can begin a much larger investigation. RiverGuard creates a bridge between a citizen's attention and scientific evaluation.",
        source = "Documented by the European Environment Agency"
    )
)

@Composable
fun HomeScreen(
    onDetailVisibilityChanged: (Boolean) -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedArticleIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedStoryIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val feedScrollState = rememberScrollState()
    val selectedArticle = selectedArticleIndex?.let(awarenessArticles::getOrNull)
    val selectedStory = selectedStoryIndex?.let(impactStories::getOrNull)

    LaunchedEffect(selectedArticle != null, selectedStory != null) {
        onDetailVisibilityChanged(selectedArticle != null || selectedStory != null)
    }

    if (selectedArticle == null && selectedStory == null) {
        AwarenessFeed(
            modifier = modifier,
            scrollState = feedScrollState,
            onReportClick = onReportClick,
            onArticleClick = { selectedArticleIndex = it },
            onStoryClick = { selectedStoryIndex = it }
        )
    } else if (selectedArticle != null) {
        AwarenessDetail(
            article = selectedArticle,
            modifier = modifier,
            onBack = { selectedArticleIndex = null }
        )
    } else if (selectedStory != null) {
        StoryDetail(
            story = selectedStory,
            modifier = modifier,
            onBack = { selectedStoryIndex = null }
        )
    }
}

@Composable
private fun AwarenessFeed(
    onArticleClick: (Int) -> Unit,
    onStoryClick: (Int) -> Unit,
    onReportClick: () -> Unit,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeActionPanel(onReportClick = onReportClick)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Real Stories, Real Change",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Discover moments when observation, evidence, and human health came together.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        StoryCarousel(
            onStoryClick = onStoryClick,
            contentPadding = PaddingValues(0.dp),
            cardWidth = 300.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Know the Ergene Basin",
                style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        awarenessArticles.forEachIndexed { index, article ->
            AwarenessCard(
                article = article,
                onClick = { onArticleClick(index) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun HomeActionPanel(onReportClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Small Observations, Real Change",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "A single observation can help make a change in our water visible.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onReportClick,
                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
            ) {
                Text("Report an Observation")
            }
        }
    }
}

@Composable
private fun AwarenessCard(
    article: AwarenessArticle,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(article.imageRes),
            contentDescription = article.imageDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Black.copy(alpha = 0.08f),
                            0.38f to Color.Black.copy(alpha = 0.22f),
                            1f to Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(androidx.compose.ui.Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                AwarenessCardIcon(
                    icon = article.icon,
                    modifier = Modifier.size(28.dp),
                    color = Color.White
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "›",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun AwarenessCardIcon(
    icon: AwarenessIcon,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.dp.toPx()
        val stroke = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (icon) {
            AwarenessIcon.RIVER -> {
                listOf(0.28f, 0.5f, 0.72f).forEach { y ->
                    val wave = Path().apply {
                        moveTo(size.width * 0.08f, size.height * y)
                        cubicTo(
                            size.width * 0.25f,
                            size.height * (y - 0.13f),
                            size.width * 0.36f,
                            size.height * (y + 0.13f),
                            size.width * 0.53f,
                            size.height * y
                        )
                        cubicTo(
                            size.width * 0.7f,
                            size.height * (y - 0.13f),
                            size.width * 0.81f,
                            size.height * (y + 0.13f),
                            size.width * 0.94f,
                            size.height * y
                        )
                    }
                    drawPath(path = wave, color = color, style = stroke)
                }
            }

            AwarenessIcon.WATER -> {
                val drop = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.08f)
                    cubicTo(
                        size.width * 0.43f,
                        size.height * 0.24f,
                        size.width * 0.2f,
                        size.height * 0.48f,
                        size.width * 0.2f,
                        size.height * 0.66f
                    )
                    cubicTo(
                        size.width * 0.2f,
                        size.height * 0.86f,
                        size.width * 0.33f,
                        size.height * 0.94f,
                        size.width * 0.5f,
                        size.height * 0.94f
                    )
                    cubicTo(
                        size.width * 0.67f,
                        size.height * 0.94f,
                        size.width * 0.8f,
                        size.height * 0.86f,
                        size.width * 0.8f,
                        size.height * 0.66f
                    )
                    cubicTo(
                        size.width * 0.8f,
                        size.height * 0.48f,
                        size.width * 0.57f,
                        size.height * 0.24f,
                        size.width * 0.5f,
                        size.height * 0.08f
                    )
                    close()
                }
                drawPath(path = drop, color = color, style = stroke)
            }

            AwarenessIcon.FLOW -> {
                val yValues = listOf(0.28f, 0.5f, 0.72f)
                yValues.forEachIndexed { index, y ->
                    val startX = if (index == 1) 0.1f else 0.2f
                    drawLine(
                        color = color,
                        start = androidx.compose.ui.geometry.Offset(size.width * startX, size.height * y),
                        end = androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * y),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.12f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.84f, size.height * 0.28f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.84f, size.height * 0.28f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.44f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            AwarenessIcon.STREAM -> {
                val house = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.48f)
                    lineTo(size.width * 0.5f, size.height * 0.18f)
                    lineTo(size.width * 0.82f, size.height * 0.48f)
                    moveTo(size.width * 0.27f, size.height * 0.42f)
                    lineTo(size.width * 0.27f, size.height * 0.68f)
                    moveTo(size.width * 0.73f, size.height * 0.42f)
                    lineTo(size.width * 0.73f, size.height * 0.68f)
                }
                drawPath(path = house, color = color, style = stroke)
                val stream = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.77f)
                    cubicTo(
                        size.width * 0.28f,
                        size.height * 0.62f,
                        size.width * 0.38f,
                        size.height * 0.9f,
                        size.width * 0.55f,
                        size.height * 0.77f
                    )
                    cubicTo(
                        size.width * 0.7f,
                        size.height * 0.66f,
                        size.width * 0.82f,
                        size.height * 0.88f,
                        size.width * 0.92f,
                        size.height * 0.77f
                    )
                }
                drawPath(path = stream, color = color, style = stroke)
            }

            AwarenessIcon.CAMERA -> {
                drawRoundRect(
                    color = color,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.08f, size.height * 0.28f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.84f, size.height * 0.6f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth * 1.5f),
                    style = stroke
                )
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.17f,
                    center = center,
                    style = stroke
                )
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.35f, size.height * 0.28f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.16f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.16f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.62f, size.height * 0.16f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.62f, size.height * 0.16f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.69f, size.height * 0.28f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun AwarenessDetail(
    article: AwarenessArticle,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box {
            Image(
                painter = painterResource(article.imageRes),
                contentDescription = article.imageDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
            Surface(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(14.dp)
                    .size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                IconButton(onClick = onBack) {
                    ThickBackIcon(color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "ERGENE AWARENESS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = article.summary,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = article.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = article.source,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
