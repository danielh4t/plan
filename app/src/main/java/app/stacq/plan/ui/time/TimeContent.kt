package app.stacq.plan.ui.time

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.stacq.plan.domain.TaskTime
import app.stacq.plan.ui.task.Title


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeContent(
    groupedTasks: Map<String, List<TaskTime>>,
    modifier: Modifier = Modifier,
    handleTask: () -> Unit = {},
    onTaskClick: (TaskTime) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Title(
                onTitleClick = handleTask,
            )
        }
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                groupedTasks.forEach { (date, dayTasks) ->
                    stickyHeader {
                        Text(
                            text = date.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }


                    items(dayTasks) { task ->
                        TimeItem(
                            task = task,
                            onClick = { onTaskClick(task) }
                        )
                    }
                }
            }
        }
}