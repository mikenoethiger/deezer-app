package fhnw.emoba.freezerapp.ui

// inspired by androidx.compose.animation.Crossfade

import androidx.compose.animation.*
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.invalidate
import androidx.compose.runtime.key
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.util.fastForEach

@ExperimentalAnimationApi
@Composable
fun <T> CrossfadeSlide(
    current: T,
    forward: Boolean = true,
    modifier: Modifier = Modifier,
    animation: AnimationSpec<Float> = tween(),
    children: @Composable (T) -> Unit
) {
    val state = remember { CrossfadeState<T>() }
    if (current != state.current) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()
        keys.mapTo(state.items) { key ->
            CrossfadeAnimationItem(key) { children ->
                if (forward) {
                    if (key == current) {
                        // slide current screen in from right
                        SlideInRight { children() }
                    } else {
                        // slide old screen out left
                        SlideOutLeft { children() }
                    }
                } else {
                    // TODO apply same logic as in forward but reversed
                    if (key == current) {
                        children()
                    }
                }
            }
//            CrossfadeAnimationItem(key) { children ->
//                val opacity = animatedOpacity(
//                    animation = animation,
//                    visible = key == current,
//                    onAnimationFinish = {
//                        if (key == state.current) {
//                            // leave only the current in the list
//                            state.items.removeAll { it.key != state.current }
//                            state.invalidate()
//                        }
//                    }
//                )
//                Box(Modifier.drawOpacity(opacity.value)) {
//                    children()
//                }
//            }
        }
    }
    Box(modifier) {
        state.invalidate = invalidate
        state.items.fastForEach { (item, opacity) ->
            key(item) {
                opacity {
                    children(item)
                }
            }
        }
    }
}

private class CrossfadeState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: Any? = Any()
    var items = mutableListOf<CrossfadeAnimationItem<T>>()
    var invalidate: () -> Unit = { }
}

private data class CrossfadeAnimationItem<T>(
    val key: T,
    val transition: CrossfadeTransition
)

private typealias CrossfadeTransition = @Composable (children: @Composable () -> Unit) -> Unit

@Composable
private fun animatedOpacity(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {}
): AnimatedFloat {
    val animatedFloat = animatedFloat(if (!visible) 1f else 0f)
    onCommit(visible) {
        animatedFloat.animateTo(
            if (visible) 1f else 0f,
            anim = animation,
            onEnd = { reason, _ ->
                if (reason == AnimationEndReason.TargetReached) {
                    onAnimationFinish()
                }
            }
        )
    }
    return animatedFloat
}

@ExperimentalAnimationApi
@Composable
fun SlideInRight(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        initiallyVisible = false,
        enter = slideInHorizontally(initialOffsetX = {it}),
        exit = slideOutHorizontally(targetOffsetX = {0}),
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun SlideOutLeft(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = false,
        initiallyVisible = true,
        enter = slideInHorizontally(initialOffsetX = {0}),
        exit = slideOutHorizontally(targetOffsetX = {-it}),
        content = content
    )
}