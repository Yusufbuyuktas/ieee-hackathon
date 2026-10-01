package com.hackathon_ieee.myapplication.feature.more

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.R
import com.hackathon_ieee.myapplication.ui.components.SubtlePanel
import com.hackathon_ieee.myapplication.ui.components.ThickBackIcon

internal data class ImpactStory(
    @param:DrawableRes val imageRes: Int,
    val imageDescription: String,
    val city: String,
    val country: String,
    val year: String,
    val title: String,
    val body: String,
    val whyItMatters: String,
    val source: String
)

internal val impactStories = listOf(
    ImpactStory(
        imageRes = R.drawable.london,
        imageDescription = "Tower Bridge over the River Thames in London",
        city = "London",
        country = "United Kingdom",
        year = "1854",
        title = "A Map That Saved Lives",
        body = "As cholera spread through a London neighborhood, physician John Snow began marking each known case on a map. The illnesses formed a striking cluster around the Broad Street water pump.\n\nBy connecting health records with location data, Snow helped identify contaminated water as the likely source. The pump handle was removed, and his investigation became one of history's most influential examples of using place to understand public health.",
        whyItMatters = "A single case may appear isolated. Mapping many observations can reveal the pattern connecting them.",
        source = "Documented by the Centers for Disease Control and Prevention"
    ),
    ImpactStory(
        imageRes = R.drawable.minamata,
        imageDescription = "Minamata Bay and the surrounding city",
        city = "Minamata",
        country = "Japan",
        year = "1950s",
        title = "When Symptoms Revealed the Truth",
        body = "Residents living around Minamata Bay began witnessing something deeply unsettling. Fish behaved strangely, birds struggled to fly, and cats appeared to lose control of their movements. Soon, people developed numbness, tremors, impaired vision, and difficulty walking or speaking.\n\nDoctors and scientists eventually connected these symptoms to methylmercury released into the bay and accumulated in locally caught fish and shellfish. What first looked like unrelated illnesses became evidence of a devastating connection between industrial pollution, the environment, and human health.",
        whyItMatters = "Connecting environmental exposure with clinical symptoms can uncover dangers that might otherwise remain hidden.",
        source = "Documented by the World Health Organization"
    ),
    ImpactStory(
        imageRes = R.drawable.michigan,
        imageDescription = "The Flint River running through Flint, Michigan",
        city = "Flint",
        country = "United States",
        year = "2014–2015",
        title = "A Community That Refused to Be Ignored",
        body = "After Flint changed its drinking-water source, residents began reporting unusual color, odor, and taste. Many felt that their concerns were not being taken seriously.\n\nIndependent water testing later revealed elevated lead levels. Healthcare researchers also found an increase in elevated blood lead levels among young children after the change in water source. Community observations, environmental measurements, and medical evidence came together to reveal a crisis that individual complaints alone could not fully expose.",
        whyItMatters = "Community reports become harder to ignore when environmental measurements and health data support them.",
        source = "Documented by the Centers for Disease Control and Prevention"
    ),
    ImpactStory(
        imageRes = R.drawable.walkerton,
        imageDescription = "The Saugeen River near Walkerton, Ontario",
        city = "Walkerton",
        country = "Canada",
        year = "2000",
        title = "The Cost of a Missed Warning",
        body = "When disease-causing bacteria entered Walkerton's drinking-water system, thousands of residents became ill. Seven people died, and more than 2,300 experienced illness linked to the outbreak.\n\nThe investigation revealed that the tragedy was not caused by a single failure. Contamination, inadequate treatment, poor monitoring, and delayed communication allowed the danger to reach the community. The disaster transformed how drinking water was monitored and protected across Ontario.",
        whyItMatters = "Early warnings, transparent monitoring, and timely communication can prevent contamination from becoming a public-health emergency.",
        source = "Documented by Health Canada"
    ),
    ImpactStory(
        imageRes = R.drawable.milwaukee,
        imageDescription = "Milwaukee beside Lake Michigan",
        city = "Milwaukee",
        country = "United States",
        year = "1993",
        title = "When a Pattern Became an Outbreak",
        body = "Across Milwaukee, emergency rooms began receiving more patients with severe gastrointestinal illness. Schools and workplaces reported unusual levels of absence, while pharmacies experienced increased demand for antidiarrheal medicine.\n\nThese scattered signals were part of the same crisis. A failure in water filtration had allowed the parasite Cryptosporidium to pass into the public water supply. An estimated 403,000 people became ill. The outbreak demonstrated how healthcare activity and environmental monitoring can expose a shared source affecting an entire city.",
        whyItMatters = "Patterns across healthcare reports can reveal an environmental threat before individual cases tell the full story.",
        source = "Documented by the Centers for Disease Control and Prevention"
    )
)

@Composable
fun MoreScreen(
    modifier: Modifier = Modifier
) {
    MoreFeed(modifier = modifier)
}

@Composable
private fun MoreFeed(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, top = 18.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WaterConnectsPanel()
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Meet the Team",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Column {
                    TeamMemberRow(
                        name = "Serranur Türkoğlu",
                        role = "Mobile Development",
                        githubUrl = "https://github.com/serra888"
                    )
                    TeamDivider()
                    TeamMemberRow(
                        name = "Ahmet Hilmi Güler",
                        role = "Backend & FHIR/Data",
                        githubUrl = "https://github.com/ahilmii"
                    )
                    TeamDivider()
                    TeamMemberRow(
                        name = "Yusuf Büyüktaş",
                        role = "Web Development",
                        githubUrl = "https://github.com/Yusufbuyuktas"
                    )
                    TeamDivider()
                    TeamMemberRow(
                        name = "Faruk Turnalı",
                        role = "AI Development",
                        githubUrl = "https://github.com/farukk06"
                    )
                }
            }
        }

        Text(
            text = "Version 1.0",
            modifier = Modifier.padding(horizontal = 20.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
internal fun WaterConnectsPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Our Purpose",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "WATER CONNECTS US ALL",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "RiverGuard brings citizens, environmental monitoring, healthcare professionals, and public action into one shared space. A citizen's report can make an overlooked problem visible. Environmental data can reveal the larger pattern. A doctor can better understand the conditions surrounding a community's health. Together, these perspectives can turn concern into evidence—and evidence into earlier action.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "You do not need to be a scientist to make a difference. Sometimes change begins by simply noticing, caring, and choosing to speak up.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Every observation matters. Every voice counts. Every drop connects us.",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
internal fun StoryCarousel(
    onStoryClick: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    cardWidth: androidx.compose.ui.unit.Dp = 320.dp
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val visibleStoryIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex.coerceIn(impactStories.indices) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(impactStories) { index, story ->
                StoryCard(
                    story = story,
                    width = cardWidth,
                    onClick = { onStoryClick(index) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            impactStories.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (index == visibleStoryIndex) 9.dp else 6.dp)
                        .background(
                            color = if (index == visibleStoryIndex) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun StoryCard(
    story: ImpactStory,
    width: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(190.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(story.imageRes),
            contentDescription = story.imageDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.48f to Color.Black.copy(alpha = 0.18f),
                        1f to Color.Black.copy(alpha = 0.88f)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${story.city},",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = story.country,
                    modifier = Modifier.padding(bottom = 2.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.82f)
                )
            }
            Text(
                text = story.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun StoryDetail(
    story: ImpactStory,
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
                painter = painterResource(story.imageRes),
                contentDescription = story.imageDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.36f), Color.Transparent)
                        )
                    )
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
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${story.city},",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = story.country,
                    modifier = Modifier.padding(bottom = 2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = story.year,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = story.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = story.body,
                style = MaterialTheme.typography.bodyLarge
            )

            SubtlePanel {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "WHY IT MATTERS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = story.whyItMatters,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Text(
                text = story.source,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun TeamDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    )
}

@Composable
private fun TeamMemberRow(
    name: String,
    role: String,
    githubUrl: String
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = { uriHandler.openUri(githubUrl) }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_github),
                contentDescription = "Open $name's GitHub profile",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
