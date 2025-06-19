import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LoadingWithImageBar(
    modifier: Modifier = Modifier,
    imageResId: Int,
    loadingTexts: List<String> = listOf("Cargando...")
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(1f)
        ){
            PulseAnimation(
                modifier = Modifier.matchParentSize()
            )
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Logo Loading",
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .graphicsLayer {
                        this.alpha = 1f
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        AnimatedLoadingText(texts = loadingTexts)
    }
}

@Composable
fun AnimatedLoadingText(
    texts: List<String> = listOf("Cargando..."),
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    var currentTextIndex by remember { mutableIntStateOf(0) }
    
    // Cambia el texto cada 4 segundos de forma aleatoria
    LaunchedEffect(texts) {
        while (true) {
            delay(3000)
            currentTextIndex = Random.nextInt(texts.size)
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "textAlpha")
    
    // Animación de alfa para el efecto pulsante
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha animation"
    )

    val currentText = texts[currentTextIndex]

    Text(
        text = currentText,
        color = color.copy(alpha = alpha),
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}

@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000),
        ),
        label = "progress animation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = progress
                scaleY = progress
                alpha = progress
            }
            .border(
                width = 6.dp,
                color = color.copy(alpha = 1f - progress),
                shape = CircleShape
            )
    )
}
