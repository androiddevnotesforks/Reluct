package work.racka.reluct.android.compose.components.cards.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import work.racka.reluct.android.compose.components.bottom_sheet.add_edit_goal.ReluctSelectionButton
import work.racka.reluct.android.compose.theme.Dimens
import work.racka.reluct.common.model.util.time.Week

@Composable
fun SelectedDaysOfWeekViewer(
    modifier: Modifier = Modifier,
    selectedDays: List<Week>,
    onUpdateDaysOfWeek: (List<Week>) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SmallPadding.size)
    ) {
        Week.values().forEach { day ->
            ReluctSelectionButton(
                modifier = Modifier.weight(1f),
                isSelected = selectedDays.contains(day),
                content = {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(
                                horizontal = Dimens.SmallPadding.size,
                                vertical = Dimens.MediumPadding.size
                            ),
                        text = day.dayAcronym.first().toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = {
                    val new = selectedDays.toMutableSet()
                    new.apply {
                        if (selectedDays.contains(day)) remove(day)
                        else add(day)
                    }
                    onUpdateDaysOfWeek(new.toList())
                }
            )
        }
    }
}