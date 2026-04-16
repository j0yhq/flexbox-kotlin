package io.joy.flowcompose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

/**
 * Compose UI integration tests for [FlexBox] on iOS.
 *
 * These tests run on the iOS simulator using Compose Multiplatform's
 * [runComposeUiTest] API, which is equivalent to the Android test API.
 * They verify end-to-end rendering of [FlexBox] through the CMP layout pipeline.
 *
 * The test cases mirror [FlexBoxComposeTest] (Android host tests).
 */
@OptIn(ExperimentalTestApi::class)
class FlexBoxIosTest {

    // ── justifyContent ────────────────────────────────────────────────────────

    @Test
    fun row_flexStart_childrenPackedLeft() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.FlexStart,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(50.dp)
    }

    @Test
    fun row_spaceBetween_threeChildren() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.SpaceBetween,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
                Box(Modifier.flexItem().size(60.dp, 30.dp).testTag("c"))
            }
        }
        // total=190, free=110, spacing=55
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(105.dp)
        onNodeWithTag("c").assertLeftPositionInRootIsEqualTo(240.dp)
    }

    @Test
    fun row_center_childrenCentered() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.Center,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("b"))
            }
        }
        // total=100, free=200, start=100
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(100.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(150.dp)
    }

    @Test
    fun row_spaceAround_twoChildren() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.SpaceAround,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("b"))
            }
        }
        // free=200, unit=100, start=50
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(50.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(200.dp)
    }

    // ── alignItems ────────────────────────────────────────────────────────────

    @Test
    fun alignItems_stretch_childrenFillHeight() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.Stretch),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
            }
        }
        onNodeWithTag("a").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun alignItems_center_verticalCenter() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.Center),
            ) {
                Box(Modifier.flexItem().size(50.dp, 40.dp).testTag("a"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(30.dp)  // (100-40)/2=30
    }

    @Test
    fun alignItems_flexEnd_childrenAtBottom() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexEnd),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(70.dp)  // 100-30
    }

    // ── flexGrow ──────────────────────────────────────────────────────────────

    @Test
    fun flexGrow_expandsToFillWidth() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexStart),
            ) {
                Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).size(0.dp, 30.dp).testTag("a"))
            }
        }
        onNodeWithTag("a").assertWidthIsEqualTo(300.dp)
    }

    @Test
    fun flexGrow_splitsFreeSpaceEqually() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexStart),
            ) {
                Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).size(0.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).size(0.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertWidthIsEqualTo(150.dp)
        onNodeWithTag("b").assertWidthIsEqualTo(150.dp)
    }

    // ── column direction ──────────────────────────────────────────────────────

    @Test
    fun column_childrenStackVertically() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(100.dp, 200.dp),
                containerStyle = FlexContainerStyle(
                    flexDirection = FlexDirection.Column,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(60.dp, 50.dp).testTag("a"))
                Box(Modifier.flexItem().size(60.dp, 80.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(50.dp)
    }

    @Test
    fun column_stretch_fillsWidth() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(100.dp, 200.dp),
                containerStyle = FlexContainerStyle(
                    flexDirection = FlexDirection.Column,
                    alignItems = AlignItems.Stretch,
                ),
            ) {
                Box(Modifier.flexItem().size(40.dp, 50.dp).testTag("a"))
            }
        }
        onNodeWithTag("a").assertWidthIsEqualTo(100.dp)
    }

    // ── flexWrap ──────────────────────────────────────────────────────────────

    @Test
    fun wrap_secondItemOnNewLine() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(150.dp, 200.dp),
                containerStyle = FlexContainerStyle(
                    flexWrap = FlexWrap.Wrap,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(100.dp, 40.dp).testTag("a"))
                Box(Modifier.flexItem().size(100.dp, 50.dp).testTag("b"))
            }
        }
        // a=100, container=150, a+b=200>150 → b wraps
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(40.dp)
    }

    // ── gap ───────────────────────────────────────────────────────────────────

    @Test
    fun columnGap_spacesRowItems() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    columnGap = 20f,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(70.dp)   // 50+20
    }

    @Test
    fun rowGap_spacesColumnItems() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(100.dp, 300.dp),
                containerStyle = FlexContainerStyle(
                    flexDirection = FlexDirection.Column,
                    rowGap = 15f,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 40.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 40.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(55.dp)   // 40+15
    }

    // ── alignSelf ─────────────────────────────────────────────────────────────

    @Test
    fun alignSelf_overridesContainerAlignment() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexStart),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(
                    Modifier.flexItem(FlexItemStyle(alignSelf = AlignSelf.FlexEnd))
                        .size(50.dp, 30.dp)
                        .testTag("b")
                )
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(70.dp)  // 100-30
    }

    // ── rowReverse ────────────────────────────────────────────────────────────

    @Test
    fun rowReverse_firstChildAppearsRightmost() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    flexDirection = FlexDirection.RowReverse,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(150.dp)  // 200-50
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(70.dp)   // 200-50-80
    }
}
