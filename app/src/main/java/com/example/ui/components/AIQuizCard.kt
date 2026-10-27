package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.data.model.QuizPayload
import com.example.data.model.QuizQuestion
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraError
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraSuccess
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import org.json.JSONObject

@Composable
fun AIQuizCard(
    payloadJson: String,
    modifier: Modifier = Modifier
) {
    val quiz = remember(payloadJson) { parseQuizPayload(payloadJson) } ?: return
    val context = LocalContext.current

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionIndex -> selectedOptionIndex

    val question = quiz.questions.getOrNull(currentQuestionIndex) ?: return
    val selectedOption = userAnswers[currentQuestionIndex]
    val isAnswered = selectedOption != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E1B2E),
                        Color(0xFF13131A)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(NeuraVibrantViolet.copy(alpha = 0.6f), NeuraCardBorder)),
                RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {
        // Quiz Header with Title & Question Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(NeuraVibrantViolet.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = NeuraIcons.Quiz,
                        contentDescription = null,
                        tint = NeuraVibrantViolet,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = quiz.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeuraTextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF252532), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${currentQuestionIndex + 1}/${quiz.questions.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeuraNeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Question text
        Text(
            text = question.question,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Options List
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.options.forEachIndexed { index, optionText ->
                val isSelected = selectedOption == index
                val isCorrect = index == question.correctIndex

                val (bgColor, borderColor, textColor) = when {
                    !isAnswered -> Triple(Color(0xFF181820), NeuraCardBorder.copy(alpha = 0.6f), NeuraTextPrimary)
                    isCorrect -> Triple(NeuraSuccess.copy(alpha = 0.18f), NeuraSuccess, Color.White)
                    isSelected -> Triple(NeuraError.copy(alpha = 0.18f), NeuraError, Color.White)
                    else -> Triple(Color(0xFF181820).copy(alpha = 0.5f), NeuraCardBorder.copy(alpha = 0.3f), NeuraTextMuted)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                        .clickable(enabled = !isAnswered) {
                            triggerHaptic(context)
                            userAnswers[currentQuestionIndex] = index
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("quiz_opt_$index"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val letter = ('A' + index).toString()
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (isAnswered && isCorrect) NeuraSuccess
                                else if (isAnswered && isSelected) NeuraError
                                else Color(0xFF282834),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = optionText,
                        fontSize = 13.5.sp,
                        color = textColor,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )

                    if (isAnswered && isCorrect) {
                        Icon(
                            imageVector = NeuraIcons.Check,
                            contentDescription = "Correct",
                            tint = NeuraSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (isAnswered && isSelected && !isCorrect) {
                        Icon(
                            imageVector = NeuraIcons.Close,
                            contentDescription = "Incorrect",
                            tint = NeuraError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Explanation & Navigation
        AnimatedVisibility(visible = isAnswered, enter = fadeIn(), exit = fadeOut()) {
            Column(modifier = Modifier.padding(top = 14.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1A26), RoundedCornerShape(12.dp))
                        .border(1.dp, NeuraCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Explication : ${question.explanation}",
                        fontSize = 12.5.sp,
                        color = NeuraTextSecondary,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (currentQuestionIndex < quiz.questions.size - 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .clip(RoundedCornerShape(18.dp))
                            .background(NeuraElectricBlue)
                            .clickable {
                                triggerHaptic(context)
                                currentQuestionIndex++
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("quiz_next_button")
                    ) {
                        Text(
                            text = "Question suivante →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                } else {
                    val correctCount = userAnswers.count { (qIdx, optIdx) ->
                        optIdx == quiz.questions[qIdx].correctIndex
                    }
                    Text(
                        text = "Quiz complété ! Score : $correctCount/${quiz.questions.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeuraNeonCyan,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

private fun parseQuizPayload(jsonStr: String): QuizPayload? {
    return try {
        val root = JSONObject(jsonStr)
        val title = root.optString("title", "Quiz interactif")
        val questionsArray = root.optJSONArray("questions") ?: return null
        val questions = mutableListOf<QuizQuestion>()
        for (i in 0 until questionsArray.length()) {
            val q = questionsArray.getJSONObject(i)
            val optArray = q.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optArray.length()) {
                options.add(optArray.getString(j))
            }
            questions.add(
                QuizQuestion(
                    id = q.optInt("id", i + 1),
                    question = q.getString("question"),
                    options = options,
                    correctIndex = q.getInt("correctIndex"),
                    explanation = q.optString("explanation", "")
                )
            )
        }
        QuizPayload(title, questions)
    } catch (_: Exception) {
        null
    }
}
