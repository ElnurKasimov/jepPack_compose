package com.example.emptyproject

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Grid(
    cells: List<Int>,
    modifier: Modifier,
    onChipClick: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        repeat(4) { outerIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(4) { innerIndex ->
                    Chip(cells[outerIndex * 4 + innerIndex], onChipClick)
                }
            }
        }
    }
}


@Composable
fun Chip(
    cell: Int,
    onClick: (Int) -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val myBorder: BorderStroke?
    val myText: String
    val myColor: Long
    if (cell == 16) {
        myBorder = null
        myText = ""
        myColor = 0x00FEE1FC
    } else {
        myBorder = BorderStroke(
            5.dp, Brush.linearGradient(
                listOf(
                    Color(0xFFE5DBE4),
                    Color(0xFF71566E)
                )
            )
        )
        myText = cell.toString()
        myColor = 0xFFFEE1FC
    }
    Button(
        onClick = {
            onClick(cell)
            Log.i("MyButtonOnClick", "Clicking on chip with number $cell")
        },
        modifier = Modifier.size(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(myColor),
            contentColor = Color(0xFFFE5A8F)
        ),
        border = myBorder,
        shape = shape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = myText,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(
    device = "spec:parent=pixel_5",
    showBackground = true, showSystemUi = true, locale = "uk"
)
@Composable
fun StateHolderPreview(
    engine: FifteenEngine = object : FifteenEngine by FifteenEngine.Companion {
        override fun getInitialState(): List<Int> =
            buildList {
                repeat(14) {
                    add(it + 1)
                }
                add(16)
                add(15)
            }
    }
) {
    var cells by remember { mutableStateOf(engine.getInitialState()) }
    val isWin by remember { derivedStateOf {engine.isWin(cells)} }
    var move by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    fun onChipClick(chipNumber : Int) {
        val oldState = cells
        cells = engine.transitionState(cells, chipNumber)
        if(cells != oldState) {
            move++
        }
    }

    fun onResetClick() {
        cells = engine.getInitialState()
        startTime = System.currentTimeMillis()
        move = 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MyTopBar() },
        bottomBar = { MyBottomBar(::onResetClick) }
    ) { innerPadding ->
        Main(
            cells,
            isWin,
            move,
            startTime,
            onChipClick = ::onChipClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }

}