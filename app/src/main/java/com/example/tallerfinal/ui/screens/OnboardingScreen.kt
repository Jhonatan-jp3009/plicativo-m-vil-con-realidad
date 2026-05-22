package com.example.tallerfinal.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallerfinal.ui.theme.CyanPrimary
import com.example.tallerfinal.ui.theme.DarkBackground
import com.example.tallerfinal.ui.theme.DarkSurface
import com.example.tallerfinal.ui.theme.TextPrimary
import com.example.tallerfinal.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Pantalla de Incorporación (Onboarding) de máximo 4 pantallas (HU 8.3).
 * Explica las funciones principales antes del primer uso:
 * 1. Modelos 3D y Canones Anatómicos (HU 8.1)
 * 2. Visualizador y Simulador de Realidad Aumentada (HU 8.4)
 * 3. Tutoriales con Simplificación Geométrica (HU 8.5)
 * 4. Carga de Referencias Fotográficas locales (HU 8.6)
 *
 * Diseñada de forma 100% responsiva (usando Scroll y ponderaciones de peso) para
 * adaptarse perfectamente a pantallas de celulares pequeños o tablets en cualquier orientación.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit // Acción que se ejecuta al terminar o saltar el onboarding
) {
    // Definimos los datos de las 4 pantallas informativas
    val pages = listOf(
        OnboardingPageData(
            title = "Aprende Proporciones 3D",
            description = "Observa el cuerpo humano en 3D desde múltiples ángulos (frente, lado y espalda) con líneas de guía anatómicas precisas.",
            icon = "🧍"
        ),
        OnboardingPageData(
            title = "Realidad Aumentada (RA)",
            description = "Visualiza y manipula el modelo en tu entorno real detectando superficies y rotándolo con simples gestos táctiles.",
            icon = "🕶️"
        ),
        OnboardingPageData(
            title = "Simplificación Geométrica",
            description = "Domina la construcción anatómica paso a paso superponiendo ovoides, cajas y cilindros sencillos en cada etapa.",
            icon = "📐"
        ),
        OnboardingPageData(
            title = "Importa tus Referencias",
            description = "Carga fotos de tu galería o cámara local offline y superpone el canon de 8 cabezas para comparar tus propios dibujos.",
            icon = "📷"
        )
    )

    // Estado del cargador de páginas (Pager) - Puntos indicadores inferiores
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fila Superior: Botón Saltar (Skip)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinished) {
                    Text(
                        text = "Saltar",
                        color = CyanPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Cuerpo Central: Carrusel deslizable de páginas
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                OnboardingPage(data = pages[pageIndex])
            }

            // Barra Inferior: Indicador de puntos (Dots Indicator) y botones de navegación
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicador de Puntos (Dots Indicator)
                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (isSelected) 18.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CyanPrimary else TextSecondary.copy(alpha = 0.5f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fila de Botones: Atrás / Siguiente (Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Atrás
                    if (pagerState.currentPage > 0) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ) {
                            Text(
                                text = "Atrás",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        // Espacio vacío para alinear a la derecha el botón Siguiente
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // Botón Siguiente / Empezar
                    Button(
                        onClick = {
                            if (pagerState.currentPage < pages.size - 1) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onFinished()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (pagerState.currentPage == pages.size - 1) "Empezar" else "Siguiente",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Representa una única página del onboarding.
 * Adaptada con Scroll vertical para que, si el dispositivo es pequeño o se rota
 * de forma horizontal, el contenido nunca se desborde ni se corte.
 */
@Composable
fun OnboardingPage(data: OnboardingPageData) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tarjeta con efecto Glassmorphic que aloja el ícono/emoji
        Card(
            modifier = Modifier
                .size(140.dp)
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = data.icon,
                    fontSize = 64.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Título de la sección
        Text(
            text = data.title,
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción de la funcionalidad
        Text(
            text = data.description,
            color = TextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Modelo de datos simple para estructurar las páginas del carrusel.
 */
data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: String
)
