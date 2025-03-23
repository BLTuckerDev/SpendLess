package dev.bltucker.spendless.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.bltucker.spendless.R
import dev.bltucker.spendless.MainActivity

class CreateTransactionWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(
                        ImageProvider(R.drawable.widget_background)
                    )
                    .clickable {
                        // Launch MainActivity with intent to open CreateTransactionScreen
                        val intent = Intent(context, MainActivity::class.java).apply {
                            action = ACTION_OPEN_CREATE_TRANSACTION
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.padding(16.dp)
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(48.dp)
                            .background(
                                ImageProvider(R.drawable.widget_icon_background)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.wallet_money),
                            contentDescription = "Create Transaction",
                            modifier = GlanceModifier.size(24.dp)
                        )
                    }

                    Spacer(GlanceModifier.height(8.dp))

                    Text(
                        text = "Create",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 18.sp
                        )
                    )

                    Text(
                        text = "Transaction",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }

    companion object {
        const val ACTION_OPEN_CREATE_TRANSACTION = "dev.bltucker.spendless.ACTION_OPEN_CREATE_TRANSACTION"
    }
}

class CreateTransactionWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CreateTransactionWidget()
}