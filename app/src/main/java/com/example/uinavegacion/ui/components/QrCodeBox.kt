package com.example.uinavegacion.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun QrCodeBox(data: String, modifier: Modifier = Modifier) {
    val size = 512
    val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size, hints)
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.Black.hashCode() else Color.White.hashCode())
        }
    }
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Código QR", modifier = modifier.aspectRatio(1f))
}