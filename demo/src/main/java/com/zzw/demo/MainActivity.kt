package com.zzw.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zzw.demo.ui.theme.ComposeRefreshTheme
import com.zzw.demo.ui.theme.Purple200
import com.zzw.demo.ui.theme.Teal200
import com.zzw.demo.ui.theme.c_f6f7f8

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeRefreshTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    Column {
        val context = LocalContext.current
        Text(text = "   经典样式", Modifier.background(Purple200).padding(0.dp, 20.dp).fillMaxWidth().clickable {
            context.startActivity(Intent(context, ClassicsActivity::class.java))
        })
        Divider(color = c_f6f7f8)
        Text(text = "   经典样式-到底自动加载", Modifier.background(Purple200).padding(0.dp, 20.dp).fillMaxWidth().clickable {
            context.startActivity(Intent(context, ClassicsAutoActivity::class.java))
        })
        Divider(color = c_f6f7f8)
        Text(text = "   动画样式", Modifier.background(Teal200).padding(0.dp, 20.dp).fillMaxWidth().clickable {
            context.startActivity(Intent(context, CartoonActivity::class.java))
        })
        Divider(color = c_f6f7f8)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeRefreshTheme {
        Greeting()
    }
}