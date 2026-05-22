package com.example.tallerfinal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallerfinal.ui.theme.*

/**
 * Módulo 2: Tutorial Práctico con Simplificación Geométrica (HU 8.5).
 *
 * Características pedagógicas y técnicas:
 * 1. **Progresión de Aprendizaje:** Divide la anatomía en 4 pasos progresivos.
 * 2. **Superposición Geométrica Precisa:** Dibuja de manera interactiva cilindros, esferas
 *    y cajas superpuestas y alineadas sobre la silueta del cuerpo utilizando
 *    gráficos transparentes (`ShapeSphereColor`, `ShapeCylinderColor`, `ShapeBoxColor`).
 * 3. **Integración SQLite Room:** Guarda y actualiza el progreso real del estudiante al presionar
 *    el botón de "Completar Paso". Esto actualiza el porcentaje del Dashboard principal (HU 8.2).
 */
@Composable
fun TutorialScreen(
    completedSteps: List<Int>,              // Lista de IDs de pasos completados (desde Room)
    onSaveProgress: (Int, Boolean) -> Unit, // Acción asíncrona para guardar el progreso en Room
    onNavigateBack: () -> Unit
) {
    // Estado del paso actual en el tutorial (de 1 a 4)
    var currentStep by remember { mutableIntStateOf(1) }

    // Textos e instrucciones para cada paso interactivo del tutorial (HU 8.5.2)
    val stepDescriptions = listOf(
        TutorialStepInfo(
            title = "Paso 1: Cabeza y Cuello (Ovoides)",
            instruction = "La cabeza se simplifica mediante una forma de huevo o huevo invertido (ovoide). El cuello se representa como un cilindro alineado al centro de la base del cráneo.",
            tip = "Proporción: El cuello mide aproximadamente la mitad de la altura de la cabeza."
        ),
        TutorialStepInfo(
            title = "Paso 2: Caja Torácica y Tronco (Cajas)",
            instruction = "El torso se construye como una gran caja torácica ovalada y la pelvis como un bloque trapezoidal. Ambas estructuras se conectan mediante la espina dorsal flexible.",
            tip = "Proporción: Hay una distancia equivalente a una cabeza entre el pecho y la pelvis."
        ),
        TutorialStepInfo(
            title = "Paso 3: Extremidades y Articulaciones (Cilindros)",
            instruction = "Los brazos y piernas se dividen en cilindros rectos. Las articulaciones críticas (hombros, codos, rodillas) se posicionan como esferas perfectas para facilitar la rotación.",
            tip = "Proporción: El codo se alinea con la cintura y la muñeca con la pelvis."
        ),
        TutorialStepInfo(
            title = "Paso 4: Integración Completa",
            instruction = "Superpón todas las formas volumétricas juntas en un solo canon articulado para crear la pose básica del maniquí de dibujo anatómico antes de detallar los músculos.",
            tip = "Proporción: El cuerpo humano estándar mide 8 cabezas de alto."
        )
    )

    val currentStepInfo = stepDescriptions[currentStep - 1]
    val isCurrentStepCompleted = completedSteps.contains(currentStep)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra Superior de Navegación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("← Panel", color = TextPrimary)
                }

                Text(
                    text = "Tutorial de Estructura",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Indicador de Paso
                Text(
                    text = "$currentStep/4",
                    color = CyanPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✍️ CANVA DE SIMPLIFICACIÓN GEOMÉTRICA (HU 8.5.2)
            // Renderiza la figura anatómica base y le superpone las figuras correspondientes al paso.
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    
                    val modelHeight = 360.dp.toPx()
                    val modelWidth = modelHeight / 2.5f
                    
                    // 1. Dibujamos la silueta anatómica de fondo de forma sutil
                    drawAnatomicalSilhouette(
                        centerX = centerX,
                        centerY = centerY,
                        width = modelWidth,
                        height = modelHeight,
                        viewType = "FRENTE"
                    )

                    // 2. Superposición de Formas Geométricas Transparentes en base al paso activo (HU 8.5.2)
                    val headRadius = modelHeight / 16f
                    val topY = centerY - (modelHeight / 2f)
                    val torsoTopY = topY + (headRadius * 2.5f)
                    val torsoHeight = modelHeight * 0.3f
                    val pelvicTopY = torsoTopY + torsoHeight
                    val pelvicHeight = modelHeight * 0.15f
                    val legHeight = modelHeight * 0.4f
                    val legY = pelvicTopY + pelvicHeight

                    when (currentStep) {
                        1 -> {
                            // PASO 1: Cabeza y Cuello (Esfera y Cilindro)
                            // Esfera de la cabeza
                            drawCircle(
                                color = ShapeSphereColor,
                                radius = headRadius * 1.1f,
                                center = Offset(centerX, topY + headRadius)
                            )
                            // Cilindro del cuello
                            drawRect(
                                color = ShapeCylinderColor,
                                topLeft = Offset(centerX - 15f, topY + (headRadius * 1.8f)),
                                size = Size(30f, headRadius * 0.8f)
                            )
                        }
                        2 -> {
                            // PASO 2: Torso y Pelvis (Cajas)
                            // Caja Torácica
                            drawRect(
                                color = ShapeBoxColor,
                                topLeft = Offset(centerX - (modelWidth * 0.35f), torsoTopY + 10f),
                                size = Size(modelWidth * 0.7f, torsoHeight - 20f),
                                style = Stroke(width = 6f)
                            )
                            drawRect(
                                color = ShapeBoxColor.copy(alpha = 0.2f),
                                topLeft = Offset(centerX - (modelWidth * 0.35f), torsoTopY + 10f),
                                size = Size(modelWidth * 0.7f, torsoHeight - 20f)
                            )
                            
                            // Bloque de la Pelvis
                            val pathPelvis = Path().apply {
                                moveTo(centerX - (modelWidth * 0.3f), pelvicTopY)
                                lineTo(centerX + (modelWidth * 0.3f), pelvicTopY)
                                lineTo(centerX + (modelWidth * 0.2f), pelvicTopY + pelvicHeight)
                                lineTo(centerX - (modelWidth * 0.2f), pelvicTopY + pelvicHeight)
                                close()
                            }
                            drawPath(path = pathPelvis, color = ShapeBoxColor)
                        }
                        3 -> {
                            // PASO 3: Extremidades y Articulaciones (Cilindros y Esferas)
                            // Articulación Hombros (Esferas)
                            drawCircle(color = ShapeSphereColor, radius = 18f, center = Offset(centerX - (modelWidth * 0.38f), torsoTopY))
                            drawCircle(color = ShapeSphereColor, radius = 18f, center = Offset(centerX + (modelWidth * 0.38f), torsoTopY))
                            
                            // Articulación Rodillas (Esferas)
                            drawCircle(color = ShapeSphereColor, radius = 16f, center = Offset(centerX - (modelWidth * 0.12f), legY + (legHeight * 0.5f)))
                            drawCircle(color = ShapeSphereColor, radius = 16f, center = Offset(centerX + (modelWidth * 0.12f), legY + (legHeight * 0.5f)))
                            
                            // Huesos de las piernas (Cilindros de conexión)
                            drawLine(color = ShapeCylinderColor, start = Offset(centerX - (modelWidth * 0.12f), legY), end = Offset(centerX - (modelWidth * 0.12f), legY + legHeight), strokeWidth = 14f)
                            drawLine(color = ShapeCylinderColor, start = Offset(centerX + (modelWidth * 0.12f), legY), end = Offset(centerX + (modelWidth * 0.12f), legY + legHeight), strokeWidth = 14f)
                        }
                        4 -> {
                            // PASO 4: Todo integrado
                            // Cabeza
                            drawCircle(color = ShapeSphereColor, radius = headRadius * 1.05f, center = Offset(centerX, topY + headRadius))
                            // Cuello
                            drawRect(color = ShapeCylinderColor, topLeft = Offset(centerX - 12f, topY + (headRadius * 1.8f)), size = Size(24f, headRadius * 0.7f))
                            // Torso
                            drawRect(color = ShapeBoxColor.copy(alpha = 0.25f), topLeft = Offset(centerX - (modelWidth * 0.32f), torsoTopY + 10f), size = Size(modelWidth * 0.64f, torsoHeight - 20f))
                            // Pelvis
                            val pathPelvis = Path().apply {
                                moveTo(centerX - (modelWidth * 0.28f), pelvicTopY)
                                lineTo(centerX + (modelWidth * 0.28f), pelvicTopY)
                                lineTo(centerX + (modelWidth * 0.18f), pelvicTopY + pelvicHeight)
                                lineTo(centerX - (modelWidth * 0.18f), pelvicTopY + pelvicHeight)
                                close()
                            }
                            drawPath(path = pathPelvis, color = ShapeBoxColor.copy(alpha = 0.25f))
                            // Esferas de articulaciones
                            drawCircle(color = ShapeSphereColor, radius = 15f, center = Offset(centerX - (modelWidth * 0.35f), torsoTopY))
                            drawCircle(color = ShapeSphereColor, radius = 15f, center = Offset(centerX + (modelWidth * 0.35f), torsoTopY))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📖 INSTRUCCIONES Y RECOMENDACIONES (ZONA DESLIZABLE)
            // Adaptada responsivamente con scroll independiente por si el texto es largo.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = currentStepInfo.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = currentStepInfo.instruction,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Consejo de Oro
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyanPrimary.copy(alpha = 0.08f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 ${currentStepInfo.tip}",
                            color = CyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 💾 ACCIONES DE CONTROL: Atras / Guardar / Siguiente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Anterior
                Button(
                    onClick = { if (currentStep > 1) currentStep-- },
                    enabled = currentStep > 1,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Anterior", color = TextPrimary)
                }

                // Botón: MARCAR COMO COMPLETADO (Guarda en SQLite Room en tiempo real)
                Button(
                    onClick = {
                        onSaveProgress(currentStep, !isCurrentStepCompleted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCurrentStepCompleted) EmeraldSecondary else CyanPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isCurrentStepCompleted) "✓ Paso Completado" else "Marcar como Completado",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Botón Siguiente
                Button(
                    onClick = { if (currentStep < 4) currentStep++ },
                    enabled = currentStep < 4,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Siguiente", color = TextPrimary)
                }
            }
        }
    }
}

/**
 * Clase contenedora simple para almacenar el desglose de textos de cada paso.
 */
data class TutorialStepInfo(
    val title: String,
    val instruction: String,
    val tip: String
)
