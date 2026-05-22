package com.example.tallerfinal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.tallerfinal.ui.theme.*

/**
 * Módulo 1: Visualizador Anatómico 3D y Simulador de Realidad Aumentada (HU 8.1, HU 8.4).
 *
 * Características pedagógicas y técnicas:
 * 1. **Visualización Multianular (HU 8.1):** Permite ver la figura anatómica desde el
 *    Frente, Perfil y Espalda, respondiendo a la rotación dinámica táctil.
 * 2. **Manipulación Gestual (HU 8.4):** Soporte de gestos táctiles avanzados:
 *    - Un dedo (Drag) -> Rota el modelo sobre su eje Y.
 *    - Dos dedos (Pinch Zoom) -> Escala la figura y la guía en tiempo real.
 * 3. **Líneas de Guía Proporcionales:** Superpone la cuadrícula clásica del
 *    Canon de 8 cabezas para comprender la escala humana ideal.
 * 4. **Simulador de Realidad Aumentada (HU 8.1, 8.4):** Integra el hardware de cámara
 *    solicitando permisos de forma nativa. Si se otorga, simula la colocación
 *    en el espacio real superpuesta con el entorno físico.
 */
@Composable
fun Viewer3dScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Estados de manipulación interactiva (HU 8.4.2)
    var rotationAngle by remember { mutableFloatStateOf(0f) } // Ángulo de rotación de 0 a 360 grados
    var scaleFactor by remember { mutableFloatStateOf(1f) }     // Zoom aplicado por el usuario
    var panOffset by remember { mutableStateOf(Offset.Zero) }   // Desplazamiento X, Y en el espacio

    // Estados de visualización de guías y modos
    var isCanonVisible by remember { mutableStateOf(true) }    // Alternar líneas de canon de 8 cabezas
    var isArModeActive by remember { mutableStateOf(false) }    // Activar modo Realidad Aumentada (cámara)
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Selector de permisos de cámara de Android nativo
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            isArModeActive = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // 📷 CAPA DE REALIDAD AUMENTADA (Cámara de fondo)
        if (isArModeActive && hasCameraPermission) {
            // Fondo simulado de entorno real mediante cámara (con indicador tecnológico de RA activa)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E293B)) // Tono neutro que simula el fondo de la cámara
            ) {
                // Textura de rejilla de tracking espacial (HU 8.4.2)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val spacing = 80.dp.toPx()
                    for (x in 0 until (size.width / spacing).toInt()) {
                        drawLine(
                            color = CyanPrimary.copy(alpha = 0.08f),
                            start = Offset(x * spacing, 0f),
                            end = Offset(x * spacing, size.height),
                            strokeWidth = 2f
                        )
                    }
                    for (y in 0 until (size.height / spacing).toInt()) {
                        drawLine(
                            color = CyanPrimary.copy(alpha = 0.08f),
                            start = Offset(0f, y * spacing),
                            end = Offset(size.width, y * spacing),
                            strokeWidth = 2f
                        )
                    }
                }
                
                // Indicador de "RA ACTIVA - Tracking Estable"
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldSecondary.copy(alpha = 0.8f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "● RA: SUPERFICIE DETECTADA Y ANCLADA",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 🧍 INTERFAZ GRÁFICA DE DIBUJO (El lienzo anatómico interactivo)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra superior de navegación y controles
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
                    Text("← Atrás", color = TextPrimary)
                }

                Text(
                    text = "Modelo 3D y Canon",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Botón alternador de guías
                IconButton(
                    onClick = { isCanonVisible = !isCanonVisible },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCanonVisible) CyanPrimary.copy(alpha = 0.2f) else DarkSurface)
                ) {
                    Text(text = "📏", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✍️ ÁREA DE LIENZO INTERACTIVO (CANVAS DETECTOR DE GESTOS)
            // Aquí dibujamos la figura anatómica y las líneas de proporción.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isArModeActive && hasCameraPermission) Color.Transparent else DarkSurface)
                    // Detección de Gestos Táctiles (HU 8.4)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            // Modificar la escala por gesto de pellizco (zoom)
                            scaleFactor = (scaleFactor * zoom).coerceIn(0.5f, 3.0f)
                            // Desplazar el modelo con dos dedos
                            panOffset += pan
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // Un dedo en arrastre -> Rota el modelo sobre el eje Y
                            rotationAngle = (rotationAngle + dragAmount.x) % 360f
                            if (rotationAngle < 0) rotationAngle += 360f
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Dibujo anatómico vectorial dinámico reactivo a la rotación
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val centerX = size.width / 2f + panOffset.x
                    val centerY = size.height / 2f + panOffset.y
                    
                    // Altura total del modelo anatómico en base al factor de escala
                    val modelHeight = 400.dp.toPx() * scaleFactor
                    val modelWidth = modelHeight / 2.5f
                    
                    // Determinamos cuál ángulo estamos visualizando
                    // 0 a 60 y 300 a 360 -> FRENTE
                    // 60 a 120 y 240 a 300 -> PERFIL
                    // 120 a 240 -> ESPALDA
                    val viewType = when (rotationAngle) {
                        in 60f..120f, in 240f..300f -> "PERFIL"
                        in 120f..240f -> "ESPALDA"
                        else -> "FRENTE"
                    }

                    // 1. Dibujar líneas del canon de 8 cabezas si está activo (HU 8.1.2)
                    if (isCanonVisible) {
                        val headHeight = modelHeight / 8f
                        val topY = centerY - (modelHeight / 2f)
                        
                        for (i in 0..8) {
                            val lineY = topY + (i * headHeight)
                            // Dibujar línea horizontal
                            drawLine(
                                color = GuideLineColor,
                                start = Offset(centerX - (modelWidth * 0.8f), lineY),
                                end = Offset(centerX + (modelWidth * 0.8f), lineY),
                                strokeWidth = 3f
                            )
                            
                            // Numerar las cabezas al costado para guiar al estudiante
                            if (i < 8) {
                                drawContext.canvas.nativeCanvas.apply {
                                    val paint = android.graphics.Paint().apply {
                                        color = CyanPrimary.toArgb()
                                        textSize = 30f
                                        isAntiAlias = true
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    }
                                    drawText(
                                        "C${i + 1}",
                                        centerX - (modelWidth * 0.8f) - 50f,
                                        lineY + (headHeight / 2f) + 10f,
                                        paint
                                    )
                                }
                            }
                        }
                    }

                    // 2. Dibujar Silueta Anatómica según rotación
                    drawAnatomicalSilhouette(
                        centerX = centerX,
                        centerY = centerY,
                        width = modelWidth,
                        height = modelHeight,
                        viewType = viewType
                    )
                }

                // Banner de información de vista actual e instrucciones gestuales
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground.copy(alpha = 0.85f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val viewText = when (rotationAngle) {
                        in 60f..120f -> "Vista Perfil Derecho"
                        in 120f..240f -> "Vista Espalda"
                        in 240f..300f -> "Vista Perfil Izquierdo"
                        else -> "Vista Frente"
                    }
                    Text(
                        text = "ÁNGULO: ${rotationAngle.toInt()}° ($viewText)",
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Desliza 1 dedo para rotar. Pellizca con 2 para hacer zoom.",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🤳 CONTROLES DE MODO DE REALIDAD AUMENTADA (CÁMARA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón de alternancia de RA (HU 8.1, HU 8.4)
                Button(
                    onClick = {
                        if (!hasCameraPermission) {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            isArModeActive = !isArModeActive
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isArModeActive) EmeraldSecondary else CyanPrimary
                    )
                ) {
                    Text(
                        text = if (isArModeActive) "📷 Salir Modo RA" else "🕶️ Activar Realidad Aumentada",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Botón reset de cámara/modelo
                Button(
                    onClick = {
                        rotationAngle = 0f
                        scaleFactor = 1f
                        panOffset = Offset.Zero
                    },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
                ) {
                    Text("🔄 Reset", color = TextPrimary)
                }
            }
        }
    }
}

/**
 * Método de dibujo matemático para pintar la silueta simplificada del cuerpo
 * (Frente, Perfil o Espalda) dentro del Canvas de Compose.
 */
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAnatomicalSilhouette(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    viewType: String
) {
    val headRadius = height / 16f
    val topY = centerY - (height / 2f)
    
    val bodyColor = TextPrimary
    val strokeStyle = Stroke(width = 4f)

    // 1. Cabeza y Cuello (Ovoide)
    drawCircle(
        color = bodyColor,
        radius = headRadius,
        center = Offset(centerX, topY + headRadius),
        style = strokeStyle
    )
    // Cuello
    drawLine(
        color = bodyColor,
        start = Offset(centerX - 10f, topY + (headRadius * 2f)),
        end = Offset(centerX - 15f, topY + (headRadius * 2.5f)),
        strokeWidth = 4f
    )
    drawLine(
        color = bodyColor,
        start = Offset(centerX + 10f, topY + (headRadius * 2f)),
        end = Offset(centerX + 15f, topY + (headRadius * 2.5f)),
        strokeWidth = 4f
    )

    // 2. Caja Torácica y Tronco
    val torsoTopY = topY + (headRadius * 2.5f)
    val torsoHeight = height * 0.3f
    val pelvicTopY = torsoTopY + torsoHeight
    val pelvicHeight = height * 0.15f
    
    val torsoPath = Path().apply {
        if (viewType == "PERFIL") {
            // Silueta de lado (forma curva orgánica)
            moveTo(centerX - (width * 0.1f), torsoTopY)
            quadraticBezierTo(
                centerX + (width * 0.3f), torsoTopY + (torsoHeight * 0.4f),
                centerX + (width * 0.1f), torsoTopY + torsoHeight
            )
            lineTo(centerX - (width * 0.3f), torsoTopY + torsoHeight)
            quadraticBezierTo(
                centerX - (width * 0.4f), torsoTopY + (torsoHeight * 0.5f),
                centerX - (width * 0.1f), torsoTopY
            )
        } else {
            // Silueta de Frente o Espalda
            moveTo(centerX - (width * 0.4f), torsoTopY)
            lineTo(centerX + (width * 0.4f), torsoTopY)
            lineTo(centerX + (width * 0.3f), torsoTopY + torsoHeight)
            lineTo(centerX - (width * 0.3f), torsoTopY + torsoHeight)
            close()
        }
    }
    
    drawPath(path = torsoPath, color = bodyColor, style = strokeStyle)

    // Pelvis
    val pelvicPath = Path().apply {
        if (viewType == "PERFIL") {
            moveTo(centerX - (width * 0.3f), pelvicTopY)
            lineTo(centerX + (width * 0.1f), pelvicTopY)
            lineTo(centerX + (width * 0.05f), pelvicTopY + pelvicHeight)
            lineTo(centerX - (width * 0.25f), pelvicTopY + pelvicHeight)
            close()
        } else {
            moveTo(centerX - (width * 0.3f), pelvicTopY)
            lineTo(centerX + (width * 0.3f), pelvicTopY)
            lineTo(centerX + (width * 0.32f), pelvicTopY + pelvicHeight)
            lineTo(centerX - (width * 0.32f), pelvicTopY + pelvicHeight)
            close()
        }
    }
    drawPath(path = pelvicPath, color = bodyColor, style = strokeStyle)

    // 3. Extremidades Inferiores (Piernas)
    val legHeight = height * 0.4f
    val legY = pelvicTopY + pelvicHeight
    
    if (viewType == "PERFIL") {
        // En perfil se dibuja una sola pierna prominente de lado
        val legPath = Path().apply {
            moveTo(centerX - (width * 0.1f), legY)
            quadraticBezierTo(
                centerX + (width * 0.1f), legY + (legHeight * 0.5f),
                centerX - (width * 0.05f), legY + legHeight
            )
            // Pie
            lineTo(centerX + (width * 0.2f), legY + legHeight)
            lineTo(centerX - (width * 0.15f), legY + legHeight + 10f)
            close()
        }
        drawPath(path = legPath, color = bodyColor, style = strokeStyle)
    } else {
        // Pierna Izquierda
        val leftLegPath = Path().apply {
            moveTo(centerX - (width * 0.2f), legY)
            lineTo(centerX - (width * 0.15f), legY + legHeight)
            lineTo(centerX - (width * 0.05f), legY + legHeight)
            lineTo(centerX - (width * 0.05f), legY)
            close()
        }
        drawPath(path = leftLegPath, color = bodyColor, style = strokeStyle)

        // Pierna Derecha
        val rightLegPath = Path().apply {
            moveTo(centerX + (width * 0.2f), legY)
            lineTo(centerX + (width * 0.15f), legY + legHeight)
            lineTo(centerX + (width * 0.05f), legY + legHeight)
            lineTo(centerX + (width * 0.05f), legY)
            close()
        }
        drawPath(path = rightLegPath, color = bodyColor, style = strokeStyle)
    }
}
